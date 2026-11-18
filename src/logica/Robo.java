package logica;

import enums.StatusMissao;
import enums.StatusRobo;
import interfaces.Comunicavel;

import java.io.Serializable;
import java.util.Objects;

public abstract class Robo implements Comunicavel, Serializable {
    private static final long serialVersionUID = 1L;

	private final int id;
	private String nome;
	private int nivelBateria;
	private StatusRobo status;
	private int missoesRealizadas;

	protected Robo(int id, String nome, int nivelBateria) {
        if (id <= 0) {
            throw new IllegalArgumentException("O id deve ser positivo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome nao pode ser vazio.");
        }
        if (nivelBateria < 0 || nivelBateria > 100) {
            throw new IllegalArgumentException("A bateria deve estar entre 0 e 100.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.nivelBateria = nivelBateria;
        this.status = nivelBateria == 0 ? StatusRobo.SEM_BATERIA : StatusRobo.DISPONIVEL;
        this.missoesRealizadas = 0;
    }

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome nao pode ser vazio.");
        }
        this.nome = nome.trim();
    }

	public StatusRobo getStatus() {
		return status;
	}

	void setStatus(StatusRobo status) {
		this.status = Objects.requireNonNull(status, "O status do robô é obrigatório.");
	}

	public int getMissoesRealizadas() {
		return missoesRealizadas;
	}

	public void setBateria(int bat) {
		if (bat < 0 || bat > 100) {
			throw new IllegalArgumentException("Valor Inválido");
		} else {
			this.nivelBateria = bat;
		}
		if (bat == 0) {
			status = StatusRobo.SEM_BATERIA;
		}
	}

	public boolean verificarDisponibilidade() {
		if (status == StatusRobo.DISPONIVEL) {
			return true;
		}
		return false;
	}

	public int consultarBateria() {
		return nivelBateria;
	}

	public void recarregar() {
		if (status == StatusRobo.EM_MISSAO) {
			throw new IllegalStateException("Operação não permitida");
		}
		setBateria(100);
		if (status == StatusRobo.SEM_BATERIA) {
			status = StatusRobo.DISPONIVEL;
		}
	}

	public void exibirResumo() {
		System.out.println(toString());
	}
	public abstract int calcularConsumoBateria(Missao missao);
	public abstract void executarMissao(Missao missao);

	public abstract boolean podeExecutar(Missao missao);

    protected void validarExecucao(Missao missao) {
        if (missao == null || missao.getRoboResponsavel() != this
                || missao.getStatus() != StatusMissao.EM_ANDAMENTO
                || status != StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("O robô precisa estar atribuído a uma missão em andamento.");
        }
    }

    int verificarBateriaPara(Missao missao) {
        int consumo = calcularConsumoBateria(missao);
        if (consultarBateria() < consumo) {
            throw new IllegalStateException("Bateria insuficiente para executar essa missão.");
        }
        return consumo;
    }

    void registrarExecucao(int consumo) {
        setBateria(consultarBateria() - consumo);
        missoesRealizadas++;
    }

	@Override
	public void enviarMensagem(String mensagem) {
		System.out.println("Robo " + nome + " enviou: " + mensagem);
	}

	@Override
	public void receberMensagem(String mensagem) {
		System.out.println("Robo " + nome + " recebeu: " + mensagem);
	}

	public String toString() {
		return "ID: " + id +
				"\n Nome: " + nome +
				"\n Nível Bateria: " + nivelBateria +
				"\n Status: " + status +
				"\n missoesRealizadas: " + missoesRealizadas;
	}
}
