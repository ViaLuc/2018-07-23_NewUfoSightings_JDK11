package it.polito.tdp.newufosightings.model;

import java.util.List;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	private NewUfoSightingsDAO dao;
	private List<String> forme;
	
	
	public Model() {
		dao = new NewUfoSightingsDAO();
	}
	
	public void creaGrafo() {
		
	}

	public List<String> getForme(int anno) {
		forme = dao.loadForme(anno);
		return forme;
	}
}
