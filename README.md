# TF-POO-2026-02
Trabalho final da disciplina de programação orientada a objetos.

A aplicação simula uma central de resgate. Pelo menu do terminal, é possível cadastrar áreas e robôs, atribuir e executar missões e gerar um relatório.

## Como executar

No IntelliJ IDEA, selecione um JDK 17 ou mais recente em **Project Structure** e execute `app.Main`. Em um terminal Bash, execute estes comandos na raiz do repositório.

```sh
javac -encoding UTF-8 -d out src/*/*.java
java -cp out app.Main
```

Cadastre uma área e um robô. Em seguida, registre a missão, atribua um robô compatível, inicie a missão e conclua a execução. A opção de relatório permite salvar o resumo na pasta `relatorios/`.
