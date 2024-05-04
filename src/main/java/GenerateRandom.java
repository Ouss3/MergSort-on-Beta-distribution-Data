import org.apache.commons.math3.distribution.BetaDistribution;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class GenerateRandom {
    private static final double[] MEANS = {0.2, 0.4, 0.6, 0.8};
    private static final double[] VARIANCES = {0.05, 0.1, 0.15, 0.2};
    private static final int[] DATA_SIZES = {100, 200, 300, 400,1000};

    private static int operationCount = 0;
    private static double  avergeCount = 0;
    private static double  devider = 0;



    public static void main(String[] args) {
        // LinkedList to store the generated data
        // 3D matrix to store the generated data
        LinkedList<Double>[][][] data = new LinkedList[DATA_SIZES.length][MEANS.length][VARIANCES.length];

        // For each data size
        for (int i = 0; i < DATA_SIZES.length; i++) {
            int dataSize = DATA_SIZES[i];

            // For each mean
            for (int j = 0; j < MEANS.length; j++) {
                double mean = MEANS[j];

                // For each variance
                for (int k = 0; k < VARIANCES.length; k++) {
                    double variance = VARIANCES[k];

                    // Calculate alpha and beta parameters for the beta distribution
                    double alpha = ((1 - mean) / variance - 1 / mean) * mean * mean;
                    double beta = alpha * (1 / mean - 1);

                    // Create a beta distribution with the calculated parameters
                    BetaDistribution distribution = new BetaDistribution(alpha, beta);

                    // Generate the random numbers and store them in the matrix
                    LinkedList<Double> samples = new LinkedList<>();
                    for (int l = 0; l < dataSize; l++) {
                        samples.add(distribution.sample());
                    }
                    data[i][j][k] = new LinkedList<>(samples);
                }
            }
        }


     //    Warm up JVM
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < DATA_SIZES.length; j++) {
                for (int k = 0; k < MEANS.length; k++) {
                    for (int l = 0; l < VARIANCES.length; l++) {
                        LinkedList<Double> samples = new LinkedList<>(data[j][k][l]) ;
                      mergeSort(samples);
                    }
                }
            }
        }
        Path resultsDirectory = Paths.get("results");
        if (!Files.exists(resultsDirectory)) {
            try {
                Files.createDirectory(resultsDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Measure execution time for each data size, mean, and variance
        for (int i = 0; i < DATA_SIZES.length; i++) {
            for (int j = 0; j < MEANS.length; j++) {
                for (int k = 0; k < VARIANCES.length; k++) {
                    LinkedList<Double> originalSamples = data[i][j][k];

                    // Repeat 10 times and average the results
                    long totalElapsedTime = 0;
                    for (int l = 0; l < 10; l++) {
                        LinkedList<Double> samples = new LinkedList<>(originalSamples); // Create a copy of the original list
                        long startTime = System.nanoTime();
                         mergeSort(samples);
                        long endTime = System.nanoTime();
                        totalElapsedTime += endTime - startTime;
                    }
                    long averageElapsedTime = totalElapsedTime / 10;
                    // calculate the operation count
                    operationCount = 0;
                    LinkedList<Double> samples = new LinkedList<>(originalSamples); // Create a copy of the original list
                    mergeSortCountOP(samples);
                    avergeCount +=  (averageElapsedTime/operationCount);
                    devider++;
                    System.out.println("Operation Count for Data Size: " + DATA_SIZES[i] +  " is: " + operationCount  + " ns and averge time of one operation is: " + averageElapsedTime/operationCount + " ns");

                     originalSamples= mergeSort(originalSamples);
                    // Create a new file with a descriptive name
                    String fileName = "results_datasize_" + DATA_SIZES[i] + "_mean_" + MEANS[j] + "_variance_" + VARIANCES[k] + ".csv";
                    Path filePath = resultsDirectory.resolve(fileName);
                    try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
                        // Write the metadata
                        writer.println("Algorithm: Merge Sort");
                        writer.println("Distribution: Beta");
                        writer.println("Data Size: " + DATA_SIZES[i]);
                        writer.println("Mean: " + MEANS[j]);
                        writer.println("Variance: " + VARIANCES[k]);
                        writer.println("Timestamp: " + LocalDateTime.now());
                        writer.println("Number of Experiments: 10");
                        writer.println("JVM Warm Up Rounds: 10");
                        writer.println("Average Execution Time: " + ((double)averageElapsedTime/1000000) + ": ms");
                        writer.println("Operation Count: " + operationCount);
                        writer.println("Average Time per Operation: " + averageElapsedTime/operationCount + " ns");

                        // Write the sorted data
                        for (double sample : originalSamples) {
                            writer.println(sample);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("Average Time per Operation: " + (avergeCount/devider)/1000000  + " ns");



        }








  // Merge sort for LinkedList
  private static LinkedList<Double> mergeSort(LinkedList<Double> list) {
      if (list.size() <= 1) {
          return list;
      }

      int middle = list.size() / 2;
      LinkedList<Double> left = new LinkedList<>();
      LinkedList<Double> right = new LinkedList<>();

      for (int i = 0; i < middle; i++) {
          left.add(list.get(i));
      }
      for (int i = middle; i < list.size(); i++) {
          right.add(list.get(i));
      }

      left = mergeSort(left);
      right = mergeSort(right);

      return merge(left, right);
  }

    private static LinkedList<Double> merge(LinkedList<Double> left, LinkedList<Double> right) {
        LinkedList<Double> result = new LinkedList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            if (left.getFirst() <= right.getFirst()) {
                result.add(left.removeFirst());
            } else {
                result.add(right.removeFirst());
            }
        }
        while (!left.isEmpty()) {
            result.add(left.removeFirst());
        }
        while (!right.isEmpty()) {
            result.add(right.removeFirst());
        }
        return result;
    }

    private static LinkedList<Double> mergeSortCountOP(LinkedList<Double> list) {
        if (list.size() <= 1) {
           // operationCount++; // Counting the base case
            return list;
        }

        int middle = list.size() / 2;
       // operationCount++; // Counting the division operation
        LinkedList<Double> left = new LinkedList<>();
        LinkedList<Double> right = new LinkedList<>();

        for (int i = 0; i < middle; i++) {
            //operationCount+=2; // Counting the comparison operation
            left.add(list.get(i));
          //  operationCount++; // Counting the add operation
        }
       // operationCount++; // Counting the loop operation
        for (int i = middle; i < list.size(); i++) {
           // operationCount+=2; // Counting the comparison operation
            right.add(list.get(i));
           // operationCount++; // Counting the add operation
        }
       // operationCount++; // Counting the loop operation

        left = mergeSortCountOP(left);
        right = mergeSortCountOP(right);

        return mergeCountOP(left, right);
    }

    private static LinkedList<Double> mergeCountOP(LinkedList<Double> left, LinkedList<Double> right) {
        LinkedList<Double> result = new LinkedList<>();
        while (!left.isEmpty() && !right.isEmpty()) {
            operationCount++; // Counting the comparison operation
            if (left.getFirst() <= right.getFirst()) {
               // operationCount++; // Counting the comparison operation
                result.add(left.removeFirst());
            } else {
                //operationCount++; // Counting the comparison operation
                result.add(right.removeFirst());
            }
           // operationCount++; // Counting the add operation
        }
      //  operationCount++; // Counting the last comparison in  loop operation
        while (!left.isEmpty()) {
            //operationCount++; // Counting the comparison operation
            result.add(left.removeFirst());
           // operationCount++; // Counting the add operation
        }
      //  operationCount++; // Counting the last comparison in  loop operation
        while (!right.isEmpty()) {
           // operationCount++; // Counting the comparison operation
            result.add(right.removeFirst());
          //  operationCount++; // Counting the add operation
        }
      //  operationCount++; // Counting the last comparison in  loop operation
        return result;
    }









}


