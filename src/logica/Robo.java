package logica;

import enums.StatusRobo;
import interfaces.Comunicavel;

public abstract class Robo implements Comunicavel {

	private String id;
	private String nome;
	private int nivelBateria;
	private StatusRobo status;
	private int missoesRealizadas;

	public Robo(String id, String nome, int nivelBateria){
		this.id = id;
		this.nome = nome;
		setBateria(nivelBateria);

		if(nivelBateria == 0){
			this.status = StatusRobo.SEM_BATERIA;
		}else{
			this.status = StatusRobo.DISPONIVEL;
		}
		this.missoesRealizadas = 0;
	}

	public void setBateria(int bat){
		if(bat < 0 || bat >100){
			throw new IllegalArgumentException("Valor Inválido");
		}else {
			this.nivelBateria = bat;
		}
		if(bat == 0){
			status = StatusRobo.SEM_BATERIA;
		}
	}


	public boolean verificarDisponibilidade() {
		if(status == StatusRobo.DISPONIVEL){
			return true;
		}
		return false;
	}

	public int consultarBateria() {
		return nivelBateria;
	}

	public void recarregar() {
		if(status == StatusRobo.EM_MISSAO){
			throw new IllegalStateException("Operação não permitida");
		}
		setBateria(100);
		if(status == StatusRobo.SEM_BATERIA){
			status = StatusRobo.DISPONIVEL;
		}
	}

	public void exibirResumo() {
		System.out.println(toString());
	}

	public abstract void executarMissao();

	/**
	 * @see Comunicavel#enviarMensagem(String)
	 */
	public void enviarMensagem(String mensagem) {

	}


	/**
	 * @see Comunicavel#receberMensagem(String)
	 */
	public void receberMensagem(String mensagem) {

	}

	public String toString(){
		return "ID: "+ id+ 
				"\n Nome: "+ nome+
				"\n Nível Bateria: "+nivelBateria+
				"\n Status: "+ status+
				"\n missoesRealizadas: "+missoesRealizadas;
	}
}
