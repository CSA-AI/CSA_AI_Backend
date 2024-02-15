package com.nighthawk.spring_portfolio.mvc.lstm;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LSTMGraph {

    public LSTMGraph(ArrayList<Double> actual, ArrayList<Double> predicted) {
        
        // Create datasets
        XYSeries series1 = new XYSeries("Actual");
        for (int i = 0; i < actual.size(); i++) {
            series1.add(i, actual.get(i));
        }

        XYSeries series2 = new XYSeries("Predicted");
        for (int i = 0; i < predicted.size(); i++) {
            series2.add(i, predicted.get(i));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);

        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Actual vs Predicted stock prices",
                "Time",
                "Price",
                dataset
        );

        // Save the chart to an image file
        try {
            File imageFile = new File("src/main/java/com/nighthawk/spring_portfolio/mvc/lstm/resources/graphs/line_chart.png");
            ChartUtils.saveChartAsPNG(imageFile, chart, 800, 600);
            System.out.println("Chart saved to: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}