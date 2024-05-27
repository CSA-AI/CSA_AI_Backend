package com.nighthawk.spring_portfolio.mvc.performance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PerformanceIterator implements Iterable<PerformanceObject> {
    private List<PerformanceObject> stockList;

    public PerformanceObjectIterator(List<PerformanceObject> stockList) {
        this.stockList = stockList;
    }

    public Integer size() {
        return this.stockList.size();
    }

    public void setKeyType(PerformanceObject.KeyType keyType) {
        // Update keyType for all PerformanceObjects in the iterator
        for (PerformanceObject PerformanceObject : stockList) {
            PerformanceObject.setOrder(keyType);
        }
    }

    @Override
    public Iterator<PerformanceObject> iterator() {
        return stockList.iterator();
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

        List<PerformanceObject> leftList = new ArrayList<>(this.stockList.subList(left, mid + 1));
        List<PerformanceObject> rightList = new ArrayList<>(this.stockList.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < n1 && j < n2) {
            if (leftList.get(i).compareTo(rightList.get(j)) >= 0) {
                this.stockList.set(k++, leftList.get(i++));
            } else {
                this.stockList.set(k++, rightList.get(j++));
            }
        }

        while (i < n1) {
            this.stockList.set(k++, leftList.get(i++));
        }

        while (j < n2) {
            this.stockList.set(k++, rightList.get(j++));
        }
    }
}

