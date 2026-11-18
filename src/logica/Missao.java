package logica;

import enums.TipoMissao;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.StatusRobo;

public class Missao {

    private int id;
    private String descricao;
    private TipoMissao tipo;
    private AreaOperacao area;
    private PrioridadeMissao prioridade;
    private StatusMissao status;
    private Robo roboResponsavel;
    private String resultado;
    private double pesoCargaKg;

    public Missao(int id, String descricao, TipoMissao tipo,
                  AreaOperacao area, PrioridadeMissao prioridade,
                  double pesoCargaKg) {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição não pode ser vazia.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("O tipo da missão não pode ser nulo.");
        }

        if (area == null) {
            throw new IllegalArgumentException("A área de operação não pode ser nula.");
        }

        if (prioridade == null) {
            throw new IllegalArgumentException("A prioridade não pode ser nula.");
        }

        if (!Double.isFinite(pesoCargaKg) || pesoCargaKg < 0) {
            throw new IllegalArgumentException("O peso da carga não pode ser negativo ou inválido.");
        }

        if (tipo != TipoMissao.TRANSPORTE_SUPRIMENTOS && pesoCargaKg != 0) {
            throw new IllegalArgumentException(
                    "Apenas missões de transporte podem possuir peso de carga."
            );
        }

        this.id = id;
        this.descricao = descricao;
        this.tipo = tipo;
        this.area = area;
        this.prioridade = prioridade;
        this.pesoCargaKg = pesoCargaKg;

        this.status = StatusMissao.PENDENTE;
        this.roboResponsavel = null;
        this.resultado = null;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public AreaOperacao getArea() {
        return area;
    }

    public PrioridadeMissao getPrioridade() {
        return prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public Robo getRoboResponsavel() {
        return roboResponsavel;
    }

    public String getResultado() {
        return resultado;
    }

    public double getPesoCargaKg() {
        return pesoCargaKg;
    }

    public int calculaBateria() {
        return switch (tipo) {
            case RECONHECIMENTO -> 15;
            case LOCALIZACAO_PESSOAS -> 25;
            case TRANSPORTE_SUPRIMENTOS -> 40;
        };
    }

    public void atribuirRobo(Robo robo) {
        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("A missão não está pendente.");
        }

        if (robo == null) {
            throw new IllegalArgumentException("O robô não pode ser nulo.");
        }

        if (roboResponsavel != null) {
            throw new IllegalStateException("A missão já possui um robô responsável.");
        }

        if (!robo.verificarDisponibilidade()) {
            throw new IllegalStateException("O robô não está disponível.");
        }

        if (!robo.podeExecutar(this)) {
            throw new IllegalArgumentException("Esse robô não é compatível com a missão.");
        }

        robo.verificarBateriaPara(this);
        this.roboResponsavel = robo;
        roboResponsavel.setStatus(StatusRobo.EM_MISSAO);
    }

    public void iniciar() {
        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("A missão não está pendente.");
        }

        if (roboResponsavel == null) {
            throw new IllegalStateException("Nenhum robô foi atribuído à missão.");
        }

        this.status = StatusMissao.EM_ANDAMENTO;
    }

    public void registrarResultado(String resultado) {
        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missão não está em andamento.");
        }

        if (resultado == null || resultado.isBlank()) {
            throw new IllegalArgumentException("O resultado não pode ser vazio.");
        }

        this.resultado = resultado;
    }

    public void concluir() {
        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missão não está em andamento.");
        }

        if (roboResponsavel == null) {
            throw new IllegalStateException("Nenhum robô foi atribuído à missão.");
        }

        int consumo = roboResponsavel.verificarBateriaPara(this);
        roboResponsavel.executarMissao(this);

        if (resultado == null) {
            throw new IllegalStateException("A execução não registrou um resultado.");
        }

        roboResponsavel.registrarExecucao(consumo);
        this.status = StatusMissao.CONCLUIDA;
        liberarRobo();
    }


    public void cancelar() {
        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("Apenas missões pendentes podem ser canceladas.");
        }

        if (roboResponsavel != null) {
            liberarRobo();
        }

        this.resultado = "Missão " + id + " - " + descricao + " foi cancelada.";
        this.status = StatusMissao.CANCELADA;
    }


    private void liberarRobo() {
        StatusRobo statusRobo = roboResponsavel.consultarBateria() == 0
                ? StatusRobo.SEM_BATERIA
                : StatusRobo.DISPONIVEL;
        roboResponsavel.setStatus(statusRobo);
    }

    public String exibirResumo() {
        return "Missão " + id +
                "\nDescrição: " + descricao +
                "\nTipo: " + tipo +
                "\nÁrea: " + area.exibirInformacoes() +
                "\nPrioridade: " + prioridade +
                "\nStatus: " + status +
                "\nRobô responsável: " +
                (roboResponsavel != null
                        ? roboResponsavel.getId() + " - " + roboResponsavel.getNome()
                        : "Nenhum") +
                (tipo == TipoMissao.TRANSPORTE_SUPRIMENTOS
                        ? "\nPeso da carga: " + pesoCargaKg + " kg"
                        : "") +
                "\nResultado: " + (resultado != null ? resultado : "Não disponível");
    }
}
