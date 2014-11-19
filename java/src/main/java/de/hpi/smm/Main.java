package de.hpi.smm;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {

        String statement = "SELECT POSTCONTENT FROM SYSTEM.WEBPAGE";
        DatabaseAdapter dbAdapter = DatabaseAdapter.getSmaAdapter();
        ResultSet rs = dbAdapter.executeQuery(statement);
        try {
            if (rs.next()){
                System.out.println(rs.getString("POSTCONTENT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String filePath = "test.txt";

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line = reader.readLine();

        Tokenizer tokenizer = new Tokenizer();
        Map<String, Integer> words = tokenizer.tokenizeText(line);

        for (Map.Entry<String, Integer> word : words.entrySet()) {
            System.out.println(word);
        }
    }
}
