package de.hpi.smm.sets;

import de.hpi.smm.helper.DatabaseAdapter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HanaSet extends AbstractDataSet implements TestSet {
    private final int dataSet;

    private ResultSet resultSet;

    private String authorColumn;
    private String postColumn;

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public HanaSet(int dataSet, int limit) {
        super(limit);
        this.dataSet = dataSet;
        String statement;

        if (dataSet == DataSetSelector.SMM_SET) {

            statement = "SELECT POSTAUTHOR, POSTCONTENT FROM SYSTEM.WEBPAGE WHERE TYPE = 'POST'";
            this.postColumn = "POSTCONTENT";
            this.authorColumn = "POSTAUTHOR";

        } else if (dataSet == DataSetSelector.SPINNER_SET) {

            statement = "SELECT \"atomname\",\"content_extract\" FROM \"SPINN3R\".\"ENTRY\" WHERE \"atomname\" IS NOT NULL";
            this.postColumn = "content_extract";
            this.authorColumn = "atomname";

        } else {
            throw new RuntimeException("Invalid data set selected!");
        }
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaHanaAdapter();
        this.resultSet = dbAdapter.executeQuery(statement);
    }

    public boolean next() {
        if (isLimitReached()){
            return false;
        }
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
        if (isLimitReached()){
            return null;
        }
        try {
            String author = this.resultSet.getString(this.authorColumn);
            if (author == null){
                putAuthorName(null);
            } else {
                String hashName = hash(author);
                putAuthorName(hashName);
            }
            return this.resultSet.getString(this.postColumn);
        } catch (SQLException e) {
            throw new RuntimeException("getting text failed");
        }
    }

    private String hash(String name) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(name.getBytes());
        return bytesToHex(messageDigest.digest());
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
