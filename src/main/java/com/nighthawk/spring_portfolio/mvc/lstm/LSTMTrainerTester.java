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
public class LSTMTrainerTester {

    private String file;
    private double splitRatio;
    private int labelIndex;
    private int features; //5
    private int labels; // 1
    private int batchSize; //32
    private int stepCount; //10
    private ROC roc;
    private DataSetIterator iterator;

    public LSTMTrainerTester(String directory, String ticker, int features, int labels, int batchSize, int stepCount) {
        this.file = directory + "/" + ticker + "_test.csv";
        this.features = features;
        this.labels = labels;
        this.batchSize = batchSize;
        this.stepCount = stepCount;
        this.roc = new ROC(100);
    }

    public void TrainAndTestModel(MultiLayerNetwork net) {
        ArrayList<Double> actual = new ArrayList<Double>();
        ArrayList<Double> predicted = new ArrayList<Double>();
        NormalizerMinMaxScaler minMaxScaler = new NormalizerMinMaxScaler(0,1);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            System.out.println(line);
            for (int i = 0; i < 10; i ++) {
                System.out.println("Training Dataset " + i);
                double[][][] featureMatrix = new double[batchSize][this.features][this.stepCount];
                double[][][] labelsMatrix = new double[batchSize][this.labels][this.stepCount];
                for (int batch = 0; batch < this.batchSize; batch++) {
                    for (int series = 0; series < this.stepCount; series++){
                        line = br.readLine();
                        String[] values = line.split(",");
                        featureMatrix[batch][0][series] = Double.parseDouble(values[1]); // OPEN
                        featureMatrix[batch][1][series] = Double.parseDouble(values[2]); // HIGH
                        featureMatrix[batch][2][series] = Double.parseDouble(values[3]); // LOW
                        labelsMatrix[batch][0][series] = Double.parseDouble(values[4]); // CLOSE
                    }  
                }
                INDArray featuresArray = Nd4j.create(featureMatrix);
                INDArray labelsArray = Nd4j.create(labelsMatrix);
                DataSet train = new DataSet(featuresArray, labelsArray);
                minMaxScaler.fit(train);
                // System.out.println(train);
                net.fit(train);
                net.rnnClearPreviousState();
            }
            System.out.println("Testing Dataset");
            double[][][] featureMatrix = new double[1][this.features][this.stepCount];
            double[][][] labelsMatrix = new double[1][this.labels][this.stepCount];
            for (int batch = 0; batch < this.stepCount; batch++) {
                line = br.readLine();
                String[] values = line.split(",");
                featureMatrix[0][0][batch] = Double.parseDouble(values[1]); // OPEN
                featureMatrix[0][1][batch] = Double.parseDouble(values[2]); // HIGH
                featureMatrix[0][2][batch] = Double.parseDouble(values[3]); // LOW
                labelsMatrix[0][0][batch] = Double.parseDouble(values[4]); // CLOSE
            }   
            INDArray featuresArray = Nd4j.create(featureMatrix);
            INDArray labelsArray = Nd4j.create(labelsMatrix);
            DataSet test = new DataSet(featuresArray, labelsArray);
            minMaxScaler.fit(test);
            INDArray output = net.output(test.getFeatures());
            for (int j = 0; j < this.stepCount; j++) {
                actual.add(test.getLabels().getDouble(0,0,j));
                predicted.add(output.getDouble(0,0,j));
            }
            roc.evalTimeSeries(test.getLabels(), output);
            System.out.println("Output: ");
            System.out.println(predicted);
            System.out.println("Actual: ");
            System.out.println(actual);
                
            System.out.println("FINAL TEST AUC: " + roc.calculateAUC());
            line = br.readLine();
            System.out.println(line);
                // File locationToSave = new File("src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/StockPriceLSTM_".concat("CLOSE").concat(".zip"));
                // ModelSerializer.writeModel(net, locationToSave, true);
            LSTMGraph plotter = new LSTMGraph(actual, predicted);
            System.out.println("Image created");
                //net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
        e.printStackTrace();
    }
}
}
