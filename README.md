# Sistema de Coordenação de Robôs para Missões de Resgate

Projeto desenvolvido para a disciplina de **Programação Orientada a Objetos (POO) — PUCRS, 2026/2**.

O sistema simula uma central responsável por coordenar robôs terrestres e drones em missões de resgate, permitindo cadastrar equipamentos e áreas de operação, registrar e executar missões, acompanhar recursos e gerar relatórios.

## Funcionalidades

- **Cadastro de robôs:** registro de drones e robôs terrestres, com características e níveis de bateria específicos.
- **Cadastro de áreas:** registro de localização, tipo de terreno, nível de risco e descrição.
- **Gerenciamento de missões:** criação, atribuição de robôs, início, conclusão e cancelamento de missões.
- **Compatibilidade:** verificação das capacidades dos robôs, condições do terreno e requisitos de cada missão.
- **Controle de bateria:** consumo de energia conforme o tipo de robô e a missão executada.
- **Comunicação:** simulação de troca de mensagens entre os robôs e a central.
- **Relatórios:** geração de resumos das operações e salvamento em arquivos.
- **Persistência:** armazenamento e recuperação automática dos dados entre execuções do programa.

## Funcionalidade inovadora: Sistema Inteligente de Priorização de Missões

O sistema possui um mecanismo de pontuação que organiza automaticamente a fila de missões de acordo com sua urgência.

A pontuação considera três critérios:

| Critério | Classificação | Pontuação |
|---|---|---:|
| Prioridade cadastrada | Alta | 50 |
| | Média | 30 |
| | Baixa | 10 |
| Risco da área | Alto | 30 |
| | Médio | 20 |
| | Baixo | 10 |
| Tipo de missão | Localização de pessoas | 20 |
| | Transporte de suprimentos | 15 |
| | Reconhecimento | 10 |

A pontuação final é calculada pela soma dos três critérios.

As missões são organizadas da maior para a menor pontuação. Em caso de empate, a missão registrada há mais tempo recebe prioridade.

A classificação é atualizada quando uma missão é cadastrada, quando o risco de uma área é alterado e quando a fila é consultada.

## Tipos de robôs

**Drone**
- Executa missões de reconhecimento.
- Pode realizar localização de pessoas quando possui câmera.
- Possui características próprias, como altitude máxima e capacidade de captar imagens.

**Robô terrestre**
- Executa missões de transporte de suprimentos.
- Considera capacidade de carga e capacidade de superar obstáculos.
- Possui restrições relacionadas ao tipo de terreno.

## Tecnologias e conceitos utilizados

- Java
- Programação Orientada a Objetos
- Encapsulamento
- Herança e classes abstratas
- Polimorfismo
- Interfaces
- Enumerações
- Coleções Java (`ArrayList`, `Queue` e `LinkedList`)
- Expressões lambda e ordenação de coleções
- Tratamento de exceções
- Manipulação de arquivos e serialização de objetos

## Persistência de dados

O sistema utiliza serialização Java para salvar o estado da central no arquivo:

`dados/central-resgate.dat`

Ao iniciar uma nova execução, os dados são recuperados automaticamente, preservando os cadastros e o estado das operações.

Os relatórios gerados também podem ser armazenados em arquivos separados.

## Organização do projeto

```text
TF-POO-2026-02/
│
├── src/
│   ├── app/
│   │   └── main.java
│   │
│   ├── controle/
│   │   └── CentralResgate.java
│   │
│   ├── enums/
│   │   ├── NivelRisco.java
│   │   ├── PrioridadeMissao.java
│   │   ├── StatusMissao.java
│   │   ├── StatusRobo.java
│   │   ├── TipoMissao.java
│   │   └── TipoTerreno.java
│   │
│   ├── interfaces/
│   │   └── Comunicavel.java
│   │
│   ├── logica/
│   │   ├── AreaOperacao.java
│   │   ├── Drone.java
│   │   ├── Missao.java
│   │   ├── Relatorio.java
│   │   ├── Robo.java
│   │   └── RoboTerrestre.java
│   │
│   └── persistencia/
│       └── Persistencia.java
│
├── dados/
├── relatorios/
├── DiagramaDeClassesTFPOO.asta
└── README.md
```

## Como executar

### Requisitos

- JDK 17 ou superior.
- Git (caso o projeto seja obtido pelo GitHub).

### Clonar o repositório

```bash
git clone -b fix/revisao-integracao https://github.com/RibeiroEduardo11/TF-POO-2026-02.git
cd TF-POO-2026-02
```

### Compilar e executar no Windows PowerShell

```powershell
javac -d out (Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
java -cp out app.main
```

### Compilar e executar no Linux ou GitHub Codespaces

```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out app.main
```

Após a execução, o usuário poderá interagir com o sistema pelo menu apresentado no terminal.

## Equipe e contexto acadêmico

Trabalho final desenvolvido em equipe para a disciplina de Programação Orientada a Objetos da Pontifícia Universidade Católica do Rio Grande do Sul (PUCRS).

**Semestre:** 2026/2

**Projeto:** Sistema de Coordenação de Robôs para Missões de Resgate.