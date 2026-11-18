package logica;

import enums.StatusMissao;
import enums.StatusRobo;
import interfaces.Comunicavel;
import java.io.Serializable;

public abstract class Robo implements Comunicavel, Serializable {

    private static final long serialVersionUID = 1L;

    public final int id;
    private String nome;
    public int nivelBateria;
    public StatusRobo status;
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
        this.status = nivelBateria == 0
                ? StatusRobo.SEM_BATERIA
                : StatusRobo.DISPONIVEL;
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

    public void setStatus(StatusRobo status) {
        this.status = status;
    }

    public int getMissoesRealizadas() {
        return missoesRealizadas;
    }

    public void setBateria(int bat) {
        if (bat < 0 || bat > 100) {
            throw new IllegalArgumentException("Valor invalido.");
        }

        this.nivelBateria = bat;

        if (bat == 0) {
            status = StatusRobo.SEM_BATERIA;
        }
    }

    public boolean verificarDisponibilidade() {
        return status == StatusRobo.DISPONIVEL;
    }

    public int consultarBateria() {
        return nivelBateria;
    }

    public void recarregar() {
        if (status == StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("Operacao nao permitida.");
        }

        setBateria(100);
        status = StatusRobo.DISPONIVEL;
    }

    public void exibirResumo() {
        System.out.println(toString());
    }

    public abstract int calcularConsumoBateria(Missao missao);

    public abstract void executarMissao(Missao missao);

    public abstract boolean podeExecutar(Missao missao);

    protected int iniciarExecucao(Missao missao) {

        if (missao == null) {
            throw new IllegalArgumentException("Missao invalida.");
        }

        if (missao.getStatus() != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missao nao esta em andamento.");
        }

        if (missao.getRoboResponsavel() != this) {
            throw new IllegalStateException("Este robo nao foi atribuido a missao.");
        }

        if (status != StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("O robo nao esta atribuido.");
        }

        if (!podeExecutar(missao)) {
            throw new IllegalArgumentException("Robo incompativel.");
        }

        int consumo = calcularConsumoBateria(missao);

        if (nivelBateria < consumo) {
            throw new IllegalStateException("Bateria insuficiente.");
        }

        return consumo;
    }

    protected void concluirExecucao(Missao missao, int consumo, String resultado) {

        if (missao == null) {
            throw new IllegalArgumentException("Missao invalida.");
        }

        if (resultado == null || resultado.isBlank()) {
            throw new IllegalArgumentException("Resultado invalido.");
        }

        if (consumo < 0 || consumo > nivelBateria) {
            throw new IllegalArgumentException("Consumo de bateria invalido.");
        }

        missao.registrarResultado(resultado);

        setBateria(nivelBateria - consumo);
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

    @Override
    public String toString() {
        return "ID: " + id +
                "\nNome: " + nome +
                "\nNivel Bateria: " + nivelBateria +
                "\nStatus: " + status +
                "\nMissoes realizadas: " + missoesRealizadas;
    }
}