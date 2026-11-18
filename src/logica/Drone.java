
package logica;

import enums.TipoMissao;

public class Drone extends Robo {

    private double altitudeMax;
    private boolean captarImagem;

    public Drone(int id, String nome, int nivelBateria,
                 double altitudeMax, boolean captarImagem) {

        super(id, nome, nivelBateria);

        if (!Double.isFinite(altitudeMax) || altitudeMax <= 0) {
            throw new IllegalArgumentException(
                    "A altitude maxima deve ser positiva."
            );
        }

        this.altitudeMax = altitudeMax;
        this.captarImagem = captarImagem;
    }

    public double getAltitudeMaximaMetros() {
        return altitudeMax;
    }

    public boolean isPossuiCamera() {
        return captarImagem;
    }

    @Override
    public boolean podeExecutar(Missao missao) {

        if (missao == null) {
            return false;
        }

        return missao.getTipo() == TipoMissao.RECONHECIMENTO
                || (missao.getTipo() == TipoMissao.LOCALIZACAO_PESSOAS
                && captarImagem);
    }

    @Override
    public int calcularConsumoBateria(Missao missao) {

        if (!podeExecutar(missao)) {
            throw new IllegalArgumentException(
                    "Missao incompativel com drone."
            );
        }

        return missao.getTipo() == TipoMissao.RECONHECIMENTO ? 20 : 30;
    }

    @Override
    public void executarMissao(Missao missao) {

        int consumo = iniciarExecucao(missao);

        String resultado;

        if (missao.getTipo() == TipoMissao.RECONHECIMENTO) {
            resultado = "Area sobrevoada e informacoes coletadas.";
        } else {
            resultado = "Busca aerea simulada realizada com camera.";
        }

        concluirExecucao(missao, consumo, resultado);
    }
}
