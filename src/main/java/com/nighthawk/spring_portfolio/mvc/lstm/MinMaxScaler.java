// package com.nighthawk.spring_portfolio.mvc.lstm;

// import java.util.ArrayList;
// import java.util.Collections;

// public class MinMaxScaler {
    
//     // Min-max scaling function
//     public static ArrayList<Double> minMaxScale(ArrayList<Double> data, double min, double max) {
//         ArrayList<Double> scaledData = new ArrayList<>();
//         double dataMin = Collections.min(data);
//         double dataMax = Collections.max(data);
        
//         for (double value : data) {
//             double scaledValue = min + (value - dataMin) * (max - min) / (dataMax - dataMin);
//             scaledData.add(scaledValue);
//         }
        
//         return scaledData;
//     }
    
//     // Min-max scaling inverse (descale) function
//     public static ArrayList<Double> minMaxScaleInverse(ArrayList<Double> normalizedList, double min, double max) {
//         ArrayList<Double> descaledList = new ArrayList<>();
        
//         for (double normalizedValue : normalizedList) {
//             double descaledValue = normalizedValue * (max - min) + min;
//             descaledList.add(descaledValue);
//         }
        
//         return descaledList;
//     }
// }

