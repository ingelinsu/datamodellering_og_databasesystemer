package databaseprosjekt;

import java.util.Scanner;

public class RunProgram {
	
	private int usecase=0;
	
	
	public void start() {
		// printer ut brukerens alternativer 
		System.out.println(" Press 1 if you want to log in (usecase 1)\n Press 2 if you want to create a new thread in the folder 'Exam' in"
				+ " this course. (usecase 2) \n Press 3 if you want to answer a post in the folder 'Exam' in this course. (usecase 3)\n"
				+ " Press 4 if you want to find all post-IDs in this course that contains a certain phrase. (usecase 4)"
				+ "\n Press 5 if you want to view statistics of users activities. (usecase 5)\n Press 6 if you want to quit the program.");
		
		// registrerer brukerens valg
		Scanner scanner = new Scanner(System.in);
		usecase = scanner.nextInt();
		scanner.nextLine();
		
		
		/* utfører ønsket brukerhistorie og kaller deretter på start-metoden igjen, slik at 
		   brukeren får mulighet til å velge en ny brukerhistorie å utføre */
		if (usecase==1) {
			PiazzaUser user = new PiazzaUser();
			System.out.println("Your task has been completed. What do you want to do next? \n");
			start();
		}
		else if (usecase==2) {
			System.out.println("Enter the name of the folder in this course you want to publish a thread in: ");
			String folderName = scanner.nextLine();
			folderName = folderName.toLowerCase().trim();
			Post thread = new Post(folderName);
			System.out.println("Your task has been completed. What do you want to do next? \n");
			start();
		}
		else if (usecase == 3) {
			System.out.println("Enter the number of the post you want to reply to. ");
			int postAnswering = scanner.nextInt();
			Post answer = new Post(postAnswering);
			System.out.println("Your task has been completed. What do you want to do next? \n");
			start();
			
		}
		else if (usecase == 4) {
			System.out.println("Enter the phrase you want to search for: ");
			String line;
			line = scanner.nextLine();
			Search search = new Search(line);
			System.out.println("Your task has been completed. What do you want to do next? \n");
			start();
			
		}
		else if (usecase == 5) {
			Statistics statistics = new Statistics();
			statistics.viewStatistics();
			System.out.println("Your task has been completed. What do you want to do next? \n");
			start();
		}
		else if (usecase == 6) {
			System.out.println("You have exited the program. Run the program again to restart. ");
		}
		else {
			System.out.println("Not a valid number. Please try again.\n");
			start();
		}
	}
	
	public static void main(String[] args) {
		// introduserer programmet og kaller på metoden start
		System.out.println("Hello and welcome to our project in TDT4145! This is an implementation of Piazza. "
				+ "\nWe are currently in the Course TDT4145: 'Datamodellering og databasesystemer' in Piazza.\n");
		RunProgram runProgram = new RunProgram();
		runProgram.start();
		
	}

}
