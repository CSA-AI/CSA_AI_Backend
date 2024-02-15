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

import com.nighthawk.spring_portfolio.mvc.lstm.MinMaxScaler;

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
    private int featureIndex;
    private double minClose;
    private double maxClose;

    public LSTMDataSetCreator(String directory, String ticker, double splitRatio, int features, int labels, int stepCount) {
        this.directory = directory;
        this.ticker = ticker;
        this.file = directory + "/" + this.ticker + "_test.csv";
        this.splitRatio = splitRatio;
        this.features = features; // 1
        this.labels = labels; // 1
        this.stepCount = stepCount; // 60
        this.featureIndex = 4;
        this.minClose = Double.MAX_VALUE;
        this.maxClose = Double.MIN_VALUE;
    }

    public ArrayList<Double> extractDataFromCSV() {
        ArrayList<Double> data = new ArrayList<Double>(); 
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            double closeValue;
            while ((line = br.readLine()) != null) {
                closeValue = Double.parseDouble(line.split(",")[featureIndex]);
                this.minClose = Math.min(closeValue, this.minClose);
                this.maxClose = Math.max(closeValue, this.maxClose);
                data.add(closeValue);
            }
            data = MinMaxScaler.minMaxScale(data, 0, 1);
            return MinMaxScaler.minMaxScaleInverse(data, this.minClose, this.maxClose);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // public DataSet createDataset() {
    //     ArrayList<Double> closeData = extractDataFromCSV();
    //     DataSet dataset = DataSet(Nd4j.create(closeData));
    //     return dataset;
    // }
}