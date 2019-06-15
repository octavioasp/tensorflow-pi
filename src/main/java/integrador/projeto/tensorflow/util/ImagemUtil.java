package integrador.projeto.tensorflow.util;

import integrador.projeto.tensorflow.Configuracoes;
import integrador.projeto.tensorflow.model.ObjetoIdentificado;
import integrador.projeto.tensorflow.model.PosicaoDoBloco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Util - Classe para processamento de imagem.
 */
public class ImagemUtil {
    private final static Logger logger = LoggerFactory.getLogger(ImagemUtil.class);
    private static ImagemUtil imagemUtil;

    private ImagemUtil() {
        IOUtil.criarDiretorioSeNaoExistir(new File(Configuracoes.OUTPUT_DIR));
    }

    /**
     *
     * Retorna a instância singleton dessa classe.
     * @return ImagemUtil instancia
     */
    public static ImagemUtil getInstancia() {
        if (imagemUtil == null) {
            imagemUtil = new ImagemUtil();
        }

        return imagemUtil;
    }

    /**
     * Etiqueta com classes e previsões dadas pelo TensorFLow
     * @param imagem armazenada em buffer para etiqueta
     * @param objetoIdentificados lista de objetos reconhecidos
     */
    public void etiquetaDaImagem(final byte[] imagem, final List<ObjetoIdentificado> objetoIdentificados, final String nomeDoArquivo) {
        BufferedImage bufferedImage = imagemUtil.criarImagensParaBytes(imagem);
        float escalaX = (float) bufferedImage.getWidth() / (float) Configuracoes.SIZE;
        float escalaY = (float) bufferedImage.getHeight() / (float) Configuracoes.SIZE;
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();

        for (ObjetoIdentificado objetoIdentificado : objetoIdentificados) {
            PosicaoDoBloco box = objetoIdentificado.getLocalizacaoEscalonada(escalaX, escalaY);
            //desenhar o texto
            graphics.drawString(objetoIdentificado.getTitulo() + " " + objetoIdentificado.getConfidencia(), box.getEsquerda(), box.getTopo() - 7);
            // desenhar a caixa delimitadora
            graphics.drawRect(box.getEsquerdaInt(),box.getTopoInt(), box.getLarguraInt(), box.getAlturaInt());
        }

        graphics.dispose();
        salvarImagem(bufferedImage, Configuracoes.OUTPUT_DIR + "/" + nomeDoArquivo);
    }

    public void salvarImagem(final BufferedImage imagem, final String obj) {
        try {
            ImageIO.write(imagem,"jpg", new File(obj));
        } catch (IOException e) {
            logger.error("Impossível salvar a imagem {}!", obj);
        }
    }

    private BufferedImage criarImagensParaBytes(final byte[] imagemData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imagemData);
        try {
            return ImageIO.read(bais);
        } catch (IOException ex) {
            throw new ServicoException("Não foi possível crir imagem!", ex);
        }
    }
}
