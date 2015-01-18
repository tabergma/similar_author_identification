package de.hpi.smm.sets;

import java.util.List;

public class CrossValidation implements TestSet {
    private final AbstractDataSet dataSet;
    private int k;
    private int n;
    private List<String> texts;
    private List<Author> authors;
    int id = -1;

    public static final int TEST_SET = 0;
    public static final int TRAIN_SET = 1;
    private boolean testing = false;

    public CrossValidation(AbstractDataSet dataSet){
        this.dataSet = dataSet;
        while (dataSet.next()){
            texts.add(dataSet.getText());
            authors.add(dataSet.getAuthor());
        }
    }

    public void setN(int n){
        this.n = n;
    }

    public void testWith(int k){
        this.id = -1;
        this.k = k;
        testing = false;
    }

    public void switchToSet(int set){
        if (set == TEST_SET){
            testing = true;
            this.id = k - n;
        } else {
            testing = false;
        }
    }

    public boolean next(){
        if (testing){
            id += n;
            return id < texts.size();
        } else {
            id++;
            if (id % n == k) {
                id++;
            }
            return id < texts.size();
        }
    }

    @Override
    public String getText() {
        return texts.get(id);
    }

    @Override
    public Author getAuthor() {
        return authors.get(id);
    }
}
