package it.polito.tdp.newufosightings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private NewUfoSightingsDAO dao;
	private List<String> forme;
	private Graph<State, DefaultWeightedEdge> grafo;
	private List<State> stati;
	private Map<String, State> stateIdMap;

	public Model() {
		dao = new NewUfoSightingsDAO();
	}

	public List<String> getForme(int anno) {
		forme = dao.loadForme(anno);
		return forme;
	}

	public void creaGrafo(String forma, int anno) {
		grafo = new SimpleWeightedGraph<State, DefaultWeightedEdge>(DefaultWeightedEdge.class);

		//Aggiungi i vertici
		stati = dao.loadAllStates();
		Graphs.addAllVertices(this.grafo, stati);

		//stateIdMap
		stateIdMap = new HashMap<String, State>();
		for(State s : stati) {
			if(!stateIdMap.containsKey(s.getId()))
				stateIdMap.put(s.getId(), s);
		}

		//Aggiungi gli archi
		for (Adiacenza a : this.dao.getAdiacenze(forma, anno, stateIdMap)) {
			if(this.grafo.getEdge(a.getS1(), a.getS2()) == null) {
				Graphs.addEdge(this.grafo, a.getS1(), a.getS2(), a.getPeso());
			}
		}


		System.out.println("Grafo creato!\n# vertici: "+ this.grafo.vertexSet().size()+"\n# archi: "+this.grafo.edgeSet().size());
	}

}
