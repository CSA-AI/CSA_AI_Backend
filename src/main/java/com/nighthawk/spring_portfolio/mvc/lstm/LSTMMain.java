package com.nighthawk.spring_portfolio.mvc.lstm;

import java.io.IOException;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;

import java.io.File;

import com.nighthawk.spring_portfolio.mvc.lstm.LSTMNetModel;
import com.nighthawk.spring_portfolio.mvc.lstm.LSTMTrainerTester;

public class LSTMMain {
    public LSTMMain(String ticker1) {
        String directory = "src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/stock_data";
        String ticker = ticker1; // stock name

        System.out.println("Create dataSet iterator...");
   
        LSTMTrainerTester iterator1 = new LSTMTrainerTester(directory, ticker, 3, 1, 32, 10);
            
        MultiLayerNetwork net = LSTMNetModel.buildLstmNetworks(iterator1.getFeatures(), iterator1.getLabels());

        iterator1.TrainAndTestModel(net);
    }
}