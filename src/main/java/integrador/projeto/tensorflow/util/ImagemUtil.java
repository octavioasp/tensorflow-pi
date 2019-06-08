package integrador.projeto.tensorflow.util;

import integrador.projeto.tensorflow.Config;
import integrador.projeto.tensorflow.model.PosicaoDoBloco;
import integrador.projeto.tensorflow.model.Reconhecimento;
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
        IOUtil.criarDiretorioSeNaoExistir(new File(Config.OUTPUT_DIR));
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
     * @param reconhecimentos lista de objetos reconhecidos
     */
    public void etiquetaDaImagem(final byte[] imagem, final List<Reconhecimento> reconhecimentos, final String nomeDoArquivo) {
        BufferedImage bufferedImage = imagemUtil.criarImagensParaBytes(imagem);
        float escalaX = (float) bufferedImage.getWidth() / (float) Config.SIZE;
        float escalaY = (float) bufferedImage.getHeight() / (float) Config.SIZE;
        Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();

        for (Reconhecimento reconhecimento: reconhecimentos) {
            PosicaoDoBloco box = reconhecimento.getLocalizacaoEscalonada(escalaX, escalaY);
            //desenhar o texto
            graphics.drawString(reconhecimento.getTitulo() + " " + reconhecimento.getConfidencia(), box.getEsquerda(), box.getTopo() - 7);
            // desenhar a caixa delimitadora
            graphics.drawRect(box.getEsquerdaInt(),box.getTopoInt(), box.getLarguraInt(), box.getAlturaInt());
        }

        graphics.dispose();
        saveImage(bufferedImage, Config.OUTPUT_DIR + "/" + nomeDoArquivo);
    }

    public void saveImage(final BufferedImage imagem, final String obj) {
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
