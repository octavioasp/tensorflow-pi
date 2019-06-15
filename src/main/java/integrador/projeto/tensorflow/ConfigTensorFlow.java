package integrador.projeto.tensorflow;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

/**
 * Verificacao das configuracoes do tensor flow
 */
public class ConfigTensorFlow {
    public static void main(String[] args) throws Exception {
        try (Graph grafico = new Graph()) {
            final String valor = "Versão: " + TensorFlow.version();

            // Constante
            try (Tensor t = Tensor.create(valor.getBytes("UTF-8"))) {
                // The Java API doesn't yet include convenience functions for adding operations.
                grafico.opBuilder("Const", "MyConst").setAttr
                        ("dtipo", t.dataType()).setAttr("valor", t).build();
            }

            try (Session s = new Session(grafico);
                 Tensor output = s.runner().fetch("MyConst").run().get(0)) {
                System.out.println(new String(output.bytesValue(), "UTF-8"));
            }
        }
    }
}