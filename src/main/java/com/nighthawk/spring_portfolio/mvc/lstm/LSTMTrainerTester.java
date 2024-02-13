package com.nighthawk.spring_portfolio.mvc.lstm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.deeplearning4j.eval.ROC;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
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
    private int stepCount; //1
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
        NormalizerMinMaxScaler minMaxScaler = new NormalizerMinMaxScaler(0,1);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            System.out.println(line);
            for (int i = 0; i < 120; i ++) {
                double[][][] featureMatrix = new double[batchSize][this.features][this.stepCount];
                double[][][] labelsMatrix = new double[batchSize][this.labels][this.stepCount];
                for (int batch = 0; batch < this.batchSize; batch++) {
                    line = br.readLine();
                    String[] values = line.split(",");
                    featureMatrix[batch][0][0] = Double.parseDouble(values[1]); // OPEN
                    featureMatrix[batch][1][0] = Double.parseDouble(values[2]); // HIGH
                    featureMatrix[batch][2][0] = Double.parseDouble(values[4]); // LOW
                    labelsMatrix[batch][0][0] = Double.parseDouble(values[3]); // CLOSE
                }
                INDArray featuresArray = Nd4j.create(featureMatrix);
                INDArray labelsArray = Nd4j.create(labelsMatrix);
                DataSet train = new DataSet(featuresArray, labelsArray);
                minMaxScaler.fit(train);
                // System.out.println(train);
                net.fit(train);
                net.rnnClearPreviousState();
            }
            for (int i = 0; i<10; i++) {
                double[][][] featureMatrix = new double[batchSize][this.features][this.stepCount];
                double[][][] labelsMatrix = new double[batchSize][this.labels][this.stepCount];
                for (int batch = 0; batch < this.batchSize; batch++) {
                    line = br.readLine();
                    String[] values = line.split(",");
                    featureMatrix[batch][0][0] = Double.parseDouble(values[1]); // OPEN
                    featureMatrix[batch][1][0] = Double.parseDouble(values[2]); // HIGH
                    featureMatrix[batch][2][0] = Double.parseDouble(values[4]); // LOW
                    labelsMatrix[batch][0][0] = Double.parseDouble(values[3]); // CLOSE
                }
                INDArray featuresArray = Nd4j.create(featureMatrix);
                INDArray labelsArray = Nd4j.create(labelsMatrix);
                DataSet test = new DataSet(featuresArray, labelsArray);
                minMaxScaler.fit(test);
                INDArray output = net.output(test.getFeatures());
                ArrayList<Double> outputs = new ArrayList<Double>();
                ArrayList<Double> actual = new ArrayList<Double>();
                System.out.println("Output: ");
                System.out.println(output);
                System.out.println("Actual: ");
                System.out.println(test.getLabels());
                for (int d=0; d < output.shape()[0]; d++ ){
                    outputs.add(output.getDouble(d,0,0));
                    actual.add(test.getLabels().getDouble(d,0,0));
                }
                roc.evalTimeSeries(test.getLabels(), output);
            }
            
            System.out.println("FINAL TEST AUC: " + roc.calculateAUC());
            File locationToSave = new File("src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/StockPriceLSTM_".concat("CLOSE").concat(".zip"));
            ModelSerializer.writeModel(net, locationToSave, true);
            //net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
}
