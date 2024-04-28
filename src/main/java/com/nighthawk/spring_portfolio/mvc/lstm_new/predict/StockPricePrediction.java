package com.nighthawk.spring_portfolio.mvc.lstm_new.predict;

import com.nighthawk.spring_portfolio.mvc.lstm_new.model.RecurrentNets;
import com.nighthawk.spring_portfolio.mvc.lstm_new.representation.PriceCategory;
import com.nighthawk.spring_portfolio.mvc.lstm_new.representation.StockDataSetIterator;
// import com.nighthawk.spring_portfolio.mvc.lstm_new.utils.PlotUtil;
import javafx.util.Pair;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.io.FileWriter;

public class StockPricePrediction {

    private static int exampleLength = 22; // time series length, assume 22 working days per month

    public static void main (String[] args, String ticker) throws IOException {
        String symbol = ticker; // stock name
        String file = new ClassPathResource("stock_data/" + symbol + ".csv").getFile().getAbsolutePath();
        int batchSize = 32; // mini-batch size // 64
        double splitRatio = 0.85; // 90% for training, 10% for testing // 0.9
        int epochs = 5; // training epochs // 100

        System.out.println("Create dataSet iterator...");
        PriceCategory category = PriceCategory.CLOSE; // CLOSE: predict close price
        StockDataSetIterator iterator = new StockDataSetIterator(file, symbol, batchSize, exampleLength, splitRatio, category);
        System.out.println("Load test dataset...");
        List<Pair<INDArray, INDArray>> test = iterator.getTestDataSet();

        System.out.println("Build lstm networks...");
        MultiLayerNetwork net = RecurrentNets.buildLstmNetworks(iterator.inputColumns(), iterator.totalOutcomes());

        System.out.println("Training...");
        for (int i = 0; i < epochs; i++) {
            while (iterator.hasNext()) net.fit(iterator.next()); // fit model using mini-batch data
            iterator.reset(); // reset iterator
            net.rnnClearPreviousState(); // clear previous state
        }

        System.out.println("Saving model...");
        File locationToSave = new File("src/main/resources/StockPriceLSTM_".concat(String.valueOf(category)).concat(".zip"));
        // saveUpdater: i.e., the state for Momentum, RMSProp, Adagrad etc. Save this to train your network more in the future
        ModelSerializer.writeModel(net, locationToSave, true);

        System.out.println("Load model...");
        net = ModelSerializer.restoreMultiLayerNetwork(locationToSave);

        System.out.println("Testing...");
        if (category.equals(PriceCategory.ALL)) {
            INDArray max = Nd4j.create(iterator.getMaxArray());
            INDArray min = Nd4j.create(iterator.getMinArray());
            predictAllCategories(net, test, max, min);
        } else {
            double max = iterator.getMaxNum(category);
            double min = iterator.getMinNum(category);
            predictPriceOneAhead(net, test, max, min, category, symbol);
        }
        System.out.println("Done...");
    }

    /** Predict one feature of a stock one-day ahead */
    private static void predictPriceOneAhead (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min, PriceCategory category, String symbol) {
        double[] predicts = new double[testData.size()];
        double[] actuals = new double[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getDouble(exampleLength - 1) * (max - min) + min;
            actuals[i] = testData.get(i).getValue().getDouble(0);
        }
        System.out.println("Print out Predictions and Actual Values...");
        System.out.println("Predict,Actual");
        for (int i = 0; i < predicts.length; i++) System.out.println(predicts[i] + "," + actuals[i]);

        String fileName = "src/main/resources/predictions/" + symbol + "_predictions.csv";

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.append("Index,PredictedValue\n");
            for (int i = 0; i < predicts.length; i++) {
                writer.append(String.valueOf(i))
                      .append(",")
                      .append(String.valueOf(predicts[i]))
                      .append("\n");
            }
            writer.close();
            System.out.println("CSV file has been created successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("Plot...");
        // PlotUtil.plot(predicts, actuals, String.valueOf(category));
    }

    private static void predictPriceMultiple (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, double max, double min) {
        // TODO                             
    }

    /** Predict all the features (open, close, low, high prices and volume) of a stock one-day ahead */
    private static void predictAllCategories (MultiLayerNetwork net, List<Pair<INDArray, INDArray>> testData, INDArray max, INDArray min) {
        INDArray[] predicts = new INDArray[testData.size()];
        INDArray[] actuals = new INDArray[testData.size()];
        for (int i = 0; i < testData.size(); i++) {
            predicts[i] = net.rnnTimeStep(testData.get(i).getKey()).getRow(exampleLength - 1).mul(max.sub(min)).add(min);
            actuals[i] = testData.get(i).getValue();
        }
        System.out.println("Print out Predictions and Actual Values...");
        System.out.println("Predict\tActual");
        for (int i = 0; i < predicts.length; i++) System.out.println(predicts[i] + "\t" + actuals[i]);
        System.out.println("Plot...");
        for (int n = 0; n < 5; n++) {
            double[] pred = new double[predicts.length];
            double[] actu = new double[actuals.length];
            for (int i = 0; i < predicts.length; i++) {
                pred[i] = predicts[i].getDouble(n);
                actu[i] = actuals[i].getDouble(n);
            }
            String name;
            switch (n) {
                case 0: name = "Stock OPEN Price"; break;
                case 1: name = "Stock CLOSE Price"; break;
                case 2: name = "Stock LOW Price"; break;
                case 3: name = "Stock HIGH Price"; break;
                case 4: name = "Stock VOLUME Amount"; break;
                default: throw new NoSuchElementException();
            }
            // PlotUtil.plot(pred, actu, name);
        }
    }

}
