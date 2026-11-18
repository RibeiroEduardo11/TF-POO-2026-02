package logica;

import enums.TipoTerreno;
import enums.NivelRisco;
import java.io.Serializable;

public class AreaOperacao implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String localizacao;
    private TipoTerreno tipoTerreno;
    private NivelRisco nivelRisco;
    private String descricao;

    public AreaOperacao(int id, String nome, String localizacao,
                        TipoTerreno tipoTerreno, NivelRisco nivelRisco,
                        String descricao) {

        if (id <= 0 || nome == null || nome.isBlank()
                || localizacao == null || localizacao.isBlank()
                || tipoTerreno == null || nivelRisco == null
                || descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Dados da area invalidos.");
        }

        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.tipoTerreno = tipoTerreno;
        this.nivelRisco = nivelRisco;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void atualizarNivelRisco(NivelRisco novoRisco) {
        if (novoRisco == null) {
            throw new IllegalArgumentException("O novo nivel de risco nao pode ser nulo.");
        }

        this.nivelRisco = novoRisco;
    }

    public TipoTerreno getTipoTerreno() {
        return tipoTerreno;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public String exibirInformacoes() {
        return nome +
                "\nID: " + id +
                "\nLocalizacao: " + localizacao +
                "\nTipo de terreno: " + tipoTerreno +
                "\nNivel de risco: " + nivelRisco +
                "\nDescricao: " + descricao;
    }
}