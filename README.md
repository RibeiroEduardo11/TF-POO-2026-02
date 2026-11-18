# TF-POO-2026-02
Trabalho final da disciplina de programação orientada a objetos.

A aplicação simula uma central de resgate. O menu permite cadastrar e consultar robôs e áreas, atribuir e executar missões, acompanhar o histórico e exportar relatórios.

O [relatório final](docs/relatorio-final.pdf) reúne o UML, as decisões de implementação, as capturas de tela e os resultados dos testes. O [diagrama editável](docs/diagrama-classes.dot) pode ser aberto com Graphviz.

## Como executar

No IntelliJ IDEA, selecione um JDK 17 ou mais recente em **Project Structure** e execute `app.Main`. Em um terminal Bash, execute estes comandos na raiz do repositório.

```sh
javac -encoding UTF-8 -d out src/*/*.java
java -cp out app.Main
```

## Demonstração

1. Cadastre uma área pelo menu **5**, um drone com câmera e bateria de 30% pelo menu **1** e uma missão de reconhecimento pelo menu **8**.
2. Use **16** para ver o despacho assistido. Ele considera prioridade, risco e ordem da fila, mostra o consumo previsto e solicita confirmação da atribuição.
3. Inicie a missão pelo menu **11**. Encerre com **0** e abra a central novamente. Use **17** para consultar a missão recuperada, **12** para concluí-la e **14** para consultar o histórico.
4. Use **15** para ver os totais e, se desejado, salvar um arquivo de texto em `relatorios/`. A opção **4** recarrega um robô sem missão reservada.

O arquivo `dados/central-resgate.dat` é atualizado a cada alteração e lido na abertura. Para iniciar uma demonstração independente, informe um caminho diferente, como `java -cp out app.Main dados/demo.dat`. Use uma instância por arquivo.

## Testes

Com Python 3 e o JDK no `PATH`, execute `python3 testes/testar_sistema.py`. Os testes compilam o projeto, exercitam o menu e usam arquivos temporários.
