package cisc327BackEnd;

import java.util.Scanner;
import java.io.*;

public class Main {

	//Back End takes in 4 arguments: Master Accounts File, Merged Transaction Summary File, 
	// new master accounts file, new valid accounts file
	public static void main(String[] args) {
		
		String[][] oldMasterAccounts = getOldAccounts(args[0]);
		String[][] transSummary = getTransSummary(args[1]);

		readTransSum(transSummary, oldMasterAccounts);
		String[][] newMasterAccounts = getNewMasterAccounts(oldMasterAccounts);
		writeToFiles(newMasterAccounts, args[2], args[3]);
		
	}
	
	public static void writeToFiles(String[][] newMasterAccounts, String masterAccountsFile, String validAccountsFile) {
		try {
			FileWriter masterWriter = new FileWriter(masterAccountsFile);
			FileWriter validWriter = new FileWriter(validAccountsFile);
			
			PrintWriter masterPrinter = new PrintWriter(masterWriter);
			PrintWriter validPrinter = new PrintWriter(validWriter);
			
			for (int i = 0; i < newMasterAccounts.length; i++) {
				masterPrinter.printf("%s %s %s%n", newMasterAccounts[i][0], newMasterAccounts[i][1], newMasterAccounts[i][2]);
				validPrinter.printf("%s%n", newMasterAccounts[i][0]);
			}
			
			validPrinter.printf("%s", "0000000");
			
			masterPrinter.close();
			validPrinter.close();
		} catch (IOException e) {
			System.out.println("could not find either the new masteraccounts file or the new valid accounts file.");
			System.out.println("=============== nothing was printed to the new files. =======================");
		}
	}
	
	public static String[][] getNewMasterAccounts(String[][] oldMasterAccounts) {
		//get size of oldMasterAccounts ---- note not to include deleted accounts
		int size = 0;
		for(int i = 0; i < oldMasterAccounts.length; i++) {
			if (oldMasterAccounts[i][0] != null )
				size++;
		}
		//get size of BackEnd.toCreate
		for(int i = 0; i < BackEnd.toCreate.size(); i++)
			size++;
		
		String[][] newMasterAccounts = new String[size][3];
		
		//creates an array of the numbers
		Object[] toCreate = BackEnd.toCreate.keySet().toArray();
		
		for(int i = 0; i < toCreate.length; i++) {
			System.out.println("Creating: ");
			System.out.println(toCreate[i]);
		}
		//sort both --------- oldMasterAccounts is already sorted, could use a function to grab the lowest key value
		
		int oldIterator = 0;
		for(int i = 0; i < newMasterAccounts.length; i++) {
			
			//gets the lowest account number from the accounts to be created
			int lowestNew = getLowest(toCreate);
			int lowestOld = 10000000; //an impossibly high value
			
			//if there are still values in oldMasterAccounts that have not been read then lowestOld will be set to a smaller value
			if (oldIterator < oldMasterAccounts.length) {
				lowestOld = Integer.parseInt(oldMasterAccounts[oldIterator][0]);	
			} 
			
			//add to the array in an ascending order
			if(lowestOld < lowestNew) {
				newMasterAccounts[i][0] = Integer.toString(lowestOld);
				newMasterAccounts[i][1] = oldMasterAccounts[oldIterator][1];
				newMasterAccounts[i][2] = oldMasterAccounts[oldIterator][2];
				oldIterator++;
			} else {
				newMasterAccounts[i][0] = Integer.toString(lowestNew);
				newMasterAccounts[i][1] = "0";	//a new account has a balance of 0
				newMasterAccounts[i][2] = (String)BackEnd.toCreate.get(lowestNew);
			}
		}
		
		return newMasterAccounts;
	}
	
	public void removeFromArray(Object[] fullArray, int toRemove) {
		for (int i = 0; i < fullArray.length; i++) {
			if (toRemove == Integer.parseInt(fullArray[i].toString())) {
				fullArray[i] = 10000000; //set it to an impossibly high value so that it does not get grabbed by 'getLowest'
			}
		}
	}
	
	public static int getLowest(Object[] arr) {
		int lowest = 10000000; //this is a larger number than any possible account number, so this will ALWAYS be overwritten
		int index = 0;
		int toCompare;
		for(int i = 0; i<arr.length; i++) {
			
		
			//ran into a ClassCastException error
			toCompare = Integer.parseInt(arr[i].toString());
						
			if(toCompare < lowest) {
				//save the lowest value
				lowest = toCompare;
				index = i;
			}
		}
		return lowest;
	}
	
	//reads the transaction summary one line at a time
	public static void readTransSum(String[][] transSum, String[][] oldMasterAccounts) {
		for(int i = 0; i < transSum.length; i++) {
			BackEnd.decide(transSum[i], oldMasterAccounts);
		}
	}
	
	public static String[][] getOldAccounts(String fileName) {
		File file = new File(fileName);
		Scanner lengthFinder;
		Scanner reader;
		try {
			lengthFinder = new Scanner(file);
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find old master accounts file. Should be the first argument.");
			System.exit(1);
			
			//====================================================================================================
			//this code NEVER runs, just to get eclipse to not recognize errors below, should remove at some point
			//====================================================================================================
			lengthFinder = new Scanner(System.in);
			reader = new Scanner(System.in);
		}
		
		int length = 0;
		String line;
		while(lengthFinder.hasNextLine()) {
			length++;
			line = lengthFinder.nextLine();
		}
		
		//instantiate the accounts array with the length of the old master accounts file
		String[][] accountsArray = new String[length][3];
		String[] split;
		
		for(int i = 0; reader.hasNextLine(); i++) {
			
			line = reader.nextLine();
			//splits the line into each part: [0] = acctNum, [1] = balance, [2] = name
			split = line.split(" ");
			
			//fills the accountsArray
			//n < 3 because there are 3 values in each line in the master accounts file
			for(int n = 0; n < 3; n++) {
				accountsArray[i][n] = split[n];
			}
		}
		
		//remove potential memory leaks
		lengthFinder.close();
		reader.close();
		
		return accountsArray;
	}
	
	public static String[][] getTransSummary(String fileName){
		File file = new File(fileName);
		Scanner lengthFinder;
		Scanner reader;
		try {
			lengthFinder = new Scanner(file);
			reader = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("Could not find transaction summary file. Should be the second argument.");
			System.exit(1);
			
			//====================================================================================================
			//this code NEVER runs, just to get eclipse to not recognize errors below, should remove at some point
			//====================================================================================================
			lengthFinder = new Scanner(System.in);
			reader = new Scanner(System.in);
		}
		
		int length = 0;
		String line;
		
		while(lengthFinder.hasNextLine()) {
			length++;
			line = lengthFinder.nextLine();
		}
		
		//instantiate the accounts array with the length of the old master accounts file
		String[][] transactionArray = new String[length][5];
		String[] split;
		
		for(int i = 0; reader.hasNextLine(); i++) {
			line = reader.nextLine();
			//splits the line into each part: [0] = acctNum, [1] = balance, [2] = name
			split = line.split(" ");
			
			//fills the accountsArray 
			//n < 5 because there are 5 values in each line in the transaction summary file
			for(int n = 0; n < 5; n++) {
				transactionArray[i][n] = split[n];
			}
		}
		
		//remove potential memory leaks
		lengthFinder.close();
		reader.close();
		
		return transactionArray;
	}
	
	static String newMasterAccounts;
	static String newValidAccounts;
	
	static Scanner scanner;
}
