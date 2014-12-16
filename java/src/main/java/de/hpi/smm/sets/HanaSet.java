package de.hpi.smm.sets;

import de.hpi.smm.helper.DatabaseAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HanaSet extends AbstractDataSet implements TestSet {
    public static int DATA_SET_SMM = 0;
    public static int DATA_SET_SPINNER = 1;
    private final int dataSet;

    private int i = 0;
    private ResultSet resultSet;

    private String authorColumn;
    private String postColumn;

    public HanaSet(int dataSet, int size) {
        this.dataSet = dataSet;
        String statement = null;
        if (dataSet == DATA_SET_SMM) {
            statement = String.format("SELECT POSTAUTHOR, POSTCONTENT FROM SYSTEM.WEBPAGE WHERE TYPE = 'POST' LIMIT %d", size);
            this.postColumn = "POSTCONTENT";
            this.authorColumn = "POSTAUTHOR";
        } else if (dataSet == DATA_SET_SPINNER) {
            statement = String.format("SELECT \"atomname\",\"content_extract\" FROM \"SPINN3R\".\"ENTRY\" WHERE \"atomname\" IS NOT NULL LIMIT %d", size);
            this.postColumn = "content_extract";
            this.authorColumn = "atomname";
        } else {
            throw new RuntimeException("Invalid data set selected!");
        }
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaHanaAdapter();
        this.resultSet = dbAdapter.executeQuery(statement);
    }

    public boolean next() {
        try {
            boolean result = this.resultSet.next();
            while (result && (this.resultSet.getString(this.postColumn) == null || (this.resultSet.getString(this.postColumn).length() < this.minLength))) {
                result = this.resultSet.next();
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("getting next item failed");
        }
    }

    public String getText() {
        try {
            putAuthorName(this.resultSet.getString(this.authorColumn));
            return this.resultSet.getString(this.postColumn);
        } catch (SQLException e) {
            throw new RuntimeException("getting text failed");
        }
    }

}
