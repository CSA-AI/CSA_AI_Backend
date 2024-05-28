package com.nighthawk.spring_portfolio.mvc.stockObj;

import java.util.List;

public class StockMergeSort {

    // Method to sort the list of StockObjects
    public static void mergeSort(List<StockObject> stocks) {
        if (stocks.size() > 1) {
            int mid = stocks.size() / 2;

            // Split the list into two halves
            List<StockObject> left = stocks.subList(0, mid);
            List<StockObject> right = stocks.subList(mid, stocks.size());

            // Recursively sort each half
            mergeSort(left);
            mergeSort(right);

            // Merge the sorted halves
            merge(stocks, left, right);
        }
    }

    // Helper method to merge two halves
    private static void merge(List<StockObject> stocks, List<StockObject> left, List<StockObject> right) {
        int i = 0, j = 0, k = 0;

        // Merge while there are elements in both halves
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getPredictionsPercentGrowth() <= right.get(j).getPredictionsPercentGrowth()) {
                stocks.set(k++, left.get(i++));
            } else {
                stocks.set(k++, right.get(j++));
            }
        }

        // Copy remaining elements from the left half
        while (i < left.size()) {
            stocks.set(k++, left.get(i++));
        }

        // Copy remaining elements from the right half
        while (j < right.size()) {
            stocks.set(k++, right.get(j++));
        }
    }

    // Utility method to print the list of stocks
    public static void printStocks(List<StockObject> stocks) {
        for (StockObject stock : stocks) {
            System.out.println(stock + " -> Growth: " + stock.getPredictionsPercentGrowth());
        }
    }

}
