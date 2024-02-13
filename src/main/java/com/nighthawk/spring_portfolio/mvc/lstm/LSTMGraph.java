package com.nighthawk.spring_portfolio.mvc.lstm;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LSTMGraph extends JFrame {

    public LSTMGraph(ArrayList<Double> actual, ArrayList<Double> predicted) {
        super("Actual vs Predicted stock prices");

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
                "Line Plot Example",
                "Time",
                "Price",
                dataset
        );

        // Create and configure the panel to display the chart
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        // Add the panel to the frame
        setContentPane(chartPanel);

        // Save the chart to an image file
        try {
            File imageFile = new File("line_chart.png");
            ChartUtils.saveChartAsPNG(imageFile, chart, 800, 600);
            System.out.println("Chart saved to: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}