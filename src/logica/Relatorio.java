package logica;

import enums.StatusMissao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class Relatorio {
    private final LocalDateTime dataGeracao = LocalDateTime.now();
    private final int totalRobos;
    private final long robosDisponiveis;
    private final int totalMissoes;
    private final long missoesPendentes;
    private final long missoesEmAndamento;
    private final long missoesConcluidas;
    private final long missoesCanceladas;

    public Relatorio(Collection<Robo> robos, Collection<Missao> missoes) {
        this.totalRobos = robos.size();
        this.robosDisponiveis = robos.stream().filter(Robo::verificarDisponibilidade).count();
        this.totalMissoes = missoes.size();
        this.missoesPendentes = contarMissoes(missoes, StatusMissao.PENDENTE);
        this.missoesEmAndamento = contarMissoes(missoes, StatusMissao.EM_ANDAMENTO);
        this.missoesConcluidas = contarMissoes(missoes, StatusMissao.CONCLUIDA);
        this.missoesCanceladas = contarMissoes(missoes, StatusMissao.CANCELADA);
    }

    private static long contarMissoes(Collection<Missao> missoes, StatusMissao status) {
        return missoes.stream().filter(missao -> missao.getStatus() == status).count();
    }

    public String gerarResumo() {
        return """
                === RELATÓRIO DA CENTRAL DE RESGATE ===
                Data da geração: %s
                Total de robôs: %d
                Robôs disponíveis: %d
                Total de missões: %d
                Missões pendentes: %d
                Missões em andamento: %d
                Missões concluídas: %d
                Missões canceladas: %d
                """.formatted(dataGeracao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                totalRobos, robosDisponiveis, totalMissoes, missoesPendentes,
                missoesEmAndamento, missoesConcluidas, missoesCanceladas);
    }

    public void salvarEmArquivo() {
        Path pasta = Path.of("relatorios");
        String carimbo = dataGeracao.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSSSSS"));
        Path arquivo = pasta.resolve("relatorio_" + carimbo + ".txt");

        try {
            Files.createDirectories(pasta);
            Files.writeString(arquivo, gerarResumo(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            System.out.println("Relatório salvo em: " + arquivo.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }
}
