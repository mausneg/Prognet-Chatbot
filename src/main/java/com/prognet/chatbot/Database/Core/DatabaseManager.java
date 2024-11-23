package com.prognet.chatbot.Database.Core;

import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Base64;
import java.util.logging.Logger;

public class DatabaseManager {

    // // URL koneksi database
    // private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/chatboxdb";
    // private static final String DB_USER = "root"; // Ubah dengan username
    // database Anda
    // private static final String DB_PASSWORD = ""; // Ubah dengan password
    // database Anda

    // // Method untuk mendapatkan koneksi ke database
    // public static Connection getConnection() throws SQLException {
    // return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    // }

    // // Method untuk menguji koneksi database
    // public static void main(String[] args) {
    // try (Connection connection = getConnection()) {
    // System.out.println("Koneksi ke database berhasil!");
    // // Contoh query untuk memastikan tabel tersedia
    // String testQuery = "SELECT 1";
    // Statement statement = connection.createStatement();
    // statement.executeQuery(testQuery);
    // System.out.println("Database tersedia dan siap digunakan.");
    // } catch (SQLException e) {
    // System.out.println("Koneksi ke database gagal.");
    // e.printStackTrace();
    // }
    // }

    private Statement statement;
    private ResultSet resultSet;
    private Connection connection;

    public DatabaseManager() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatboxdb", "root", "");
            statement = connection.createStatement();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            String hashedPassword = Base64.getEncoder().encodeToString(digest);
            return hashedPassword;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register(String username, String password) {
        try {
            String checkQuery = "SELECT * FROM user WHERE username = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            String hashedPassword = hashPassword(password);
            String query = "INSERT INTO user (username, password) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public boolean login(String username, String password) {
        try {
            String hashedPassword = hashPassword(password);
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return false;
    }

    public int getUsername(String username) {
        try {
            String query = "SELECT username FROM user WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return 0;
    }




}
