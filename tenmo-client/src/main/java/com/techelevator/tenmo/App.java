package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final ConsoleService consoleService = new ConsoleService();
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private Account currentUserAccount;

    private final int APPROVED_TRANSFER_ID = 2;
    private final int SEND_TRANSFER_TYPE = 2;
    private final int REQ_TRANSFER_TYPE = 1;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";



    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                System.out.println("/n" + "▀█▀ █░█ ▄▀█ █▄░█ █▄▀ █▀   █▀▀ █▀█ █▀█   █░█ █▀ █ █▄░█ █▀▀   ▀█▀ █▀▀ █▄░█ █▀▄▀█ █▀█ █\n" +
                        "  ░█░ █▀█ █▀█ █░▀█ █░█ ▄█   █▀░ █▄█ █▀▄   █▄█ ▄█ █ █░▀█ █▄█   ░█░ ██▄ █░▀█ █░▀░█ █▄█ ▄ ");
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }


    private void viewCurrentBalance() {
        Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser);
        System.out.println("Your current balance is: $" + currentAccount.getBalance());
    }


    private void viewTransferHistory() {
        //sets current user
        Account userAccount = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser);
        Account otherAccount;
        String sUser = null;
        String rUser= null;

        // userAccount.getAccountId is getting the account id (id is an int)
        Transfer[] transfers = transferService.getAllTransfers(currentUser, userAccount.getAccountId());

        //display text
        System.out.println("********************************************************************");
        System.out.println("                         TRANSFERS           ");
        System.out.println("Transfer ID:              From/To:                   Amount:");
        System.out.println("********************************************************************");

        //for loop for all transfers
        for(Transfer transfer : transfers){
            //if user account matches current account
            if(transfer.getAccountFrom() == userAccount.getAccountId() || transfer.getAccountTo() == userAccount.getAccountId()){
                Account fromUser = accountService.getAccountByAccountId(currentUser, transfer.getAccountFrom());
                Account toUser = accountService.getAccountByAccountId(currentUser, transfer.getAccountTo());

                String sending = userService.getUserByUserId(currentUser, fromUser.getUserId()).getUsername();
                String receive = userService.getUserByUserId(currentUser, toUser.getUserId()).getUsername();

               sUser = sending;// currentUser.getUser().getUsername();
               int reqAccountId = transfer.getAccountTo();
               Account receiveAccount = accountService.getAccountByAccountId(currentUser, reqAccountId);
               int temp = receiveAccount.getUserId();
               rUser =  receive;// userService.getUserByUserId(currentUser, temp).getUsername();

                System.out.println(transfer.getTransferId() + "        From: " + sending + " To: " + receive +  "     " +   '$' + transfer.getAmount());
            }
        }
        //display option to allow user to cancel or not
        System.out.println();
        int userInput = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (userInput == 0) {
            System.out.println("Transfer details view has been cancelled");
        } else { //display details of transfer
            Transfer transfer = transferService.getTransferByTransferId(currentUser, userInput);
            System.out.println("*********************************************************");
            System.out.println("                  TRANSFER DETAILS                       ");
            System.out.println("*********************************************************");
            System.out.println();
            System.out.println("Transfer ID: " + transfer.getTransferId());
            System.out.println("From: " +  sUser);
            System.out.println("To: " + rUser);
            System.out.println("Type: " + transfer.getTypeById());
            System.out.println("Status: " + transfer.getStatusById());
            System.out.println("Amount: " + transfer.getAmount());
        }
    }


    private void viewPendingRequests() {
        Account userAccount = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser);
        long userAcctId = userAccount.getAccountId();
        Transfer[] transfers = transferService.getAllTransfers(currentUser, userAcctId);
        //print out to console
        System.out.println("*****************************************************");
        System.out.println("                 PENDING TRANSFERS                   ");
        System.out.println("*****************************************************");
        System.out.println();
        System.out.println("     ID:              To:              Amount");
        System.out.println("*****************************************************");
        System.out.println();
        //loop through transfers, then pull pending transfers to user
        for (Transfer transfer : transfers) {
            if (transfer.getAccountTo() == userAcctId && transfer.getTransferId() == 1) {
                String username = userService.getUserByUserId(currentUser, userAccount.getUserId()).getUsername();
                System.out.println("         To: " + username + "         " + '$' + transfer.getAmount());
            }
        }
        //display option to cancel view of pending requests
        System.out.println();
        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel)");
        if (transferId == 0) {  //Todo: it breaks around here T.T , I think at 169 or 172 when it calls getTransferByTransferId
            System.out.println("Transfer details view has been cancelled");
        } else { //show transfer details
            Transfer transfer = transferService.getTransferByTransferId(currentUser, transferId);
            System.out.println();
            System.out.println("--------------------------------------------------------------");
            System.out.println("Pending Transfer Details");
            System.out.println("--------------------------------------------------------------");
            System.out.println();
            System.out.println("ID: " + transfer.getTransferId());
            System.out.println("From: " + userService.getUserByUserId(currentUser, transfer.getAccountFrom()).getUsername());
            System.out.println("To: " + userService.getUserByUserId(currentUser, transfer.getAccountTo()).getUsername() );
            System.out.println("Type: " + transferService.getTransferTypeByTransferTypeId(currentUser, transfer.getTransferTypeId()).getTransferTypeId());
            System.out.println("Status: " + transferService.getTransferStatusByTransferStatusId(currentUser, transfer.getTransferStatusId()).getTransferStatusId());
            System.out.println("Amount: " + "$" + transfer.getAmount());
            System.out.println();

            //get user input for pending transfer ID
            int pendingTransferInput = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
            while (pendingTransferInput != transferId && pendingTransferInput != 0) {
                pendingTransferInput = consoleService.promptForInt("Invalid value. Please enter transfer ID to approve/reject (0 to cancel): ");
            }
            if (pendingTransferInput == 0) { //cancel
                System.out.println("Pending transfer details cancelled");
            } else { //display
                System.out.println();
                System.out.println("1: Approve");
                System.out.println("2: Reject");
                System.out.println("0: Cancel");
                System.out.println();

                //get input from user to accept, reject, or cancel
                int approveOrRejectInput = consoleService.promptForInt("Please enter choice to approve/reject (0 to cancel): ");
                //does not complete if insufficient funds
                if (transfer.getAmount().compareTo(accountService.getBalance(currentUser)) > 0) {
                    approveOrRejectInput = 0;
                    System.out.println("Insufficient funds");
                }
                //does not approve or reject transfer
                if (approveOrRejectInput == 0) {
                    System.out.println("Request pending");
                }
                //approve transfer
                if (approveOrRejectInput == 1) {
                    transferService.approveTransfer(currentUser, transfer);
                    System.out.println("Transfer request approved!");
                }
                //reject transfer
                if (approveOrRejectInput == 2) {
                    transferService.rejectTransfer(currentUser, transfer);
                    System.out.println("Transfer request rejected!");
                }
            }
        }
    }

	private void sendBucks() {
		// TODO Auto-generated method stub
        currentUserAccount = accountService.getAccountByUserId(currentUser.getUser().getId(), currentUser);
        List<User> userList = userService.getAllUsers(currentUser);
        getUserList();
        Transfer sendTransfer = new Transfer();
        sendTransfer.setAccountFrom(currentUserAccount.getAccountId());
        sendTransfer.setTransferStatusId(APPROVED_TRANSFER_ID);
        sendTransfer.setTransferTypeId(SEND_TRANSFER_TYPE);

        int userInput = 0;
        try {
            int userSelection = consoleService.promptForInt("Select user to receive funds or [0] to exit: ");
            while (true) {
                if (userSelection == 0) {
                    break;
                }
                if (userSelection <= userList.size()) {
                    if(userList.get(userSelection -1). getId() == currentUser.getUser().getId()) {
                        throw new IllegalArgumentException( ANSI_RED + "Invalid selection, please select a user that is not yourself" + ANSI_RESET);
                    }

                    //Get id for receiving account.
                    int sendMoneyToUser = userList.get(userSelection - 1).getId();
                    Account recipientAccount = accountService.getAccountByUserId(sendMoneyToUser, currentUser);
                    sendTransfer.setAccountTo(recipientAccount.getAccountId());

                    System.out.println("Account balance: " + (currentUserAccount.getBalance()));
                    BigDecimal moneyToSend = consoleService.promptForBigDecimal("Enter Your Funds: ");

                    if (moneyToSend.compareTo(currentUserAccount.getBalance()) > 0) {
                        System.out.println("Insufficient funds");

                    } else if (moneyToSend.compareTo(BigDecimal.ZERO) <= 0) {
                        System.out.println("Must send a positive number more than $0.00");
                    } else {
                        //Attempts to send money from current account to selected account.
                        //removed the 1 that was making it a type id
                        sendTransfer.setAmount(moneyToSend);
                        transferService.transferMoney(sendTransfer, currentUser);
                        transferService.executeTransfer(sendTransfer, currentUser);

                        System.out.println("You sent: " + moneyToSend + " TE bucks to " + userList.get(userSelection - 1).getUsername());
                    }
                }
                break;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please select a valid user");
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
        }
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}


    public List<User> getUserList() {
        List<User> userList = userService.getAllUsers(currentUser);
        int counter = 0;
        for (User user : userList) {
            counter++;
            System.out.println(counter + " : " + user.getUsername());
        }
        return userList;
    }


}