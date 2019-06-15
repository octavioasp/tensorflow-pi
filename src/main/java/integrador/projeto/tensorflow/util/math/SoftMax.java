package integrador.projeto.tensorflow.util.math;

public class SoftMax {
    private final double[] parametros;

    public SoftMax(double[] parametros) {
        this.parametros = parametros;
    }

    public double[] getValor() {
        double soma = 0;

        for (int i = 0; i< parametros.length; i++) {
            parametros[i] = Math.exp(parametros[i]);
            soma += parametros[i];
        }

        if (Double.isNaN(soma) || soma < 0) {
            for (int i = 0; i< parametros.length; i++) {
                parametros[i] = 1.0 / parametros.length;
            }
        } else {
            for (int i = 0; i< parametros.length; i++) {
                parametros[i] = parametros[i] / soma;
            }
        }

        return parametros;
    }
}
