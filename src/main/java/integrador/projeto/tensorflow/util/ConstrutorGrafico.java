package integrador.projeto.tensorflow.util;

import org.tensorflow.DataType;
import org.tensorflow.Graph;
import org.tensorflow.Output;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;

public class ConstrutorGrafico {
    private Graph grafico;

    public ConstrutorGrafico(Graph grafico) {
        this.grafico = grafico;
    }

    public Output<Float> div(Output<Float> x, Output<Float> y) {
        return binaryOp("Div", x, y);
    }

    public <T> Output<Float> resizeBilinear(Output<T> imagens, Output<Integer> tamanho) {
        return binaryOp3("RedimensionamentoBilinear", imagens, tamanho);
    }

    public <T> Output<T> expandDims(Output<T> entrada, Output<Integer> dimensao) {
        return binaryOp3("ExpandirDimensao", entrada, dimensao);
    }

    public <T, U> Output<U> cast(Output<T> valor, Class<U> tipo) {
        DataType dtype = DataType.fromClass(tipo);
        return grafico.opBuilder("Conversao", "Conversao")
                .addInput(valor)
                .setAttr("DstT", dtype)
                .build()
                .<U>output(0);
    }

    public Output<UInt8> decodificarJpeg(Output<String> conteudo, long canais) {
        return grafico.opBuilder("JpegDecodificado", "JpegDecodificado")
                .addInput(conteudo)
                .setAttr("canais", canais)
                .build()
                .<UInt8>output(0);
    }

    public <T> Output<T> constante(String name, Object valor, Class<T> tipo) {
        try (Tensor<T> t = Tensor.<T>create(valor, tipo)) {
            return grafico.opBuilder("Constante", name)
                    .setAttr("tipo", DataType.fromClass(tipo))
                    .setAttr("valor", t)
                    .build()
                    .<T>output(0);
        }
    }

    public Output<String> constante(String nome, byte[] valor) {
        return this.constante(nome, valor, String.class);
    }

    public Output<Integer> constante(String nome, int valor) {
        return this.constante(nome, valor, Integer.class);
    }

    public Output<Integer> constante(String nome, int[] valor) {
        return this.constante(nome, valor, Integer.class);
    }

    public Output<Float> constante(String nome, float valor) {
        return this.constante(nome, valor, Float.class);
    }

    private <T> Output<T> binaryOp(String tipo, Output<T> in1, Output<T> in2) {
        return grafico.opBuilder(tipo, tipo).addInput(in1).addInput(in2).build().<T>output(0);
    }

    private <T, U, V> Output<T> binaryOp3(String type, Output<U> in1, Output<V> in2) {
        return grafico.opBuilder(type, type).addInput(in1).addInput(in2).build().<T>output(0);
    }
}
