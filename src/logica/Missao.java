package logica;

import enums.TipoMissao;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.StatusRobo;
import java.io.Serializable;

public class Missao implements Serializable {

    private static final long serialVersionUID = 1L;

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
            throw new IllegalArgumentException("A descricao nao pode ser vazia.");
        }

        if (tipo == null || area == null || prioridade == null) {
            throw new IllegalArgumentException("Dados da missao invalidos.");
        }

        if (!Double.isFinite(pesoCargaKg) || pesoCargaKg < 0) {
            throw new IllegalArgumentException("Peso de carga invalido.");
        }

        if (tipo != TipoMissao.TRANSPORTE_SUPRIMENTOS && pesoCargaKg != 0) {
            throw new IllegalArgumentException(
                    "Apenas missoes de transporte podem possuir peso de carga."
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
        switch (tipo) {
            case RECONHECIMENTO:
                return 15;
            case LOCALIZACAO_PESSOAS:
                return 25;
            case TRANSPORTE_SUPRIMENTOS:
                return 40;
            default:
                throw new IllegalStateException("Tipo de missao invalido.");
        }
    }

    public void atribuirRobo(Robo robo) {

        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("A missao nao esta pendente.");
        }

        if (robo == null) {
            throw new IllegalArgumentException("O robo nao pode ser nulo.");
        }

        if (roboResponsavel != null) {
            throw new IllegalStateException("A missao ja possui um robo.");
        }

        this.roboResponsavel = robo;
        roboResponsavel.setStatus(StatusRobo.EM_MISSAO);
    }

    public void iniciar() {

        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException("A missao nao esta pendente.");
        }

        if (roboResponsavel == null) {
            throw new IllegalStateException("Nenhum robo foi atribuido.");
        }

        this.status = StatusMissao.EM_ANDAMENTO;
    }

    public void registrarResultado(String resultado) {

        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missao nao esta em andamento.");
        }

        if (resultado == null || resultado.isBlank()) {
            throw new IllegalArgumentException("O resultado nao pode ser vazio.");
        }

        this.resultado = resultado;
    }

    public void concluir() {

        if (status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("A missao nao esta em andamento.");
        }

        if (roboResponsavel == null) {
            throw new IllegalStateException("Nenhum robo foi atribuido.");
        }

        roboResponsavel.executarMissao(this);

        if (resultado == null) {
            throw new IllegalStateException(
                    "A execucao nao registrou um resultado."
            );
        }

        this.status = StatusMissao.CONCLUIDA;

        roboResponsavel.setStatus(
                roboResponsavel.consultarBateria() == 0
                        ? StatusRobo.SEM_BATERIA
                        : StatusRobo.DISPONIVEL
        );
    }

    public void cancelar() {

        if (status != StatusMissao.PENDENTE) {
            throw new IllegalStateException(
                    "Apenas missoes pendentes podem ser canceladas."
            );
        }

        if (roboResponsavel != null) {
            roboResponsavel.setStatus(
                    roboResponsavel.consultarBateria() == 0
                            ? StatusRobo.SEM_BATERIA
                            : StatusRobo.DISPONIVEL
            );
        }

        this.resultado = "Missao " + id + " - " + descricao + " foi cancelada.";
        this.status = StatusMissao.CANCELADA;
    }

    public String exibirResumo() {

        return "Missao " + id +
                "\nDescricao: " + descricao +
                "\nTipo: " + tipo +
                "\nArea: " + area.exibirInformacoes() +
                "\nPrioridade: " + prioridade +
                "\nStatus: " + status +
                "\nRobo responsavel: " +
                (roboResponsavel != null
                        ? roboResponsavel.getId() + " - " + roboResponsavel.getNome()
                        : "Nenhum") +
                (tipo == TipoMissao.TRANSPORTE_SUPRIMENTOS
                        ? "\nPeso da carga: " + pesoCargaKg + " kg"
                        : "") +
                "\nResultado: " +
                (resultado != null ? resultado : "Nao disponivel");
    }
}