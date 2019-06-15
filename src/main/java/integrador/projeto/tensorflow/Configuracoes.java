package integrador.projeto.tensorflow;

/**
 * Configuration file for TensorFlow Java Yolo application
 */
public interface Configuracoes {
    String GRAPH_FILE = "/YOLO/yolo-voc.pb";
    String LABEL_FILE = "/YOLO/yolo-voc-labels.txt";

    // Params used for image processing
    int SIZE = 416;
    float MEAN = 255f;

    // Output directory
    String OUTPUT_DIR = "./sample";
}
