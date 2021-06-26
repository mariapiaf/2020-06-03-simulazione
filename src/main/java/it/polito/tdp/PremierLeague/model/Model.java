package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {

	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private TopPlayer topPlayer;
	private List<Player> soluzioneMigliore;
	private int titolaritaMigliore;
	
	
	public Model() {
		dao = new PremierLeagueDAO();
		topPlayer = null;
	}
	
	public void creaGrafo(double x) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		// aggiungo vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(x, idMap));
		
		// aggiungo archi
		for(Arco a: this.dao.getArchi(idMap)) {
			if(a.getPeso() > 0) {
				Graphs.addEdge(this.grafo, a.getP1(), a.getP2(), a.getPeso());
			}
			else if(a.getPeso()<0) {
				Graphs.addEdge(this.grafo, a.getP2(), a.getP1(), (-1)*a.getPeso());
			}
		}
	}
	
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public TopPlayer topPlayer() {
		if(grafo == null)
			return null;
		
		Player best = null;
		Integer maxAvvBattuti = Integer.MIN_VALUE;
		
		for(Player p: grafo.vertexSet()) {
			if(this.grafo.outDegreeOf(p) > maxAvvBattuti) {
				best = p;
				maxAvvBattuti = this.grafo.outDegreeOf(p);
			}
		}
		
		TopPlayer ottimo = new TopPlayer(best, maxAvvBattuti);
		topPlayer = ottimo;
		return ottimo;
		
	}
	
	public List<GiocatoriBattuti> giocatoriBattutiDaTopPlayer() {
		Player p = topPlayer.getP();
		List<GiocatoriBattuti> result = new LinkedList<>();
		for(DefaultWeightedEdge edge: this.grafo.outgoingEdgesOf(p)) {
			Player battuto = this.grafo.getEdgeTarget(edge);
			GiocatoriBattuti nuovo = new GiocatoriBattuti(battuto, (int) this.grafo.getEdgeWeight(edge));
			result.add(nuovo);
		}
		Collections.sort(result);
		return result;
	}
	
	public int calcolaTitolaritaGiocatore(Player giocatore) {
		int titolarita = 0;
		
		int pesoUscente = 0;
		for(DefaultWeightedEdge edge: this.grafo.outgoingEdgesOf(giocatore)) {
			pesoUscente += this.grafo.getEdgeWeight(edge);
		}
		
		int pesoEntrante = 0;
		for(DefaultWeightedEdge edge: this.grafo.incomingEdgesOf(giocatore)) {
			pesoEntrante += this.grafo.getEdgeWeight(edge);
		}
		
		titolarita = pesoUscente - pesoEntrante;
		return titolarita;
	}
	
	public int calcolaTitolaritaTeam(List<Player> parziale) {
		int titolarita = 0;
		for(Player p: parziale) {
			titolarita += this.calcolaTitolaritaGiocatore(p);
		}
		return titolarita; 
	}
	
	
	public List<Player> dreamTeam(int k) {
		this.soluzioneMigliore = null;
		this.titolaritaMigliore = 0;
		List<Player> parziale = new ArrayList<>();
		List<Player> giocatori = new ArrayList<>(this.grafo.vertexSet());
		cerca(parziale, giocatori, k);
		
		return soluzioneMigliore;
		
	}
	
	public void cerca(List<Player> parziale, List<Player> tuttiIGiocatori, int k) {
		if(parziale.size() == k) {
			if(calcolaTitolaritaTeam(parziale) > titolaritaMigliore) {
				soluzioneMigliore = new ArrayList<>(parziale);
				titolaritaMigliore = calcolaTitolaritaTeam(parziale);
			}
			return;
		}
		
		for(Player p: tuttiIGiocatori) {
			if(!parziale.contains(p)) {
				parziale.add(p);
				List<Player> rimasti = new ArrayList<>(tuttiIGiocatori);
				rimasti.removeAll(Graphs.successorListOf(this.grafo, p));
				cerca(parziale, rimasti, k);
				parziale.remove(p);
			}
		}
		
		
	}
	
	
}
