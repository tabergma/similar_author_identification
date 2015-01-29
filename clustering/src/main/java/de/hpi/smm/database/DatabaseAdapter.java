package de.hpi.smm.database;

import de.hpi.smm.Config;

import java.sql.*;

public class DatabaseAdapter {

    static String urlFormat = "jdbc:sap://%s:3%s15/";

    private Connection connection = null;

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

    public AbstractTableDefinition getWriteTable(String tableName){
        AbstractTableDefinition tableDefinition = this.schema.getTableDefinition(tableName);
        PreparedStatement preparedStatement = tableDefinition.getPreparedStatement();
        if (preparedStatement == null){
            String formattedStatement = tableDefinition.formatInsertStatement();
            try {
                preparedStatement = this.connection.prepareStatement(formattedStatement);
            } catch (SQLException e) {
                exceptionCaught(e, formattedStatement);
                e.printStackTrace();
            }
            tableDefinition.setPreparedStatement(preparedStatement);
        }
        return tableDefinition;
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

    public void closeConnection() {
        for (AbstractTableDefinition tableDefinition : schema.getTableDefinitions().values()){
            if (tableDefinition.getPreparedStatement() != null) {
                try {
                    tableDefinition.getPreparedStatement().executeBatch();
                } catch (SQLException e) {
                    exceptionCaught(e, "executeBatch");
                    e.printStackTrace();
                }
            }
        }
        try {
            this.connection.commit();
        } catch (SQLException e) {
            exceptionCaught(e, "commit");
            e.printStackTrace();
        }
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Schema getSchema() {
        return this.schema;
    }

    public AbstractTableDefinition getReadTable(String tableName) {
        AbstractTableDefinition tableDefinition = this.schema.getTableDefinition(tableName);
        return getReadTable(tableDefinition);
    }

    public AbstractTableDefinition getReadTable(AbstractTableDefinition tableDefinition) {
        ResultSet resultSet = this.executeQuery(tableDefinition.formatCompleteReadStatement());
        tableDefinition.setResultSet(resultSet);
        return tableDefinition;
    }
}
