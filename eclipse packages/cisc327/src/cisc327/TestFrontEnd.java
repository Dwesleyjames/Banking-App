package cisc327;


import java.io.*;

public class TestFrontEnd {
	
	//test to see if the given amount is valid for transfering or deposits
	public static boolean testAmount(String amount) {
		int isNum = 0;
		try { 
			isNum = Integer.parseInt(amount);
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number or type 'cancel'.");
			return false;
		}
		
		//check if the user is in atm mode
		if(FrontEnd.privilege.equals("ATM")) {
			if (isNum > 0 && isNum <= 100000) {
				return true;
			} else {
				System.out.println("Please enter a number between 1 and 100000 or type 'cancel'.");
				return false;
			}
		
		//else the user is in agent mode
		} else {
			if (isNum > 0 && isNum <= 99999999) {
				return true;
			} else {
				System.out.println("Please enter a number between 1 and 99999999 or type 'cancel'.");
				return false;
			}
		}
	}
	
	//test to see if an account number is valid
	public static boolean testValidAccount(String acctNum) {
		//valid account numbers are 7 digits, not starting with 0
		if(acctNum.length() == 7 && acctNum.charAt(0) != '0') {
			try {
				//checks if acctNum is a number
				int canCast = Integer.parseInt(acctNum);
				return true;
			} catch (NumberFormatException e){
				System.out.println("could not cast acctNum as an int");
			}
		}
		System.out.println("Account is invalid number. Account number must be 7 digits, not starting with 0.");
		return false;
	}
	
	//test whether the account name is valid
	public static boolean testValidAccountName(String acctName) {
		//valid account names are 3-30 characters, not starting or ending with a space
		
		//test for valid length
		if (acctName.length() >= 3 && acctName.length() <= 30) {
			
			//test for no spaces at the start or end
			if(acctName.charAt(0) != ' ' && acctName.charAt(acctName.length() - 1) != ' ') {
				return true;
			} 
		}
		System.out.println("Please enter an account name with 3-30 characters in length and not begin or end with a space or type 'cancel'.");
		return false;
	}
	
	//account names are only kept in the master accounts file, NOT the valid accounts list file, so this will not be used until
	//the back end work begins
	//test whether the account name already exists
	public static boolean testExistingAccountName(String acctName) {
		String line;
		try {
			FileReader fileReader = new FileReader(Main.accountsFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null) {
				if (line == acctName) {
					bufferedReader.close();
					return true;
				}
			}
			
			bufferedReader.close();
			return false;
			
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND - test existing account name");
			return false;
		}
	}
	
	public static boolean testExistingAccount(String acctNum) {
		String line;
		try {
			FileReader fileReader = new FileReader(Main.accountsFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null) {
				if (acctNum.equals(line)) {
					bufferedReader.close();
					return true;
				}
			}
			
			bufferedReader.close();
			return false;
			
		} catch (IOException e) {
			System.out.println("FILE NOT FOUND - test existing account ");
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	//test to see if the given amount is valid for specifically withdrawing (because the total amount in one session matters)
	public static boolean testWithdrawAmount(String amount, String acct) {
		System.out.println("testwithdraw amount");
		//if the user is in ATM mode
		if(FrontEnd.privilege.equals("ATM")) {
			try {
				System.out.println("amount");
				int a = Integer.parseInt(amount);
				
				if(!FrontEnd.totalWithdrawn.containsKey(acct)) {
					FrontEnd.totalWithdrawn.put(acct, 0);
				}
				
				if (a <= 100000 && a > 0 && a+(int)FrontEnd.totalWithdrawn.get(acct) <= 100000) {
					FrontEnd.totalWithdrawn.put(acct, (int)FrontEnd.totalWithdrawn.get(acct)+a);
					return true;
				} else {
					System.out.println("Please enter a number between 1 and 100000, with your total withdrawn this session less than or equal to 100000. Type 'cancel' to cancel.");
					return false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number between 1 and 100000 or type 'cancel'.");
				return false;
			}
			
		//else the user is in agent mode
		} else {
			try {
				int a = Integer.parseInt(amount);
				if (a <= 99999999 && a > 0) {
					return true;
				} else {
					System.out.println("Please enter a number between 1 and 99999999 or type 'cancel' to cancel.");
					return false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number between 1 and 99999999 or type 'cancel'.");
				return false;
			}
		}
	}
	
	public static boolean checkPrivilege() {
		if(FrontEnd.privilege == null) {
			System.out.println("Must enter ATM or agent first");
			return false;
		} else {
			return true;
		}
	}
}
