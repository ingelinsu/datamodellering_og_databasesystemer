package databaseprosjekt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;



public class Post {
	
	private String courseID;
	private int folderID;
	private String folderName;
	private PiazzaUser user;
	
	private int postID;
	private String content;
	private int threadID;
	private int colourcode;
	private boolean courseIsAnonymous;
	private int postIsAnonymous; //
	private String postAnswering;
	
	
	
	// konstruktør som oppretter en post i Java når man vil lage en ny tråd
	Post(String folderName){ 
		
		//courseID og piazzaUser som trengs for å opprette en post er definert fra før 
		this.courseID = "V21TDT4145";
		this.user = new PiazzaUser("student@mail", "password", 0); 
		
		
		Connection connection = new MyDBconnect().getConnection();
		try {
			// henter folderID fra databasen
			Statement stmt1 = (Statement) connection.createStatement();
			String query1 = "select folderID from folder where folderName = '"+folderName+"' and courseID = '"+courseID+"';";
			ResultSet rs1 = stmt1.executeQuery(query1);
			
			if(rs1.next()) {
				this.folderID = rs1.getInt("folderID");
			}
			else {
				throw new IllegalArgumentException("Not a valid folder-name");
			}
			
			
			// henter ut om poster kan være anonyme eller ikke, og lagrer dette i variabelen courseIsAnonymous
			Statement stmt = (Statement) connection.createStatement();
		    String query = "select courseIsAnonymous from course where courseID='"+ courseID +"';";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()==false) {
				throw new IllegalArgumentException("CourseID incorrect");
			}
	        int anonymous = rs.getInt("courseIsAnonymous");
		 
			if (anonymous == 1) {
				courseIsAnonymous = true;
			}
			else {
				courseIsAnonymous = false;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		// kaller på metoden som oppretter en tråd 
		createThread();
		
	}
	
	
	// createThread tar inn input fra brukeren og skriver til databasen
	public void createThread() {
		Connection connection = new MyDBconnect().getConnection();
		try {
			// henter inn data fra brukeren og lagrer dem i variabler 
			Scanner scanner = new Scanner(System.in);
			
			String title;
			String tag;
			System.out.println("Enter title: ");
			title = scanner.nextLine();
			System.out.println("Enter your post: ");
			content = scanner.nextLine();
			System.out.println("Enter tag [question / announcement / homework / homework solution / lecture notes / general announcement] : ");
			tag = scanner.nextLine();
			tag  = tag.toLowerCase().trim();
			if (!(tag.equals("question") || tag.equals("announcement") || tag.equals("homework") || 
				tag.equals("homework solution") || tag.equals("lecture notes") || tag.equals("general announcement"))) {
				tag = null;
				System.out.println("Not a valid tag. Your post has not been marked with any tags.");
			}
			

			// hvis courset tillater anonyme poster får brukeren et valg om de vil poste anonymt eller ikke 
			if(courseIsAnonymous) {
				char anonymous;
				System.out.println("Do you want to publish your post anonymously? [y/n]");
				anonymous = scanner.nextLine().charAt(0);
				if (anonymous == 'y') {
					postIsAnonymous = 1;
				}
				else if (anonymous == 'n'){
					postIsAnonymous = 0;
				}
				else {
					postIsAnonymous = 0;
					System.out.println("Not a valid answer. Your post will not be anonymous. ");
				}
			}
			else {
				postIsAnonymous = 0;
			}
			// setter colourcoden på threaden lik 2, som betyr at tråden er ubesvart og har fargen rød
			colourcode = 2;
			
			
			// oppretter en ny post i databasen
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate("insert into post (content, colourcode, courseID, folderID, email, postIsAnonymous, threadID, fpostID)  values ('" +content+"',"+colourcode+",'"+courseID
				+ "',"+folderID+",'" +user.getEmail()+"', "+postIsAnonymous+", NULL, NULL)", Statement.RETURN_GENERATED_KEYS);
			
			// lagrer den automatisk genererte postID-en i en variabel
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
			    postID = rs.getInt(1);
			}
			
			// oppretter en ny thread i databasen
			stmt.executeUpdate("insert into thread values ("+postID+",'" +tag+"','"+title+"',"+ postID+ ")");
			
			// oppdaterer fremmednøkkelen threadID i post til thread
			stmt.executeUpdate("update post set threadID = "+postID+" where postID ="+postID+";");
			
			// legger til i databasen at brukeren har lest sin egen post
			stmt.execute("insert into readBy values ('"+user.getEmail()+"', "+ postID+");");
			
			
			System.out.println("Your post: " +postID+ ", has been published. ");
			
			
			
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	
	
	
	// oppretter en followup-post i Java (når man vil svare på, kommentere eller stille et oppfølgningsspørsmål til en post)
	Post(int threadID){ 
		
		// piazzaUser som trengs for å opprette en post defineres på forhånd
		this.user = new PiazzaUser("instructor@mail", "password", 1); 
		
		
		this.threadID = threadID; 	//posten brukeren svarer på
		Connection connection = new MyDBconnect().getConnection();
		try {
			// sjekker at threadID-en faktisk er en threadID (ikke en followupPost-id eller en id som ikke eksisterer i databasen)
			Statement stmt2 = (Statement) connection.createStatement();
			String query2 = "select * from thread where threadID = "+threadID+";";
			ResultSet rs2 = stmt2.executeQuery(query2);
	
			// hvis posten med id = threadID ikke finnes (eller ikke er en tråd) får man en feilmelding
			if(rs2.next()==false) {
				throw new IllegalArgumentException("Not a valid threadID");
			}
			
			// henter ut innholdet i posten man vil svare på 
			Statement stmt1 = (Statement) connection.createStatement();
			String query1 = "select content, courseID, folderID from post where postID="+ threadID +";";
			ResultSet rs1 = stmt1.executeQuery(query1);
			
			// henter courseID og folderID fra threaden man svarer på
			if(rs1.next()) {
				this.courseID = rs1.getString("courseID");
				this.folderID = rs1.getInt("folderID");
				this.postAnswering = rs1.getString("content");
			}
			
			
			// sjekker om courset tillater anonyme poster
			Statement stmt = (Statement) connection.createStatement();
		    String query = "select courseIsAnonymous from course where courseID='"+ courseID +"';";
			ResultSet rs = stmt.executeQuery(query);
			int anonymous = 0;
			if(rs.next()) {
				anonymous = rs.getInt("courseIsAnonymous");
			}
	        
			if (anonymous == 1) {
				courseIsAnonymous = true;
			}
			else {
				courseIsAnonymous = false;
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		// kaller på metoden som oppretter en followuppost
		createFpost();	
		
	}
	
	
	
	// createFpost tar inn input fra brukeren og skriver til databasen
	public void createFpost() { 
		Connection connection = new MyDBconnect().getConnection();
		
		try {
			Scanner scanner = new Scanner(System.in);
		    System.out.println("You are replying to post nr " + threadID + ": '" + postAnswering+"'");
		    
		    // tar inn input fra brukeren om hva slags post det er, og lagrer det i variabelen fposType
			int fpostType;
			System.out.println("Enter type: (0 = answer, 1 = comment, 2 = followup-question) ");
			fpostType = scanner.nextInt();
			scanner.nextLine();
			
			// setter rett colourcode på followupPosten
			if (fpostType == 2) {
			colourcode = 2;
			}
			else if (user.getUserType() == 0) {
				colourcode = 0;
			}
			else if (user.getUserType() == 1) {
				colourcode = 1;
			}
			
			// setter colourcode på threaden lik 3, som betyr at den er besvart og har fargen hvit
			if(fpostType==0) {
				Statement stmt = (Statement) connection.createStatement();
				stmt.executeUpdate("update post set colourcode = "+3+" where postID ="+threadID+";");
			}
		    
		    // tar inn input fra brukeren om hva som skal stå i posten, og lagrer det i variabelen content
		    System.out.println("Enter your post: ");
			content = scanner.nextLine();
			
			// hvis courset tillater anonyme poster får brukeren et valg om de vil poste anonymt eller ikke 
			if(courseIsAnonymous) {
				char anonymous;
				System.out.println("Do you want to publish your post anonymously? [y/n]");
				anonymous = scanner.nextLine().charAt(0);
				if (anonymous == 'y') {
					postIsAnonymous = 1;
				}
				else if (anonymous == 'n'){
					postIsAnonymous = 0;
				}
				// hvis brukeren ikke skriver inn y eller n, settes posten til å ikke være anonym 
				else {
					postIsAnonymous = 0;
					System.out.println("Not a valid answer. Your post will not be anonymous.");
				}
			}
			else {
				postIsAnonymous = 0;
			}
			
			
			// legger til den nye posten i databasen
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate("insert into post (content, colourcode, courseID, folderID, email, postIsAnonymous, threadID, fpostID) values ('" +content+"',"+colourcode+",'"+courseID
				+"',"+folderID+",'" +user.getEmail()+"',"+postIsAnonymous +", NULL, NULL )", Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
			    postID = rs.getInt(1);
			}
			
			// legger til followup-posten i databasen
			stmt.executeUpdate("insert into followupPost values ("+postID+", " +threadID+", "+fpostType+", "+ postID+ ");");
			
			
			// oppdaterer fremmednøkkelen fpostID i post til followupPost
			stmt.executeUpdate("update post set fpostID = "+postID+" where postID ="+postID+";");
			
			// legger til at brukeren har lest sin egen post
			stmt.execute("insert into readBy values ('"+user.getEmail()+"', "+ postID+");");
			
			System.out.println("Your post: " +postID+ ", has been published. ");
			
			
		} catch(SQLException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
