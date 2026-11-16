public class Missao {

    private int id;
    private String descricao;
    private String tipo;
    private AreaOperacao area;
    private String prioridade;
    private String status;
    private Robo roboResponsavel;
    private boolean resultado;

	public void atribuirRobo(Robo robo) {
		this.roboResponsavel = robo;
	}

	public void iniciar() {

	}

	public void concluir() {

	}

	public void cancelar() {

	}

	public String exibirResumo() {
		return nome + 
			"\nID: " + id +
        	"\nDescrição: " + descricao +
        	"\nTipo: " + tipo +
        	"\nÁrea de Operação: " + AreaOperacao.localizacao +
			"\nPrioridade: " + prioridade +
			"\nRobô responsável: " + roboResponsavel +
        	"\nStatus da missão: " + status;
	}

}
