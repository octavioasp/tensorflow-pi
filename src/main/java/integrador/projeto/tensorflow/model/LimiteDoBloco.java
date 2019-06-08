package integrador.projeto.tensorflow.model;

/**
 * Modelo para armazenar os dados do limite do bloco
 */
public class LimiteDoBloco {
    private double x;
    private double y;
    private double largura;
    private double altura;
    private double confidencia;
    private double[] classes;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getLargura() {
        return largura;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getConfidencia() {
        return confidencia;
    }

    public void setConfidencia(double confidencia) {
        this.confidencia = confidencia;
    }

    public double[] getClasses() {
        return classes;
    }

    public void setClasses(double[] classes) {
        this.classes = classes;
    }
}
