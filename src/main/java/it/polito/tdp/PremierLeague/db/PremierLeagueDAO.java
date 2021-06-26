package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Arco;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public void listAllPlayers(Map<Integer, Player> idMap){
		String sql = "SELECT * FROM Players";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				idMap.put(res.getInt("PlayerID"), player);
				
			}
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Player> getVertici(double x, Map<Integer, Player> idMap) {
		String sql = "SELECT DISTINCT p.PlayerID, p.Name "
				+ "FROM players p, actions a "
				+ "WHERE p.PlayerID = a.PlayerID "
				+ "GROUP BY p.PlayerID, p.Name "
				+ "HAVING AVG(a.Goals) > ?";
		
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setDouble(1, x);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("p.PlayerID"))) {
					Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
					idMap.put(res.getInt("p.PlayerID"), player);
					result.add(player);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Arco> getArchi(Map<Integer, Player> idMap) {
		String sql = "SELECT a1.PlayerID, a2.PlayerID, (SUM(a1.TimePlayed)- SUM(a2.TimePlayed)) AS diff "
				+ "FROM actions a1, actions a2 "
				+ "WHERE a1.TeamID != a2.TeamID AND a1.`Starts` = 1 AND a2.`Starts` = 1 "
				+ "	AND a1.MatchID = a2.MatchID AND a1.PlayerID > a2.PlayerID "
				+ "GROUP BY a1.PlayerID, a2.PlayerID "
				+ "HAVING diff !=0";
		
		List<Arco> result = new ArrayList<Arco>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(idMap.containsKey(res.getInt("a1.PlayerID")) && idMap.containsKey(res.getInt("a2.PlayerID"))) {
					Player p1 = idMap.get(res.getInt("a1.PlayerID"));
					Player p2 = idMap.get(res.getInt("a2.PlayerID"));
					result.add(new Arco (p1,p2, res.getInt("diff")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
