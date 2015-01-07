package de.hpi.smm.sets;

import de.hpi.smm.helper.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDataSet implements TestSet {
    protected Map<Integer, Author> authors = new HashMap<Integer, Author>();
    protected Map<String, Author> nameAuthorMapping = new HashMap<String, Author>();

    protected int documentId = 0;
    protected int minLength = 0;
    protected int limit = -1;

    AbstractDataSet (int limit){
        this.limit = limit;
    }

    protected void putAuthorName(String authorName) {
        Author author = nameAuthorMapping.get(authorName);
        if (author == null){
            author = new Author(authorName);
            nameAuthorMapping.put(authorName, author);
        }
        authors.put(documentId++, author);
    }

    protected boolean isLimitReached() {
        return documentId == limit;
    }

    public List<Author> getAuthors(){
        return Util.asSortedList(nameAuthorMapping.values());
    }

    public Author getAuthor(int documentId) {
        return authors.get(documentId);
    }

    public void applyMinimumLength(int minLength) {
        this.minLength = minLength;
    }

    public int getCurrentDocumentId() {
        return documentId;
    }
}