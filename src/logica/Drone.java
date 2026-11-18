package logica;

import enums.StatusRobo;
import interfaces.Comunicavel;

public class Drone extends Robo implements Comunicavel {

	private double altitudeMax;
	private double alcanceVoo;
	private boolean captarImagem;
	private String tipoTerreno;

	 public Drone(int id, String nome, int nivelBateria,
                 double altitudeMax, boolean captarImagem) {
        super(id, nome, nivelBateria);
        if (altitudeMax <= 0) {
            throw new IllegalArgumentException("A altitude maxima deve ser positiva.");
        }
        this.altitudeMax = altitudeMax;
        this.captarImagem = captarImagem;
    }

	public double getAltitudeMaximaMetros() { return altitudeMax; }
    public boolean isPossuiCamera() { return captarImagem; }

	public boolean podeExecutar(Missao missao) {
        if (missao == null) return false;
        return missao.getTipo() == TipoMissao.RECONHECIMENTO
                || (missao.getTipo() == TipoMissao.LOCALIZACAO_PESSOAS && possuiCamera);
    }

	public int calcularConsumoBateria(Missao missao) {
        if (!podeExecutar(missao)) {
            throw new IllegalArgumentException("Missao incompativel com drone.");
        }
        return missao.getTipo() == TipoMissao.RECONHECIMENTO ? 20 : 30;
    }

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

	public void exibirResumo() {
		System.out.println(toString());
	}

	@Override
	public void enviarMensagem(String mensagem) {
		System.out.println("Robo " + getNome() + " enviou: " + mensagem);
	}

	@Override
	public void receberMensagem(String mensagem) {
		System.out.println("Robo " + getNome() + " recebeu: " + mensagem);
	}
}
