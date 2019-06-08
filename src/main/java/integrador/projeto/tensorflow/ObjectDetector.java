package integrador.projeto.tensorflow;

import integrador.projeto.tensorflow.classificador.ClassificadorDeImagensYOLO;
import integrador.projeto.tensorflow.model.Reconhecimento;
import integrador.projeto.tensorflow.util.GraphBuilder;
import integrador.projeto.tensorflow.util.IOUtil;
import integrador.projeto.tensorflow.util.ImagemUtil;
import integrador.projeto.tensorflow.util.ServicoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import java.nio.FloatBuffer;
import java.util.List;

import static integrador.projeto.tensorflow.Config.*;

/**
 * ObjectDetector class to detect objects using pre-trained models with TensorFlow Java API.
 */
public class ObjectDetector {
    private final static Logger LOGGER = LoggerFactory.getLogger(ObjectDetector.class);
    private byte[] GRAPH_DEF;
    private List<String> LABELS;

    public ObjectDetector() {
        try {
            GRAPH_DEF = IOUtil.lerTodosOsBytesOrSair(GRAPH_FILE);
            LABELS = IOUtil.lerTodasAsLinhasOrSair(LABEL_FILE);
        } catch (ServicoException ex) {
            LOGGER.error("Download one of my graph file to run the program! \n" +
                    "You can find my graphs here: https://drive.google.com/open?id=1GfS1Yle7Xari1tRUEi2EDYedFteAOaoN");
        }
    }

    /**
     * Detect objects on the given image
     * @param imageLocation the location of the image
     */
    public void detect(final String imageLocation) {
        byte[] image = IOUtil.lerTodosOsBytesOrSair(imageLocation);
        try (Tensor<Float> normalizedImage = normalizeImage(image)) {
            List<Reconhecimento> reconhecimentos = ClassificadorDeImagensYOLO.getInstancia().classificarImagem(executeYOLOGraph(normalizedImage), LABELS);
            saida(reconhecimentos);
            ImagemUtil.getInstancia().etiquetaDaImagem(image, reconhecimentos, IOUtil.getNomeDoArquivo(imageLocation));
        }
    }

    /**
     * Pre-process input. It resize the image and normalize its pixels
     * @param imageBytes Input image
     * @return Tensor<Float> with shape [1][416][416][3]
     */
    private Tensor<Float> normalizeImage(final byte[] imageBytes) {
        try (Graph graph = new Graph()) {
            GraphBuilder graphBuilder = new GraphBuilder(graph);

            final Output<Float> output =
                graphBuilder.div( // Divide each pixels with the MEAN
                    graphBuilder.resizeBilinear( // Resize using bilinear interpolation
                            graphBuilder.expandDims( // Increase the output tensors dimension
                                    graphBuilder.cast( // Cast the output to Float
                                            graphBuilder.decodeJpeg(
                                                    graphBuilder.constant("input", imageBytes), 3),
                                            Float.class),
                                    graphBuilder.constant("make_batch", 0)),
                            graphBuilder.constant("size", new int[]{SIZE, SIZE})),
                    graphBuilder.constant("scale", MEAN));

            try (Session session = new Session(graph)) {
                return session.runner().fetch(output.op().name()).run().get(0).expect(Float.class);
            }
        }
    }

    /**
     * Executes graph on the given preprocessed image
     * @param image preprocessed image
     * @return output tensor returned by tensorFlow
     */
    private float[] executeYOLOGraph(final Tensor<Float> image) {
        try (Graph graph = new Graph()) {
            graph.importGraphDef(GRAPH_DEF);
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
     * Prints out the recognize objects and its confidence
     * @param reconhecimentos lista de reconhecimentos
     */
    private void saida(final List<Reconhecimento> reconhecimentos) {
        for (Reconhecimento reconhecimento : reconhecimentos) {
            LOGGER.info("Objeto: {} - confidencia: {}", reconhecimento.getTitulo(), reconhecimento.getConfidencia());
        }
    }
}
