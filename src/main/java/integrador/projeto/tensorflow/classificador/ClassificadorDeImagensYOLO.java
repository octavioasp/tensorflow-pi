package integrador.projeto.tensorflow.classificador;

import com.google.common.collect.Lists;
import integrador.projeto.tensorflow.model.LimiteDoBloco;
import integrador.projeto.tensorflow.model.PosicaoDoBloco;
import integrador.projeto.tensorflow.model.Reconhecimento;
import integrador.projeto.tensorflow.util.math.ArgMax;
import integrador.projeto.tensorflow.util.math.SoftMax;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.tensorflow.Tensor;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ClassificadorDeImagensYOLO {

    private final static float LIMITE_DE_SOBREPOSICAO = 0.5f;
    private final static double[] ANCORAS = {1.08, 1.19, 3.42, 4.41, 6.63, 11.38, 9.42, 5.11, 16.62, 10.52};
    private final static int TAMANHO = 13;
    private final static int MAXIMO_RECONHECIMENTO_CLASSES = 24;
    private final static float LIMITE = 0.5f;
    private final static int MAXIMO_RESULTADOS = 24;
    private final static int NUMERO_DE_BLOCOS_DELIMITADORES = 5;
    private static ClassificadorDeImagensYOLO classificador;

    private ClassificadorDeImagensYOLO() {
    }

    public static ClassificadorDeImagensYOLO getInstancia() {
        if (classificador == null) {
            classificador = new ClassificadorDeImagensYOLO();
        }
        return classificador;
    }

    /**
     * Obtém o número de classes com base na forma de tensor
     *
     * @param resultado - saida do tensorflow
     * @return numero de classes
     */
    public int getTamanhoSaidaPeloFormato(Tensor<Float> resultado) {
        return (int) (resultado.shape()[3] * Math.pow(TAMANHO, 2));
    }

    /**
     * Classifica os objetos na imagem
     *
     * @param saidaTensorFLow saída do tensorFlow, eh um tensor de 13x13x((numero_de_classes +1) * 5)
     *                        125 = (numClass +  Tx, Ty, Tw, Th, To) * 5 - porque temos 5 caixas por celula
     * @param etiquetas       um vetor de string com as etiquetas
     * @return lista de objetos de reconhecimento.
     */
    public List<Reconhecimento> classificarImagem(final float[] saidaTensorFLow, final List<String> etiquetas) {
        int numeroDeClasses = (int) (saidaTensorFLow.length / (Math.pow(TAMANHO, 2) * NUMERO_DE_BLOCOS_DELIMITADORES) - 5);
        LimiteDoBloco[][][] posicaoDoBlocoPorCelula = new LimiteDoBloco[TAMANHO][TAMANHO][NUMERO_DE_BLOCOS_DELIMITADORES];
        PriorityQueue<Reconhecimento> prioridadeNaFila = new PriorityQueue(MAXIMO_RECONHECIMENTO_CLASSES, new RecognitionComparator());

        int offset = 0;
        for (int cy = 0; cy < TAMANHO; cy++) {        // TAMANHO * TAMANHO celulas
            for (int cx = 0; cx < TAMANHO; cx++) {
                for (int b = 0; b < NUMERO_DE_BLOCOS_DELIMITADORES; b++) {   // 5 blocos delimitadores por cada célula
                    posicaoDoBlocoPorCelula[cx][cy][b] = getModelo(saidaTensorFLow, cx, cy, b, numeroDeClasses, offset);
                    calcularPrincipaisPrevisoes(posicaoDoBlocoPorCelula[cx][cy][b], prioridadeNaFila, etiquetas);
                    offset = offset + numeroDeClasses + 5;
                }
            }
        }

        return getReconhecimento(prioridadeNaFila);
    }

    private LimiteDoBloco getModelo(final float[] saidaTensorFlow, int cx, int cy, int b, int numeroDeClasses, int compensador) {
        LimiteDoBloco modelo = new LimiteDoBloco();
        Sigmoid sigmoid = new Sigmoid();
        modelo.setX((cx + sigmoid.value(saidaTensorFlow[compensador])) * 32);
        modelo.setY((cy + sigmoid.value(saidaTensorFlow[compensador + 1])) * 32);
        modelo.setLargura(Math.exp(saidaTensorFlow[compensador + 2]) * ANCORAS[2 * b] * 32);
        modelo.setAltura(Math.exp(saidaTensorFlow[compensador + 3]) * ANCORAS[2 * b + 1] * 32);
        modelo.setConfidencia(sigmoid.value(saidaTensorFlow[compensador + 4]));

        modelo.setClasses(new double[numeroDeClasses]);

        for (int probIndex = 0; probIndex < numeroDeClasses; probIndex++) {
            modelo.getClasses()[probIndex] = saidaTensorFlow[probIndex + compensador + 5];
        }

        return modelo;
    }

    private void calcularPrincipaisPrevisoes(final LimiteDoBloco limiteDoBloco, final PriorityQueue<Reconhecimento> previsaoFila,
                                             final List<String> etiquetas) {
        for (int i = 0; i < limiteDoBloco.getClasses().length; i++) {
            ArgMax.Result argMax = new ArgMax(new SoftMax(limiteDoBloco.getClasses()).getValue()).getResult();
            double confideciaDaClasse = argMax.getMaxValue() * limiteDoBloco.getConfidencia();
            if (confideciaDaClasse > LIMITE) {
                previsaoFila.add(new Reconhecimento(argMax.getIndex(), etiquetas.get(argMax.getIndex()), (float) confideciaDaClasse,
                        new PosicaoDoBloco((float) (limiteDoBloco.getX() - limiteDoBloco.getLargura() / 2),
                                (float) (limiteDoBloco.getY() - limiteDoBloco.getAltura() / 2),
                                (float) limiteDoBloco.getLargura(),
                                (float) limiteDoBloco.getLargura())));
            }
        }
    }

    private List<Reconhecimento> getReconhecimento(final PriorityQueue<Reconhecimento> priorityQueue) {
        List<Reconhecimento> reconhecimentos = Lists.newArrayList();

        if (priorityQueue.size() > 0) {
            // Melhor Reconhecimento
            Reconhecimento melhorReconhecimento = priorityQueue.poll();
            reconhecimentos.add(melhorReconhecimento);

            for (int i = 0; i < Math.min(priorityQueue.size(), MAXIMO_RESULTADOS); ++i) {
                Reconhecimento reconhecimento = priorityQueue.poll();
                boolean sobreposicoes = false;
                for (Reconhecimento reconhecimentoPrevio : reconhecimentos) {
                    sobreposicoes = sobreposicoes || (getInterseccaoProporcional(reconhecimentoPrevio.getLocalizacao(),
                            reconhecimento.getLocalizacao()) > LIMITE_DE_SOBREPOSICAO);
                }

                if (!sobreposicoes) {
                    reconhecimentos.add(reconhecimento);
                }
            }
        }

        return reconhecimentos;
    }

    private float getInterseccaoProporcional(PosicaoDoBloco formaPrimaria, PosicaoDoBloco formaSecundaria) {
        if (sobreposicoes(formaPrimaria, formaSecundaria)) {
            float interseccaoDeSuperficie = Math.max(0, Math.min(formaPrimaria.getDireita(), formaSecundaria.getDireita()) - Math.max(formaPrimaria.getEsquerda(), formaSecundaria.getEsquerda())) *
                    Math.max(0, Math.min(formaPrimaria.getInferior(), formaSecundaria.getInferior()) - Math.max(formaPrimaria.getTopo(), formaSecundaria.getTopo()));

            float superficiePrimaria = Math.abs(formaPrimaria.getDireita() - formaPrimaria.getEsquerda()) * Math.abs(formaPrimaria.getInferior() - formaPrimaria.getTopo());

            return interseccaoDeSuperficie / superficiePrimaria;
        }

        return 0f;

    }

    private boolean sobreposicoes(PosicaoDoBloco posPrimaria, PosicaoDoBloco posSecundaria) {
        return posPrimaria.getEsquerda() < posSecundaria.getDireita() && posPrimaria.getDireita() > posSecundaria.getEsquerda()
                && posPrimaria.getTopo() < posSecundaria.getInferior() && posPrimaria.getInferior() > posSecundaria.getTopo();
    }

    // // Invertido intencionalmente pra manter uma alta confidencia no topo da fila.
    private class RecognitionComparator implements Comparator<Reconhecimento> {
        @Override
        public int compare(final Reconhecimento reconhecimento1, final Reconhecimento reconhecimento2) {
            return Float.compare(reconhecimento2.getConfidencia(), reconhecimento1.getConfidencia());
        }
    }
}

