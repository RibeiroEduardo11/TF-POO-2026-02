"""Testes de integração do menu, usando Java 17 e diretórios temporários."""

import os
import subprocess
import tempfile
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[1]


def exigir(texto, *trechos):
    for trecho in trechos:
        if trecho not in texto:
            raise AssertionError(f"Não encontrei {trecho!r} na saída:\n{texto}")


def testar(classes, pasta):
    def executar(*opcoes, dados="central.dat", codigo=0):
        resultado = subprocess.run(
            [os.environ.get("JAVA", "java"), "-Dfile.encoding=UTF-8", "-cp", str(classes),
             "app.Main", dados],
            input="\n".join(map(str, opcoes)) + "\n",
            cwd=pasta, text=True, encoding="utf-8", capture_output=True, timeout=20,
        )
        if resultado.returncode != codigo:
            raise AssertionError(resultado.stderr + resultado.stdout)
        return resultado.stdout if codigo == 0 else resultado.stderr

    cadastro = (
        5, "Parque", "Rua A", 1, 1, "Área urbana",
        5, "Margem", "Rua B", 2, 3, "Área alagada",
        1, 1, "Drone 1", 25, 100, "s",
        1, 1, "Drone 2", 80, 120, "s",
        1, 1, "Drone 3", 14, 120, "n",
        1, 2, "Terrestre", 40, "5,5", "n",
        8, "Transportar na área alagada", 3, 3, 2, 2,
        8, "Mapear área de risco alto", 1, 3, 2,
        8, "Buscar no parque", 2, 3, 1,
        8, "Transportar no parque", 3, 1, 1, "5,5",
        8, "Segundo reconhecimento", 1, 3, 2,
    )
    saida = executar(*cadastro, 16, "n", 16, "s", 11, 2, 0)
    exigir(saida, "Missão 1 permanece pendente", "Missão 2 - Mapear área de risco alto",
           "Robô 1 - Drone 1 | Bateria: 25% | Consumo: 15 p.p. | Restante: 10%",
           "Sugestão recusada", "Robo Drone 1 recebeu: Central:",
           "Robo Drone 1 enviou: Central, iniciei a missão 2.")
    if saida.count("=== DESPACHO ASSISTIDO ===") != 2:
        raise AssertionError("A sugestão recusada deveria reaparecer.")
    ordem = executar(
        5, "Parque", "Rua A", 1, 1, "Risco baixo",
        5, "Margem", "Rua B", 2, 3, "Risco alto",
        1, 1, "Drone", 50, 100, "s",
        8, "Prioridade média", 1, 2, 2,
        8, "Risco baixo", 1, 3, 1,
        8, "Primeira de risco e prioridade altos", 1, 3, 2,
        8, "Segunda de risco e prioridade altos", 1, 3, 2,
        16, "n", 0, dados="ordem.dat"
    )
    exigir(ordem, "Missão 3 - Primeira de risco e prioridade altos | Prioridade: ALTA | Risco: ALTO")
    print("OK 1 - Despacho ordena por prioridade, risco e fila, pula missão sem robô apto "
          "e confirma a atribuição ao robô com menor sobra.")

    saida = executar(17, 10, 5, 1, 12, 2, 2, 14, 0)
    exigir(saida, "Status: EM_ANDAMENTO", "O robô não está disponível.",
           "Missão concluída com sucesso.", "Área sobrevoada e informações coletadas.",
           "Nível Bateria: 10", "missoesRealizadas: 1", "Status: CONCLUIDA",
           "Câmera: sim", "Capacidade de carga (kg): 5.5")
    print("OK 2 - A reinicialização preserva a missão iniciada e o robô reservado. "
          "A conclusão desconta 15 p.p. e atualiza o histórico.")

    saida = executar(
        10, 3, 3, 10, 1, 4, 10, 5, 3, 11, 3, 10, 3, 2, 11, 3, 12, 3,
        10, 4, 4, 11, 4, 12, 4, 2, 13, 1, 15, "s", 0
    )
    exigir(saida, "Esse robô não é compatível", "Bateria insuficiente",
           "Nenhum robô foi atribuído à missão", "Busca aérea simulada realizada com câmera.",
           "Transporte simulado de", "Status: SEM_BATERIA", "foi cancelada",
           "Missões pendentes: 1", "Missões em andamento: 0",
           "Missões concluídas: 3", "Missões canceladas: 1", "Relatório salvo em")
    relatorios = list((pasta / "relatorios").glob("relatorio_*.txt"))
    if len(relatorios) != 1:
        raise AssertionError("Esperava um relatório exportado.")
    exigir(relatorios[0].read_text(encoding="utf-8"),
           "Total de missões: 5", "Missões concluídas: 3", "Missões canceladas: 1")
    print("OK 3 - Câmera ausente, área alagada, bateria insuficiente e início sem robô "
          "são rejeitados. Busca, transporte e relatório funcionam.")

    saida = executar(4, 4, 2, 13, 5, 8, "Nova missão", 1, 1, 1, 14, 0)
    exigir(saida, "Robô recarregado com sucesso", "Nível Bateria: 100",
           "Missão registrada com sucesso! ID: 6", "Status: CANCELADA")
    print("OK 4 - Recarga, cancelamento e IDs continuam funcionando após novo reinício.")

    arquivo_invalido = pasta / "invalido.dat"
    original = b"arquivo invalido"
    arquivo_invalido.write_bytes(original)
    exigir(executar(0, dados=arquivo_invalido.name, codigo=1),
           "Não foi possível recuperar os dados da central")
    if arquivo_invalido.read_bytes() != original:
        raise AssertionError("O programa sobrescreveu o arquivo inválido.")
    print("OK 5 - Arquivo inválido gera erro controlado e é preservado.")


def main():
    with tempfile.TemporaryDirectory(prefix="tf-poo-testes-") as temporario:
        pasta = Path(temporario)
        classes = pasta / "classes"
        fontes = sorted((RAIZ / "src").rglob("*.java"))
        subprocess.run(
            [os.environ.get("JAVAC", "javac"), "--release", "17", "-encoding", "UTF-8",
             "-Xlint:all", "-d", str(classes), *map(str, fontes)],
            timeout=30, check=True,
        )
        testar(classes, pasta)


if __name__ == "__main__":
    main()
