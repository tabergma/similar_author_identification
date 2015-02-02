#!/bin/bash

rm similar_author.zip
zip -r similar_author.zip res/stanford-postagger-full-2014-08-27/models/english-left3words-distsim.tagger res/stanford-postagger-full-2014-08-27/models/german-fast.tagger res/langdetect-03-03-2014/profiles/ res/FunctionWords_de.txt res/Abbreviations_de.txt

cd build/classes/artifacts/similar_author_identification_jar/
zip -r ../../../../similar_author.zip *