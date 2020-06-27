package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Adiacenza;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates() {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
						rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
						rs.getString("Neighbors"));
				result.add(state);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<String> loadForme(int anno) {
		String sql = "SELECT DISTINCT s.shape " + 
				"FROM sighting AS s " + 
				"WHERE YEAR(s.datetime) = ? AND s.shape != \"\"" + 
				"ORDER BY s.shape ASC";
		List<String> result = new ArrayList<String>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();

			while (res.next()) {
			
				result.add(res.getString("shape"));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Adiacenza> getAdiacenze(String forma, int anno, Map<String, State> stateIdMap) {
		String sql = "SELECT s1.state AS st1, s2.state AS st2, COUNT(DISTINCT s1.id)+COUNT(DISTINCT s2.id) AS peso " + 
				"FROM sighting AS s1, sighting AS s2, neighbor AS n " + 
				"WHERE s1.state = n.state1 AND s2.state = n.state2 AND s1.state < s2.state " + 
				"AND s1.shape = ? AND s2.shape = ? AND YEAR(s1.datetime)= ? AND YEAR(s2.datetime) = ? " + 
				"GROUP BY st1, st2";
		List<Adiacenza> result = new ArrayList<Adiacenza>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, forma);
			st.setString(2, forma);
			st.setInt(3, anno);
			st.setInt(4, anno);
			
			ResultSet res = st.executeQuery();

			while (res.next()) {
				if(stateIdMap.containsKey(res.getString("st1")) && stateIdMap.containsKey(res.getString("st2"))) {
					result.add(new Adiacenza(stateIdMap.get(res.getString("st1")), stateIdMap.get(res.getString("st2")), res.getDouble("peso")));
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}

