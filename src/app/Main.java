package app;

import controle.CentralResgate;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Path arquivo = Path.of(args.length == 0 ? "dados/central-resgate.dat" : args[0]);
            CentralResgate central = new CentralResgate("Central de Resgate", entrada, arquivo);

            try {
                executarMenu(entrada, central);
            } finally {
                central.salvarDadosPendentes();
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. A central foi fechada.");
        } catch (IOException | InvalidPathException e) {
            System.err.println("Não foi possível recuperar os dados da central: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void executarMenu(Scanner entrada, CentralResgate central) {
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
                case "16" -> central.sugerirDespacho();
                case "17" -> central.listarMissoesEmAndamento();
                case "0" -> {
                    System.out.println("Central encerrada.");
                    return;
                }
                default -> System.out.println("Opção inválida. Escolha um número do menu.");
            }
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
                9 - Listar missões pendentes
                10 - Atribuir robô à missão
                11 - Iniciar missão
                12 - Concluir missão
                13 - Cancelar missão
                14 - Listar missões finalizadas
                15 - Gerar relatório geral
                16 - Sugerir despacho
                17 - Listar missões em andamento
                0 - Sair
                """);
        System.out.print("Escolha uma opção: ");
    }
}
