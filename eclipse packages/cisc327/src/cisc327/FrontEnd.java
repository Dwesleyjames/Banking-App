package cisc327;

import java.io.*;
import java.util.*;

public class FrontEnd {

	//systemState is set to a string that dictates the current systemState of the system
	static String systemState = "logout";
	
	//commandState dictates the current command
	static String privilege = null;
	
	//keeps track of the total withdrawn in the current session
	static Map totalWithdrawn = new HashMap();
	
	static Map createdThisSession = new HashMap();
	
	static Scanner scanner = Main.scanner;
	
	//function that is called from Main, dictates all of the Front End
	public static void useCommand(String command) {
		if (systemState.equals("login") || command.equals("login")) {
			switch (command){
				case "login":
					login();
					break;
				case "logout":
					logout();
					break;
				case "atm":
					setATM();
					break;
				case "agent":
					setAgent();
					break;
				case "transfer":
					if(TestFrontEnd.checkPrivilege()) { transfer(); }
					break;
				case "withdraw":
					if(TestFrontEnd.checkPrivilege()) { withdraw(); }
					break;
				case "deposit":
					if(TestFrontEnd.checkPrivilege()) { deposit(); }
					break;
				case "createacct":
					if(TestFrontEnd.checkPrivilege()) { createAcct(); }
					break;
				case "deleteacct":
					if(TestFrontEnd.checkPrivilege()) { deleteAcct(); }
					break;
			}
		} else {
			System.out.println("Must login first.");
		}
	}
	
	//handle logins
	public static void login() {
		if(systemState != "logout") {
			System.out.println("Already logged in. Unacceptable command.");
		} else {
			System.out.println("Successful login.");
			systemState = "login";
		}
	}
	
	//handle logouts
	public static void logout() {
		if(systemState != "login") {
			System.out.println("Not logged in, so cannot logout. Unacceptable command.");
		} else {
			System.out.println("Successful logout. Have a great day!");
			writeToTransFile("EOS", "0000000", "000", "0000000", "***");
			
			//reset the systems states
			systemState = "logout";
			privilege = null;
			totalWithdrawn.clear();
			createdThisSession.clear();
		}
	}
	
	//set privilege to ATM
	public static void setATM() {
		if(privilege != null) {
			System.out.println("Already in "+privilege+" mode. Unacceptable command.");
		} else {
			System.out.println("ATM mode selected.");
			privilege = "ATM";
		}
	}
	
	//set privilege to agent
	public static void setAgent() {
		if(privilege != null) {
			System.out.println("Already in "+privilege+" mode. Unacceptable command.");
		} else {
			System.out.println("Agent mode selected.");
			privilege = "agent";
		}
	}

	//handle transfers
	public static void transfer() {
		//get the first account number
		System.out.print("From account number: ");
		String acct1 = scanner.nextLine();
		
		while (!acct1.equals("cancel") && !TestFrontEnd.testExistingAccount(acct1)) {
			System.out.println("Account does not exist, please enter an existing account number or type 'cancel'.");
			acct1 = scanner.nextLine();
		}
		
		if (createdThisSession.containsKey(acct1)) {
			System.out.println("This account was created this session, you cannot make any transactions with it.");
		} else if (!acct1.equals("cancel")) {
			//get the second account number
			System.out.print("\nTo account number: ");
			String acct2 = scanner.nextLine();
			TestFrontEnd.testExistingAccount(acct2);
			
			while (!acct2.equals("cancel") && !TestFrontEnd.testExistingAccount(acct2)) {
				System.out.println("Account does not exist, please enter an existing account number or type 'cancel'.");
				acct2 = scanner.nextLine();
			}
			
			if (createdThisSession.containsKey(acct2)) {
				System.out.println("This account was created this session, you cannot make any transactions with it.");
			} else if(!acct2.equals("cancel")) {
				//get the amount
				System.out.print("\nAmount: ");
				String amount = scanner.nextLine();		//this will need to be changed to handle Strings and then cast to an int later
				
				while (!amount.equals("cancel") && !TestFrontEnd.testAmount(amount)) {
					amount = scanner.nextLine();
				}
				
				//if we have not cancelled through any of the input fields we will finish the transfer and write to the transaction file
				if (!amount.equals("cancel")) {
					writeToTransFile("XFR", acct2, amount, acct1, "***");
				}
			}
		}
	}
	
