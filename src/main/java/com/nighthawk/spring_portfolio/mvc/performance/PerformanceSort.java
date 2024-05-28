package com.nighthawk.spring_portfolio.mvc.performance;

import java.util.List;

public class PerformanceSort {

    public static void mergeSort(List<PerformanceObject> performances) {
        if (performances.size() > 1) {
            int mid = performances.size() / 2;

            List<PerformanceObject> left = performances.subList(0, mid);
            List<PerformanceObject> right = performances.subList(mid, performances.size());

            mergeSort(left);
            mergeSort(right);

            merge(performances, left, right);
        }
    }

    private static void merge(List<PerformanceObject> performances, List<PerformanceObject> left, List<PerformanceObject> right) {
        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i).getRating().compareTo(right.get(j).getRating()) <= 0) {
                performances.set(k++, left.get(i++));
            } else {
                performances.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) {
            performances.set(k++, left.get(i++));
        }

        while (j < right.size()) {
            performances.set(k++, right.get(j++));
        }
    }
}