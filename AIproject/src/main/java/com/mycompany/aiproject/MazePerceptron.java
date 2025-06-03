/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aiproject;

/**
 *
 * @author hp
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//public class MazePerceptron {
//    private double[] weights;
//    private double bias;
//    private final double learningRate;
//    private final int epochs;
//
//    public MazePerceptron(double learningRate, int epochs) {
//        this.learningRate = learningRate;
//        this.epochs = epochs;
//    }
//
//    // Sigmoid activation function
////    private double sigmoid(double x) {
////        return 1 / (1 + Math.exp(-x));
////    }
////
////    // Predict method with sigmoid activation
////    public int predict(double[] features) {
////        double linearOutput = bias;
////        for (int i = 0; i < weights.length; i++) {
////            linearOutput += weights[i] * features[i];
////        }
////        return sigmoid(linearOutput) > 0.5 ? 1 : 0;
////    }
//
//    // Step activation function
//    private int stepFunction(double x) {
//        return x >= 0 ? 1 : 0;
//    }
//
//    // Predict method with step function
//    public int predict(double[] features) {
//        double linearOutput = bias;
//        for (int i = 0; i < weights.length; i++) {
//            linearOutput += weights[i] * features[i];
//        }
//        return stepFunction(linearOutput);
//    }
//
//    // Train the perceptron
//    public void train(List<double[]> features, List<Integer> labels) {
//        // Initialize weights and bias
//        weights = new double[features.get(0).length];
//        bias = 0;
//        
//        for (int epoch = 0; epoch < epochs; epoch++) {
//            for (int i = 0; i < features.size(); i++) {
//                double[] x = features.get(i);
//                int y = labels.get(i);
//                
//                // Calculate prediction
//                int prediction = predict(x);
//                
//                // Update weights and bias
//                double error = y - prediction;
//                bias += learningRate * error;
//                
//                for (int j = 0; j < weights.length; j++) {
//                    weights[j] += learningRate * error * x[j];
//                }
//            }
//        }
//    }


public class MazePerceptron {
    private double[] weights;
    private double bias;
    private final double learningRate;
    private final double targetAccuracy;
    private final int maxEpochs;

    public MazePerceptron(double learningRate, double targetAccuracy, int maxEpochs) {
        this.learningRate = learningRate;
        this.targetAccuracy = targetAccuracy;
        this.maxEpochs = maxEpochs;
    }

    // Step activation function
    private int stepFunction(double x) {
        return x >= 0 ? 1 : 0;
    }

    // Predict method with step function
    public int predict(double[] features) {
        double linearOutput = bias;
        for (int i = 0; i < weights.length; i++) {
            linearOutput += weights[i] * features[i];
        }
        return stepFunction(linearOutput);
    }

    // Calculate accuracy on given dataset
    private double calculateAccuracy(List<double[]> features, List<Integer> labels) {
        int correct = 0;
        for (int i = 0; i < features.size(); i++) {
            int prediction = predict(features.get(i));
            if (prediction == labels.get(i)) {
                correct++;
            }
        }
        return (double) correct / features.size();
    }

    // Train the perceptron with goal-based stopping
    public void train(List<double[]> features, List<Integer> labels) {
        // Initialize weights and bias
        weights = new double[features.get(0).length];
        bias = 0;
        
        double currentAccuracy = 0;
        int epoch = 0;
        
        while (epoch < maxEpochs && currentAccuracy < targetAccuracy) {
            // Training loop
            for (int i = 0; i < features.size(); i++) {
                double[] x = features.get(i);
                int y = labels.get(i);
                
                // Calculate prediction
                int prediction = predict(x);
                
                // Update weights and bias
                double error = y - prediction;
                bias += learningRate * error;
                
                for (int j = 0; j < weights.length; j++) {
                    weights[j] += learningRate * error * x[j];
                }
            }
            
            // Calculate accuracy after each epoch
            currentAccuracy = calculateAccuracy(features, labels);
            epoch++;
            
            // Optional: Print progress
            if (epoch % 100 == 0) {
                System.out.printf("Epoch %d - Accuracy: %.2f%%\n", epoch, currentAccuracy * 100);
            }
        }
        
        System.out.printf("Training completed after %d epochs with %.2f%% accuracy\n", 
                         epoch, currentAccuracy * 100);
    }

    // Load training data from Excel file
    public static List<TrainingData> loadTrainingData(String filePath) throws IOException {
        List<TrainingData> data = new ArrayList<>();
        
        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        
        for (Row row : sheet) {
            // Skip header row
            if (row.getRowNum() == 0) continue;
            
            double terrain = row.getCell(0).getNumericCellValue();
            double elevation = row.getCell(1).getNumericCellValue();
            double obstacleDist = row.getCell(2).getNumericCellValue();
            int label = (int) row.getCell(3).getNumericCellValue();
            
            data.add(new TrainingData(terrain, elevation, obstacleDist, label));
        }
        
        workbook.close();
        file.close();
        
        return data;
    }

    // Normalize features to [0, 1] range
    public static double[] normalizeFeatures(double terrain, double elevation, double obstacleDist) {
        // Terrain is already 0 or 1
        // Elevation: 0-10 -> 0-1
        // Obstacle distance: assuming max is 10 (from data), normalize to 0-1
        return new double[] {
            terrain,
            elevation / 10.0,
            obstacleDist / 10.0
        };
    }

    
 // debugging
public void printWeights() {
    System.out.println("Perceptron weights:");
    System.out.println("Bias: " + bias);
    for (int i = 0; i < weights.length; i++) {
        System.out.println("Weight " + i + ": " + weights[i]);
    }
}   
 
}

class TrainingData {
    double terrain;
    double elevation;
    double obstacleDist;
    int label;
    
    public TrainingData(double terrain, double elevation, double obstacleDist, int label) {
        this.terrain = terrain;
        this.elevation = elevation;
        this.obstacleDist = obstacleDist;
        this.label = label;
    }
    
    
    
 
    
}
