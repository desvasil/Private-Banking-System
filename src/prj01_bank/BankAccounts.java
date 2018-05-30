/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj01_bank;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


/**
 *
 * @author Despoina
 */
public class BankAccounts {
    
    public Connection con=DatabaseConnection.connectDb();
    private ApplicationMenu appMenu=new ApplicationMenu();
    private String user;
    private String bankTransaction;
    static ArrayList<String> arraylist=new ArrayList<>();
    private Date date=new Date();

    public BankAccounts(ArrayList<String> arraylist) {
        BankAccounts.arraylist = arraylist;
    }

    public BankAccounts(String user) {
        this.user = user;
    }

    
    private void addTransaction(String bankTransaction){
        this.bankTransaction=bankTransaction;
        arraylist.add(bankTransaction);
    }
    
    protected void viewInternalAdminAccount(String user) {
        
        //balance is the balance of the account that logged in.
        double balance=0 ;
        try {
            String query="select accounts.amount"+ 
                            " from accounts,users"+
                                " where accounts.id=users.id"+
                                    " and users.username= ?";
            PreparedStatement stm=con.prepareStatement(query);
            stm.setString(1,user);
            stm.executeQuery();
            ResultSet result=stm.getResultSet();
            while(result.next()){
                balance=result.getDouble("amount");
                System.out.println("Your account has: "+balance+" € ");
            }
            bankTransaction= "-"+user+" viewed his account at :"+date.toString()+".";
        } catch (SQLException ex){
            System.out.println(ex);
        }
        addTransaction(bankTransaction);
        nextTransaction(user);
    }
    
