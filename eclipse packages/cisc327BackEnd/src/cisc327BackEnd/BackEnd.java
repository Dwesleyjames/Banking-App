package cisc327BackEnd;

import java.util.*;

public class BackEnd {

	//takes a string array that contains a transaction and decides what to do with it
	public static void decide(String[] transaction, String[][] oldMasterAccounts) {
			
		switch (transaction[0]) {
			case("EOS"):
				break;
			case("NEW"):
				createAccount(transaction, oldMasterAccounts);
				break;
			case("DEL"):
				deleteAccount(transaction, oldMasterAccounts);
				break;
			case("DEP"):
				deposit(transaction, oldMasterAccounts);
				break;
			case("XFR"):
				transfer(transaction, oldMasterAccounts);
				break;
			case("WDR"):
				withdraw(transaction, oldMasterAccounts);
				break;
			}
		}
		
	//adds appropriate new accounts to a hashmap toCreate
	public static void createAccount(String[] transaction, String[][] oldMasterAccounts) {
		
		String acctNum = transaction[1];
		String acctName = transaction[4];
		
		if (acctName.length() < 3) {
			printTransaction(transaction, "Creation error, account name is less than 3 characters.");
		} else {
			//if the account number does NOT already exist then...
			if (!containsAccount(acctNum, oldMasterAccounts) && !toCreate.containsKey(acctNum)) {
				toCreate.put(acctNum, acctName);
			} else {
				//else it does already exist, log the transaction error
				printTransaction(transaction, "Creation error, account number already exists.");
			}
		}
		
	}
	
	//deletes an account
	public static void deleteAccount(String[] transaction, String[][] oldMasterAccounts) {
		//account number and name to be removed
		String acctNum = transaction[1];
		String acctName = transaction[4];
		
		String[] account = findAccount(acctNum, oldMasterAccounts);
		
		if(Integer.parseInt(account[1]) == 0) {
			//then we can delete
			
			if(acctName.equals(account[2])) {
				account[0] = null;
				account[1] = null;
				account[2] = null;
			} else {
				printTransaction(transaction, "Deletion error, mismatch is account names.");
			}	
		} else {
			printTransaction(transaction, "Deletion error, cannot delete an account with a nonzero balance.");
		}
		
		if(acctName.equals(account[2])) {
			//then delete the account
			//=====================================================================================
			//I don't know whether this is supposed to delete the account or set the balance to 0?
			//=====================================================================================
			account[0] = null;
			account[1] = null;
			account[2] = null;
		} else {
			System.out.println("Tried to delete an existing account, but with the wrong account name. Did not delete.");
		}	
	}

	//deposits money into an account
	public static void deposit(String[] transaction, String[][] oldMasterAccounts) {
		String acctNum = transaction[1];
		String amount = transaction[2];
		
		String[] account = findAccount(acctNum, oldMasterAccounts);
		
		//adds the current balance and the added amount
		int accountTotal = Integer.parseInt(account[1]) + Integer.parseInt(amount);
		
		account[1] = Integer.toString(accountTotal);
	}
	
	//withdraws money from an account
	public static void withdraw(String[] transaction, String[][] oldMasterAccounts) {
		String acctNum = transaction[1];
		String amount = transaction[2];

		String[] account = findAccount(acctNum, oldMasterAccounts);
		
		if(Integer.parseInt(amount) > 0) { 
		
			if (canWithdraw(transaction, oldMasterAccounts)) {
				int accountTotal = Integer.parseInt(account[1]) - Integer.parseInt(amount);
				account[1] = Integer.toString(accountTotal);
			} else {
				printTransaction(transaction, "Withdraw error, cannot have an account balance < 0.");
			}
		} else {
			printTransaction(transaction, "Withdraw error, cannot have a negative withdrawal amount.");
		}
	}
	
	public static boolean canWithdraw(String[] transaction, String[][] oldMasterAccounts) {
		int toWithdraw = Integer.parseInt(transaction[2]);
		String acctNum = transaction[1];
		
		String[] account = findAccount(acctNum, oldMasterAccounts);
		
		int inAccount = Integer.parseInt(account[1]);
		int total = inAccount - toWithdraw;
		
		if(total > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	//transfers money between two accounts
	public static void transfer(String[] transaction, String[][] oldMasterAccounts) {
		String fromAcctNum = transaction[3];
		String amount = transaction[2];
		
		//normally all transactions have a length of 5, but withdraw only actually needs to access values at index 1 and 2
		String[] tempTransaction = {null, fromAcctNum, amount};
		
		//if the account has enough funds to be taken out
		if (canWithdraw(transaction, oldMasterAccounts)) {
			//withdraw from one account
			withdraw(tempTransaction, oldMasterAccounts);
			
			
			//if that went through without an error, deposit into the other
			//we don't need a temporary transaction here because the 'to account' is already in the correct spot in the transaction
			deposit(transaction, oldMasterAccounts);
		} else {
			printTransaction(transaction, "Transfer error, 'from' account does not have sufficient funds.");
		}
	}

	public static String[] findAccount(String num, String[][] oldMasterAccounts) {
		for(int i = 0; true; i++) {
			
			//this is more of a WHEN not IF situation
			if(num.equals(oldMasterAccounts[i][0])) {
				return oldMasterAccounts[i];
			}
		}
	}
	
	public static boolean containsAccount(String num, String[][] oldMasterAccounts) {
		for(int i = 0; i < oldMasterAccounts.length; i++) {
			if(num.equals(oldMasterAccounts[i][0])) {
				return true;
			}
		}
		return false;
	}
	
	public static void printTransaction(String[] transaction, String errMessage) {
		System.out.println(errMessage);
		System.out.println("Error on transaction: ");
		
		for(int i = 0; i < transaction.length; i++) {
			System.out.print(transaction[i] + "       ");
		}
	}
	
	static Map toCreate = new HashMap();
}
