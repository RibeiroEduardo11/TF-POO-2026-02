import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Scanner;

public class CentralResgate {

    private String nome;
    private ArrayList<Robo> robos;
    private ArrayList<AreaOperacao> areas;
    private Queue<Missao> missoesPendentes;
    private ArrayList<Missao> missoesFinalizadas;
    private Scanner sc = new Scanner(System.in);

    public CentralResgate() {
        this.robos = new ArrayList<>();
        this.areas = new ArrayList<>();
		this.missoesPendentes = new LinkedList<>();
		this.missoesFinalizadas = new ArrayList<>();
    }
    //CONFERIR MEDIDAS DE FALHA (exemplo não int)
    public void cadastrarRobo() {
		System.out.println("(1) Cadastrar Drone" + "\n(2) Cadastrar Robô Terrestre");
		int op = sc.nextInt();
		if(op == 1){ 
	    	Robo robo = new Drone();
	    	robos.add(robo);
			System.out.println("O Drone foi cadastrado com sucesso!");
		}
		else if(op == 2){
	    	Robo robo = new roboTerrestre();
	    	robos.add(robo);
			System.out.println("O Robo Terrestre foi cadastrado com sucesso!");
		} else {
	      	System.out.println("Alternativa Inválida.\nNenhum robô foi cadastrado!");
    	}
	}

    public void cadastrarArea() {
		AreaOperacao area = new AreaOperacao();
		areas.add(area);
    }

    public void registrarMissao() {
		Missao missão = new Missao();
		missões.add(missao);
    }

    public void listarRobosDisponiveis() {
		for (Robo robo : robos) {
            if (robo.getStatus() == StatusRobo.DISPONIVEL) {
               System.out.println(robo);
            }
		}
    }

    public void atribuirRoboMissao(Robo robo, Missao missao) {
		missao.atribuirRobo(robo);
    }

    public void listarMissoesPendentes() {
		for (Missao missao : missoesPendentes){
	    	System.out.println(missão.exibirResumo());
    }

    public void gerarRelatorioGeral() {

    }
}