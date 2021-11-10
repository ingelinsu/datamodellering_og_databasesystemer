package databaseprosjekt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Statistics {
	
	
	public void viewStatistics(){
		// en spørring som finner ønsket statistikkresultat fra MySQL
		Connection connection = new MyDBconnect().getConnection();
		String query = "Select t1.email, t1.postsRead, t2.postsCreated from "
				+ "(Select piazzaUser.email, count(readBy.email) "
				+ "as postsRead From PiazzaUser "
				+ "left outer join readBy on readBy.email = PiazzaUser.email "
				+ "Group by piazzaUser.email) as t1 "
				+ "left outer join "
				+ "(Select piazzaUser.email, count(Post.email) as postsCreated "
				+ "From PiazzaUser "
				+ "left outer join Post on piazzaUser.email = post.email "
				+ "Group by piazzaUser.email) as t2 "
				+ "on (t1.email = t2.email) "
				+ "Order by t1.postsRead DESC;";
		
		try {
			String email;
			int postsRead;
			int postsCreated;
			// utfører spørringen
			Statement stmt = (Statement) connection.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("Statistics for users in the database sorted on highest number of posts read: \n");
			while (rs.next()) {
				// printer ut alle resultatene i rekkefølgen spørringen returnerte dem
				email = rs.getString("t1.email");
				postsRead = rs.getInt("t1.postsRead");
				postsCreated = rs.getInt("t2.postsCreated");
				System.out.println(email + ": Posts read: " + postsRead + "\n\t\tPosts created: "+ postsCreated + "\n");
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		
	}
	
}
