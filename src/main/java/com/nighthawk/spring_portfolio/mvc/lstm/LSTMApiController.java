package com.nighthawk.spring_portfolio.mvc.lstm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.cpu.nativecpu.bindings.Nd4jCpu.lstm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nighthawk.spring_portfolio.mvc.lstm.*;

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
        String imagePath = "src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/graphs/line_chart.png";

        // Path imagePath = Paths.get(directory, ticker);
        ArrayList<String> tickers = new ArrayList<String>(Arrays.asList("GOOGL", "AMZN", "AAPL", "TSLA", "WMT", "MSFT", "META", "COST", "LMT", "NOC", "UNH"));
        if (tickers.contains(ticker)) {
            //LSTMMain model = new LSTMMain(ticker);
            //String base64Data = convertPNGToBase64(imagePath);
            LSTMDataSetCreator lstmDataSetCreator = new LSTMDataSetCreator("/home/eris29/APCSA/CSA_AI_Backend/src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/stock_data", ticker, 0.9, 1, 1, 60);
            DataSet train = lstmDataSetCreator.createTrainDataset();
            DataSet test = lstmDataSetCreator.createTestDataset();
            MultiLayerNetwork net = LSTMNetModel.buildLstmNetworks(60, 1); // 1 feature, 1 label and that is CLOSE
            net.fit(train);
            INDArray output = net.output(test.getFeatures());
            ArrayList<Double> unNormalizedOutput = new ArrayList<Double>();
            for (int i = 0; i < output.data().asDouble().length; i++) {
                unNormalizedOutput.add((Double) output.data().asDouble()[i]);
            }
            return new ResponseEntity<>( MinMaxScaler.minMaxScaleInverse(unNormalizedOutput, lstmDataSetCreator.getMinClose(), lstmDataSetCreator.getMaxClose()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
        
    }

}
