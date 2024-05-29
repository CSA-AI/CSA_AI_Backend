package com.nighthawk.spring_portfolio.mvc.aslAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nighthawk.spring_portfolio.mvc.person.*;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Stream;

@Service
public class PredictionService {
    @Autowired
    private PersonJpaRepository personRepo;

    @Autowired
    private PredictionRepository predictionRepository;

    double[][] weights = new double[7][785];  // Weights are available to all classes within PredictionService

    @PostConstruct
    public void init() {
        trainLogic();
    }

    public String predictAndSave(List<List<Integer>> mnistData) {
        System.out.println("---------predictAndSave");
        String predictionResult = predictionLogic(mnistData);
        savePrediction(predictionResult);
        return predictionResult;
    }

    public List<List<Integer>> readCSV(String fileName) throws IOException {
        List<List<Integer>> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                List<Integer> row = parseLine(line);
                data.add(row);
            }
        }

        return data;
    }

    private List<Integer> parseLine(String line) {
        List<Integer> row = new ArrayList<>();
        int start = 0;
        int end = 0;

        while (end < line.length()) {
            if (line.charAt(end) == ',') {
                String value = line.substring(start, end).trim();
                try {
                    row.add(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping non-numeric value: " + value);
                }
                start = end + 1;
            }
            end++;
        }

        String value = line.substring(start).trim();
        try {
            row.add(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            System.out.println("Skipping non-numeric value: " + value);
        }

        return row;
    }

    public String trainLogic() {
        System.out.println("---------trainLogic");

        try {
            List<List<Integer>> data = readAllCSVFiles("src/main/java/com/nighthawk/spring_portfolio/mvc/datalogin/csv");
            if (data.isEmpty()) {
                return "No data to train.";
            }

            int lg = data.size();
            int lg1 = data.get(0).size();
            int[][] x = new int[lg - 1][lg1];

            for (int i = 0; i < lg - 1; i++) {
                for (int j = 0; j < lg1; j++) {
                    x[i][j] = data.get(i + 1).get(j);
                }
            }

            int[] digit = {0, 1, 2, 3, 4, 5, 6};
            int m = weights[0].length - 1;
            List<Person> alphabet = personRepo.findAllByOrderByNameAsc();
            List<Person> orderedById = new ArrayList<>(alphabet);
            List<String> emailsOnly = new ArrayList<>();
            for (Person person : orderedById) {
                emailsOnly.add(person.getEmail());
            }

            orderedById.sort((p1, p2) -> Long.compare(p1.getId(), p2.getId()));
            double rate = 0.5;
            int epoch = 50;

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
                    System.out.println("Letter: " + emailsOnly.get(s) + ", Epoch: " + ii + ", Error: " + error);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV files: " + e.getMessage());
            return "Error";
        }

        return "Training complete.";
    }

    private List<List<Integer>> readAllCSVFiles(String directoryPath) throws IOException {
        List<List<Integer>> allData = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".csv"))
                 .forEach(path -> {
                     try {
                         allData.addAll(readCSV(path.toString()));
                     } catch (IOException e) {
                         System.err.println("Error reading file " + path + ": " + e.getMessage());
                     }
                 });
        }

        return allData;
    }

    private String predictionLogic(List<List<Integer>> mnistData) {
        List<Person> alphabet = personRepo.findAllByOrderByNameAsc();
        List<Person> orderedById = new ArrayList<>(alphabet);
        List<String> emailsOnly = new ArrayList<>();
        for (Person person : orderedById) {
            emailsOnly.add(person.getEmail());
        }

        int lgt = mnistData.size();
        int lg1t = mnistData.get(0).size() - 1;
        System.out.println(lgt + ", " + lg1t);
        int s0 = 0;
        int[][] xt = new int[lgt][lg1t];

        for (int i = 0; i < lgt; i++) {
            for (int j = 0; j < lg1t; j++) {
                xt[i][j] = mnistData.get(i).get(j + 1);
            }
        }

        for (int[] innerArray : xt) {
            System.out.println(Arrays.toString(innerArray));
        }

        double pred0 = -100000000;
        for (int s = 0; s < 7; s++) {
            double y_pred = weights[s][0];
            for (int k = 0; k < lg1t; k++) {
                y_pred += weights[s][k + 1] * xt[lgt - 1][k];
            }
            if (y_pred > pred0) {
                pred0 = y_pred;
                s0 = s;
            }
        }

        return emailsOnly.get(s0);
    }

    public Prediction savePrediction(String prediction) {
        Prediction newPrediction = new Prediction(prediction, LocalDateTime.now());
        return predictionRepository.save(newPrediction);
    }

    public List<Prediction> getAllPredictions() {
        return predictionRepository.findAll();
    }
}
