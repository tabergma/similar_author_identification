package de.hpi.smm.sets;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDataSet implements TestSet {
    protected Map<Integer, Author> authors = new HashMap<Integer, Author>();
    protected Map<String, Author> nameAuthorMapping = new HashMap<String, Author>();

    int documentId = 0;

    protected void putAuthorName(String authorName) {
        Author author = nameAuthorMapping.get(authorName);
        if (author == null){
            author = new Author(authorName);
            nameAuthorMapping.put(authorName, author);
        }
        authors.put(documentId++, author);
    }

    public DocumentToAuthorMapping getDocumentToAuthorMapping() {
        DocumentToAuthorMapping documentToAuthorMapping = new DocumentToAuthorMapping(){
            @Override
            public Author getAuthor(int documentId) {
                return authors.get(documentId);
            }
        };

        return documentToAuthorMapping;
    }
}
