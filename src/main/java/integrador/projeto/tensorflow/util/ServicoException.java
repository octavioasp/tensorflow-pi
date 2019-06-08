package integrador.projeto.tensorflow.util;

/**
 * ServicoException, Excecoes para camada de servico
 */
public class ServicoException extends RuntimeException {
    public ServicoException() {
        super();
    }

    public ServicoException(String mensagem) {
        super(mensagem);
    }

    public ServicoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
