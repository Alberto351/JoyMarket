package database;

import java.sql.*;

public class Connect {
    private final String USERNAME = "root";
    private final String PASSWORD = ""; 	
    private final String DATABASE = "db_joyMarket";
    private final String HOST = "localhost:3306";
    private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);
    
    private Connection con;
    private Statement st;
    private static Connect instance;

    public static Connect getInstance() {
        if (instance == null) instance = new Connect();
        return instance;
    }

    private Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
            st = con.createStatement();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public ResultSet execQuery(String query) {
        try { return st.executeQuery(query); } 
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public void execUpdate(String query) {
        try { st.executeUpdate(query); } 
        catch (SQLException e) { e.printStackTrace(); }
    }
    
    public PreparedStatement prepareStatement(String query) {
        try { return con.prepareStatement(query); } 
        catch (SQLException e) { e.printStackTrace(); return null; }
    }
}