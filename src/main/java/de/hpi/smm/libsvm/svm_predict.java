package de.hpi.smm.libsvm;

import de.hpi.smm.cluster_determination.BlogPost;
import libsvm.*;

import java.io.FileNotFoundException;
import java.io.IOException;

public class svm_predict {

    private double getCluster(BlogPost blogPost, svm_model model, int predict_probability) throws IOException {
        int correct = 0;
        int total = 0;
        double error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;

        int svm_type = svm.svm_get_svm_type(model);
        int nr_class = svm.svm_get_nr_class(model);
        double[] prob_estimates = null;

        if (predict_probability == 1) {
            if (svm_type == svm_parameter.EPSILON_SVR ||
                    svm_type == svm_parameter.NU_SVR) {
                svm_predict.info("Prob. model for test data: target value = predicted value + z,\nz: Laplace distribution e^(-|z|/sigma)/(2sigma),sigma=" + svm.svm_get_svr_probability(model) + "\n");
            } else {
                int[] labels = new int[nr_class];
                svm.svm_get_labels(model, labels);
                prob_estimates = new double[nr_class];
            }
        }

        double resultCluster = -1.0;

        // convert blog post into nodes
        double cluster = -1.0;
        int setValues = blogPost.getSetValues();
        svm_node[] nodes = new svm_node[setValues];
        Float[] point = blogPost.getPoint();
        int index = 0;
        for (int i = 0; i < point.length; i++) {
            if (point[i] != 0) {
                svm_node node = new svm_node();
                node.index = i + 1;
                node.value = point[i];
                nodes[index] = node;
                index++;
            }
        }

        // determine cluster
        if (predict_probability == 1 && (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
            resultCluster = svm.svm_predict_probability(model, nodes, prob_estimates);
        } else {
            resultCluster = svm.svm_predict(model, nodes);
        }

        if (resultCluster == cluster)
            ++correct;
        error += (resultCluster - cluster) * (resultCluster - cluster);
        sumv += resultCluster;
        sumy += cluster;
        sumvv += resultCluster * resultCluster;
        sumyy += cluster * cluster;
        sumvy += resultCluster * cluster;
        ++total;

        if (svm_type == svm_parameter.EPSILON_SVR || svm_type == svm_parameter.NU_SVR) {
            svm_predict.info("Mean squared error = " + error / total + " (regression)\n");
            svm_predict.info("Squared correlation coefficient = " +
                    ((total * sumvy - sumv * sumy) * (total * sumvy - sumv * sumy)) /
                            ((total * sumvv - sumv * sumv) * (total * sumyy - sumy * sumy)) +
                    " (regression)\n");
        } else
            svm_predict.info("Accuracy = " + (double) correct / total * 100 +
                    "% (" + correct + "/" + total + ") (classification)\n");

        return resultCluster;
    }

    public double predict(String args[], BlogPost blogPost) throws IOException {
        int predict_probability = 0;
        svm_print_string = svm_print_stdout;

        // parse options
        int i = 0;
        for (i = 0; i < args.length; i++) {
            if (args[i].charAt(0) != '-') break;
            ++i;
            switch (args[i - 1].charAt(1)) {
                case 'b':
                    predict_probability = atoi(args[i]);
                    break;
                case 'q':
                    svm_print_string = svm_print_null;
                    i--;
                    break;
                default:
                    System.err.print("Unknown option: " + args[i - 1] + "\n");
                    exit_with_help();
            }
        }
        if (i >= args.length - 1)
            exit_with_help();

        // read model
        try {
            svm_model model = svm.svm_load_model(args[i]);
            if (model == null) {
                System.err.print("can't open model file " + args[i] + "\n");
                System.exit(1);
            }
            if (predict_probability == 1) {
                if (svm.svm_check_probability_model(model) == 0) {
                    System.err.print("Model does not support probabiliy estimates\n");
                    System.exit(1);
                }
            } else {
                if (svm.svm_check_probability_model(model) != 0) {
                    svm_predict.info("Model supports probability estimates, but disabled in prediction.\n");
                }
            }
            return getCluster(blogPost, model, predict_probability);
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            exit_with_help();
        }
        return -1.0;
    }

    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    private static svm_print_interface svm_print_stdout = new svm_print_interface() {
        public void print(String s) {
            System.out.print(s);
        }
    };

    private static svm_print_interface svm_print_string = svm_print_stdout;

    static void info(String s) {
        svm_print_string.print(s);
    }

    private static double atof(String s) {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private static void exit_with_help() {
        System.err.print("usage: svm_predict [options] test_file model_file output_file\n"
                + "options:\n"
                + "-b probability_estimates: whether to predict probability estimates, 0 or 1 (default 0); one-class SVM not supported yet\n"
                + "-q : quiet mode (no outputs)\n");
        System.exit(1);
    }
}
