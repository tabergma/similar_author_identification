package de.hpi.smm.evaluation;

import de.hpi.smm.helper.MutableInt;
import de.hpi.smm.helper.MutableIntStartAtOne;
import de.hpi.smm.sets.Author;
import de.hpi.smm.sets.DocumentToAuthorMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    public void evaluate(DocumentToAuthorMapping documentToAuthorMapping, Map<Integer, List<Integer>> clusterToDocument) {
        for (Map.Entry<Integer, List<Integer>> clusterToDocumentEntry : clusterToDocument.entrySet()){
            Integer cluster = clusterToDocumentEntry.getKey();
            Map<Author, MutableInt> authorHits = new HashMap<Author, MutableInt>();
            for (Integer documentId : clusterToDocumentEntry.getValue()) {
                Author author = documentToAuthorMapping.getAuthor(documentId);
                MutableInt hits = authorHits.get(author);
                if (hits == null){
                    authorHits.put(author, new MutableIntStartAtOne());
                } else {
                    hits.increment();
                }
            }
            Author resultingAuthor = new Author(null);
            int max = -1;
            double precision = 0.0f;
            for (Map.Entry<Author, MutableInt> authorHitEntry : authorHits.entrySet()) {
                if (authorHitEntry.getValue().get() > max) {
                    resultingAuthor = authorHitEntry.getKey();
                    precision = (double) authorHitEntry.getValue().get() / clusterToDocumentEntry.getValue().size();
                }
            }
            System.out.println(String.format("Cluster %d belongs to author %s and has precision %f.", cluster, resultingAuthor.getName(), precision));
        }
    }
}
