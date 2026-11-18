package controle;

import enums.NivelRisco;
import enums.PrioridadeMissao;
import enums.StatusMissao;
import enums.TipoMissao;
import enums.TipoTerreno;
import interfaces.Comunicavel;
import logica.AreaOperacao;
import logica.Drone;
import logica.Missao;
import logica.Relatorio;
import logica.Robo;
import logica.RoboTerrestre;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;

public class CentralResgate {

    private final String nome;
    private final DadosResgate dados;
    private final Map<Integer, Robo> robos;
    private final Map<Integer, AreaOperacao> areas;
    private final List<Missao> missoes;
    private final Queue<Missao> missoesPendentes = new ArrayDeque<>();
    private final Scanner sc;
    private final Path arquivo;
    private boolean dadosAlterados;

    public CentralResgate() {
        this("Central de Resgate");
    }

    public CentralResgate(String nome) {
        this(nome, new Scanner(System.in));
    }

    public CentralResgate(String nome, Scanner sc) {
        this(nome, sc, new DadosResgate(), null);
    }

    public CentralResgate(String nome, Scanner sc, Path arquivo) throws IOException {
        this(nome, sc, PersistenciaResgate.carregar(arquivo), arquivo.toAbsolutePath().normalize());
    }

    private CentralResgate(String nome, Scanner sc, DadosResgate dados, Path arquivo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da central não pode ser vazio.");
        }

