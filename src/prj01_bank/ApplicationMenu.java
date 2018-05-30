/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj01_bank;


import java.sql.Connection;
import java.util.Scanner;

/**
 *
 * @author Despoina
 */
public class ApplicationMenu {
    
    public Connection con=DatabaseConnection.connectDb();
    
    public void adminAccount(String user) {
        
        BankAccounts bank=new BankAccounts(user);
        FilesAccess fa=new FilesAccess();
        
        Scanner admin = new Scanner(System.in);
        
        System.out.println("You have the following options :");
        System.out.println("1.View your bank account.");
        System.out.println("2.View a member's bank account.");
        System.out.println("3.Deposit to member’s bank account.");
        System.out.println("4.Withdraw from a member's bank account.");
        System.out.println("5.Send today's transactions to txt file.");
        System.out.println("Press a key from 1 to 5 to continue: ");
        
        while(!admin.hasNextInt()){
            System.out.println("Illegal entry. Please try again!");
            admin.next();
        }
        int adminChoice=admin.nextInt();
        switch(adminChoice)
        {
            case 1: bank.viewInternalAdminAccount(user);
                    break;
            case 2: bank.viewMemberAccount(user);
                    break;
            case 3: bank.depositToMember(user);
                    break;
            case 4: bank.withdrawFromMember(user);
                    break;
            case 5: fa.fileAccess(user);
                    bank.nextTransaction(user);
                    break;
            default:System.out.println("Invalid choice. Please try again !");
                    adminAccount(user);         
        }
    }   
    
    public void userAccount(String user){
            
        BankAccounts bank=new BankAccounts(user);
        FilesAccess fa=new FilesAccess();
        
        Scanner users=new Scanner(System.in);
        
        System.out.println("You have the following options :");
        System.out.println("1.View your internal bank account.");
        System.out.println("2.Deposit to Cooperative’s bank account");
        System.out.println("3.Deposit to another member’s bank account");
        System.out.println("4.Send today's transactions to txt file");
        System.out.println("Press a key from 1 to 4");
        
        while(!users.hasNextInt()){
            System.out.println("Illegal entry. Please try again!");
            users.next();
        }
        int userChoice=users.nextInt();
        switch(userChoice)
        {
            case 1: bank.viewInternalUserAccount(user);
                    break;
            case 2: bank.depositToCooperative(user);
                    break;
            case 3: bank.depositToOtherMember(user);
                    break;
            case 4: fa.fileAccess(user);
                    bank.nextTransaction(user);
                    break;
            default:System.out.println("Invalid choice");
                    userAccount(user);         
        }
    }
}
