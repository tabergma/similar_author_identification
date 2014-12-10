package de.hpi.smm.evaluation;

import de.hpi.smm.helper.MutableInt;
import de.hpi.smm.sets.AbstractDataSet;
import de.hpi.smm.sets.Author;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Evaluator {
    public List<EvaluationResult> evaluate(AbstractDataSet dataSet, Map<Integer, List<Integer>> clusterToDocument) {
        int numberOfClusters = clusterToDocument.keySet().size();
        Map<Author, List<MutableInt>> authorHits = new HashMap<Author, List<MutableInt>>();
        for (Author author : dataSet.getAuthors()){
            List<MutableInt> tempList = new ArrayList<MutableInt>();
            for (int i = 0; i < numberOfClusters; i++){
                tempList.add(new MutableInt());
            }
            authorHits.put(author, tempList);
        }

        for (Map.Entry<Integer, List<Integer>> clusterToDocumentEntry : clusterToDocument.entrySet()){
            Integer cluster = clusterToDocumentEntry.getKey();
            for (Integer documentId : clusterToDocumentEntry.getValue()) {
                Author author = dataSet.getAuthor(documentId);
                authorHits.get(author).get(cluster).increment();
            }
        }

        List<EvaluationResult> resultList = new ArrayList<EvaluationResult>();
        for (Author author : dataSet.getAuthors()){
            int maxHits = -1;
            int sum = 0;
            int resultCluster = -1;
            for (int cluster = 0; cluster < numberOfClusters; cluster++) {
                int currentHits = authorHits.get(author).get(cluster).get();
                if (currentHits > maxHits) {
                    maxHits = currentHits;
                    resultCluster = cluster;
                }
                sum += currentHits;
            }
            double precision = (double) maxHits / clusterToDocument.get(resultCluster).size();
            double recall = (double) maxHits / sum;
            resultList.add(new EvaluationResult(author, resultCluster, precision, recall));
        }
        return resultList;
    }
}
