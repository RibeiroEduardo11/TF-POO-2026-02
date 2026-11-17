
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Relatorio {

    private LocalDateTime dataGeracao;
    private int totalMissoes;
    private int totalRobos;
    private int missoesConcluidas;
    private int missoesPendentes;

    public Relatorio(int missoesConcluidas, int missoesPendentes,
                     int totalRobos, int totalMissoes) {

        this.missoesConcluidas = missoesConcluidas;
        this.missoesPendentes = missoesPendentes;
        this.totalMissoes = totalMissoes;
        this.totalRobos = totalRobos;
        this.dataGeracao = LocalDateTime.now();
    }

    public String gerarResumo() {

        DateTimeFormatter formatoData =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String resumo = "====================================\n" +
                "     RELATORIO DA CENTRAL DE RESGATE\n" +
                "====================================\n" +
                "Data da geracao: " +
                dataGeracao.format(formatoData) + "\n\n" +
                "Total de robos: " + totalRobos + "\n" +
                "Total de missoes: " + totalMissoes + "\n" +
                "Missoes concluidas: " + missoesConcluidas + "\n" +
                "Missoes pendentes: " + missoesPendentes + "\n" +
                "====================================";

        return resumo;
    }

    public void salvarEmArquivo() {

        Path pasta = Paths.get("relatorios");

        String nomeArquivo = "relatorio_" +
                dataGeracao.format(
                    DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                ) + ".txt";

        Path arquivo = pasta.resolve(nomeArquivo);

        try {
            Files.createDirectories(pasta);

            try (BufferedWriter escritor =
                    Files.newBufferedWriter(arquivo, StandardCharsets.UTF_8)) {

                escritor.write(gerarResumo());
            }

            System.out.println("Relatorio salvo em: " +
                    arquivo.toAbsolutePath());

        } catch (IOException e) {
            System.out.println("Erro ao salvar relatorio: " +
                    e.getMessage());
        }
    }
}
