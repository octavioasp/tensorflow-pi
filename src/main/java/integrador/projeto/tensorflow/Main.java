package integrador.projeto.tensorflow;

public class Main {
    private final static String imagem = "/image/teste.jpg";

    public static void main(String[] args) {
        IdentificadorObjeto identificador = new IdentificadorObjeto();
        identificador.identificar(imagem);
    }
}
