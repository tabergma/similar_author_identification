package de.hpi.smm.libsvm;

import de.hpi.smm.cluster_determination.BlogPost;
import libsvm.*;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class svm_train {

    private svm_parameter param;        // set by parseArguments
    private svm_problem problem;        // set by read_problem
    private svm_model model;
    private String input_file_name;        // set by parseArguments
    private String model_file_name;        // set by parseArguments
    private String error_msg;
    private int cross_validation;
    private int nr_fold;
    private double cross_validation_result;

    public double crossValidation(String args[], List<BlogPost> blogPosts) {
        parseArguments(args);
        createProblem(blogPosts);
        error_msg = svm.svm_check_parameter(problem, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
            System.exit(1);
        }

        do_cross_validation();
        return cross_validation_result;
    }

    public void train(String args[], List<BlogPost> blogPosts) throws IOException {
        parseArguments(args);
        createProblem(blogPosts);
        error_msg = svm.svm_check_parameter(problem, param);

        if (error_msg != null) {
            System.err.print("ERROR: " + error_msg + "\n");
            System.exit(1);
        }

        model = svm.svm_train(problem, param);
        svm.svm_save_model(model_file_name, model);
    }

    private void do_cross_validation() {
        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[problem.l];

        svm.svm_cross_validation(problem, param, nr_fold, target);
        if (param.svm_type == svm_parameter.EPSILON_SVR ||
                param.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < problem.l; i++) {
                double y = problem.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }
            System.out.print("Cross Validation Mean squared error = " + total_error / problem.l + "\n");
            System.out.print("Cross Validation Squared correlation coefficient = " +
                            ((problem.l * sumvy - sumv * sumy) * (problem.l * sumvy - sumv * sumy)) /
                                    ((problem.l * sumvv - sumv * sumv) * (problem.l * sumyy - sumy * sumy)) + "\n"
            );
        } else {
            for (i = 0; i < problem.l; i++)
                if (target[i] == problem.y[i])
                    ++total_correct;

            cross_validation_result = 100.0 * total_correct / problem.l;
        }
    }

    private void createProblem(List<BlogPost> blogPosts) {
        Vector<Double> clusters = new Vector<>();
        Vector<svm_node[]> points = new Vector<>();
        int maxIndex = 0;

        for (BlogPost post : blogPosts) {
            clusters.addElement((double) post.getNumber());

            int setValues = post.getSetValues();
            svm_node[] nodes = new svm_node[setValues];
            Float[] point = post.getPoint();

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

            if (setValues > 0)
                maxIndex = Math.max(maxIndex, nodes[setValues - 1].index);

            points.addElement(nodes);
        }

        problem = new svm_problem();
        problem.l = clusters.size();
        problem.x = new svm_node[problem.l][];
        for (int i = 0; i < problem.l; i++)
            problem.x[i] = points.elementAt(i);
        problem.y = new double[problem.l];
        for (int i = 0; i < problem.l; i++)
            problem.y[i] = clusters.elementAt(i);

        if (param.gamma == 0 && maxIndex > 0)
            param.gamma = 1.0 / maxIndex;

        if (param.kernel_type == svm_parameter.PRECOMPUTED)
            for (int i = 0; i < problem.l; i++) {
                if (problem.x[i][0].index != 0) {
                    System.err.print("Wrong kernel matrix: first column must be 0:sample_serial_number\n");
                    System.exit(1);
                }
                if ((int) problem.x[i][0].value <= 0 || (int) problem.x[i][0].value > maxIndex) {
                    System.err.print("Wrong input format: sample_serial_number out of range\n");
                    System.exit(1);
                }
            }
    }

    private static double atof(String s) {
        double d = Double.valueOf(s).doubleValue();
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            System.err.print("NaN or Infinity in input\n");
            System.exit(1);
        }
        return (d);
    }

    private static int atoi(String s) {
        return Integer.parseInt(s);
    }

    private void parseArguments(String argv[]) {
        int i;
        svm_print_interface print_func = null;    // default printing to stdout

        param = new svm_parameter();
        // default values
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0;    // 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];
        cross_validation = 0;

        // parse options
        for (i = 0; i < argv.length; i++) {
            if (argv[i].charAt(0) != '-') break;
            if (++i >= argv.length)
                exit_with_help();
            switch (argv[i - 1].charAt(1)) {
                case 's':
                    param.svm_type = atoi(argv[i]);
                    break;
                case 't':
                    param.kernel_type = atoi(argv[i]);
                    break;
                case 'd':
                    param.degree = atoi(argv[i]);
                    break;
                case 'g':
                    param.gamma = atof(argv[i]);
                    break;
                case 'r':
                    param.coef0 = atof(argv[i]);
                    break;
                case 'n':
                    param.nu = atof(argv[i]);
                    break;
                case 'm':
                    param.cache_size = atof(argv[i]);
                    break;
                case 'c':
                    param.C = atof(argv[i]);
                    break;
                case 'e':
                    param.eps = atof(argv[i]);
                    break;
                case 'p':
                    param.p = atof(argv[i]);
                    break;
                case 'h':
                    param.shrinking = atoi(argv[i]);
                    break;
                case 'b':
                    param.probability = atoi(argv[i]);
                    break;
                case 'q':
                    print_func = svm_print_null;
                    i--;
                    break;
                case 'v':
                    cross_validation = 1;
                    nr_fold = atoi(argv[i]);
                    if (nr_fold < 2) {
                        System.err.print("n-fold cross validation: n must >= 2\n");
                        exit_with_help();
                    }
                    break;
                case 'w':
                    ++param.nr_weight;
                {
                    int[] old = param.weight_label;
                    param.weight_label = new int[param.nr_weight];
                    System.arraycopy(old, 0, param.weight_label, 0, param.nr_weight - 1);
                }

                {
                    double[] old = param.weight;
                    param.weight = new double[param.nr_weight];
                    System.arraycopy(old, 0, param.weight, 0, param.nr_weight - 1);
                }

                param.weight_label[param.nr_weight - 1] = atoi(argv[i - 1].substring(2));
                param.weight[param.nr_weight - 1] = atof(argv[i]);
                break;
                default:
                    System.err.print("Unknown option: " + argv[i - 1] + "\n");
                    exit_with_help();
            }
        }

        svm.svm_set_print_string_function(print_func);

        // determine filenames

        if (i >= argv.length)
            exit_with_help();

        input_file_name = argv[i];

        if (i < argv.length - 1)
            model_file_name = argv[i + 1];
        else {
            int p = argv[i].lastIndexOf('/');
            ++p;    // whew...
            model_file_name = argv[i].substring(p) + ".model";
        }
    }

    private static svm_print_interface svm_print_null = new svm_print_interface() {
        public void print(String s) {
        }
    };

    private static void exit_with_help() {
        System.out.print(
                "Usage: svm_train [options] training_set_file [model_file]\n"
                        + "options:\n"
                        + "-s svm_type : set type of SVM (default 0)\n"
                        + "	0 -- C-SVC		(multi-class classification)\n"
                        + "	1 -- nu-SVC		(multi-class classification)\n"
                        + "	2 -- one-class SVM\n"
                        + "	3 -- epsilon-SVR	(regression)\n"
                        + "	4 -- nu-SVR		(regression)\n"
                        + "-t kernel_type : set type of kernel function (default 2)\n"
                        + "	0 -- linear: u'*v\n"
                        + "	1 -- polynomial: (gamma*u'*v + coef0)^degree\n"
                        + "	2 -- radial basis function: exp(-gamma*|u-v|^2)\n"
                        + "	3 -- sigmoid: tanh(gamma*u'*v + coef0)\n"
                        + "	4 -- precomputed kernel (kernel values in training_set_file)\n"
                        + "-d degree : set degree in kernel function (default 3)\n"
                        + "-g gamma : set gamma in kernel function (default 1/num_features)\n"
                        + "-r coef0 : set coef0 in kernel function (default 0)\n"
                        + "-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)\n"
                        + "-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)\n"
                        + "-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)\n"
                        + "-m cachesize : set cache memory size in MB (default 100)\n"
                        + "-e epsilon : set tolerance of termination criterion (default 0.001)\n"
                        + "-h shrinking : whether to use the shrinking heuristics, 0 or 1 (default 1)\n"
                        + "-b probability_estimates : whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)\n"
                        + "-wi weight : set the parameter C of class i to weight*C, for C-SVC (default 1)\n"
                        + "-v n : n-fold cross validation mode\n"
                        + "-q : quiet mode (no outputs)\n"
        );
        System.exit(1);
    }
}
