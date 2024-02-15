package com.nighthawk.spring_portfolio.mvc.lstm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.deeplearning4j.eval.ROC;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class LSTMDataSetCreator {
    private String directory;
    private String ticker;
    private String file;
    private double splitRatio;
    private int features;
    private int labels;
    private int batchSize;
    private int stepCount;
    public LSTMDataSetCreator(String directory, String ticker, double splitRatio, int features, int labels, int batchSize, int stepCount) {
        this.directory = directory;
        this.ticker = ticker;
        this.file = directory + "/" + this.ticker + "_test.csv";
        this.splitRatio = splitRatio;
        this.features = features; // 1
        this.labels = labels; // 1
        this.batchSize = batchSize; //
        this.stepCount = stepCount; // 60
    }
}