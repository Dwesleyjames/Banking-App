package cisc327;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
	public static void main(String args[]) {
		boolean cont = true;
		
		
		//File file = new File("inputs.txt");
		File file = new File(args[2]);
		try {
			System.out.println("Input file found.");
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("No input file found, command will need to be entered manually.");
			scanner = new Scanner(System.in);
		}
		System.out.println("Welcome!\nEnter your commands: ");
		
		//String accountsList = args[0];
		
		accountsFile = args[0];
		//accountsFile = "validAccountsList.txt";
		transFile = args[1];
		//transFile = "transactionSum.txt";
		masterFile = "masterFile.txt";
		
		String command;
		//loop for listening to logging in
		while(cont) {
			
			//prompt to enter a command
			System.out.print("\n>>> ");
			
			if(scanner.hasNextLine()) {
			//get command from user, change to lower case for more flexible input management
				command = scanner.nextLine();
				command = command.toLowerCase();
			} else {
				command = "exit";
			}
			
			//gives a quick and easy way to exit out of the application, used for faster testing while writing code
			//this will be changed in the future to only work after logging out
			if (command.equals("exit")) {
				cont = false;
				System.out.println("=========================== End of session =============================");
				scanner.close();
			} else {
				FrontEnd.useCommand(command);
			}
		}
		
		//save to transaction file
		//BackEnd.saveToTransactionFile();
		
	}
	static Scanner scanner;
	static String accountsFile;
	static String transFile;
	static String masterFile;
}