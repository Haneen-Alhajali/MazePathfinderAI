/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aiproject;

/**
 *
 * @author user
 */
import static com.mycompany.aiproject.MazePathfinder.testObstacleDistanceAndSimpleMaze;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PathfindingTest {
    
    public static void testPathfinding() {
                testObstacleDistanceAndSimpleMaze();

        System.out.println("=== Starting Pathfinding Test ===");
        System.out.println("Testing A* pathfinding with Perceptron safety classification...");
        
        // Create a test maze
        int rows = 10;
        int cols = 10;
        MazeGUI.Cell[][] maze = createTestMaze(rows, cols);
        
        // Define start and end points
        Point start = new Point(0, 0);
        Point end = new Point(9, 9);
        
        // Set static references
        MazeGUI.Cell.startPoint = start;
        MazeGUI.Cell.endPoint = end;
        
        try {
            // Load and train perceptron
            String filePath = "src/main/resources/Data.xlsx";
            List<TrainingData> rawData = MazePerceptron.loadTrainingData(filePath);
            List<double[]> features = new ArrayList<>();
            List<Integer> labels = new ArrayList<>();
            
            for (TrainingData data : rawData) {
                features.add(MazePerceptron.normalizeFeatures(data.terrain, data.elevation, data.obstacleDist));
                labels.add(data.label);
            }
            
//            MazePerceptron perceptron = new MazePerceptron(0.01, 1000);
MazePerceptron perceptron = new MazePerceptron(0.1, 1, 5000);

            perceptron.train(features, labels);
                // Test perceptron on some known cases
    System.out.println("\nTesting perceptron on sample tiles:");
    testPerceptronOnTile(perceptron, 1, 10, 6); // Grass, medium elevation, medium distance
    testPerceptronOnTile(perceptron, 0, 10, 1); // Water, medium elevation, medium distance
    testPerceptronOnTile(perceptron, 0, 9, 1); // Grass, high elevation, near obstacle
            
            // Print maze representation
            System.out.println("Initial Maze (G=Grass, W=Water, O=Obstacle, S=Start, E=End):");
            printMaze(maze, rows, cols, start, end, null);
            
            // Find path
            MazePathfinder pathfinder = new MazePathfinder(maze, rows, cols, perceptron);
            List<Point> path = pathfinder.findPath(start, end);
            
            if (path.isEmpty()) {
                System.out.println("No safe path found!");
            } else {
                System.out.println("\nPath found with length: " + path.size());
                System.out.println("Path points: " + path);
                
                // Print maze with path
                System.out.println("\nMaze with path (P=Path):");
                printMaze(maze, rows, cols, start, end, path);
                
            }
            
        } catch (IOException e) {
            System.err.println("Error loading training data: " + e.getMessage());
        }
    }
    
    /**
     * Creates a test maze with a mix of terrain types
     */
private static void testPerceptronOnTile(MazePerceptron perceptron, double terrain, double elevation, double obstacleDist) {
    double[] features = MazePerceptron.normalizeFeatures(terrain, elevation, obstacleDist);
    int prediction = perceptron.predict(features);
    System.out.printf("Test Tile - Terrain: %.0f, Elev: %.1f, ObsDist: %.1f -> %s\n",
            terrain, elevation, obstacleDist,
            prediction == 1 ? "SAFE" : "UNSAFE");
}


    private static MazeGUI.Cell[][] createTestMaze(int rows, int cols) {
        MazeGUI.Cell[][] maze = new MazeGUI.Cell[rows][cols];
        
        // Initialize with grass (safe terrain)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = new MazeGUI.Cell(MazeGUI.TileType.GRASS, 0);
            }
        }
        
        // Add some water
        maze[1][2].type = MazeGUI.TileType.WATER;
        maze[1][2].elevation = 5;
        maze[2][2].type = MazeGUI.TileType.WATER;
        maze[2][2].elevation = 8;
        maze[3][2].type = MazeGUI.TileType.WATER;
        maze[3][2].elevation = 7;
        maze[4][3].type = MazeGUI.TileType.WATER;
        maze[4][3].elevation = 2;
        maze[5][4].type = MazeGUI.TileType.WATER;
        maze[5][4].elevation = 3;
        
        // Add some obstacles
        maze[3][3].type = MazeGUI.TileType.OBSTACLE;
        maze[3][4].type = MazeGUI.TileType.OBSTACLE;
        maze[3][5].type = MazeGUI.TileType.OBSTACLE;
        maze[4][5].type = MazeGUI.TileType.OBSTACLE;
        maze[5][5].type = MazeGUI.TileType.OBSTACLE;
        maze[6][5].type = MazeGUI.TileType.OBSTACLE;
        maze[7][5].type = MazeGUI.TileType.OBSTACLE;
        
        // Add some elevation to grass
        maze[1][1].elevation = 2;
        maze[2][1].elevation = 3;
        maze[6][7].elevation = 6;
        maze[7][7].elevation = 9;
        maze[8][8].elevation = 8;
        
        return maze;
    }
    
    /**
     * Prints a text representation of the maze with the path
     */
    private static void printMaze(MazeGUI.Cell[][] maze, int rows, int cols, 
                                  Point start, Point end, List<Point> path) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == start.x && j == start.y) {
                    System.out.print("S ");
                } else if (i == end.x && j == end.y) {
                    System.out.print("E ");
                } else if (path != null && pathContains(path, i, j)) {
                    System.out.print("P ");
                } else {
                    switch (maze[i][j].type) {
                        case GRASS:
                            System.out.print("G" + maze[i][j].elevation + " ");
                            break;
                        case WATER:
                            System.out.print("W" + maze[i][j].elevation + " ");
                            break;
                        case OBSTACLE:
                            System.out.print("O  ");
                            break;
                    }
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Checks if a point is in the path
     */
    private static boolean pathContains(List<Point> path, int x, int y) {
        for (Point p : path) {
            if (p.x == x && p.y == y) {
                return true;
            }
        }
        return false;
    }
    

}

