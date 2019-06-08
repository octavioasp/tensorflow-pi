package integrador.projeto.tensorflow.model;

/**
 * Modelo para armazenar a posicao dos blocos delimitadores
 */
public class PosicaoDoBloco {
    private float esquerda;
    private float topo;
    private float direita;
    private float inferior;
    private float largura;
    private float altura;

    public PosicaoDoBloco(float esquerda, float topo, float largura, float altura) {
        this.esquerda = esquerda;
        this.topo = topo;
        this.largura = largura;
        this.altura = altura;

        iniciar();
    }

    public PosicaoDoBloco(final PosicaoDoBloco posicaoDoBloco) {
        this.esquerda = posicaoDoBloco.esquerda;
        this.topo = posicaoDoBloco.topo;
        this.largura = posicaoDoBloco.largura;
        this.altura = posicaoDoBloco.altura;

        iniciar();
    }

    public PosicaoDoBloco(final PosicaoDoBloco posicaoDoBloco, final float escalaX, final float escalaY) {
        this.esquerda = posicaoDoBloco.esquerda * escalaX;
        this.topo = posicaoDoBloco.topo * escalaY;
        this.largura = posicaoDoBloco.largura * escalaX;
        this.altura = posicaoDoBloco.altura * escalaY;

        iniciar();
    }

    public void iniciar() {
        float tmpEsquerda = this.esquerda;
        float tmpTopo = this.topo;
        float tmpDireita = this.esquerda + this.largura;
        float tmpInferior = this.topo + this.altura;

        this.esquerda = Math.min(tmpEsquerda, tmpDireita); // esquerda deve ter um valor mais baixo que a direita
        this.topo = Math.min(tmpTopo, tmpInferior);  // topo deve ter um valor mais baixo que o inferior
        this.direita = Math.max(tmpEsquerda, tmpDireita);
        this.inferior = Math.max(tmpTopo, tmpInferior);
    }

    public float getEsquerda() {
        return esquerda;
    }

    public int getEsquerdaInt() {
        return (int) esquerda;
    }

    public float getTopo() {
        return topo;
    }

    public int getTopoInt() {
        return (int) topo;
    }

    public float getLargura() {
        return largura;
    }

    public int getLarguraInt() {
        return (int) largura;
    }

    public float getAltura() {
        return altura;
    }

    public int getAlturaInt() {
        return (int) altura;
    }

    public float getDireita() {
        return direita;
    }

    public int getDireitaInt() {
        return (int) direita;
    }

    public float getInferior() {
        return inferior;
    }

    public int getInferiorInt() {
        return (int) inferior;
    }

    @Override
    public String toString() {
        return "PosicaoDoBloco{" +
                "esquerda=" + esquerda +
                ", topo=" + topo +
                ", largura=" + largura +
                ", altura=" + altura +
                '}';
    }
}
