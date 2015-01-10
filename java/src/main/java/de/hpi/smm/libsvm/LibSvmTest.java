package de.hpi.smm.libsvm;

import libsvm.*;

public class LibSvmTest {
    double[][] train = new double[1000][];
    double[][] test = new double[10][];

    public static void main(String[] args) throws Exception {
        new LibSvmTest();
    }

    public LibSvmTest(){
        for (int i = 0; i < train.length; i++) {
            if (i + 1 > (train.length / 2)) {        // 50% positive
                double[] vals = {1, 0, i + i};
                train[i] = vals;
            } else {
                double[] vals = {0, 0, i - i - i - 2}; // 50% negative
                train[i] = vals;
            }
        }
        test[0] = new double[]{0, 0, 1};
        test[1] = new double[]{0, 0, 2};
        test[2] = new double[]{0, 0, 4};
        test[3] = new double[]{0, 0, -6};
        test[4] = new double[]{0, 0, 10};
        test[5] = new double[]{1, 0, -5};
        test[6] = new double[]{1, 0, -6};
        test[7] = new double[]{1, 0, -7};
        test[8] = new double[]{1, 0, 8};
        test[9] = new double[]{1, 0, -9};

        svm_model model = svmTrain();
        for (int i = 0; i < test.length; i++){
            evaluate(test[i], model);
        }
    }

    private svm_model svmTrain() {
        svm_problem prob = new svm_problem();
        int dataCount = train.length;
        prob.y = new double[dataCount];
        prob.l = dataCount;
        prob.x = new svm_node[dataCount][];

        for (int i = 0; i < dataCount; i++){
            double[] features = train[i];
            prob.x[i] = new svm_node[features.length-1];
            for (int j = 1; j < features.length; j++){
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j-1] = node;
            }
            prob.y[i] = features[0];
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;

        svm_model model = svm.svm_train(prob, param);

        return model;
    }

    public double evaluate(double[] features, svm_model model)
    {
        svm_node[] nodes = new svm_node[features.length-1];
        for (int i = 1; i < features.length; i++)
        {
            svm_node node = new svm_node();
            node.index = i;
            node.value = features[i];

            nodes[i-1] = node;
        }

        int totalClasses = 2;
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model,labels);

        double[] prob_estimates = new double[totalClasses];
        double v = svm.svm_predict_probability(model, nodes, prob_estimates);

        for (int i = 0; i < totalClasses; i++){
            System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
        }
        System.out.println("(Actual:" + features[0] + " Prediction:" + v + ")");

        return v;
    }
}
