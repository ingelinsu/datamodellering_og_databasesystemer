package databaseprosjekt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Search {
	
	
	// konstruktøren tar inn en string man vil søke etter og kaller på metoden searchForLine
	Search(String line){
		searchForLine(line);
		
	}
	
	public void searchForLine(String line) {
		Connection connection = new MyDBconnect().getConnection();
		
		// lager en array der alle postID-ene som matcher søket skal legges til
		ArrayList<Integer> posts = new ArrayList<Integer>();
		
		try {
			// utfører en spørring som finner alle poster som inneholder stringen i content
			Statement stmt = (Statement) connection.createStatement();
			String query = "select postID from post where content like '%"+ line +"%';";
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				int post = rs.getInt("postID");
				posts.add(post);
			}
			
			// utfører en spørring som finner alle poster som inneholder stringen i title
			Statement stmt1 = (Statement) connection.createStatement();
			String query1 = "select postID from thread where title like '%"+ line +"%';";
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()) {
				int post = rs1.getInt("postID");
				// legger bare til disse postID-ene i arrayen hvis den ikke eksisterer der fra før 
				if(!posts.contains(post)) {
					posts.add(post);
				}
			}
			
			// hvis ingen poster matcher søket vil brukeren få beskjed om dette
			if(posts.isEmpty()) {
				System.out.println("There are no posts that contains: '" +line+"'");
			}
			// printer ut arrayen med alle postID-ene som matcher søket
			else {
				System.out.println("Post-IDs of posts containing '"+line+"':");
				System.out.println(posts);
			}
			
			
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	
	}


}
