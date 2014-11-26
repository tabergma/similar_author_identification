package de.hpi.smm.helper;

import de.hpi.smm.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseAdapter {

    static String urlFormat = "jdbc:sap://%s:3%s15/";

    private Connection connection = null;

    private String schema = null;

    DatabaseAdapter(){
    }

    public static DatabaseAdapter getSmaHanaAdapter() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter();

        String ip = Config.IP;
        String instance = Config.INSTANCE;
        String user = Config.USER;
        String password = Config.PASSWORD;

        databaseAdapter.open(ip, instance, user, password);
        databaseAdapter.setSchema("SYSTEM");

        return databaseAdapter;
    }

    public int executeUpdate(String statement){
        try {
            Statement sqlStatement = this.connection.createStatement();
            return sqlStatement.executeUpdate(statement);
        } catch (SQLException e) {
            exceptionCaught(e, statement);
            return -1;
        }
    }

    public ResultSet executeQuery(String statement){
        try {
            Statement sqlStatement = this.connection.createStatement();
            return sqlStatement.executeQuery(statement);
        } catch (SQLException e) {
            exceptionCaught(e, statement);
            return null;
        }
    }

    private void exceptionCaught(SQLException e, String statement) {
        System.out.println(String.format("Exception caught while executing statement: %s", statement));
        e.printStackTrace();
    }

    private boolean open(String ip, String instance, String user, String password) {
        try {
            Class.forName("com.sap.db.jdbc.Driver");

            String url = String.format(urlFormat, ip, instance);

            this.connection = java.sql.DriverManager.getConnection(url, user, password);
            this.connection.setAutoCommit(false);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
