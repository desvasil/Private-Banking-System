/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj01_bank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Despoina
 */
public class FilesAccess {
    
    public Connection con=DatabaseConnection.connectDb();
    private static Date date=new Date();
    private static SimpleDateFormat dateFormat=new SimpleDateFormat("dd_MM_yyyy");
    private File file;
    
    public void  fileAccess(String user) {
        BufferedWriter out;
        try {
            file=new File("Statement_"+user+"_"+dateFormat.format(date)+".txt");
            out = new BufferedWriter(new FileWriter(file));
                for (int i=0;i<BankAccounts.arraylist.size();i++) {
                    out.write(BankAccounts.arraylist.get(i));
                    out.newLine();
                }
            out.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FilesAccess.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }    
}
        
