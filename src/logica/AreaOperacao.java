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

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser positivo.");
        }

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome nao pode ser vazio.");
        }

        if (localizacao == null || localizacao.isBlank()) {
            throw new IllegalArgumentException("A localizacao nao pode ser vazia.");
        }

        if (tipoTerreno == null) {
            throw new IllegalArgumentException("O tipo de terreno nao pode ser nulo.");
        }

        if (nivelRisco == null) {
            throw new IllegalArgumentException("O nivel de risco nao pode ser nulo.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descricao nao pode ser vazia.");
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

    public String exibirInformacoes() {
        return nome +
                "\nID: " + id +
                "\nLocalizacao: " + localizacao +
                "\nTipo de terreno: " + tipoTerreno +
                "\nNivel de risco: " + nivelRisco +
                "\nDescricao: " + descricao;
    }
}