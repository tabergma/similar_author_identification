package de.hpi.smm.sets;

import de.hpi.smm.helper.DatabaseAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HanaSet extends AbstractDataSet implements TestSet {

    private int i = 0;
    private ResultSet resultSet;

    public HanaSet(int size) {
        String statement = String.format("SELECT POSTAUTHOR, POSTCONTENT FROM SYSTEM.WEBPAGE LIMIT %d WHERE TYPE = 'POST'", size);
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
            putAuthorName(this.resultSet.getString("POSTAUTHOR"));
            return this.resultSet.getString(("POSTCONTENT"));
        } catch (SQLException e) {
            throw new RuntimeException("getting text failed");
        }
    }

}
