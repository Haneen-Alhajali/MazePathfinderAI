/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aiproject;

import java.awt.Point;
import java.util.*;

public class MazePathfinder {
    // Define directions: up, right, down, left
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    
    private final MazeGUI.Cell[][] maze;
    private final int rows;
    private final int cols;
    private final MazePerceptron perceptron;
    
    public MazePathfinder(MazeGUI.Cell[][] maze, int rows, int cols, MazePerceptron perceptron) {
        this.maze = maze;
        this.rows = rows;
        this.cols = cols;
        this.perceptron = perceptron;
    }
    
    /**
     * Finds the shortest path from start to end through safe tiles using A* algorithm.
     * @param start The starting point
     * @param end The destination point
     * @return List of points forming the path, or empty list if no path exists
     */
    
    public List<Point> findPath(Point start, Point end) {
    System.out.println("\n=== STARTING PATHFINDING ===");
    System.out.printf("Start: (%d,%d), End: (%d,%d)\n", start.x, start.y, end.x, end.y);
    
    if (!isValidPoint(start.x, start.y) || !isValidPoint(end.x, end.y)) {
        System.out.println("Invalid start or end point!");
        return Collections.emptyList();
    }
    
//    PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));
        PriorityQueue<Node> openSet = new PriorityQueue<>(
            Comparator.comparingInt((Node n) -> n.fScore)
                      .thenComparingInt(n -> n.hScore)  // Prefer nodes closer to goal
                      .thenComparingInt(n -> n.x)       // Then by x-coordinate
                      .thenComparingInt(n -> n.y)       // Then by y-coordinate
        );

    Map<String, Integer> gScore = new HashMap<>();
    Map<String, Point> cameFrom = new HashMap<>();
    
    // Initialize with start node
    int startH = calculateHeuristic(start, end);
    Node startNode = new Node(start.x, start.y, 0, startH);
    openSet.add(startNode);
    gScore.put(pointToKey(start.x, start.y), 0);
    
    System.out.printf("Added start node: %s\n", startNode);
    
    while (!openSet.isEmpty()) {
        Node current = openSet.poll();
        Point currentPoint = new Point(current.x, current.y);
        System.out.printf("\nProcessing current node: %s\n", current);
        
        if (current.x == end.x && current.y == end.y) {
            System.out.println("Reached destination!");
            return reconstructPath(cameFrom, currentPoint);
        }
        
        System.out.println("Exploring neighbors:");
        for (int[] dir : DIRECTIONS) {
            int nx = current.x + dir[0];
            int ny = current.y + dir[1];
            String neighborKey = pointToKey(nx, ny);
            
            System.out.printf("\n  Checking neighbor (%d,%d):\n", nx, ny);
            
            if (!isValidPoint(nx, ny)) {
                System.out.println("    Out of bounds - skipped");
                continue;
            }
            
            if (maze[nx][ny].type == MazeGUI.TileType.OBSTACLE) {
                System.out.println("    Obstacle - skipped");
                continue;
            }
            
            boolean safe = isTileSafe(nx, ny);
            System.out.printf("    Safety check: %s\n", safe ? "SAFE" : "UNSAFE");
            if (!safe) {
                continue;
            }
            
            int tentativeGScore = gScore.get(pointToKey(current.x, current.y)) + 1;
            System.out.printf("    Tentative gScore: %d (current gScore: %d)\n", 
                tentativeGScore, gScore.get(pointToKey(current.x, current.y)));
            
            if (!gScore.containsKey(neighborKey) || tentativeGScore < gScore.get(neighborKey)) {
                int oldGScore = gScore.getOrDefault(neighborKey, Integer.MAX_VALUE);
                System.out.printf("    Better path found! Updating (%d,%d) gScore: %d -> %d\n",
                    nx, ny, oldGScore, tentativeGScore);
                
                cameFrom.put(neighborKey, currentPoint);
                gScore.put(neighborKey, tentativeGScore);
                
                int h = calculateHeuristic(new Point(nx, ny), end);
                Node neighborNode = new Node(nx, ny, tentativeGScore, h);
                openSet.add(neighborNode);
                
                System.out.printf("    Added to open set: %s\n", neighborNode);
            } else {
                System.out.println("    Not a better path - skipped");
            }
        }
        
        System.out.println("Open set contents:");
        for (Node n : openSet) {
            System.out.printf("  %s\n", n);
        }
    }
    
    System.out.println("No path found!");
    return Collections.emptyList();
}
    /**
     * Determines if a tile is safe based on perceptron classification
     */

