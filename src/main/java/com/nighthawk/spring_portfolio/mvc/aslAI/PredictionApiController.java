package com.nighthawk.spring_portfolio.mvc.aslAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
// @RequestMapping("/api")

public class PredictionApiController {
    private final PredictionService predictionService;

    @Autowired
    public PredictionApiController(PredictionService predictionService) {
        this.predictionService = predictionService;
        System.out.println("---------Autowired");
    }

    // Endpoint to receive MNIST data and return the prediction
    // @PostMapping("/mnist")
    // public ResponseEntity<String> predict(@RequestBody List<List<Integer>> mnistData) {
    //     String predictionResult = predictionService.predictAndSave(mnistData);
    //     return new ResponseEntity<>(predictionResult, HttpStatus.OK);
    // }
    
    @PostMapping("/smnist")
 //   public ResponseEntity<String> receiveDataAndPredict(@RequestBody List<Integer> mnistData) {
    public ResponseEntity<String> receiveDataAndPredict(@RequestBody List<List<Integer>> mnistData) {

        System.out.println("---------PostMapping");
        String predictionResult = predictionService.predictAndSave(mnistData);
        System.out.println("predictionResult=" + predictionResult);
        return new ResponseEntity<>(predictionResult, HttpStatus.OK);
    }
/* 
    // Adjusted to match the corrected constructor name
    @PostMapping("/prediction")
    public ResponseEntity<Prediction> createPrediction(@RequestBody String prediction) {
        Prediction savedPrediction = predictionService.savePrediction(prediction);
        return new ResponseEntity<>(savedPrediction, HttpStatus.CREATED);
    }*/

    @GetMapping("/predictions")
    public ResponseEntity<List<Prediction>> getAllPredictions() {
        List<Prediction> predictions = predictionService.getAllPredictions();
        return new ResponseEntity<>(predictions, HttpStatus.OK);
    }
}