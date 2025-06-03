/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.aiproject;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import java.io.IOException;

import java.io.IOException;

public class AIproject {
    // Flag to run pathfinding test without GUI
    private static final boolean RUN_PATHFINDING_TEST = false;

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Failed to set look and feel");
            }
        }
        
        // Run GUI in Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MazeGUI gui = new MazeGUI();
            gui.setMinimumSize(new Dimension(900, 600));
            gui.setVisible(true);
        });
        
        // Load and train the perceptron
        trainAndTestPerceptron();
        
        // Run pathfinding test if flag is set
        if (RUN_PATHFINDING_TEST) {
            PathfindingTest.testPathfinding();
        }
    }
    
    private static void trainAndTestPerceptron() {
        try {
            String filePath = "src/main/resources/Data.xlsx";
            List<TrainingData> rawData = MazePerceptron.loadTrainingData(filePath);
            List<double[]> features = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            
            for (TrainingData data : rawData) {
                features.add(MazePerceptron.normalizeFeatures(data.terrain, data.elevation, data.obstacleDist));
                labels.add(data.label);
            }
            
//            MazePerceptron perceptron = new MazePerceptron(0.1, 5000);//0.01, 1000
MazePerceptron perceptron = new MazePerceptron(0.1, 1, 5000);
            perceptron.train(features, labels);
            testPerceptron(perceptron);
            perceptron.printWeights();

        } catch (IOException e) {
            System.err.println("Error loading training data: " + e.getMessage());
        }
    }
    
