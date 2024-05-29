package com.nighthawk.spring_portfolio.mvc.performance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PerformanceIterator implements Iterable<PerformanceObject> {
    private List<PerformanceObject> performanceList;

    public PerformanceIterator(List<PerformanceObject> performanceList) {
        this.performanceList = performanceList;
    }

    public Integer size() {
        return this.performanceList.size();
    }

    public void setKeyType(PerformanceObject.KeyType keyType) {
        // Update keyType for all PerformanceObjects in the iterator
        for (PerformanceObject performanceObject : performanceList) {
            performanceObject.setOrder(keyType);
        }
    }

    @Override
    public Iterator<PerformanceObject> iterator() {
        return performanceList.iterator();
    }

    public void mergeSort(int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(left, mid);
            mergeSort(mid + 1, right);
            merge(left, mid, right);
        }
    }

    private void merge(int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        List<PerformanceObject> leftList = new ArrayList<>(this.performanceList.subList(left, mid + 1));
        List<PerformanceObject> rightList = new ArrayList<>(this.performanceList.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftList.get(i).compareTo(rightList.get(j)) >= 0) {
                this.performanceList.set(k++, leftList.get(i++));
            } else {
                this.performanceList.set(k++, rightList.get(j++));
            }
        }

        while (i < n1) {
            this.performanceList.set(k++, leftList.get(i++));
        }

        while (j < n2) {
            this.performanceList.set(k++, rightList.get(j++));
        }
    }
}