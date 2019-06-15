package integrador.projeto.tensorflow.util;

import integrador.projeto.tensorflow.IdentificadorObjeto;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Util - Leitura dos arquivos de imagens, GraphDef e descricoes.
 */
public final class IOUtil {
    private final static Logger logger = LoggerFactory.getLogger(IOUtil.class);
    private IOUtil() {}

    public static byte[] lerTodosOsBytesOrSair(final String nomeArquivo) {
        try {
            return IOUtils.toByteArray(IdentificadorObjeto.class.getResourceAsStream(nomeArquivo));
        } catch (IOException | NullPointerException ex) {
            logger.error("Falha na leitura [{}]!", nomeArquivo);
            throw new ServicoException("Falha na leitura [" + nomeArquivo + "]!", ex);
        }
    }

    public static List<String> lerTodasAsLinhasOrSair(final String nomeArquivo) {
        try {
            File file = new File(IdentificadorObjeto.class.getResource(nomeArquivo).toURI());
            return Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
        } catch (IOException | URISyntaxException ex) {
            logger.error("Falha na leitura [{}]!", nomeArquivo, ex.getMessage());
            throw new ServicoException("Falha na leitura [" + nomeArquivo + "]!", ex);
        }
    }

    public static void criarDiretorioSeNaoExistir(final File diretorio) {
        if (!diretorio.exists()) {
            diretorio.mkdir();
        }
    }

    public static String getNomeDoArquivo(final String caminho) {
        return caminho.substring(caminho.lastIndexOf("/") + 1);
    }
}
