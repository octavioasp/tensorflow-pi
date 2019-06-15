package integrador.projeto.tensorflow.util.math;

public class ArgMax {

    private double[] parametros;

    public ArgMax(double[] parametros) {
        this.parametros = parametros;
    }

    public Resultado getResultado() {
        int maxIndex = 0;
        for (int i = 0; i< parametros.length; i++) {
            if (parametros[maxIndex] < parametros[i]) {
                maxIndex = i;
            }
        }

        return new Resultado(maxIndex, parametros[maxIndex]);
    }

    public class Resultado {
        private int index;
        private double valorMaximo;

        public Resultado(int index, double valorMaximo) {
            this.index = index;
            this.valorMaximo = valorMaximo;
        }

        public int getIndex() {
            return index;
        }

        public double getValorMaximo() {
            return valorMaximo;
        }
    }
}
