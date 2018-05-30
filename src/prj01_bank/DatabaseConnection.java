/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj01_bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Despoina
 */
public class DatabaseConnection {
    
    static final String USERNAME = "root";
    static final String PASS = "tatung98!!";
    static final String MYSQLURL = "jdbc:mysql://localhost:3306/afdemp_java_1?useSSL=false";
        
    public static Connection connectDb() {
        
        try {
            Connection con = DriverManager.getConnection(MYSQLURL,USERNAME,PASS);
            return con;
        } catch (SQLException ex) {
            System.out.println("There was an error in the database connection!");
            return null;
        }
    }
}
