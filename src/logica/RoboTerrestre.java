
package logica;

import enums.TipoTerreno;
import enums.TipoMissao;

public class RoboTerrestre extends Robo {

    private final double capacidadeCargaKg;
    private final boolean superaObstaculos;

    public RoboTerrestre(int id, String nome, int nivelBateria,
                         double capacidadeCargaKg, boolean superaObstaculos) {

        super(id, nome, nivelBateria);

        if (!Double.isFinite(capacidadeCargaKg) || capacidadeCargaKg <= 0) {
            throw new IllegalArgumentException(
                    "A capacidade de carga deve ser positiva."
            );
        }

        this.capacidadeCargaKg = capacidadeCargaKg;
        this.superaObstaculos = superaObstaculos;
    }

    public double getCapacidadeCargaKg() {
        return capacidadeCargaKg;
    }

    public boolean isSuperaObstaculos() {
        return superaObstaculos;
    }

    @Override
    public boolean podeExecutar(Missao missao) {

        if (missao == null || missao.getTipo() != TipoMissao.TRANSPORTE_SUPRIMENTOS) {
            return false;
        }

        TipoTerreno terreno = missao.getArea().getTipoTerreno();

        return missao.getPesoCargaKg() <= capacidadeCargaKg
                && terreno != TipoTerreno.ALAGADO
                && (terreno != TipoTerreno.IRREGULAR || superaObstaculos);
    }

    @Override
    public int calcularConsumoBateria(Missao missao) {

        if (!podeExecutar(missao)) {
            throw new IllegalArgumentException(
                    "Missao incompativel com robo terrestre."
            );
        }

        return missao.calculaBateria();
    }

    @Override
    public void executarMissao(Missao missao) {

        int consumo = iniciarExecucao(missao);

        String resultado = String.format(
                "Transporte simulado de %.1f kg de suprimentos concluido.",
                missao.getPesoCargaKg()
        );

        concluirExecucao(missao, consumo, resultado);
    }
}