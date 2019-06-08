package integrador.projeto.tensorflow.model;

public final class Reconhecimento {
    /**
     * Um identificador exclusivo para o que foi reconhecido. Específico para a classe,
     * não para a instância do objeto.
     */
    private final Integer id;
    private final String titulo;
    private final Float confidencia;
    private PosicaoDoBloco localizacao;

    public Reconhecimento(final Integer id, final String title,
                       final Float confidencia, final PosicaoDoBloco localizacao) {
        this.id = id;
        this.titulo = title;
        this.confidencia = confidencia;
        this.localizacao = localizacao;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Float getConfidencia() {
        return confidencia;
    }

    public PosicaoDoBloco getLocalizacaoEscalonada(final float escalaX, final float escalaY) {
        return new PosicaoDoBloco(localizacao, escalaX, escalaY);
    }

    public PosicaoDoBloco getLocalizacao() {
        return new PosicaoDoBloco(localizacao);
    }

    public void setLocalizacao(PosicaoDoBloco localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public String toString() {
        return "Recognition{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", confidencia=" + confidencia +
                ", localizacao=" + localizacao +
                '}';
    }
}
