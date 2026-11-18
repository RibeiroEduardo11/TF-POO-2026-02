package app;

import controle.CentralResgate;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            CentralResgate central = new CentralResgate("Central de Resgate", entrada);

            while (true) {
                exibirMenu(central.getNome());
                switch (entrada.nextLine().trim()) {
                    case "1" -> central.cadastrarRobo();
                    case "2" -> central.listarRobos();
                    case "3" -> central.listarRobosDisponiveis();
                    case "4" -> central.recarregarRobo();
                    case "5" -> central.cadastrarArea();
                    case "6" -> central.listarAreas();
                    case "7" -> central.atualizarNivelRiscoArea();
                    case "8" -> central.registrarMissao();
                    case "9" -> central.listarMissoesPendentes();
                    case "10" -> central.atribuirRoboMissao();
                    case "11" -> central.iniciarMissao();
                    case "12" -> central.concluirMissao();
                    case "13" -> central.cancelarMissao();
                    case "14" -> central.listarMissoesFinalizadas();
                    case "15" -> central.gerarRelatorioGeral();
                    case "0" -> {
                        System.out.println("Central encerrada.");
                        return;
                    }
                    default -> System.out.println("Opção inválida. Escolha um número do menu.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. A central foi fechada.");
        }
    }

    private static void exibirMenu(String nomeCentral) {
        System.out.println("\n=== " + nomeCentral + " ===");
        System.out.print("""
                1 - Cadastrar robô
                2 - Listar robôs
                3 - Listar robôs disponíveis
                4 - Recarregar robô
                5 - Cadastrar área
                6 - Listar áreas
                7 - Atualizar nível de risco de uma área
                8 - Registrar missão
                9 - Listar missões em aberto
                10 - Atribuir robô à missão
                11 - Iniciar missão
                12 - Concluir missão
                13 - Cancelar missão
                14 - Listar missões finalizadas
                15 - Gerar relatório geral
                0 - Sair
                """);
        System.out.print("Escolha uma opção: ");
    }
}