 private boolean isTileSafe(int row, int col) {
    // Start and end points are always safe
    if ((row == maze[0][0].startPoint.x && col == maze[0][0].startPoint.y) || 
        (row == maze[0][0].endPoint.x && col == maze[0][0].endPoint.y)) {
        System.out.println("    Start/end point - automatically safe");
        return true;
    }
    
    if (maze[row][col].type == MazeGUI.TileType.OBSTACLE) {
        System.out.println("    Obstacle - unsafe");
        return false;
    }
    
    double terrainValue = maze[row][col].type == MazeGUI.TileType.GRASS ? 0 : 1;
    double elevation = maze[row][col].elevation;
    double obstacleDistance = calculateDistanceToNearestObstacle(row, col);
    
    System.out.printf("    Features - Terrain: %.1f, Elevation: %.1f, ObstacleDist: %.1f\n",
        terrainValue, elevation, obstacleDistance);
    
    double[] features = MazePerceptron.normalizeFeatures(terrainValue, elevation, obstacleDistance);
    boolean safe = perceptron.predict(features) == 1;
    
    System.out.printf("    Perceptron prediction: %s\n", safe ? "SAFE" : "UNSAFE");
    return safe;
}   
    /**
     * Calculates Manhattan distance to the nearest obstacle
     */
    private double calculateDistanceToNearestObstacle(int row, int col) {
        int minDistance = Integer.MAX_VALUE;
        
        // Iterate through the maze to find obstacles
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j].type == MazeGUI.TileType.OBSTACLE) {
                    // Calculate Manhattan distance
                    int distance = Math.abs(row - i) + Math.abs(col - j);
                    minDistance = Math.min(minDistance, distance);
                }
            }
        }
        
        // Handle the case when there are no obstacles
        return minDistance == Integer.MAX_VALUE ? 10.0 : minDistance;
    }
    
    /**
     * Calculates Manhattan distance heuristic between two points
     */
    private int calculateHeuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
    
    /**
     * Reconstructs the path from end to start using the cameFrom map
     */
    
    private List<Point> reconstructPath(Map<String, Point> cameFrom, Point current) {
    System.out.println("\nReconstructing path:");
    List<Point> path = new LinkedList<>();
    path.add(current);
    System.out.printf("  Added endpoint: (%d,%d)\n", current.x, current.y);
    
    String currentKey = pointToKey(current.x, current.y);
    while (cameFrom.containsKey(currentKey)) {
        current = cameFrom.get(currentKey);
        path.add(0, current);
        currentKey = pointToKey(current.x, current.y);
        System.out.printf("  Added point: (%d,%d)\n", current.x, current.y);
    }
    
    System.out.println("Final path: " + path);
    return path;
}
    
    /**
     * Converts a point's coordinates to a unique string key
     */
    private String pointToKey(int x, int y) {
        return x + "," + y;
    }
    
    /**
     * Checks if coordinates are within maze bounds
     */
    private boolean isValidPoint(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }
    
    /**
     * Node class for A* algorithm
     */
    private static class Node {
        final int x;
        final int y;
        final int gScore; // Cost from start
        final int hScore; // Heuristic (estimated cost to goal)
        final int fScore; // g + h
        
        Node(int x, int y, int gScore, int hScore) {
            this.x = x;
            this.y = y;
            this.gScore = gScore;
            this.hScore = hScore;
            this.fScore = gScore + hScore;
        }
            @Override
            public String toString() {
                return String.format("Node(%d,%d) g=%d h=%d f=%d", 
                    x, y, gScore, hScore, fScore);
            }
    }
 
    
    
    
    
    
    
    
    
    
    
        private static MazeGUI.Cell[][] createSimpleTestMaze() {
    MazeGUI.Cell[][] maze = new MazeGUI.Cell[5][5];
    // All grass
    for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
            maze[i][j] = new MazeGUI.Cell(MazeGUI.TileType.GRASS, 0);
        }
    }
    // Add one obstacle
    maze[2][2].type = MazeGUI.TileType.OBSTACLE;
    return maze;
}
        
        public static void testObstacleDistanceAndSimpleMaze() {
    System.out.println("\n=== Testing Obstacle Distance and Simple Maze ===");
    
    // 1. First test createSimpleTestMaze()
    System.out.println("\nCreating simple 5x5 test maze with obstacle at (2,2)...");
    MazeGUI.Cell[][] simpleMaze = createSimpleTestMaze();
    
    // Print the simple maze
    System.out.println("Simple Maze Layout:");
    for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 5; j++) {
            System.out.print(simpleMaze[i][j].type == MazeGUI.TileType.OBSTACLE ? "O " : "G ");
        }
        System.out.println();
    }
    
    // 2. Test obstacle distance calculations
    System.out.println("\nTesting obstacle distance calculations...");
    
    // Create a temporary MazePathfinder instance (we won't actually use the perceptron)
    MazePathfinder pathfinder = new MazePathfinder(simpleMaze, 5, 5, null);
    
    // Test distances from various points
    System.out.println("Distance from (0,0) to nearest obstacle: " + 
        pathfinder.calculateDistanceToNearestObstacle(0, 0)); // Should be 4 (2+2)
    System.out.println("Distance from (0,2) to nearest obstacle: " + 
        pathfinder.calculateDistanceToNearestObstacle(0, 2)); // Should be 2 (2+0)
    System.out.println("Distance from (2,0) to nearest obstacle: " + 
        pathfinder.calculateDistanceToNearestObstacle(2, 0)); // Should be 2 (0+2)
    System.out.println("Distance from (4,4) to nearest obstacle: " + 
        pathfinder.calculateDistanceToNearestObstacle(4, 4)); // Should be 4 (2+2)
    System.out.println("Distance from (2,2) to nearest obstacle: " + 
        pathfinder.calculateDistanceToNearestObstacle(2, 2)); // Should be 0 (on obstacle)
    
    // 3. Test with no obstacles (should return default 10.0)
    System.out.println("\nTesting with no obstacles...");
    MazeGUI.Cell[][] noObstacleMaze = new MazeGUI.Cell[3][3];
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
            noObstacleMaze[i][j] = new MazeGUI.Cell(MazeGUI.TileType.GRASS, 0);
        }
    }
    MazePathfinder noObstaclePathfinder = new MazePathfinder(noObstacleMaze, 3, 3, null);
    System.out.println("Distance with no obstacles: " + 
        noObstaclePathfinder.calculateDistanceToNearestObstacle(1, 1)); // Should be 10.0
}
}
