package logica;

import enums.TipoMissao;

public class Drone extends Robo {
    private static final long serialVersionUID = 1L;

    private final double altitudeMax;
    private final boolean captarImagem;

    public Drone(int id, String nome, int nivelBateria,
                 double altitudeMax, boolean captarImagem) {
        super(id, nome, nivelBateria);
        if (!Double.isFinite(altitudeMax) || altitudeMax <= 0) {
            throw new IllegalArgumentException("A altitude máxima deve ser positiva.");
        }
        this.altitudeMax = altitudeMax;
        this.captarImagem = captarImagem;
    }

    public double getAltitudeMaximaMetros() { return altitudeMax; }
    public boolean isPossuiCamera() { return captarImagem; }

    @Override
    public boolean podeExecutar(Missao missao) {
        if (missao == null) {
            return false;
        }

        return missao.getTipo() == TipoMissao.RECONHECIMENTO
                || (missao.getTipo() == TipoMissao.LOCALIZACAO_PESSOAS && captarImagem);
    }

    @Override
    public int calcularConsumoBateria(Missao missao) {
        if (!podeExecutar(missao)) {
            throw new IllegalArgumentException("Missão incompatível com drone.");
        }
        return missao.calculaBateria();
    }

    @Override
    public void executarMissao(Missao missao) {
        validarExecucao(missao);
        String resultado = missao.getTipo() == TipoMissao.RECONHECIMENTO
                ? "Área sobrevoada e informações coletadas."
                : "Busca aérea simulada realizada com câmera.";
        missao.registrarResultado(resultado);
    }

    @Override
    public String toString() {
        return super.toString() + "\n Tipo: Drone"
                + "\n Altitude máxima (m): " + altitudeMax
                + "\n Câmera: " + (captarImagem ? "sim" : "não");
    }
}
