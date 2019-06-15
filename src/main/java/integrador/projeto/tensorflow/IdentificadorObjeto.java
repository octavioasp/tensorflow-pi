package integrador.projeto.tensorflow;

import integrador.projeto.tensorflow.classificador.ClassificadorDeImagensYOLO;
import integrador.projeto.tensorflow.model.ObjetoIdentificado;
import integrador.projeto.tensorflow.util.ConstrutorGrafico;
import integrador.projeto.tensorflow.util.IOUtil;
import integrador.projeto.tensorflow.util.ImagemUtil;
import integrador.projeto.tensorflow.util.ServicoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.FloatBuffer;
import java.util.List;

import static integrador.projeto.tensorflow.Configuracoes.*;

/**
 * IdentificadorObjeto classe para identificar objetos usando modelos pré-treinados com o TensorFlow Java API.
 */
public class IdentificadorObjeto {
    private final static Logger logger = LoggerFactory.getLogger(IdentificadorObjeto.class);
    private byte[] graphDef;
    private List<String> descricoes;

    public IdentificadorObjeto() {
        try {
            graphDef = IOUtil.lerTodosOsBytesOrSair(GRAPH_FILE);
            descricoes = IOUtil.lerTodasAsLinhasOrSair(LABEL_FILE);
        } catch (ServicoException ex) {
            logger.error("Erro ", ex);
        }
    }

    /**
     * Detectar objetos na imagem dada
     * @param localizacaoImagem localizacao da imagem
     */
    public void identificar(final String localizacaoImagem) {
        byte[] imagem = IOUtil.lerTodosOsBytesOrSair(localizacaoImagem);
        try (Tensor<Float> imagemNormalizada = normalizarImagem(imagem)) {
            List<ObjetoIdentificado> objetosIdentificados = ClassificadorDeImagensYOLO.getInstancia().classificarImagem(executeYOLOGraph(imagemNormalizada), descricoes);
            saida(objetosIdentificados);
            ImagemUtil.getInstancia().etiquetaDaImagem(imagem, objetosIdentificados, IOUtil.getNomeDoArquivo(localizacaoImagem));
        }
    }

    /**
     * Pre-process input. It resize the image and normalize its pixels
     * @param bytesImagem Input image
     * @return Tensor<Float> with shape [1][416][416][3]
     */
    private Tensor<Float> normalizarImagem(final byte[] bytesImagem) {
        try (Graph graph = new Graph()) {
            ConstrutorGrafico construtorGrafico = new ConstrutorGrafico(graph);

            final Output<Float> output =
                construtorGrafico.div( // Divide each pixels with the MEAN
                    construtorGrafico.resizeBilinear( // Resize using bilinear interpolation
                            construtorGrafico.expandDims( // Increase the output tensors dimension
                                    construtorGrafico.cast( // Cast the output to Float
                                            construtorGrafico.decodificarJpeg(
                                                    construtorGrafico.constante("input", bytesImagem), 3),
                                            Float.class),
                                    construtorGrafico.constante("make_batch", 0)),
                            construtorGrafico.constante("size", new int[]{SIZE, SIZE})),
                    construtorGrafico.constante("scale", MEAN));

            try (Session session = new Session(graph)) {
                return session.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
            }
        }
    }

    /**
     * Executa o gráfico na imagem pré-processada fornecida
     * @return tensor de saída retornado pelo tensorFlow
     */
    private float[] executeYOLOGraph(final Tensor<Float> image) {
        try (Graph graph = new Graph()) {
            graph.importGraphDef(graphDef);
            try (Session s = new Session(graph);
                Tensor<Float> result = s.runner().feed("input", image).fetch("output").run().get(0).expect(Float.class)) {
                float[] outputTensor = new float[ClassificadorDeImagensYOLO.getInstancia().getTamanhoSaidaPeloFormato(result)];
                FloatBuffer floatBuffer = FloatBuffer.wrap(outputTensor);
                result.writeTo(floatBuffer);
                return outputTensor;
            }
        }
    }

    /**
     * Saída dos objetos reconhecidos, nome e percentual de reconhecimento.
     * @param objetoIdentificados lista de objetos identificados
     */
    private void saida(final List<ObjetoIdentificado> objetoIdentificados) {
        for (ObjetoIdentificado objetoIdentificado : objetoIdentificados) {
            BigDecimal confidencia = BigDecimal.valueOf(objetoIdentificado.getConfidencia() * 100);
            logger.info("Objeto: {} - Confidência: {}", objetoIdentificado.getTitulo(), confidencia.setScale(2, RoundingMode.HALF_UP) + "%");
        }
    }
}
