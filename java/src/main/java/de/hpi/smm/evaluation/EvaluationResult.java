package de.hpi.smm.evaluation;

import de.hpi.smm.sets.Author;

public class EvaluationResult {
    private static final String FORMAT = "Author %s -> cluster %d: F-measure = %.2f (precision = %.2f, recall = %.2f).";

    private final double fMeasure;
    private final Author author;
    private final int cluster;
    private final double precision;
    private final double recall;

    public EvaluationResult(Author author, int cluster, double precision, double recall){
        this.author = author;
        this.cluster = cluster;
        this.fMeasure = (precision * recall) / (precision + recall) * 2;
        this.precision = precision;
        this.recall = recall;
    }

    public Author getAuthor() {
        return author;
    }

    public int getCluster() {
        return cluster;
    }

    public double getFMeasure() {
        return fMeasure;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public String toString() {
        return String.format(FORMAT, this.author.toString(), this.cluster, this.fMeasure, this.precision, this.recall);
    }
}
