package de.hpi.smm.database;

import de.hpi.smm.Config;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseAdapter {

    static String urlFormat = "jdbc:sap://%s:3%s15/";

    private Connection connection = null;

    private String schemaName = null;

    private Map<TableDefinition, PreparedStatement> preparedStatementMap = new HashMap<TableDefinition, PreparedStatement>();
    private Schema schema;

    DatabaseAdapter() {
    }

    public static DatabaseAdapter getSmaHanaAdapter() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter();

        String ip = Config.IP;
        String instance = Config.INSTANCE;
        String user = Config.USER;
        String password = Config.PASSWORD;

        databaseAdapter.open(ip, instance, user, password);
        databaseAdapter.setSchemaName("SYSTEM");

        return databaseAdapter;
    }

    public int executeUpdate(String statement) {
        try {
            Statement sqlStatement = this.connection.createStatement();
            return sqlStatement.executeUpdate(statement);
        } catch (SQLException e) {
            exceptionCaught(e, statement);
            return -1;
        }
    }

    public ResultSet executeQuery(String statement) {
        try {
            Statement sqlStatement = this.connection.createStatement();
            return sqlStatement.executeQuery(statement);
        } catch (SQLException e) {
            exceptionCaught(e, statement);
            return null;
        }
    }

//    public PreparedStatement createPreparedStatement(String statement) {
//        try {
//            PreparedStatement pstmt = this.connection.prepareStatement(statement);
//            for (int i = 0; i < firstNames.length; i++) {
//                pstmt.setInt(1, i + 1);
//                pstmt.setString(5, "");
//                pstmt.addBatch();
//            }
//        } catch (SQLException e) {
//            exceptionCaught(e, statement);
//            e.printStackTrace();
//        }
//    }

//    public void executeBatch(PreparedStatement pstmt){
//        try{
//            pstmt.executeBatch();
//        } catch (SQLException e) {
//            exceptionCaught(e, pstmt.toString());
//            e.printStackTrace();
//        }
//    }

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

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void closeConnection() {
        for (PreparedStatement preparedStatement : preparedStatementMap.values()){
            try {
                preparedStatement.executeBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
        this.schemaName = schema.getName();
    }
}
