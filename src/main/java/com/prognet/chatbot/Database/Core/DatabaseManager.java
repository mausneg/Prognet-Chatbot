package com.prognet.chatbot.Database.Core;

import java.sql.*;

import com.prognet.chatbot.Database.Config.Config;


public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static DatabaseManager getInstance(){
        try{
            if (instance == null) {
                instance = new DatabaseManager();
            } else if (instance.getConnection().isClosed()) {
                instance = new DatabaseManager();
            }
            return instance;
        }
        catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeUpdate(String query) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}