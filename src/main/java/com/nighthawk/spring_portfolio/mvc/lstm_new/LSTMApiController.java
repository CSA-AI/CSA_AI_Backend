package com.nighthawk.spring_portfolio.mvc.lstm_new;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.time.LocalDate;

// import com.github.nkzawa.socketio.client.Url;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.stock.Stock;

@RestController
@RequestMapping("/api/lstm")
public class LSTMApiController {
    //     @Autowired
    // private JwtTokenUtil jwtGen;
    /*
    #### RESTful API ####
    Resource: https://spring.io/guides/gs/rest-service/
    */


    /*
    GET Predictions for a stock
     */

    public static String convertPNGToBase64(String filePath) {
        String base64Data = null;
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);

            // Encode byte array to Base64
            base64Data = Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Data;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<?> getPredictions(@PathVariable String ticker) {
        File directory = new File("src/main/resources/predictions/");
        System.out.println("Directory Path: " + directory.getAbsolutePath());

        // List to store ticker symbols
        List<String> tickerSymbols = new ArrayList<>();

        File[] files = directory.listFiles();

        // Check if any files exist in the directory
        

        // Iterate through each file
        for (File file : files) {
            String fileName = file.getName();
            System.out.println("File Name: " + fileName);

            if (fileName != ticker + "_predictions.csv") {
                runScript();
                return new ResponseEntity<>("No prediction files found.", HttpStatus.NOT_FOUND);
            }

            // Check if it's a CSV file and matches the ticker
            if (fileName.endsWith("_predictions.csv")) {
                // Extract ticker symbol from file name and add to list
                String tickerFromFileName = extractTickerFromFileName(fileName);
                if (ticker.equals(tickerFromFileName)) {
                    // Parse the CSV file and return its content
                    List<String> csvData = parseCSVFile(file);
                    return new ResponseEntity<>(csvData, HttpStatus.OK);
                } else {
                    tickerSymbols.add(tickerFromFileName);
                }
            }
        }

        // If the loop completes and the ticker is not found in any file names
        return new ResponseEntity<>("Ticker not found in prediction files.", HttpStatus.NOT_FOUND);
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

    public void runScript() {
        // Define tickers
        List<String> tickers = Arrays.asList("GOOGL", "AMZN", "AAPL", "TSLA", "WMT", "MSFT", "META", "COST", "LMT", "NOC", "UNH");

        // Get current date
        LocalDate currentDate = LocalDate.now();
    }

    private static void saveDataToCSV(Stock stock, String ticker) throws IOException {
        File directory = new File("/home/david/vscode/BE_AI/src/main/resources/stock_data/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = directory.getAbsolutePath() + "/" + ticker + ".csv";
        FileWriter writer = new FileWriter(fileName);

        writer.append("Date,Symbol,Open,Close,Low,High,Volume\n");

        // Assuming stock data contains historical quotes
        for (yahoofinance.histquotes.HistoricalQuote quote : stock.getHistory()) {
            writer.append(quote.getDate().toString()).append(",");
            writer.append(ticker).append(",");
            writer.append(String.valueOf(quote.getOpen())).append(",");
            writer.append(String.valueOf(quote.getClose())).append(",");
            writer.append(String.valueOf(quote.getLow())).append(",");
            writer.append(String.valueOf(quote.getHigh())).append(",");
            writer.append(String.valueOf(quote.getVolume())).append("\n");
        }

        writer.flush();
        writer.close();
    }
}