	//handle withdrawals
	public static void withdraw() {
		String amount = "0";
		String acct;
		
		//get the account number
		System.out.print("From account number: ");
		acct = scanner.nextLine();
		
		//make sure to grab an existing account
		while (!acct.equals("cancel") && !TestFrontEnd.testExistingAccount(acct)) {
			System.out.println("Account does not exist, please enter an existing account number or type 'cancel'.");
			acct = scanner.nextLine();
		}
		
		if (createdThisSession.containsKey(acct)) {
			System.out.println("This account was created this session, you cannot make any transactions with it.");
		} else if(!acct.equals("cancel")) {
			//get the amount
			System.out.print("\nAmount: ");
			amount = scanner.nextLine();
		
			
			//make sure to grab a valid amount
			while (!amount.equals("cancel") && !TestFrontEnd.testWithdrawAmount(amount, acct)) {
				amount = scanner.nextLine();
			}
		
			//if successful then write to transfile
			if (!acct.equals("cancel") && !amount.equals("cancel")) {
				writeToTransFile("WDR", acct, amount, "0000000", "***");
			}
		}
	}
	
	//handle deposits
	public static void deposit() {
		String amount = "0";
		String acct;
		
		//get the account number
		System.out.print("To account number: ");
		acct = scanner.nextLine();
		
		//make sure to grab an existing account
		while (!acct.equals("cancel") && !TestFrontEnd.testExistingAccount(acct)) {
			System.out.println("Account does not exist, please enter an existing account number or type 'cancel'.");
			acct = scanner.nextLine();
		}
		
		if (createdThisSession.containsKey(acct)) {
			System.out.println("This account was created this session, you cannot make any transactions with it.");
		} else if(!acct.equals("cancel")) {
			//get the amount
			
			System.out.print("\nAmount: ");
			amount = scanner.nextLine();
			
			//make sure to grab a valid amount
			while (!amount.equals("cancel") && !TestFrontEnd.testAmount(amount)) {
				amount = scanner.nextLine();
			}
			
			//if successful then write to transfile
			if (!acct.equals("cancel") && !amount.equals("cancel")) {
				writeToTransFile("DEP", acct, amount, "0000000", "***");
			}
		}

	}

	//handle account creation
	public static void createAcct() {
		if(privilege.equals("ATM")) {
			System.out.println("You do not have the privilege. Unacceptable command.");
		} else {
			//get the account number
			System.out.print("New account number: ");
			String acct = scanner.nextLine();
			
			while (!acct.equals("cancel") && (TestFrontEnd.testExistingAccount(acct) || !TestFrontEnd.testValidAccount(acct))) {
				
				if(TestFrontEnd.testExistingAccount(acct)) {
					System.out.println("Account already exists, please enter a unique account number or type 'cancel'.");
				}
				acct = scanner.nextLine();
			}
			
			if(!acct.equals("cancel")) {
				//get the account name
				System.out.print("New account name: ");
				String name = scanner.nextLine();
				
				while (!name.equals("cancel") && !TestFrontEnd.testValidAccountName(name)) {
					name = scanner.nextLine();
				}
				
				if(!name.equals("cancel")) {
					//write to the transaction summary file
					writeToTransFile("NEW", acct, "000", "0000000", name);
					
					//add to the accounts file
					addToAcctFile(acct);
					
					//add to createdThisSession
					createdThisSession.put(acct, 0);
				}
			}
		}
	}
	