        this.nome = nome;
        this.sc = Objects.requireNonNull(sc, "A entrada da central é obrigatória.");
        this.arquivo = arquivo;
        this.dados = dados;
        this.robos = dados.robos;
        this.areas = dados.areas;
        this.missoes = dados.missoes;
        missoes.stream()
                .filter(missao -> missao.getStatus() == StatusMissao.PENDENTE)
                .forEach(missoesPendentes::add);
    }

    public void salvarDadosPendentes() {
        if (arquivo == null || !dadosAlterados) {
            return;
        }

        try {
            PersistenciaResgate.salvar(arquivo, dados);
            dadosAlterados = false;
        } catch (IOException e) {
            System.out.println("Erro ao salvar os dados. As alterações seguem em memória: " + e.getMessage());
        }
    }

    private void registrarAlteracao() {
        dadosAlterados = true;
        salvarDadosPendentes();
    }

    public String getNome() {
        return nome;
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
                        id, nomeRobo, bateria, altitude, camera
                );

                robos.put(id, novoRobo);

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

                robos.put(id, novoRobo);
            }

            registrarAlteracao();
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

            areas.put(id, novaArea);
            registrarAlteracao();

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
        registrarAlteracao();

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

        int id = missoes.size() + 1;

        try {
            Missao novaMissao = new Missao(
                    id, descricao, tipo, area, prioridade, pesoCarga
            );

            missoes.add(novaMissao);
            missoesPendentes.add(novaMissao);
            registrarAlteracao();

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

        for (Robo robo : robos.values()) {
            robo.exibirResumo();
        }
    }

    public void listarRobosDisponiveis() {
        boolean encontrou = false;

        System.out.println("\n=== ROBÔS DISPONÍVEIS ===");

        for (Robo robo : robos.values()) {
            if (robo.verificarDisponibilidade()) {
                robo.exibirResumo();
                encontrou = true;
            }
        }

        if (!encontrou) {
            System.out.println("Não existem robôs disponíveis.");
        }
    }

    public void recarregarRobo() {
        if (robos.isEmpty()) {
            System.out.println("Não existem robôs cadastrados.");
            return;
        }

        listarRobos();
        int id = lerInteiro("Informe o ID do robô: ", 1, Integer.MAX_VALUE);
        Robo robo = buscarRoboPorId(id);

        if (robo == null) {
            System.out.println("Robô não encontrado.");
            return;
        }

        try {
            robo.recarregar();
            registrarAlteracao();
            System.out.println("Robô recarregado com sucesso.");
        } catch (IllegalStateException e) {
            System.out.println("Não foi possível recarregar o robô: " + e.getMessage());
        }
    }

    // ATRIBUIÇÃO DE ROBÔ A UMA MISSÃO

    public void atribuirRoboMissao() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões pendentes.");
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

        atribuirRobo(missao, robo);
    }

    private void atribuirRobo(Missao missao, Robo robo) {
        try {
            missao.atribuirRobo(robo);
            registrarAlteracao();
            System.out.println("Robô atribuído à missão com sucesso.");

            Comunicavel canal = robo;
            canal.receberMensagem("Central: prepare-se para a missão " + missao.getId() + ".");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro ao atribuir robô: " + e.getMessage());
        }
    }

    public void sugerirDespacho() {
        Comparator<Missao> porUrgencia = Comparator.comparing(Missao::getPrioridade)
                .thenComparing(missao -> missao.getArea().getNivelRisco())
                .reversed();

        List<Missao> candidatas = missoesPendentes.stream()
                .filter(missao -> missao.getRoboResponsavel() == null)
                .sorted(porUrgencia)
                .toList();

        if (candidatas.isEmpty()) {
            System.out.println("Não existem missões pendentes sem robô atribuído.");
            return;
        }

        for (Missao missao : candidatas) {
            Optional<Robo> robo = selecionarRobo(missao);
            if (robo.isPresent()) {
                exibirSugestao(missao, robo.get());
                return;
            }

            System.out.println("Missão " + missao.getId()
                    + " permanece pendente: nenhum robô apto no momento.");
        }
    }

    private Optional<Robo> selecionarRobo(Missao missao) {
        return robos.values().stream()
                .filter(Robo::verificarDisponibilidade)
                .filter(robo -> robo.podeExecutar(missao))
                .filter(robo -> robo.consultarBateria() >= robo.calcularConsumoBateria(missao))
                .min(Comparator.comparingInt((Robo robo) ->
                        robo.consultarBateria() - robo.calcularConsumoBateria(missao))
                        .thenComparingInt(Robo::getId));
    }

    private void exibirSugestao(Missao missao, Robo robo) {
        int consumo = robo.calcularConsumoBateria(missao);
        System.out.println("\n=== DESPACHO ASSISTIDO ===");
        System.out.printf("Missão %d - %s | Prioridade: %s | Risco: %s%n",
                missao.getId(), missao.getDescricao(), missao.getPrioridade(),
                missao.getArea().getNivelRisco());
        System.out.printf("Robô %d - %s | Bateria: %d%% | Consumo: %d p.p. | Restante: %d%%%n",
                robo.getId(), robo.getNome(), robo.consultarBateria(), consumo,
                robo.consultarBateria() - consumo);
        System.out.println("Critérios: prioridade, risco e ordem da fila.");
        System.out.println("Entre os robôs aptos, é usada a menor sobra de bateria.");
        if (lerSimNao("Confirma a atribuição sugerida?")) {
            atribuirRobo(missao, robo);
        } else {
            System.out.println("Sugestão recusada. A missão permanece pendente.");
        }
    }

    // INÍCIO, CONCLUSÃO E CANCELAMENTO

    public void iniciarMissao() {
        Missao missao = selecionarMissaoEmAberto();
        if (missao == null) {
            return;
        }

        try {
            missao.iniciar();
            missoesPendentes.remove(missao);
            registrarAlteracao();
            System.out.println("Missão iniciada com sucesso.");
            Comunicavel canal = missao.getRoboResponsavel();
            canal.enviarMensagem("Central, iniciei a missão " + missao.getId() + ".");
        } catch (IllegalStateException e) {
            System.out.println("Não foi possível iniciar a missão: " + e.getMessage());
        }
    }

    public void concluirMissao() {
        Missao missao = selecionarMissaoEmAberto();
        if (missao == null) {
            return;
        }

        try {
            missao.concluir();
            registrarAlteracao();
            System.out.println("Missão concluída com sucesso.");
            System.out.println("Resultado: " + missao.getResultado());
            Comunicavel canal = missao.getRoboResponsavel();
            canal.enviarMensagem(missao.getResultado());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Erro ao concluir missão: " + e.getMessage());
        }
    }

    public void cancelarMissao() {
        Missao missao = selecionarMissaoEmAberto();
        if (missao == null) {
            return;
        }

        try {
            missao.cancelar();
            missoesPendentes.remove(missao);
            registrarAlteracao();
            System.out.println(missao.getResultado());
            Comunicavel canal = missao.getRoboResponsavel();
            if (canal != null) {
                canal.receberMensagem("Central: missão " + missao.getId() + " cancelada.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Não foi possível cancelar a missão: " + e.getMessage());
        }
    }

    private Missao selecionarMissaoEmAberto() {
        List<Missao> abertas = missoes.stream().filter(missao -> !estaFinalizada(missao)).toList();
        if (abertas.isEmpty()) {
            System.out.println("Não existem missões em aberto.");
            return null;
        }

        exibirMissoes("MISSÕES EM ABERTO", abertas);
        int id = lerInteiro("Informe o ID da missão: ", 1, Integer.MAX_VALUE);
        Missao missao = abertas.stream().filter(item -> item.getId() == id).findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missão em aberto não encontrada.");
        }
        return missao;
    }

    // LISTAGEM DE MISSÕES

    public void listarMissoesPendentes() {
        if (missoesPendentes.isEmpty()) {
            System.out.println("Não existem missões pendentes.");
            return;
        }
        exibirMissoes("FILA DE MISSÕES PENDENTES", missoesPendentes);
    }

    public void listarMissoesEmAndamento() {
        List<Missao> emAndamento = missoes.stream()
                .filter(missao -> missao.getStatus() == StatusMissao.EM_ANDAMENTO)
                .toList();
        if (emAndamento.isEmpty()) {
            System.out.println("Não existem missões em andamento.");
            return;
        }
        exibirMissoes("MISSÕES EM ANDAMENTO", emAndamento);
    }

    public void listarMissoesFinalizadas() {
        List<Missao> finalizadas = missoes.stream().filter(this::estaFinalizada).toList();
        if (finalizadas.isEmpty()) {
            System.out.println("Não existem missões finalizadas.");
            return;
        }
        exibirMissoes("HISTÓRICO DE MISSÕES", finalizadas);
    }

    private boolean estaFinalizada(Missao missao) {
        return missao.getStatus() == StatusMissao.CONCLUIDA
                || missao.getStatus() == StatusMissao.CANCELADA;
    }

    private void exibirMissoes(String titulo, Iterable<Missao> selecao) {
        System.out.println("\n=== " + titulo + " ===");

        for (Missao missao : selecao) {
            System.out.println(missao.exibirResumo());
            System.out.println("--------------------------------");
        }
    }

    public void listarAreas() {
        if (areas.isEmpty()) {
            System.out.println("Não existem áreas cadastradas.");
            return;
        }

        for (AreaOperacao area : areas.values()) {
            System.out.println(area.exibirInformacoes());
            System.out.println("--------------------------------");
        }
    }

    public void gerarRelatorioGeral() {
        Relatorio relatorio = new Relatorio(robos.values(), missoes);
        System.out.println(relatorio.gerarResumo());

        if (lerSimNao("Deseja salvar o relatório em arquivo?")) {
            relatorio.salvarEmArquivo();
        }
    }

    private Robo buscarRoboPorId(int id) {
        return robos.get(id);
    }

    private AreaOperacao buscarAreaPorId(int id) {
        return areas.get(id);
    }

    private Missao buscarMissaoPendente(int id) {
        return missoesPendentes.stream()
                .filter(missao -> missao.getId() == id)
                .findFirst()
                .orElse(null);
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
                int valor = Integer.parseInt(sc.nextLine().trim());

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
                double valor = Double.parseDouble(sc.nextLine().replace(',', '.'));

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
                double valor = Double.parseDouble(sc.nextLine().replace(',', '.'));

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
            String resposta = sc.nextLine().trim();

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
