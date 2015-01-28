package de.hpi.smm.components;


import de.hpi.smm.cluster_determination.Cluster;
import de.hpi.smm.cluster_determination.ClusterDetermination;

public class ClusterComponent {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Wrong number of arguments!");
            System.out.println("To start the program execute");
            System.out.println("  java -jar <jar-name> <method> <k> <content>");
            System.out.println("<method> can be either 'k-nearest', 'euclidean' or 'svm'.");
        }

        String method = args[0];
        String k = args[1];
        String content = args[2];

        Cluster cluster = run(content, method, k);

        System.out.println("Result: Cluster " + cluster.getNumber());
    }

    public static Cluster run(String content, String method, String k) throws Exception {
        ClusterDetermination clusterDetermination = new ClusterDetermination();
        return clusterDetermination.run(content, method, k);
    }
}
