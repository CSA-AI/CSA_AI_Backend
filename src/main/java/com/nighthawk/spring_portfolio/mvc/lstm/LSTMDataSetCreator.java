package com.nighthawk.spring_portfolio.mvc.lstm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataSet createTrainDataset() {
        ArrayList<Double> closeData = extractDataFromCSV();
        int maxIndex = (int) Math.ceil(closeData.size()*(this.splitRatio));
        double[][][] XTrain = new double[maxIndex-60+1][60][1];
        double[][][] yTrain = new double[maxIndex-60+1][1][1];
        for (int i = 60; i < maxIndex; i++){
            for (int j = 0; j < this.stepCount; j++) {
                XTrain[i-60][j][0] = closeData.get(i-60+j);
            }
            yTrain[i-60][0][0] = closeData.get(i);
        }
        INDArray XTrainArray = Nd4j.create(XTrain); 
        INDArray yTrainArray = Nd4j.create(yTrain);
        DataSet trainingDataSet = new DataSet(XTrainArray, yTrainArray);
        return trainingDataSet;
    }

    public DataSet createTestDataset() {
        ArrayList<Double> closeData = extractDataFromCSV();
        int startIndex = (int) Math.ceil(closeData.size()*(this.splitRatio));
        List<Double> testData = closeData.subList(startIndex, closeData.size());
        int maxIndex = testData.size();
        double[][][] XTest = new double[maxIndex-60+1][60][1];
        double[][][] yTest = new double[maxIndex-60+1][1][1];
        for (int i = 60; i < maxIndex; i++){
            for (int j = 0; j < this.stepCount; j++) {
                XTest[i-60][j][0] = testData.get(i-60+j);
            }
            yTest[i-60][0][0] = testData.get(i);
        }
        INDArray XTestArray = Nd4j.create(XTest); 
        INDArray yTestArray = Nd4j.create(yTest);
        DataSet testingDataSet = new DataSet(XTestArray, yTestArray);
        return testingDataSet;
    }
}