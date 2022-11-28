package com.prohgmena_themata;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DBConnection {

    /**
     * Method that establishes a connection to database
     *
     * @return Connection
     */
    public static Connection connect() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:prohgmena_themata.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Adds blockchain to database
     *
     * @param blockchain
     */
    public void addBlockChain(List<Block> blockchain) {
        Connection con = DBConnection.connect();
        String query = "INSERT INTO blockchain(hash, previous_hash, simulation_name, thread_name, thread_duration, thread_dependencies, timestamp, nonce) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        blockchain.forEach((block) -> {
            PreparedStatement ps = null;
            try {
                ps = con.prepareStatement(query);
                ps.setString(1, block.getHash());
                ps.setString(2, block.getPreviousHash());
                ps.setString(3, block.getSimulationName());
                ps.setString(4, block.getThreadName());
                ps.setLong(5, block.getThreadDuration());
                ps.setString(6, Arrays.toString(block.getThreadDependencies()));
                ps.setLong(7, block.getTimeStamp());
                ps.setLong(8, block.getNonce());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert ps != null;
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Blockchain has been inserted!");
    }

    /**
     * Gets the hash of the last block in database
     *
     * @return String
     */
    public String getLastHash() {
        Connection con = DBConnection.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT hash from blockchain ORDER BY timestamp DESC LIMIT 1";
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()) {
                return rs.getString("hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert rs != null;
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "0";
    }

    /**
     * Returns the random generated simulation name
     *
     * @return String
     */
    public String getSimulationName() {
        Connection con = DBConnection.connect();
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<String> simulationNames = new ArrayList<>();

        String query = "SELECT DISTINCT simulation_name from blockchain";
        try {
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()) {
                simulationNames.add(rs.getString("simulation_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert rs != null;
                rs.close();
                ps.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return calculateSimulationName(simulationNames);
    }

    /**
     * This method creates a random simulation name
     *
     * @param simulationNames The simulation names stored in database
     * @return String
     */
    private String calculateSimulationName(ArrayList<String> simulationNames) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 5;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        if (simulationNames.contains(generatedString)) {
            calculateSimulationName(simulationNames);
        }

        return generatedString;
    }
}
