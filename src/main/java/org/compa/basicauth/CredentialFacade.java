/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.compa.basicauth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

public class CredentialFacade {

    public static Credentials createCredentials(String basicAuth) {
        basicAuth = basicAuth.substring(6).trim();
        byte[] bytes = Base64.getDecoder().decode(basicAuth);
        basicAuth = new String(bytes);
        int colon = basicAuth.indexOf(":");
        String username = basicAuth.substring(0, colon);
        String password = basicAuth.substring(colon + 1);
        return new Credentials(username, password);
    }

    public static void save(Credentials credentials) {
        String hashed = BCrypt.withDefaults().hashToString(12, credentials.getPassword().toCharArray());
        try (Connection connection = SqlLiteConnectorFactory.getConnection()){
            Statement stmt = connection.createStatement();
            String sql = String.format("INSERT INTO user VALUES ('%s','%s')", credentials.getUsername(), hashed);
            stmt.executeUpdate(sql);
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
    }

    public static Credentials get(String username) {
        try (Connection connection = SqlLiteConnectorFactory.getConnection()) {
            Statement stmt = connection.createStatement();
            String sql = String.format("SELECT * FROM user WHERE username='%s'", username);
            ResultSet data = stmt.executeQuery(sql);
            data.next();
            return new Credentials(data.getString("username"), data.getString("password"));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public static boolean verify(String username, String password) {
        Credentials credentials = get(username);
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), credentials.getPassword());
        return result.verified;
    }
}
