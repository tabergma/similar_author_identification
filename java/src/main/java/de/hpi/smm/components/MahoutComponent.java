package de.hpi.smm.components;


import de.hpi.smm.clustering.KMeans;

import java.util.ArrayList;
import java.util.List;

public class MahoutComponent {

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        /**
         * Read features from database
         */
        List<List<Float>> features = new ArrayList<>();
        // TODO

        /**
         * Execute Mahout
         */
        KMeans kMeans = new KMeans();
        kMeans.run(features);
    }

}
