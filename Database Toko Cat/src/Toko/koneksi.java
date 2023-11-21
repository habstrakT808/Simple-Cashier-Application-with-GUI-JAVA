package Toko;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author PY7
 */
public class koneksi {
    private static Connection koneksi;
    
    public static Connection getKoneksi(){
        if (koneksi == null) {
            try {
                String url = "jdbc:sqlserver://localhost\\DESKTOP-CPA7IS4:1433;databaseName=penjualan;encrypt=true;trustServerCertificate=true;";
                String user = "sa";
                String password = "Fansspongebobno2";
                DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
                koneksi = DriverManager.getConnection(url, user, password);
                System.out.println("Berhasil");
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
        return koneksi;
    }
    public static void main(String args[]){
        getKoneksi();
    }  
}