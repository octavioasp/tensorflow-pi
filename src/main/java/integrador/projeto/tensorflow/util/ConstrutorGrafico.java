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

    public <T> Output<Float> resizeBilinear(Output<T> images, Output<Integer> size) {
        return binaryOp3("ResizeBilinear", images, size);
    }

    public <T> Output<T> expandDims(Output<T> input, Output<Integer> dim) {
        return binaryOp3("ExpandDims", input, dim);
    }

    public <T, U> Output<U> cast(Output<T> value, Class<U> type) {
        DataType dtype = DataType.fromClass(type);
        return grafico.opBuilder("Cast", "Cast")
                .addInput(value)
                .setAttr("DstT", dtype)
                .build()
                .<U>output(0);
    }

    public Output<UInt8> decodificarJpeg(Output<String> conteudo, long canais) {
        return grafico.opBuilder("DecodeJpeg", "DecodeJpeg")
                .addInput(conteudo)
                .setAttr("channels", canais)
                .build()
                .<UInt8>output(0);
    }

    public <T> Output<T> constante(String name, Object valor, Class<T> tipo) {
        try (Tensor<T> t = Tensor.<T>create(valor, tipo)) {
            return grafico.opBuilder("Const", name)
                    .setAttr("dtype", DataType.fromClass(tipo))
                    .setAttr("value", t)
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