//    private static void testPerceptron(MazePerceptron perceptron) {
//        // Test cases
//        double[][] testCases = {
//            {0, 7, 7},  
//            {1, 5, 3},  
//            {1, 6, 2},  
//            {1, 2, 2},  
//            {1, 8, 2}   
//        };
//
//        System.out.println("Perceptron Test Results:");
//        for (double[] testCase : testCases) {
//            double[] normalized = MazePerceptron.normalizeFeatures(testCase[0], testCase[1], testCase[2]);
//            int prediction = perceptron.predict(normalized);
//            System.out.printf("Terrain: %.0f, Elevation: %.0f, ObstacleDist: %.0f -> %s%n",
//                testCase[0], testCase[1], testCase[2],
//                prediction == 1 ? "Safe" : "Unsafe");
//        }
//    }

    
    
 private static void testPerceptron(MazePerceptron perceptron) {
    // Test cases
    double[][] testCases = {
        {0, 6, 10, 1},  // safe
        {0, 10, 3, 1},  // safe
        {0, 8, 2, 1},   // safe
        {0, 9, 9, 1},   // safe
        {0, 9, 2, 1},   // safe
        {0, 2, 2, 0},   // unsafe
        {1, 6, 3, 0},   // unsafe
        {1, 0, 6, 0},   // unsafe
        {0, 3, 3, 1},   // safe
        {1, 3, 8, 0},   // unsafe
        {1, 4, 0, 0},   // unsafe
        {1, 6, 7, 0},   // unsafe
        {1, 6, 6, 0},   // unsafe
        {0, 10, 1, 1},  // safe
        {1, 3, 7, 0},   // unsafe
        {0, 6, 0, 1},   // safe
        {1, 10, 10, 0}, // unsafe
        {1, 2, 8, 0},   // unsafe
        {1, 5, 8, 0},   // unsafe
        {0, 1, 1, 0},   // unsafe
        {1, 9, 6, 0},   // unsafe
        {0, 8, 9, 1},   // safe
        {1, 4, 2, 0},   // unsafe
        {0, 5, 6, 1},   // safe
        {1, 3, 9, 0},   // unsafe
        {0, 10, 8, 1},  // safe
        {0, 9, 3, 1},   // safe
        {1, 6, 0, 0},   // unsafe
        {0, 8, 1, 1},   // safe
        {1, 6, 0, 0},   // unsafe
        {1, 0, 4, 0},   // unsafe
        {1, 0, 4, 0},   // unsafe
        {1, 8, 10, 0},  // unsafe
        {1, 10, 6, 0},  // unsafe
        {1, 8, 8, 0},   // unsafe
        {1, 3, 8, 0},   // unsafe
        {1, 8, 2, 0},   // unsafe
        {1, 2, 2, 0},   // unsafe
        {1, 6, 2, 0},   // unsafe
        {1, 5, 3, 0},   // unsafe
        {0, 7, 7, 1}    // safe
    };

    int correct = 0;

    System.out.println("Perceptron Test Results:");
    for (double[] testCase : testCases) {
        double[] normalized = MazePerceptron.normalizeFeatures(testCase[0], testCase[1], testCase[2]);
        int prediction = perceptron.predict(normalized);
        int expected = (int) testCase[3];
        boolean isCorrect = (prediction == expected);

        if (isCorrect) correct++;

        System.out.printf("Terrain: %.0f, Elevation: %.0f, ObstacleDist: %.0f -> Predicted: %s | Expected: %s | %s%n",
            testCase[0], testCase[1], testCase[2],
            prediction == 1 ? "Safe" : "Unsafe",
            expected == 1 ? "Safe" : "Unsafe",
            isCorrect ? "✔ Correct" : "✘ Wrong");
    }

    double accuracy = (double) correct / testCases.length * 100;
    System.out.printf("Accuracy: %.2f%% (%d/%d correct)%n", accuracy, correct, testCases.length);
}
   
 
    
    
    
    
//    private static void testPerceptron(MazePerceptron perceptron) {
//    // Test cases
//    double[][] testCases = {
//        {0, 8, 0, 1},  // safe
//        {1, 7, 4, 0},  // unsafe
//        {0, 0, 0, 0},  // unsafe
//        {1, 7, 7, 0},  // unsafe
//        {1, 7, 0, 0},  // unsafe
//        {1, 10, 10, 0}, // unsafe
//        {1, 2, 0, 0},  // unsafe
//        {1, 0, 1, 0},  // unsafe
//        {1, 7, 1, 0},  // unsafe
//        {1, 2, 5, 0},  // unsafe
//        {1, 2, 6, 0},  // unsafe
//        {0, 0, 4, 1},  // safe
//        {0, 10, 0, 1}, // safe
//        {1, 4, 0, 0},  // unsafe
//        {1, 9, 2, 0},  // unsafe
//        {1, 6, 1, 0},  // unsafe
//        {0, 9, 4, 1},  // safe
//        {1, 8, 9, 0},  // unsafe
//        {0, 6, 5, 1},  // safe
//        {0, 8, 6, 1},  // safe
//        {0, 7, 3, 1},  // safe
//        {0, 1, 6, 1},  // safe
//        {0, 0, 10, 1}, // safe
//        {1, 6, 7, 0},  // unsafe
//        {1, 6, 10, 0}, // unsafe
//        {1, 7, 0, 0},  // unsafe
//        {1, 4, 5, 0},  // unsafe
//        {1, 2, 7, 0},  // unsafe
//        {0, 7, 4, 1},  // safe
//        {1, 5, 3, 0},  // unsafe
//        {1, 10, 1, 0}, // unsafe
//        {0, 2, 5, 1},  // safe
//        {1, 0, 5, 0},  // unsafe
//        {0, 2, 10, 1}, // safe
//        {1, 4, 0, 0},  // unsafe
//        {0, 2, 8, 1},  // safe
//        {1, 0, 10, 0}, // unsafe
//        {1, 4, 5, 0},  // unsafe
//        {0, 9, 2, 1},  // safe
//        {0, 6, 3, 1},  // safe
//        {0, 6, 10, 1}, // safe
//        {0, 10, 3, 1}, // safe
//        {0, 8, 2, 1},  // safe
//        {0, 9, 9, 1},  // safe
//        {0, 9, 2, 1},  // safe
//        {0, 2, 2, 0},  // unsafe
//        {1, 6, 3, 0},  // unsafe
//        {1, 0, 6, 0},  // unsafe
//        {0, 3, 3, 1},  // safe
//        {1, 3, 8, 0},  // unsafe
//        {1, 4, 0, 0},  // unsafe
//        {1, 6, 7, 0},  // unsafe
//        {1, 6, 6, 0},  // unsafe
//        {0, 10, 1, 1}, // safe
//        {1, 3, 7, 0},  // unsafe
//        {0, 6, 0, 1},  // safe
//        {1, 10, 10, 0},// unsafe
//        {1, 2, 8, 0},  // unsafe
//        {1, 5, 8, 0},  // unsafe
//        {0, 1, 1, 0},  // unsafe
//        {1, 9, 6, 0},  // unsafe
//        {0, 8, 9, 1},  // safe
//        {1, 4, 2, 0},  // unsafe
//        {0, 5, 6, 1},  // safe
//        {1, 3, 9, 0},  // unsafe
//        {0, 10, 8, 1}, // safe
//        {0, 9, 3, 1},  // safe
//        {1, 6, 0, 0},  // unsafe
//        {0, 8, 1, 1},  // safe
//        {1, 6, 0, 0},  // unsafe
//        {1, 0, 4, 0},  // unsafe
//        {1, 0, 4, 0},  // unsafe
//        {1, 8, 10, 0}, // unsafe
//        {1, 10, 6, 0}, // unsafe
//        {1, 8, 8, 0},  // unsafe
//        {1, 3, 8, 0},  // unsafe
//        {1, 8, 2, 0},  // unsafe
//        {1, 2, 2, 0},  // unsafe
//        {1, 6, 2, 0},  // unsafe
//        {1, 5, 3, 0},  // unsafe
//        {0, 7, 7, 1}   // safe
//    };
//
//    int correct = 0;
//
//    System.out.println("Perceptron Test Results:");
//    for (double[] testCase : testCases) {
//        double[] normalized = MazePerceptron.normalizeFeatures(testCase[0], testCase[1], testCase[2]);
//        int prediction = perceptron.predict(normalized);
//        int expected = (int) testCase[3];
//        boolean isCorrect = (prediction == expected);
//
//        if (isCorrect) correct++;
//
//        System.out.printf("Terrain: %.0f, Elevation: %.0f, ObstacleDist: %.0f -> Predicted: %s | Expected: %s | %s%n",
//            testCase[0], testCase[1], testCase[2],
//            prediction == 1 ? "Safe" : "Unsafe",
//            expected == 1 ? "Safe" : "Unsafe",
//            isCorrect ? "✔ Correct" : "✘ Wrong");
//    }
//
//    double accuracy = (double) correct / testCases.length * 100;
//    System.out.printf("Accuracy: %.2f%% (%d/%d correct)%n", accuracy, correct, testCases.length);
//}

}