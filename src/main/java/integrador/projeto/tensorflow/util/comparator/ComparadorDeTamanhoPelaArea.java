package integrador.projeto.tensorflow.util.comparator;

import integrador.projeto.tensorflow.model.Tamanho;

import java.util.Comparator;

/**
 * Campara dois {@code Tamanho}s com base em sua areas.
 */
public class ComparadorDeTamanhoPelaArea implements Comparator<Tamanho> {
    @Override
    public int compare(final Tamanho t1, final Tamanho t2) {
        // Cast para garantir a precisao dos calculos
        return Long.signum((long) t1.getLargura() * t1.getAltura()
                - (long) t2.getLargura() * t2.getAltura());
    }
}
