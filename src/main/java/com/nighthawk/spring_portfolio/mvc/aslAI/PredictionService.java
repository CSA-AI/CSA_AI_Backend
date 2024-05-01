package com.nighthawk.spring_portfolio.mvc.aslAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Service
public class PredictionService {
    private final PredictionRepository predictionRepository;
    double[][] weights = new double[7][785];  //weights are available to all classes within PredictionService

    @Autowired
    public PredictionService(PredictionRepository predictionRepository) {
        this.predictionRepository = predictionRepository;
    }

    // Here we want to run trainLogic method once after constructor to train the network by data from online repository
    @PostConstruct
    public void init() {
        trainLogic();
    }

 //   public String predictAndSave(List<Integer> mnistData) {
    public String predictAndSave(List<List<Integer>> mnistData) {   
        System.out.println("---------predictAndSave");
        //Here we run predictionLogic everytime new "mnistData" is obtained
        String predictionResult = predictionLogic(mnistData);
        savePrediction(predictionResult); // Save without needing to return the saved entity
        return predictionResult; // Return the result directly
    }

    
    private List<List<Integer>> readCSV(String fileName) throws IOException {
        List<List<Integer>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true; // To skip the header row
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the header row
                }
                String[] values = line.split(",");
                List<Integer> row = new ArrayList<>();
                for (String val : values) {
                    try {
                        row.add(Integer.parseInt(val.trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping non-numeric value: " + val);
                        // Handle the non-numeric value, or continue
                    }
                }
                data.add(row);
            }
        }
        return data;
    }
 
    // Assuming this is your train logic method; This is the methos to train only by online data
    public String trainLogic() {
        System.out.println("---------trainLogic");
        List<List<Integer>> data = new ArrayList<>();
        try {
            data = readCSV("hand_login_train.csv");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            // Handle the exception, e.g., log it or return a default value
            return "Error"; // or any other error handling mechanism
        }
        int lg = data.size();
        int lg1 = data.get(0).size();

        int[][] x = new int[lg - 1][lg1];
        for (int i = 0; i < lg - 1; i++) {
            for (int j = 0; j < lg1; j++) {
                x[i][j] = data.get(i + 1).get(j);
            }
        }

 //       double[][] weights = new double[25][lg1];
        int[] digit = {0, 1, 2, 3, 4, 5, 6};
        String[] alphabet = {"Anthony", "Tay", "Emaad", "Mr. Mort", "David", "Alex", "James"};
        int m = weights[0].length - 1;

        double rate = 0.5;
        int epoch = 50;  //change to 50 in final version
        for (int s = 0; s < 7; s++) {
            for (int ii = 0; ii < epoch; ii++) {
                double error = 0.0;
                for (int i = 0; i < x.length; i++) {
                    double y_pred = weights[s][0];
                    for (int k = 0; k < m; k++) {
                        y_pred += weights[s][k + 1] * x[i][k + 1];
                    }

                    double pred = y_pred >= 0.0 ? 1.0 : 0.0;
                    double expect = x[i][0] == digit[s] ? 1.0 : 0.0;
                    double err = pred - expect;
                    error += err * err;

                    weights[s][0] -= rate * err;
                    for (int k = 0; k < m; k++) {
                        weights[s][k + 1] -= rate * err * x[i][k + 1];
                    }
                }
                System.out.println("Letter: " + alphabet[s] + ", Epoch: " + ii + ", Error: " + error);
            }
        }
        return null;
    }

    // Assuming this is your prediction logic method
 //   private String predictionLogic(List<Integer> mnistData) {
    private String predictionLogic(List<List<Integer>> mnistData) {       
        String[] alphabet = {"Anthony", "Tay", "Emaad", "Mr. Mort", "David", "Alex", "James"};

        // To test using mnistData
        List<List<Integer>> test = mnistData;
 //       List<Integer> test = mnistData;

        int lgt = test.size();
        int lg1t = test.get(0).size() - 1;
        System.out.println(lgt + ", " + lg1t);
        int s0 = 0;
        int[][] xt = new int[lgt][lg1t];
        for (int i = 0; i < lgt; i++) {
            for (int j = 0; j < lg1t; j++) {
                xt[i][j] = test.get(i).get(j+1);
            }
        }
        for (int[] innerArray : xt) {
            System.out.println(Arrays.toString(innerArray));
        }
        double pred0 = -100000000;
 //       for (int ii = 0; ii < lgt - 1; ii++) {
            for (int s = 0; s < 7; s++) {
                double y_pred = weights[s][0];
                for (int k = 0; k < lg1t; k++) {
                    y_pred += weights[s][k + 1] * xt[lgt-1][k];
 //                   System.out.println(xt[lgt-1][k]);
                }
                if (y_pred > pred0) {
                    pred0 = y_pred;
                    s0 = s;
                }
            }
 //       }

        

 //       s0=11;  //for test only , REMOVE!
        return alphabet[s0];
    }

    public Prediction savePrediction(String prediction) {
        Prediction newPrediction = new Prediction(prediction, LocalDateTime.now());
        return predictionRepository.save(newPrediction);
    }

    public List<Prediction> getAllPredictions() {
        return predictionRepository.findAll();
    }
}