package de.hpi.smm.sets;

import de.hpi.smm.helper.DatabaseAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HanaSet implements TestSet {

    private ResultSet resultSet;

    public HanaSet(int size) {
        String statement = String.format("SELECT POSTCONTENT FROM SYSTEM.WEBPAGE LIMIT %d", size);
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaHanaAdapter();
        this.resultSet = dbAdapter.executeQuery(statement);
    }

    public boolean next() {
        try {
            return this.resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("getting next item failed");
        }
    }

    public String getText() {
        try {
            return this.resultSet.getString(("POSTCONTENT"));
        } catch (SQLException e) {
            throw new RuntimeException("getting text failed");
        }
    }
}