    protected void viewMemberAccount(String user) {
        
        //memberBalance, viewmember are the balance and the account id of the member you want to view respectively.
        double memberBalance=0;
        Scanner memberAccount=new Scanner(System.in);
        getUserIds(user);
        System.out.println("Choose which account you want to view");
        while(!memberAccount.hasNextInt()){
            System.out.println("Illegal entry. Please try again!");
            memberAccount.next();
        }
        int viewMember=memberAccount.nextInt(); 
        
        try {
            String query="select accounts.amount"+ 
                    " from accounts,users"+
                        " where accounts.id=users.id "+
                            " and users.id= ?";
            PreparedStatement stm=con.prepareStatement(query);
            stm.setInt(1,viewMember);
            stm.executeQuery();
            ResultSet result=stm.getResultSet();
            while(result.next()){
                memberBalance=result.getDouble("amount");
                System.out.println("The account with id "+viewMember+" has: "+memberBalance+" €");
            }
            bankTransaction= "-"+user+" viewed account with id "+viewMember+" at : "+date.toString()+".";
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        addTransaction(bankTransaction);
        nextTransaction(user);
    }
    
    protected void depositToMember(String user) {
        
    /*  cash is the amount you want to withdraw, balWith is the balance of the account 
     *  you withdraw from, newBalWith is the balance of the account you withdrew from, 
     *  balDep is the balance of the account you deposit to, 
     *  newBalDep is the balance after the deposit 
     */    
        
        double cash,balWith,newBalWith,balDep,newBalDep;                                 
        balWith=0;
        balDep=0;
                                                                    
        Scanner deposit=new Scanner(System.in);
        System.out.println("Enter amount you want to deposit: ");
        while(!deposit.hasNextDouble()){
            System.out.println("Illegal entry.Please try again.");
            deposit.next();
        }
        cash=deposit.nextDouble();
        
        if(cash<=0){
            System.out.println("Please enter a valid amount.");
            depositToMember(user);
        }
        else{
            try {
                // accountToWithdrawFrom is the account id of the account the user chooses to withdraw from.
                int accountToWithdrawFrom;
                Scanner withdraw=new Scanner(System.in);
                System.out.println("Choose the account you want to withdraw from :");
                getAllUserIds();
                while(!withdraw.hasNextInt()){
                    System.out.println("Illegal entry.Please try again.");
                    withdraw.next();
                }
                accountToWithdrawFrom=withdraw.nextInt();
                
                String query1="select accounts.amount"+   
                                " from accounts,users "+   
                                    " where accounts.id=users.id "+
                                        " and users.id= ?";
                PreparedStatement stm=con.prepareStatement(query1);
                stm.setInt(1, accountToWithdrawFrom);
                stm.executeQuery();
                ResultSet result1=stm.getResultSet();
                while(result1.next()){
                    balWith=result1.getDouble("amount");
                    System.out.println("Account with id  "+accountToWithdrawFrom+" has: "+balWith+" €");
                }
                //accountToDepositTo is the account id of the account the user chooses to deposit to.
                int accountToDepositTo;
                if (balWith<=cash){
                    System.out.println("Account with id  "+accountToWithdrawFrom+" has not enough monney");
                    bankTransaction="-"+user+" tried to withdraw from account with id "+accountToWithdrawFrom+", "
                                        + cash+" € but did not have enough"; 
                }
                else{  
                    Scanner depositAccount=new Scanner(System.in);
                    System.out.println("Choose the account you want to deposit to :");
                    getUserIds(user);
                    while(!depositAccount.hasNextInt()){
                        System.out.println("Illegal entry.Please try again.");
                        depositAccount.next();
                    }
                    accountToDepositTo=depositAccount.nextInt();
                    
                    String query3="select accounts.amount "+   
                                    " from accounts,users "+   
                                        " where accounts.id=users.id "+
                                            " and users.id= ?";
                    PreparedStatement depstm=con.prepareStatement(query3);
                    depstm.setInt(1, accountToDepositTo);
                    depstm.executeQuery();
                    ResultSet result3=depstm.getResultSet();
                    while (result3.next()){
                        balDep=result3.getDouble("amount");
                    }
                    newBalWith=balWith-cash;
                    String query2="update accounts " +
                                    " inner join users on accounts.id=users.id" +
                                        " set accounts.amount="+newBalWith +
                                            " where " +
                                                " users.id= ?"; 
                    PreparedStatement upstm=con.prepareStatement(query2);
                    upstm.setInt(1, accountToWithdrawFrom);
                    upstm.executeUpdate();
                    System.out.println("New ballance of account with id "+accountToWithdrawFrom+" is "+newBalWith);
                    newBalDep=balDep+cash;
                    String query4="update accounts "+
                                    " inner join users on accounts.id=users.id " +
                                        " set accounts.amount= "+newBalDep+
                                            " where users.id= ?";
                    PreparedStatement updepstm=con.prepareStatement(query4);
                    updepstm.setInt(1,accountToDepositTo);
                    updepstm.executeUpdate();
                    System.out.println(user+" withdrew from account with id "+accountToWithdrawFrom+", "+ cash+" € "
                                        +"and deposited it to account with id "+
                                            accountToDepositTo);
                
                    bankTransaction= "-"+user+" withdrew from account with id "+accountToWithdrawFrom+", "+ cash+" € "
                                        +"and deposited it to account with id "+
                                            accountToDepositTo+" at: "+date.toString();
                }    
                } 
                catch (SQLException ex) {
                    System.out.println(ex);
                }
            }
            addTransaction(bankTransaction); 
            nextTransaction(user);
    }

    protected void withdrawFromMember(String user) {
        
        /*  cash is the amount you want to withdraw, balWith is the balance of the account 
         *  you withdraw from, newBalWith is the balance of the account you withdrew from, 
         *  balDep is the balance of the account you deposit to, 
         *  newBalDep is the balance after the deposit 
         */
        
        double cash,balWith,balDep,newBalDep;
        Scanner withdraw=new Scanner(System.in);
        System.out.println("Enter amount you want to withdraw: ");
        while(!withdraw.hasNextDouble()){
            System.out.println("Illegal entry.Please try again.");
            withdraw.next();
        }
        cash=withdraw.nextDouble();
        
        balWith=0;
        balDep=0;
        
    /* 
        accountToWithdrawFrom is the account id of the account the user chooses to withdraw from.
        accountToDepositTo is the account id of the account the user chooses to deposit to.
    */
        int accountToWithdrawFrom;
        Scanner withdrawAccount=new Scanner(System.in);
        System.out.println("Choose the account you want to withdraw from :");
        getUserIds(user);
        while(!withdrawAccount.hasNextInt()){
            System.out.println("Illegal entry.Please try again.");
            withdrawAccount.next();
        }
        accountToWithdrawFrom=withdrawAccount.nextInt();
       
        int accountToDepositTo;
        Scanner depositAccount=new Scanner(System.in);
        System.out.println("Choose the account you want to deposit to :");
        getAllUserIds();
        while(!depositAccount.hasNextInt()){
            System.out.println("Illegal entry.Please try again.");
            depositAccount.next();
        }
        accountToDepositTo=depositAccount.nextInt();
    
        if(cash<=0){
            System.out.println("Please enter a valid amount.");
            withdrawFromMember(user);
        }
        else{
            try {
                String query="select accounts.amount"+ 
                            " from accounts,users"+
                                " where accounts.id=users.id "+
                                    " and users.id= ?";
                PreparedStatement stm=con.prepareStatement(query);
                stm.setInt(1, accountToWithdrawFrom);
                stm.executeQuery();
                ResultSet result=stm.getResultSet();
                while(result.next()){
                    balWith=result.getDouble("amount");
                    System.out.println("Account id "+accountToWithdrawFrom+" has: "+balWith+" €");
                }
                if(balWith<=cash){
                    System.out.println("Account id "+accountToWithdrawFrom+" does not have enough money. ");
                    bankTransaction="-"+user+" tried to withdraw from account with id "+accountToWithdrawFrom+", "
                                        + cash+" € but did not have enough"; 
                }
                else{
                    balWith=balWith-cash;
                    query="update accounts " +
                            " inner join users on accounts.id=users.id" +
                                " set accounts.amount="+balWith +
                                 " where " +
                                    " users.id= ?"; 
                    PreparedStatement upstm=con.prepareStatement(query);
                    upstm.setInt(1, accountToWithdrawFrom);
                    upstm.executeUpdate();
                    System.out.println("New ballance of the account "+accountToWithdrawFrom+" is: "+balWith);
                
                    String query3="select accounts.amount "+   
                                    " from accounts,users "+   
                                        " where accounts.id=users.id "+
                                            " and users.id= ?";
                    PreparedStatement depstm = con.prepareStatement(query3);
                    depstm.setInt(1, accountToDepositTo);
                    depstm.executeQuery();
                    ResultSet result3=depstm.getResultSet();
                    while (result3.next()){
                        balDep=result3.getDouble("amount");
                    }
                    newBalDep=balDep+cash;
                    String query4="update accounts "+
                                    " inner join users on accounts.id=users.id " +
                                        " set accounts.amount= "+newBalDep+
                                            " where users.id= ?";
                    PreparedStatement updepstm;
                    updepstm = con.prepareStatement(query4);
                    updepstm.setInt(1,accountToDepositTo);
                    updepstm.executeUpdate();
                    System.out.println(user+" withdrew "+cash+" € from account with id "+accountToWithdrawFrom+
                                        " and deposited it to account with id "
                                            +accountToDepositTo);
                    bankTransaction="-"+user+" withdrew "+cash+" € from account with id "+accountToWithdrawFrom+
                                        " and deposited it to account with id "
                                            +accountToDepositTo+" at: "+date.toString();
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }
            }
            addTransaction(bankTransaction);
            nextTransaction(user);
    }
    
    protected  void viewInternalUserAccount(String user){
        
    //balance is the balance of the logged in user.
        try {
            String query="select accounts.amount"+ 
                            " from accounts,users"+
                                " where accounts.id=users.id"+
                                    " and users.username= ?";
            PreparedStatement stm=con.prepareStatement(query);
            stm.setString(1,user);
            stm.executeQuery();
            ResultSet result=stm.getResultSet();
            while(result.next()){
                double balance=result.getDouble("amount");
                System.out.println("Your account has: "+balance+" €");
            }
            bankTransaction="-"+user+" viewed his account at : "+date.toString();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        addTransaction(bankTransaction);
        nextTransaction(user);
    }
    
    protected void depositToCooperative(String user){
    
    /*  cash is the amount you want to withdraw, balWith is the balance of the account 
     *  you withdraw from, newBalWith is the balance of the account you withdrew from, 
     *  balDep is the balance of the account you deposit to, 
     *  newBalDep is the balance after the deposit 
     */    
        double cash,balWith,newBalWith,balDep,newBalDep;                                 
        balWith=0;
        balDep=0;
                                                                    
        Scanner deposit=new Scanner(System.in);
        System.out.println("Enter amount you want to deposit: ");
        while(!deposit.hasNextDouble()){
            System.out.println("Illegal entry.Please try again.");
            deposit.next();
        }
        cash=deposit.nextDouble();
        System.out.println("You want to deposit "+cash+" to Cooperative's account");
        
    // accountToDepositTo is the account id of the account the user chooses to deposit to      
        int accountToDepositTo=0;
        String adminAccount="SELECT users.id from users where users.username='admin'";
        try {
            PreparedStatement adminStm=con.prepareStatement(adminAccount);
            ResultSet results=adminStm.executeQuery(adminAccount);
            while (results.next()){
                accountToDepositTo=results.getInt("id");
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        if(cash<=0){
            System.out.println("Please enter a valid amount.");
            depositToCooperative(user);
        }
        else{
            try {
                String query1="select accounts.amount"+   
                                " from accounts,users "+   
                                    " where accounts.id=users.id "+
                                        " and users.username= ?";
                PreparedStatement stm=con.prepareStatement(query1);
                stm.setString(1, user);
                stm.executeQuery();
                ResultSet result1=stm.getResultSet();
                while(result1.next()){
                    balWith=result1.getDouble("amount");
                    System.out.println("Your account had: "+balWith+" €");
                }
                if(balWith<=cash){
                    System.out.println("Your account does not have enough money!");
                    bankTransaction="-"+user+" tried to withdraw from account with id "+adminAccount+", "
                                        + cash+" € but did not have enough"; 
                }
                else{
                    newBalWith=balWith-cash;
                    String query2="update accounts " +
                                    " inner join users on accounts.id=users.id" +
                                        " set accounts.amount="+newBalWith +
                                            " where " +
                                                " users.username= ?"; 
                    PreparedStatement upstm=con.prepareStatement(query2);
                    upstm.setString(1, user);
                    upstm.executeUpdate();
                    System.out.println("New ballance of your account is "+newBalWith);
                
                
                    String query3="select accounts.amount "+   
                                    " from accounts,users "+   
                                        " where accounts.id=users.id "+
                                            " and users.id= "+accountToDepositTo;
                    PreparedStatement depstm=con.prepareStatement(query3);
                    ResultSet result3=depstm.executeQuery(query3);
                    while (result3.next()){
                        balDep=result3.getDouble("amount");
                    }
                    newBalDep=balDep+cash;
                    String query4="update accounts "+
                                    " inner join users on accounts.id=users.id " +
                                        " set accounts.amount= "+newBalDep+
                                            " where users.id= "+accountToDepositTo;
                    PreparedStatement updepstm=con.prepareStatement(query4);
                    updepstm.executeUpdate();
                    bankTransaction="-"+user+" deposited "+cash+" to account with id "+accountToDepositTo+" at :"+date.toString();
                } 
            }
            catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        addTransaction(bankTransaction);
        nextTransaction(user);
    }
    
    
    protected void depositToOtherMember(String user){
    
    /*  cash is the amount you want to withdraw, balWith is the balance of the account 
     *  you withdraw from, newBalWith is the balance of the account you withdrew from, 
     *  balDep is the balance of the account you deposit to, 
     *  newBalDep is the balance after the deposit 
     */     
        double cash,balWith,newBalWith,balDep,newBalDep;                                 
        balWith=0;
        balDep=0;
                                                                    
        Scanner deposit=new Scanner(System.in);
        System.out.println("Enter amount you want to deposit: ");
        while(!deposit.hasNextDouble()){
            System.out.println("Illegal entry.Please try again.");
            deposit.next();
        }    
        cash=deposit.nextDouble();
       
        if(cash<=0){
            System.out.println("Please enter a valid amount.");
            depositToOtherMember(user);
        }
        else{
            try {
                String query1="select accounts.amount"+   
                                " from accounts,users "+   
                                    " where accounts.id=users.id "+
                                        " and users.username= ?";
                PreparedStatement stm=con.prepareStatement(query1);
                stm.setString(1, user);
                stm.executeQuery();
                ResultSet result1=stm.getResultSet();
                while(result1.next()){
                    balWith=result1.getDouble("amount");
                    System.out.println("Your account had: "+balWith+" €");
                }
                if(balWith<=cash){
                    System.out.println("Your account does not have enough money!");
                    bankTransaction="-"+user+" tried to withdraw from his account "+", "
                                        + cash+" € but did not have enough"; 
                }
                else{
                //accountToDepositTo is the account id of the account the user chooses to deposit to.
                    int accountToDepositTo;
                    Scanner depositAccount=new Scanner(System.in);
                    System.out.println("Choose the account you want to deposit to :");
                    getUserIds(user);
                    while(!depositAccount.hasNextInt()){
                        System.out.println("Illegal entry.Please try again.");
                        depositAccount.next();
                    }
                    
                    accountToDepositTo=depositAccount.nextInt();
                    
                    String query3="select accounts.amount "+   
                                    " from accounts,users "+   
                                        " where accounts.id=users.id "+
                                            " and users.id= ?";
                    PreparedStatement depstm=con.prepareStatement(query3);
                    depstm.setInt(1, accountToDepositTo);
                    depstm.executeQuery();
                    ResultSet result3=depstm.getResultSet();
                    while (result3.next()){
                        balDep=result3.getDouble("amount");
                        //System.out.println("Balance of the account you want to deposit to is "+balDep);
                    }
           
                    newBalWith=balWith-cash;
                
                    String query2="update accounts " +
                                    " inner join users on accounts.id=users.id" +
                                        " set accounts.amount="+newBalWith +
                                            " where " +
                                                " users.username= ?"; 
                    PreparedStatement upstm=con.prepareStatement(query2);
                    upstm.setString(1, user);
                    upstm.executeUpdate();
                    System.out.println("New ballance of your account is "+newBalWith);
                
                    newBalDep=balDep+cash;
                    
                    String query4="update accounts "+
                                    " inner join users on accounts.id=users.id " +
                                        " set accounts.amount= "+newBalDep+
                                            " where users.id= ?";
                    PreparedStatement updepstm=con.prepareStatement(query4);
                    updepstm.setInt(1,accountToDepositTo);
                    updepstm.executeUpdate();
                    bankTransaction="-"+user+" deposited "+cash+" to account with id "+accountToDepositTo+" at :"+date.toString();
                }
            }
            catch (SQLException ex) {
                System.out.println(ex);
            }
        }
        addTransaction(bankTransaction);
        nextTransaction(user); 
    }
    
    protected void getAllUserIds(){
        int id;
        String rs;
        String query="select id, username from  users";
        PreparedStatement statement;
        try {
            statement = con.prepareStatement(query);
            ResultSet result=statement.executeQuery(query);
            while(result.next()){
                id=result.getInt("id");
                rs=result.getString("username");
                System.out.print(rs);System.out.print(" -> ");
                System.out.println("id: " +id);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    protected void getUserIds(String user) {
        
        int id;
        String rs;
        String query="select id, username from users where users.username != '"+user+"'";
        PreparedStatement statement;
        try {
            statement = con.prepareStatement(query);
            ResultSet result=statement.executeQuery(query);
            while(result.next()){
                id=result.getInt("id");
                rs=result.getString("username");
                System.out.print(rs);System.out.print(" -> ");
                System.out.println("id: " +id);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    
    protected void nextTransaction(String user){
        System.out.println("Press 1 for another transaction.");
        System.out.println("Press 2 to exit.");
        Scanner input=new Scanner(System.in);
        int in=input.nextInt();
        if (user.equals("admin")){
            switch (in) {
                case 1: appMenu.adminAccount(user);
                    break;
                case 2:
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        System.out.println(ex);
                    }   break;
                default:
                    System.out.println("Invalid choice. Please try again !");
                    nextTransaction(user);
                    break;
            } 
        }
        else{
            switch (in) {
            case 1: appMenu.userAccount(user);
                break;
            case 2:
                try {
                    con.close();
                } catch (SQLException ex) {
                    System.out.println(ex);
                }   break;
            default:
                System.out.println("Invalid choice. Please try again !");
                nextTransaction(user);
                break;
            }
        }
    }     
}