	//handle account deletion
	public static void deleteAcct() {
		if(privilege.equals("ATM")) {
			System.out.println("You do not have the privilege. Unacceptable command.");
		} else {
			//get the account number
			System.out.print("Enter account number: ");
			String acct = scanner.nextLine();
			
			while (!acct.equals("cancel") && !TestFrontEnd.testExistingAccount(acct)) {
				System.out.println("Account does not exist, please enter an existing account number or type 'cancel'.");
				acct = scanner.nextLine();
			}
			
			if (!acct.equals("cancel")) {
				//get the account name
				System.out.print("Enter account name: ");
				String name = scanner.nextLine();
				
				while (!name.equals("cancel") && !TestFrontEnd.testValidAccountName(name)) {
					name = scanner.nextLine();
				}
			
				//if all is successful, write to transaction summary file
				if(!name.equals("cancel") && !acct.equals("cancel")) {
					//writeToTransFile()
					writeToTransFile("DEL", acct, "000", "0000000", name);
					
					//remove from accts file
					removeFromAcctFile(acct);
				}
			
			}
		}
	}
	
	//handles all writing to the transaction summary file
	public static void writeToTransFile(String code, String to, String amount, String from, String name) {
		try {
			while(amount.length() < 3) {
				amount = "0"+amount;
			}
			
			FileWriter fileWriter = new FileWriter(Main.transFile, true);
			PrintWriter printWriter = new PrintWriter(fileWriter);

			printWriter.printf("%s %s %s %s %s%n", code, to, amount, from, name);
			
			printWriter.close();
		} catch (IOException e) {
			System.out.println("Can not find transaction summary file");
		}
	}
	
	
	//will change to add to*******************************
	//handles writing to the valid accounts list file
	public static void writeToAcctFile(String[] list) {
		try {
			FileWriter fileWriter = new FileWriter(Main.accountsFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			
			for(int i = 0; i < list.length ; i++) {
				printWriter.printf("%s%n", list[i]);
			}

			printWriter.close();
		} catch (IOException e) {
			System.out.println("Can not find valid accounts list file.");
		}
	}
	
	//prepares an account to be added to the accounts file list
		public static void addToAcctFile(String num) {
			try {
				FileReader fileReader1 = new FileReader(Main.accountsFile);
				FileReader fileReader2 = new FileReader(Main.accountsFile);
				BufferedReader findLength = new BufferedReader(fileReader1);
				BufferedReader bufferedReader = new BufferedReader(fileReader2);
				
				String line;
				int i;
				//just to find the length of the file 
				for(i = 0 ; (line = findLength.readLine()) != null ; i++){}
				
				String[] toOut = new String[i+1];
				toOut[0] = num;
				for (int n = 1; (line = bufferedReader.readLine()) != null ; n++) {
					toOut[n] = line;
				}
				
				findLength.close();
				bufferedReader.close();
					
				writeToAcctFile(toOut);
			} catch (IOException e) {
				System.out.println("Can not find valid accounts list file.");
			}
		}
		
		//prepares an account to be removed from the accounts file
		public static void removeFromAcctFile(String num) {
			try {
				
				//read the file to find the length, and then again to set each line
				FileReader fileReader1 = new FileReader(Main.accountsFile);
				FileReader fileReader2 = new FileReader(Main.accountsFile);
				BufferedReader findLength = new BufferedReader(fileReader1);
				BufferedReader bufferedReader = new BufferedReader(fileReader2);
				
				String line;
				int i;
				int indexToRemove = 0;
				for(i = 0 ; (line = findLength.readLine()) != null ; i++){
					
					if (line.equals(num)) {
						indexToRemove = i;
					}
				}
				
				String[] toOut = new String[i-1];
				
				for (int n = 0; (line = bufferedReader.readLine()) != null ; n++) {
					if (n == indexToRemove) {
						//when n is equal to the index to remove do not add to the array
					} else if (n < indexToRemove){
						toOut[n] = line;
					} else {
						toOut[n-1] = line;
					}
				}
				
				findLength.close();
				bufferedReader.close();
					
				writeToAcctFile(toOut);
			} catch (IOException e) {
				System.out.println("Can not find valid accounts list file.");
			}
		}
}
