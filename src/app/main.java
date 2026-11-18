package app;

import controle.CentralResgate;
import persistencia.Persistencia;

import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        CentralResgate central;

        try {
            central = Persistencia.carregar(sc);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        int opcao;

        do {
            System.out.println("\n================================");
            System.out.println("       CENTRAL DE RESGATE");
            System.out.println("================================");
            System.out.println("1  - Cadastrar robo");
            System.out.println("2  - Cadastrar area");
            System.out.println("3  - Atualizar risco de area");
            System.out.println("4  - Registrar missao");
            System.out.println("5  - Listar robos");
            System.out.println("6  - Listar robos disponiveis");
            System.out.println("7  - Atribuir robo a missao");
            System.out.println("8  - Iniciar missao");
            System.out.println("9  - Concluir missao");
            System.out.println("10 - Cancelar missao");
            System.out.println("11 - Listar fila priorizada");
            System.out.println("12 - Listar missoes finalizadas");
            System.out.println("13 - Listar areas");
            System.out.println("14 - Gerar relatorio geral");
            System.out.println("15 - Consultar criterios de prioridade");
            System.out.println("0  - Sair");
            System.out.println("================================");

            System.out.print("Escolha uma opcao: ");

            try {
                opcao = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero valido.");
                opcao = -1;
                continue;
            }

            switch (opcao) {
                case 1 -> central.cadastrarRobo();
                case 2 -> central.cadastrarArea();
                case 3 -> central.atualizarNivelRiscoArea();
                case 4 -> central.registrarMissao();
                case 5 -> central.listarRobos();
                case 6 -> central.listarRobosDisponiveis();
                case 7 -> central.atribuirRoboMissao();
                case 8 -> central.iniciarMissao();
                case 9 -> central.concluirMissao();
                case 10 -> central.cancelarMissao();
                case 11 -> central.listarMissoesPendentes();
                case 12 -> central.listarMissoesFinalizadas();
                case 13 -> central.listarAreas();
                case 14 -> central.gerarRelatorioGeral();
                case 15 -> central.exibirCriteriosPrioridade();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opcao invalida.");
            }

            try {
                Persistencia.salvar(central);
            } catch (IllegalStateException e) {
                System.out.println("Aviso: " + e.getMessage());
            }

        } while (opcao != 0);

        sc.close();
    }
}