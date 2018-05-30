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
import java.util.Scanner;

/**
 *
 * @author Despoina
 */
public class LogIn {
    
    private  Connection con=DatabaseConnection.connectDb();        
    private  String user;
    private  String password;
    

    private  boolean usernameExists(String user){
        
        ApplicationMenu appMenu=new ApplicationMenu();
        boolean exists=false;
        try {
            String query = "select username from users where username = '"+user+"'";
            PreparedStatement stm = con.prepareStatement(query);
            stm.executeQuery(query);
            ResultSet result = stm.getResultSet();
            while(result.next()){
                String users = result.getString("username");
                if (user.equalsIgnoreCase(users)){
                    exists = true;
                }
            }
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return exists;
    }
    
       
    protected  void username(String user){ 
        ApplicationMenu appMenu=new ApplicationMenu();
        Scanner scannerU = new Scanner(System.in);
        System.out.println("Enter your Username:");
        user = scannerU.nextLine();
        usernameExists(user);
        if (!usernameExists(user)){
            System.out.println("Wrong username,try again");
            username(user);
        }
        else{
            password(password);
            if (user.equals("admin")){
                appMenu.adminAccount(user);
            }
            else{
                appMenu.userAccount(user);
            }
        }
    }
    
    private void password(String password){
        ApplicationMenu appMenu=new ApplicationMenu();
        Scanner scannerP = new Scanner(System.in);
        System.out.println("Enter your Password:");
        password = scannerP.nextLine();
        passwordExists(password);
        if (!passwordExists(password)){
            System.out.println("Wrong password,try again");
            password(password);
        }
    }
    
    private boolean passwordExists(String password){
        ApplicationMenu appMenu=new ApplicationMenu();
        boolean exists =false;
        try {
            String query = "select password from users where password = '"+password+"'";
            PreparedStatement stm = con.prepareStatement(query);
            stm.executeQuery();
            ResultSet result = stm.getResultSet();
            while(result.next()){
                String users = result.getString("password");
                if(password.equalsIgnoreCase(users)){
                    exists=true;
                }
            }
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return exists;
    }    
}   
