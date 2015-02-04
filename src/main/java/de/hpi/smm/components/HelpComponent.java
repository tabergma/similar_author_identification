package de.hpi.smm.components;

public class HelpComponent {
    public static void main(String[] args) {
        System.out.println("This is the help for all components for similar author identification.");
        System.out.println("Use the following lines to call each component, usually you want to start with the first one.");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.FeatureComponent");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.MahoutComponent");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.ResultComponent");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.LabelComponent");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.SvmComponent");
        System.out.println("  java -cp similar_author_identification.jar de.hpi.smm.components.ClusterComponent");
    }
}
