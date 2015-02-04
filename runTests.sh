#!/bin/bash

# create subfolder for test extraction
rm testing -R -f
mkdir testing
cd testing

# copy zip into folder
cp ../similar_author.zip .
unzip -q similar_author.zip

# call each component
    # parameters for FeatureComponent: <data-set-id> <language> <limit>
    java -cp similar_author_identification.jar de.hpi.smm.components.FeatureComponent 1 de 1000

    # parameters for MahoutComponent: <data-set-id> <language> <k> <max-iterations>
    java -cp similar_author_identification.jar de.hpi.smm.components.MahoutComponent 1 de 10 15

    # parameters for ResultComponent: <data-set-id> <run-id> <language>
    java -cp similar_author_identification.jar de.hpi.smm.components.ResultComponent 1 4 de

    # parameters for LabelComponent: <run-id> <language> <label-count>
    java -cp similar_author_identification.jar de.hpi.smm.components.LabelComponent 4 de 3

    # parameters for SvmComponent: <data-set-id> <run-id> <language> <svm-model-file>
    java -cp similar_author_identification.jar de.hpi.smm.components.SvmComponent 1 4 de testmodel.model

    # parameters for ClusterComponent: <data-set-id> <run-id> <language> <method> <k> <svm-model-file> <blog-post-file>
    java -cp similar_author_identification.jar de.hpi.smm.components.ClusterComponent 1 4 de svm 10 testmodel.model ../res/blog_post.txt
    java -cp similar_author_identification.jar de.hpi.smm.components.ClusterComponent 1 4 de k-nearest 10 testmodel.model ../res/blog_post.txt
    java -cp similar_author_identification.jar de.hpi.smm.components.ClusterComponent 1 4 de euclidean 10 testmodel.model ../res/blog_post.txt

# switch back into root folder
cd ..