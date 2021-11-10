package databaseprosjekt;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;

public class MyDBconnect {
	
	public static Connection connection;
	
	public static Connection getConnection() {
		//kobler javaprosjeket til databasen
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prosjekt", "root", "password");//Establishing connection
			} catch (SQLException e) {
			System.out.println("Error while connecting to the database");
			}
		return connection;
	}


}
