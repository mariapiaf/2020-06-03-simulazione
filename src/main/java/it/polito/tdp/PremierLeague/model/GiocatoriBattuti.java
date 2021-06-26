package it.polito.tdp.PremierLeague.model;

public class GiocatoriBattuti implements Comparable<GiocatoriBattuti>{

	Player giocatoreBattuto;
	int delta;
	
	public GiocatoriBattuti(Player giocatoreBattuto, int delta) {
		super();
		this.giocatoreBattuto = giocatoreBattuto;
		this.delta = delta;
	}

	public Player getGiocatoreBattuto() {
		return giocatoreBattuto;
	}

	public void setGiocatoreBattuto(Player giocatoreBattuto) {
		this.giocatoreBattuto = giocatoreBattuto;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}

	@Override
	public int compareTo(GiocatoriBattuti o) {
		return -(this.delta-o.delta);
	}

	@Override
	public String toString() {
		return giocatoreBattuto.getPlayerID()+ " " + giocatoreBattuto.getName()+ " | " + delta;
	}
	
	
	
}
