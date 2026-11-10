public abstract class Robo  implements Comunicavel {

	private String id;

	private String nome;

	private int nivelBateria;

	private boolean status;

	private int missoesRealizadas;

	public void verificarDisponibilidade() {

	}

	public void consultarBateria() {

	}

	public void recarregar() {

	}

	public void exibirResumo() {

	}

	public void executarMissao() {

	}


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

}
