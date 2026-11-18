package controle;

import enums.TipoTerreno;
import logica.*;
import enums.NivelRisco;
import enums.TipoMissao;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.StatusRobo;

import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;

public class CentralResgate {

    private String nome;
    private ArrayList<Robo> robos;
    private ArrayList<AreaOperacao> areas;
    private Queue<Missao> missoesPendentes;
    private ArrayList<Missao> missoesFinalizadas;
    private Scanner sc;

    public CentralResgate() {
        this("Central de Resgate");
    }

    public CentralResgate(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da central não pode ser vazio.");
        }

        this.nome = nome;
        this.robos = new ArrayList<>();
        this.areas = new ArrayList<>();
        this.missoesPendentes = new LinkedList<>();
        this.missoesFinalizadas = new ArrayList<>();
        this.sc = new Scanner(System.in);
    }

    // CADASTRO DE ROBÔS

    public void cadastrarRobo() {
        System.out.println("\n=== CADASTRO DE ROBÔ ===");
        System.out.println("1 - Drone");
        System.out.println("2 - Robô terrestre");

        int opcao = lerOpcao("Escolha o tipo de robô: ", 1, 2);

        String nomeRobo = lerTexto("Nome do robô: ");
        int bateria = lerInteiro("Nível de bateria (0 a 100): ", 0, 100);

        int id = robos.size() + 1;

        try {
            if (opcao == 1) {
                double altitude = lerDoublePositivo("Altitude máxima (metros): ");
                boolean camera = lerSimNao("O drone possui câmera?");

                Robo novoRobo = new Drone(
                        id, nomeRobo, bateria, altitude, camera, altitude, camera
                );

                robos.add(novoRobo);

            } else {
                double capacidade = lerDoublePositivo(
                        "Capacidade de carga (kg): "
                );

                boolean obstaculos = lerSimNao(
                        "O robô consegue superar obstáculos?"
                );

                Robo novoRobo = new RoboTerrestre(
                        id, nomeRobo, bateria, capacidade, obstaculos
                );

                robos.add(novoRobo);
            }

            System.out.println("Robô cadastrado com sucesso! ID: " + id);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar robô: " + e.getMessage());
        }
    }

    // CADASTRO DE ÁREAS

    public void cadastrarArea() {
        System.out.println("\n=== CADASTRO DE ÁREA DE OPERAÇÃO ===");

        String nomeArea = lerTexto("Nome da área: ");
        String localizacao = lerTexto("Localização: ");

        System.out.println("\nTipo de terreno:");
        System.out.println("1 - Urbano");
        System.out.println("2 - Alagado");
        System.out.println("3 - Irregular");

        int opcaoTerreno = lerOpcao("Escolha o terreno: ", 1, 3);

        TipoTerreno tipoTerreno;

        switch (opcaoTerreno) {
            case 1:
                tipoTerreno = TipoTerreno.URBANO;
                break;
            case 2:
                tipoTerreno = TipoTerreno.ALAGADO;
                break;
            default:
                tipoTerreno = TipoTerreno.IRREGULAR;
                break;
        }

        System.out.println("\nNível de risco:");
        System.out.println("1 - Baixo");
        System.out.println("2 - Médio");
        System.out.println("3 - Alto");

        int opcaoRisco = lerOpcao("Escolha o nível de risco: ", 1, 3);

        NivelRisco nivelRisco;

        switch (opcaoRisco) {
            case 1:
                nivelRisco = NivelRisco.BAIXO;
                break;
            case 2:
                nivelRisco = NivelRisco.MEDIO;
                break;
            default:
                nivelRisco = NivelRisco.ALTO;
                break;
        }

        String descricao = lerTexto("Descrição da área: ");

        int id = areas.size() + 1;

        try {
            AreaOperacao novaArea = new AreaOperacao(
                    id, nomeArea, localizacao,
                    tipoTerreno, nivelRisco, descricao
            );

            areas.add(novaArea);

            System.out.println("Área cadastrada com sucesso! ID: " + id);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar área: " + e.getMessage());
        }
    }

    // ATUALIZAÇÃO DO RISCO DE UMA ÁREA

    public void atualizarNivelRiscoArea() {
        if (areas.isEmpty()) {
            System.out.println("Não existem áreas cadastradas.");
            return;
        }

        listarAreas();

        int id = lerInteiro("Informe o ID da área: ", 1, Integer.MAX_VALUE);

        AreaOperacao area = buscarAreaPorId(id);

        if (area == null) {
            System.out.println("Área não encontrada.");
            return;
        }

        System.out.println("\nNovo nível de risco:");
        System.out.println("1 - Baixo");
        System.out.println("2 - Médio");
        System.out.println("3 - Alto");

        int opcao = lerOpcao("Escolha o novo nível: ", 1, 3);

        NivelRisco novoRisco;

        switch (opcao) {
            case 1:
                novoRisco = NivelRisco.BAIXO;
                break;
            case 2:
                novoRisco = NivelRisco.MEDIO;
                break;
            default:
                novoRisco = NivelRisco.ALTO;
                break;
        }

        area.atualizarNivelRisco(novoRisco);

        System.out.println("Nível de risco atualizado com sucesso.");
    }

    // REGISTRO DE MISSÕES

    public void registrarMissao() {
        if (areas.isEmpty()) {
            System.out.println("Cadastre uma área antes de registrar uma missão.");
            return;
        }

        System.out.println("\n=== REGISTRO DE MISSÃO ===");

        String descricao = lerTexto("Descrição da missão: ");

        System.out.println("\nTipo de missão:");
        System.out.println("1 - Reconhecimento");
        System.out.println("2 - Localização de pessoas");
        System.out.println("3 - Transporte de suprimentos");

        int opcaoTipo = lerOpcao("Escolha o tipo: ", 1, 3);

        TipoMissao tipo;

        switch (opcaoTipo) {
            case 1:
                tipo = TipoMissao.RECONHECIMENTO;
                break;
            case 2:
                tipo = TipoMissao.LOCALIZACAO_PESSOAS;
                break;
            default:
                tipo = TipoMissao.TRANSPORTE_SUPRIMENTOS;
                break;
        }

        System.out.println("\nPrioridade:");
        System.out.println("1 - Baixa");
        System.out.println("2 - Média");
        System.out.println("3 - Alta");

        int opcaoPrioridade = lerOpcao("Escolha a prioridade: ", 1, 3);

        PrioridadeMissao prioridade;

        switch (opcaoPrioridade) {
            case 1:
                prioridade = PrioridadeMissao.BAIXA;
                break;
            case 2:
                prioridade = PrioridadeMissao.MEDIA;
                break;
            default:
                prioridade = PrioridadeMissao.ALTA;
                break;
        }

        System.out.println("\nÁreas disponíveis:");
        listarAreas();

        int idArea = lerInteiro("Informe o ID da área: ", 1, Integer.MAX_VALUE);

        AreaOperacao area = buscarAreaPorId(idArea);

        if (area == null) {
            System.out.println("Área não encontrada. Missão não registrada.");
            return;
        }

        double pesoCarga = 0;

        if (tipo == TipoMissao.TRANSPORTE_SUPRIMENTOS) {
            pesoCarga = lerDoubleNaoNegativo("Peso da carga (kg): ");
        }

        int id = missoesPendentes.size() + missoesFinalizadas.size() + 1;

        try {
            Missao novaMissao = new Missao(
                    id, descricao, tipo, area, prioridade, pesoCarga
            );

            missoesPendentes.add(novaMissao);

            System.out.println("Missão registrada com sucesso! ID: " + id);

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao registrar missão: " + e.getMessage());
        }
    }

    // LISTAGEM DE ROBÔS

    public void listarRobos() {
        if (robos.isEmpty()) {
            System.out.println("Não existem robôs cadastrados.");
            return;
        }

        System.out.println("\n=== ROBÔS CADASTRADOS ===");

        for (Robo robo : robos) {
            robo.exibirResumo();
        }
    }

    public void listarRobosDisponiveis() {
        boolean encontrou = false;

        System.out.println("\n=== ROBÔS DISPONÍVEIS ===");

        for (Robo robo : robos) {
            if (robo.verificarDisponibilidade()) {
                robo.exibirResumo();
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Não existem robôs disponíveis.");
        }
    }

    // ATRIBUIÇÃO DE ROBÔ A UMA MISSÃO

    public void atribuirRoboMissao() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões em aberto.");
            return;
        }

        if (robos.isEmpty()) {
            System.out.println("Não existem robôs cadastrados.");
            return;

        }

        listarMissoesPendentes();

        int idMissao = lerInteiro(
                "Informe o ID da missão: ", 1, Integer.MAX_VALUE
        );

        Missao missao = buscarMissaoPendente(idMissao);

        if (missao == null) {
            System.out.println("Missão não encontrada.");
            return;
        }

        if (missao.getRoboResponsavel() != null) {
            System.out.println("Essa missão já possui um robô atribuído.");
            return;
        }

        listarRobosDisponiveis();

        int idRobo = lerInteiro(
                "Informe o ID do robô: ", 1, Integer.MAX_VALUE
        );

        Robo robo = buscarRoboPorId(idRobo);

        if (robo == null) {
            System.out.println("Robô não encontrado.");
            return;
        }

        if (!robo.verificarDisponibilidade()) {
            System.out.println("O robô não está disponível.");
            return;
        }

		if (robo.consultarBateria() < missao.calculaBateria()) {
 	  	System.out.println("Bateria insuficiente para executar essa missão.");
 	   	return;
		}

        if (!roboCompativel(robo, missao)) {
            System.out.println("Esse robô não é compatível com a missão.");
            return;
        }

        try {
            missao.atribuirRobo(robo);
            System.out.println("Robô atribuído à missão com sucesso.");

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro ao atribuir robô: " + e.getMessage());
        }
    }

    private boolean roboCompativel(Robo robo, Missao missao) {

        if (missao.getTipo() == TipoMissao.RECONHECIMENTO) {
            return robo instanceof Drone;
        }

        if (missao.getTipo() == TipoMissao.LOCALIZACAO_PESSOAS) {
            return robo instanceof Drone
                    && ((Drone) robo).isPossuiCamera();
        }

        if (missao.getTipo() == TipoMissao.TRANSPORTE_SUPRIMENTOS) {
            if (!(robo instanceof RoboTerrestre)) {
                return false;
            }

            RoboTerrestre terrestre = (RoboTerrestre) robo;

            if (missao.getPesoCargaKg() > terrestre.getCapacidadeCargaKg()) {
                return false;
            }

            TipoTerreno terreno = missao.getArea().getTipoTerreno();

            if (terreno == TipoTerreno.ALAGADO) {
                return false;
            }

            if (terreno == TipoTerreno.IRREGULAR
                    && !terrestre.isSuperaObstaculos()) {
                return false;
            }

            return true;
        }

        return false;
    }

    // INÍCIO DE MISSÃO

    public void iniciarMissao() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões em aberto.");
            return;
        }

        listarMissoesPendentes();

        int id = lerInteiro("Informe o ID da missão: ", 1, Integer.MAX_VALUE);

        Missao missao = buscarMissaoPendente(id);

        if (missao == null) {
            System.out.println("Missão não encontrada.");
            return;
        }

        try {
            missao.iniciar();
            System.out.println("Missão iniciada com sucesso.");

        } catch (IllegalStateException e) {
            System.out.println("Não foi possível iniciar a missão: " + e.getMessage());
        }
    }

    // CONCLUSÃO DE MISSÃO

    public void concluirMissao() {
    if (missoesPendentes.isEmpty()) {
        System.out.println("Não existem missões em aberto.");
        return;
    }

    listarMissoesPendentes();

    int id = lerInteiro("Informe o ID da missão: ", 1, Integer.MAX_VALUE);

    Missao missao = buscarMissaoPendente(id);

    if (missao == null) {
        System.out.println("Missão não encontrada.");
        return;
    }

    if (missao.getStatus() != StatusMissao.EM_ANDAMENTO) {
        System.out.println("A missão precisa estar em andamento para ser concluída.");
        return;
    }

    try {
        missao.concluir();

        missoesPendentes.remove(missao);
        missoesFinalizadas.add(missao);

        System.out.println("Missão concluída com sucesso.");
        System.out.println("Resultado: " + missao.getResultado());

    } catch (RuntimeException e) {
        System.out.println("Erro ao concluir missão: " + e.getMessage());
    }
}

    // CANCELAMENTO DE MISSÃO

    public void cancelarMissao() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões em aberto.");
            return;
        }

        listarMissoesPendentes();

        int id = lerInteiro("Informe o ID da missão: ", 1, Integer.MAX_VALUE);

        Missao missao = buscarMissaoPendente(id);

        if (missao == null) {
            System.out.println("Missão não encontrada.");
            return;
        }

        try {
            missao.cancelar();

            missoesPendentes.remove(missao);
            missoesFinalizadas.add(missao);

            System.out.println(missao.getResultado());

        } catch (IllegalStateException e) {
            System.out.println("Não foi possível cancelar a missão: " + e.getMessage());
        }
    }

    // LISTAGEM DE MISSÕES

    public void listarMissoesPendentes() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões em aberto.");
            return;
        }

        System.out.println("\n=== MISSÕES EM ABERTO ===");

        for (Missao missao : missoesPendentes) {
            System.out.println(missao.exibirResumo());
            System.out.println("--------------------------------");
        }
    }

    public void listarMissoesFinalizadas() {
        if (missoesFinalizadas.isEmpty()) {
            System.out.println("Não existem missões finalizadas.");
            return;
        }

        System.out.println("\n=== MISSÕES FINALIZADAS ===");

        for (Missao missao : missoesFinalizadas) {
            System.out.println(missao.exibirResumo());
            System.out.println("--------------------------------");
        }
    }

    // LISTAGEM DE ÁREAS

    public void listarAreas() {
        if (areas.isEmpty()) {
            System.out.println("Não existem áreas cadastradas.");
            return;
        }

        for (AreaOperacao area : areas) {
            System.out.println(area.exibirInformacoes());
            System.out.println("--------------------------------");
        }
    }

    // GERAÇÃO DE RELATÓRIO

    public void gerarRelatorioGeral() {
        int totalMissoes = missoesPendentes.size()
                + missoesFinalizadas.size();

        int missoesConcluidas = 0;
        int missoesAguardandoInicio = 0;

        for (Missao missao : missoesFinalizadas) {
            if (missao.getStatus() == StatusMissao.CONCLUIDA) {
                missoesConcluidas++;
            }
        }

        for (Missao missao : missoesPendentes) {
            if (missao.getStatus() == StatusMissao.PENDENTE) {
                missoesAguardandoInicio++;
            }
        }

        Relatorio relatorio = new Relatorio(
                missoesConcluidas,
                missoesAguardandoInicio,
                robos.size(),
                totalMissoes
        );

        System.out.println(relatorio.gerarResumo());

        if (lerSimNao("Deseja salvar o relatório em arquivo?")) {
            relatorio.salvarEmArquivo();
        }
    }

    // MÉTODOS AUXILIARES DE BUSCA

    private Robo buscarRoboPorId(int id) {
        for (Robo robo : robos) {
            if (robo.getId() == id) {
                return robo;
            }
        }

        return null;
    }

    private AreaOperacao buscarAreaPorId(int id) {
        for (AreaOperacao area : areas) {
            if (area.getId() == id) {
                return area;
            }
        }

        return null;
    }

    private Missao buscarMissaoPendente(int id) {
        for (Missao missao : missoesPendentes) {
            if (missao.getId() == id) {
                return missao;
            }
        }

        return null;
    }

    // MÉTODOS AUXILIARES DE ENTRADA

    private String lerTexto(String mensagem) {
        String texto;

        do {
            System.out.print(mensagem);
            texto = sc.nextLine();

            if (texto.isBlank()) {
                System.out.println("O campo não pode ficar vazio.");
            }

        } while (texto.isBlank());

        return texto;
    }

    private int lerInteiro(String mensagem, int minimo, int maximo) {
        while (true) {
            try {
                System.out.print(mensagem);
                int valor = Integer.parseInt(sc.nextLine());

                if (valor < minimo || valor > maximo) {
                    System.out.println("Informe um valor entre "
                            + minimo + " e " + maximo + ".");
                    continue;
                }

                return valor;

            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private int lerOpcao(String mensagem, int minimo, int maximo) {
        return lerInteiro(mensagem, minimo, maximo);
    }

    private double lerDoublePositivo(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                double valor = Double.parseDouble(sc.nextLine());

                if (!Double.isFinite(valor) || valor <= 0) {
                    System.out.println("Informe um valor maior que zero.");
                    continue;
                }

                return valor;

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private double lerDoubleNaoNegativo(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                double valor = Double.parseDouble(sc.nextLine());

                if (!Double.isFinite(valor) || valor < 0) {
                    System.out.println("Informe um valor igual ou maior que zero.");
                    continue;
                }

                return valor;

            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private boolean lerSimNao(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (S/N): ");
            String resposta = sc.nextLine();

            if (resposta.equalsIgnoreCase("S")) {
                return true;
            }

            if (resposta.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println("Digite S ou N.");
        }
    }
}