package controle;

import logica.AreaOperacao;
import logica.Missao;
import logica.Robo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

final class DadosResgate implements Serializable {
    private static final long serialVersionUID = 1L;

    final LinkedHashMap<Integer, Robo> robos = new LinkedHashMap<>();
    final LinkedHashMap<Integer, AreaOperacao> areas = new LinkedHashMap<>();
    final ArrayList<Missao> missoes = new ArrayList<>();
}
