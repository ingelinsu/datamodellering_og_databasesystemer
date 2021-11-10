package databaseprosjekt;

import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class PiazzaUser extends MyDBconnect{
	
	private String email;
	private String userPassword;
	private int userType; 			// verdi 0 for student og 1 for instruktør
	private String userName;
	
	// konstruktør som brukes når man oppretter et Java-objekt, piazzaUser, som man antar eksisterer i databasen
	PiazzaUser (String email, String password, int userType){
		this.email = email;
		this.userPassword = password;
		this.userType = userType;
	}
	
	// konstruktør som brukes når man vil logge inn med en bruker 
	PiazzaUser(){
		boolean isLoggedIn = logIn();
		//løkken kjører helt til logIn returnerer verdien true
		while(isLoggedIn==false) {
			isLoggedIn = logIn();
		}
	}

	
	public boolean logIn() {
	    try {
	    	
	    	// lar brukeren skrive inn email og passord og lagrer inputen i lokale variabler
			Scanner scanner = new Scanner(System.in); 
			System.out.println("Enter email: ");
			email = scanner.nextLine();
			System.out.println("Enter password: ");
			userPassword = scanner.nextLine();
			

			// sjekker om input finnes i databasen 
			Connection connection = new MyDBconnect().getConnection();
			Statement stmt = (Statement) connection.createStatement();
		    String query = "select userType from piazzaUser where email='"+email+"' and userPassword='"+userPassword+"'";
			ResultSet rs = stmt.executeQuery(query);
			
			
			/* hvis input ikke finnes i databasen; brukeren får ikke logget inn, og får beskjed om å prøve igjen
			   Da kjøres hele logIn på nytt*/
			if (rs.next() == false){ 
				System.out.println("Incorrect username or password. Try again.");
				return false;
			}
			
			/*Hvis input finnes i databasen lagres brukerens userType i en lokal variabel, 
			  og brukeren får beskjed om at de har logget inn*/
			if(rs.next()) {
				userType = rs.getInt("userType");
			}
			System.out.println("You are now logged in");

			
			
			
			
			
		} catch (SQLException e) {
			System.out.println("You have a problem.....");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	    return true;

		
	}
	
	
	public int getUserType() {
		return userType;
	}
	
	public String getEmail() {
		return email;
	}
	
	// denne brukes ikke da den ikke er en del av prosjeket
	/*public boolean createUser() { 
		Connection connection = new MyDBconnect().getConnection();
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter email: ");
			email = scanner.nextLine();
			System.out.println("Enter password: ");
			userPassword = scanner.nextLine();
			System.out.println("Enter 0 if you are a student, and 1 if you are an instuctor");
			userType = scanner.nextInt();
			scanner.nextLine();
			System.out.println("Enter your name: ");
			userName = scanner.nextLine(); 
			
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate("insert into piazzaUser values ('"+email+"','" +userPassword+"',"+userType+",'"+userName+"')");
			System.out.println("Your new user is now active");
			return true;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}*/
	
	
}
