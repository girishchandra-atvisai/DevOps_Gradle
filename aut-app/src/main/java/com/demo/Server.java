package com.demo;

import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        logger.info("Server starting on port 8080...");

        // ✅ ROOT → Serve HTML (FIXED FOR MAVEN)
        server.createContext("/", exchange -> {
            try {
                InputStream is = Server.class
                        .getClassLoader()
                        .getResourceAsStream("web/index.html");

                if (is == null) {
                    String response = "index.html NOT FOUND in resources!";
                    exchange.sendResponseHeaders(500, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.close();
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                StringBuilder content = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                reader.close();

                String response = content.toString();

                exchange.getResponseHeaders().add("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());

            } catch (Exception e) {
                String response = "Error loading page";
                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }

            exchange.close();
        });

        // ✅ ADD Ticket
        server.createContext("/add", exchange -> {

            String body = new String(exchange.getRequestBody().readAllBytes());
            logger.info("Received ticket: " + body);

            try {
                Connection con = getConnection();

                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO tickets(name) VALUES(?)"
                );

                ps.setString(1, body);
                ps.executeUpdate();

                con.close();

                String response = "Added to DB: " + body;

                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());

                logger.info("Ticket inserted into DB");

            } catch (Exception e) {
                logger.error("DB Error: " + e.getMessage());

                String response = "DB ERROR: " + e.getMessage();

                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }

            exchange.close();
        });

        // ✅ GET Tickets
        server.createContext("/tickets", exchange -> {

            List<String> tickets = new ArrayList<>();

            try {
                Connection con = getConnection();

                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT name FROM tickets");

                while (rs.next()) {
                    tickets.add(rs.getString("name"));
                }

                con.close();

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(tickets);

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, json.getBytes().length);
                exchange.getResponseBody().write(json.getBytes());

                logger.info("Fetched tickets from DB");

            } catch (Exception e) {
                logger.error("Error fetching tickets: " + e.getMessage());

                String response = "ERROR: " + e.getMessage();

                exchange.sendResponseHeaders(500, response.length());
                exchange.getResponseBody().write(response.getBytes());
            }

            exchange.close();
        });

        server.start();
        logger.info("Server running at http://localhost:8080");
    }

    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/test";
        String user = "root";
        String password = "root@123";

        return DriverManager.getConnection(url, user, password);
    }
}