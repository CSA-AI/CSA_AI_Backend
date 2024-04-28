package com.nighthawk.spring_portfolio.mvc.lstm_new;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.io.FileWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.lstm_new.predict.StockPricePrediction;

@RestController
@RequestMapping("/api/lstm")
public class LSTMApiController {

    @GetMapping("/{ticker}")
    public ResponseEntity<?> getPredictions(@PathVariable String ticker) {
        // Directory containing prediction files
        File directory = new File("src/main/resources/predictions/");
        System.out.println("Directory Path: " + directory.getAbsolutePath());

        // Set to store predicted tickers
        Set<String> predictedTickers = new HashSet<>();

        // Retrieve predicted tickers before looping through prediction files
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String tickerFromFileName = extractTickerFromFileName(fileName);
                if (tickerFromFileName != null) {
                    predictedTickers.add(tickerFromFileName);
                }
            }
        } else {
            // Handle the case when the directory is empty or does not exist
            return new ResponseEntity<>("No prediction files found.", HttpStatus.NOT_FOUND);
        }

        if (predictedTickers.contains(ticker)) {
            File csvFile = new File("src/main/resources/predictions/" + ticker + "_predictions.csv");
            if (csvFile.exists()) {
                List<String> csvData = parseCSVFile(csvFile);
                return new ResponseEntity<>(csvData, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Ticker found in prediction files but CSV data not found.", HttpStatus.NOT_FOUND);
            }
        } else {
            File stockInfo = new File("src/main/resources/stock_data/" + ticker + ".csv");
            if (stockInfo.exists()) {
                try {
                    StockPricePrediction.main(new String[]{}, ticker);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new ResponseEntity<>("Training", HttpStatus.OK);
            } else {
                CompletableFuture.runAsync(() -> {
                    String scriptPath = "/home/david/vscode/BE_AI/src/main/resources/pull_data.sh";
                    System.out.println("Script path: " + scriptPath);

                    ProcessBuilder pb = new ProcessBuilder("/bin/bash", scriptPath);
                    pb.directory(new File("src/main/resources/"));

                    try {
                        Process process = pb.start();
                        int exitCode = process.waitFor();
                        System.out.println("Script exited with code: " + exitCode);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                return new ResponseEntity<>("Pulling Data", HttpStatus.OK);
            }
        }
    }


    private List<String> parseCSVFile(File file) {
        List<String> csvData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                csvData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception
        }
        return csvData;
    }

    private String extractTickerFromFileName(String fileName) {
        // Assuming file name format is "ticker_predictions.csv"
        int endIndex = fileName.lastIndexOf("_predictions.csv");
        if (endIndex != -1) {
            return fileName.substring(0, endIndex);
        } else {
            return ""; // Handle error case
        }
    }
}