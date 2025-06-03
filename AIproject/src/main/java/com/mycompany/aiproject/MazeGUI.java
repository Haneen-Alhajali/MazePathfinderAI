/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aiproject;

/**
 *
 * @author user
 */
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.border.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class MazeGUI extends JFrame {
    // Constants
    private static final int DEFAULT_ROWS = 5;
    private static final int DEFAULT_COLS = 5;
    private static final int MIN_SIZE = 5;
    private static final int MAX_SIZE = 50;
    private static final int CELL_SIZE = 50;
    
    // Colors
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(255, 215, 0); // Gold
    private static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private static final Color PANEL_COLOR = new Color(230, 240, 250);
    private static final Color TAB_COLOR = new Color(200, 220, 240);
    private static final Color START_COLOR = new Color(50, 205, 50);  // Lime Green
    private static final Color END_COLOR = new Color(220, 20, 60);    // Crimson
    private static final Color EXECUTE_COLOR = new Color(46, 125, 50); // Dark Green
    private static final Color PATH_COLOR = new Color(255, 255, 0, 150); // Yellow path
    
    // Maze properties
    private int rows = DEFAULT_ROWS;
    private int cols = DEFAULT_COLS;
    private Cell[][] maze;
    private Point startPoint = new Point(0, 0);
    private Point endPoint = new Point(DEFAULT_ROWS-1, DEFAULT_COLS-1);
    private boolean isPathCalculated = false;
    
    // Current selection mode
    private TileType currentTileType = TileType.GRASS;
    private int currentElevation = 0;
    private boolean isPlacingStart = false;
    private boolean isPlacingEnd = false;
    
    // UI Components
    private JPanel mazePanel;
    private JSpinner rowsSpinner;
    private JSpinner colsSpinner;
    private JLabel dimensionsLabel;
    private JSlider elevationSlider;
    private JLabel elevationValueLabel;
    private JToggleButton grassButton;
    private JToggleButton waterButton;
    private JToggleButton obstacleButton;
    private JToggleButton startButton;
    private JToggleButton endButton;
    private JButton generateButton;
    private JButton saveButton;
    private JButton executeButton;
    private JLabel statusLabel;
    private JLabel startEndInfo;
    
    // Images for tiles
    private BufferedImage grassImage;
    private BufferedImage waterImage;
    private BufferedImage obstacleImage;
    private BufferedImage startImage;
    private BufferedImage endImage;
    private BufferedImage helpImage;
    private BufferedImage executeImage;

    // Enum for tile types
    public enum TileType {
        GRASS, WATER, OBSTACLE
    }
    
    // Cell class to store maze cell information
//    public static class Cell {
//        TileType type;
//        int elevation;
//        boolean isPath = false;
//        
//        public Cell(TileType type, int elevation) {
//            this.type = type;
//            this.elevation = elevation;
//        }
//    }
public static class Cell {
    TileType type;
    int elevation;
    boolean isPath = false;
    
    // Static references to start and end points to make them accessible from MazePathfinder
    public static Point startPoint;
    public static Point endPoint;
    
    public Cell(TileType type, int elevation) {
        this.type = type;
        this.elevation = elevation;
    }
}
    public MazeGUI() {
        // Set up the JFrame
        super("Maze Safety Classification - Maze Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Set static references to start and end points
        Cell.startPoint = startPoint;
        Cell.endPoint = endPoint;
     
        
        // Load images
        loadImages();
        
        // Initialize the maze
        initializeMaze();
        
        // Build UI components
        buildUI();
        
        // Display the window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
private void loadImages() {
    try {
        // Load images from resources folder
        grassImage = loadImageResource("grass.png");
        waterImage = loadImageResource("water.png");
        obstacleImage = loadImageResource("obstacle.png");
        startImage = loadImageResource("start.png");
        endImage = loadImageResource("end.png");
        helpImage = loadImageResource("help.png");
        executeImage = loadImageResource("execute.png");
    } catch (Exception e) {
        System.out.println("Error loading images: " + e.getMessage());
        // Create fallback colored images
        grassImage = createColorImage(new Color(144, 238, 144), "G");
        waterImage = createColorImage(new Color(135, 206, 250), "W");
        obstacleImage = createColorImage(new Color(139, 69, 19), "O");
        startImage = createColorImage(START_COLOR, "S");
        endImage = createColorImage(END_COLOR, "E");
        helpImage = createColorImage(new Color(200, 200, 200), "?");
        executeImage = createColorImage(EXECUTE_COLOR, "GO"); 
    }
}

//private BufferedImage loadImageResource(String filename) throws IOException {
//    InputStream is = getClass().getResourceAsStream("/resources/" + filename);
//    if (is == null) {
//        throw new IOException("Image not found: " + filename);
//    }
//    try {
//        return ImageIO.read(is);
//    } finally {
//        is.close();
//    }
//} 


private BufferedImage loadImageResource(String filename) throws IOException {
    // Try loading from resources folder (common Maven structure)
    InputStream is = getClass().getResourceAsStream("/" + filename);
    if (is == null) {
        // Alternative path if first attempt fails
        is = getClass().getResourceAsStream("/resources/" + filename);
    }
    if (is == null) {
        throw new IOException("Image not found: " + filename);
    }
    try {
        return ImageIO.read(is);
    } finally {
        is.close();
    }
}

    private BufferedImage createColorImage(Color color, String text) {
        BufferedImage img = new BufferedImage(CELL_SIZE, CELL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Fill with color
        g2d.setColor(color);
        g2d.fillRect(0, 0, CELL_SIZE, CELL_SIZE);
        
        // Add text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (CELL_SIZE - fm.stringWidth(text)) / 2;
        int y = (CELL_SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, x, y);
        
        g2d.dispose();
        return img;
    }
    
    private void initializeMaze() {
        maze = new Cell[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = new Cell(TileType.GRASS, 0);
            }
        }
    }
    
    private void buildUI() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create maze panel with a nice border
        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawMaze(g);
            }
        };
        mazePanel.setPreferredSize(new Dimension(cols * CELL_SIZE, rows * CELL_SIZE));
        mazePanel.setBackground(Color.WHITE);
        mazePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mazePanel.addMouseListener(new MazeClickListener());
        
        // Create control panel
        JPanel controlPanel = createControlPanel();
        
        // Add components to main panel
        mainPanel.add(new JScrollPane(mazePanel), BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        
        // Status bar at bottom
        statusLabel = new JLabel("Ready. Select a tool and click on the maze to edit.");
        statusLabel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, PRIMARY_COLOR),
            new EmptyBorder(5, 10, 5, 10)
        ));
        statusLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        setContentPane(mainPanel);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlPanel.setPreferredSize(new Dimension(320, 600));
        controlPanel.setBackground(PANEL_COLOR);
        
        // Title panel with help button
        JPanel titlePanel = new JPanel(new BorderLayout(5, 0));
        titlePanel.setBackground(PANEL_COLOR);
        
        JLabel titleLabel = new JLabel("Maze Generator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        JButton helpButton = new JButton(new ImageIcon(helpImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        helpButton.setBorder(BorderFactory.createEmptyBorder());
        helpButton.setContentAreaFilled(false);
        helpButton.setToolTipText("Show help");
        helpButton.addActionListener(e -> showHelpDialog());
        titlePanel.add(helpButton, BorderLayout.EAST);
        
        controlPanel.add(titlePanel);
        controlPanel.add(Box.createVerticalStrut(10));
        
        // Create a tabbed pane with enhanced styling
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(TAB_COLOR);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        tabbedPane.setForeground(Color.DARK_GRAY);
        
        // Set tab border and padding
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(5, 5, 5, 5));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(5, 2, 0, 2));
        
        // Maze Settings Tab
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        settingsPanel.setBackground(PANEL_COLOR);
        
        // Maze dimensions section
        JPanel dimensionsPanel = createStyledPanel("Maze Dimensions");
        
        // Rows spinner
        JPanel rowsPanel = new JPanel(new BorderLayout(5, 0));
        rowsPanel.setBackground(PANEL_COLOR);
        JLabel rowsLabel = new JLabel("Rows:");
        rowsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        rowsPanel.add(rowsLabel, BorderLayout.WEST);
        
        rowsSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_ROWS, MIN_SIZE, MAX_SIZE, 1));
        rowsSpinner.setPreferredSize(new Dimension(60, 25));
        rowsPanel.add(rowsSpinner, BorderLayout.CENTER);
        
        // Columns spinner
        JPanel colsPanel = new JPanel(new BorderLayout(5, 0));
        colsPanel.setBackground(PANEL_COLOR);
        JLabel colsLabel = new JLabel("Columns:");
        colsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        colsPanel.add(colsLabel, BorderLayout.WEST);
        
        colsSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_COLS, MIN_SIZE, MAX_SIZE, 1));
        colsSpinner.setPreferredSize(new Dimension(60, 25));
        colsPanel.add(colsSpinner, BorderLayout.CENTER);
        
        // Current dimensions label
        dimensionsLabel = new JLabel("Current size: " + rows + " x " + cols);
        dimensionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dimensionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Apply dimensions button
        JButton applyButton = createStyledButton("Apply Dimensions");
        applyButton.addActionListener(e -> updateMazeDimensions());
        
        dimensionsPanel.add(rowsPanel);
        dimensionsPanel.add(Box.createVerticalStrut(5));
        dimensionsPanel.add(colsPanel);
        dimensionsPanel.add(Box.createVerticalStrut(5));
        dimensionsPanel.add(dimensionsLabel);
        dimensionsPanel.add(Box.createVerticalStrut(10));
        dimensionsPanel.add(applyButton);
        
        settingsPanel.add(dimensionsPanel);
        settingsPanel.add(Box.createVerticalStrut(10));
        
        // Tile properties section
        JPanel tilePanel = createStyledPanel("Tile Properties");
        
        // Tile type buttons with text and icons
        JPanel tileTypesPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        tileTypesPanel.setBackground(PANEL_COLOR);
        
        ButtonGroup tileTypeGroup = new ButtonGroup();
        
        grassButton = createTileButton("Grass", grassImage, TileType.GRASS);
        grassButton.setSelected(true);
        tileTypeGroup.add(grassButton);
        tileTypesPanel.add(grassButton);
        
        waterButton = createTileButton("Water", waterImage, TileType.WATER);
        tileTypeGroup.add(waterButton);
        tileTypesPanel.add(waterButton);
        
        obstacleButton = createTileButton("Obstacle", obstacleImage, TileType.OBSTACLE);
        tileTypeGroup.add(obstacleButton);
        tileTypesPanel.add(obstacleButton);
        
        // Elevation slider
        JPanel elevationPanel = new JPanel(new BorderLayout(5, 0));
        elevationPanel.setBackground(PANEL_COLOR);
        JLabel elevationLabel = new JLabel("Elevation:");
        elevationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        elevationPanel.add(elevationLabel, BorderLayout.WEST);
        
        elevationSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
        elevationSlider.setMajorTickSpacing(2);
        elevationSlider.setMinorTickSpacing(1);
        elevationSlider.setPaintTicks(true);
        elevationSlider.setPaintLabels(true);
        elevationSlider.setBackground(PANEL_COLOR);
        elevationSlider.addChangeListener(e -> {
            currentElevation = elevationSlider.getValue();
            elevationValueLabel.setText("Current: " + currentElevation);
        });
        elevationPanel.add(elevationSlider, BorderLayout.CENTER);
        
        elevationValueLabel = new JLabel("Current: 0");
        elevationValueLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        elevationValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        tilePanel.add(tileTypesPanel);
        tilePanel.add(Box.createVerticalStrut(10));
        tilePanel.add(elevationPanel);
        tilePanel.add(Box.createVerticalStrut(5));
        tilePanel.add(elevationValueLabel);
        
        settingsPanel.add(tilePanel);
        settingsPanel.add(Box.createVerticalStrut(10));
        
        // Start and End points section
        JPanel startEndPanel = createStyledPanel("Start & End Points");
        
        JPanel startEndButtonsPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        startEndButtonsPanel.setBackground(PANEL_COLOR);
        
        ButtonGroup startEndGroup = new ButtonGroup();
        
        startButton = createPointButton("Place Start", startImage);
        startButton.addActionListener(e -> {
            isPlacingStart = true;
            isPlacingEnd = false;
            tileTypeGroup.clearSelection();
            updateStatusMessage();
        });
        startEndGroup.add(startButton);
        startEndButtonsPanel.add(startButton);
        
        endButton = createPointButton("Place End", endImage);
        endButton.addActionListener(e -> {
            isPlacingEnd = true;
            isPlacingStart = false;
            tileTypeGroup.clearSelection();
            updateStatusMessage();
        });
        startEndGroup.add(endButton);
        startEndButtonsPanel.add(endButton);
        
        startEndInfo = new JLabel("<html><div style='text-align:center;'>Current:(row,column)<br> Start:   ("+ 
                                 startPoint.x + "," + startPoint.y + ")<br> End:   (" + 
                                 endPoint.x + "," + endPoint.y + ")</div></html>");
        startEndInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        startEndInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        startEndPanel.add(startEndButtonsPanel);
        startEndPanel.add(Box.createVerticalStrut(5));
        startEndPanel.add(startEndInfo);
        
        settingsPanel.add(startEndPanel);
        tabbedPane.addTab("Settings", settingsPanel);
        
        // Actions Tab
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        actionsPanel.setBackground(PANEL_COLOR);
        
        generateButton = createStyledButton("Generate Random Maze");
        generateButton.addActionListener(e -> generateRandomMaze());
        
        saveButton = createStyledButton("Save Maze");
        saveButton.addActionListener(e -> saveMaze());
        
        JButton clearButton = createStyledButton("Clear Maze");
        clearButton.addActionListener(e -> clearMaze());
        
        // Enhanced Execute Project button with icon
        executeButton = new JButton("Find the Path", new ImageIcon(executeImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
        executeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        executeButton.setBackground(EXECUTE_COLOR);
        executeButton.setForeground(Color.WHITE);
        executeButton.setFocusPainted(false);
        executeButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(EXECUTE_COLOR.darker(), 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        executeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                executeButton.setBackground(EXECUTE_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                executeButton.setBackground(EXECUTE_COLOR);
            }
        });
        executeButton.addActionListener(e -> executeProject());
        executeButton.setToolTipText("Run safety classification and pathfinding");
        
        actionsPanel.add(generateButton);
        actionsPanel.add(Box.createVerticalStrut(15));
        actionsPanel.add(saveButton);
        actionsPanel.add(Box.createVerticalStrut(15));
        actionsPanel.add(clearButton);
        actionsPanel.add(Box.createVerticalStrut(15));
        actionsPanel.add(executeButton);
        
        tabbedPane.addTab("Actions", actionsPanel);
        
        controlPanel.add(tabbedPane);
        
        return controlPanel;
    }
    
private void updateStartEndInfo() {
        startEndInfo.setText("<html><div style='text-align:center;'>Current:(row,column)<br> Start:   (" + 
                            startPoint.x + "," + startPoint.y + ")<br> End:   (" + 
                            endPoint.x + "," + endPoint.y + ")</div></html>");
    
    // Update the static references
    Cell.startPoint = startPoint;
    Cell.endPoint = endPoint;
}
    
    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1), 
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12), PRIMARY_COLOR));
        panel.setBackground(PANEL_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        return button;
    }
    
    private JToggleButton createTileButton(String text, Image icon, TileType type) {
        JToggleButton button = new JToggleButton(text, new ImageIcon(
            icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setBackground(PANEL_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.addActionListener(e -> {
            currentTileType = type;
            isPlacingStart = false;
            isPlacingEnd = false;
            updateStatusMessage();
        });
        return button;
    }
    
    private JToggleButton createPointButton(String text, Image icon) {
        JToggleButton button = new JToggleButton(text, new ImageIcon(
            icon.getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setBackground(PANEL_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }
    
    private void showHelpDialog() {
        String helpText = "<html><div style='width:300px;'>"
                + "<h2 style='color:" + colorToHex(PRIMARY_COLOR) + ";'>Maze Generator Help</h2>"
                + "<p><b>Tile Types:</b></p>"
                + "<ul>"
                + "<li><b>Grass</b>: Safe terrain (green)</li>"
                + "<li><b>Water</b>: Potentially unsafe (blue)</li>"
                + "<li><b>Obstacle</b>: Blocked path (brown)</li>"
                + "</ul>"
                + "<p><b>Elevation:</b> Higher values (0-10) make tiles darker</p>"
                + "<p><b>Start/End Points:</b> Mark where pathfinding begins and ends</p>"
                + "<p><b>Controls:</b></p>"
                + "<ul>"
                + "<li>Click on maze to place selected tile type</li>"
                + "<li>Use elevation slider to set height</li>"
                + "<li>Place start and end points for pathfinding</li>"
                + "<li>Click 'Find the Path' to run safety classification and pathfinding</li>"
                + "</ul>"
                + "</div></html>";
        
        JOptionPane.showMessageDialog(this, helpText, "Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String colorToHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    private void updateMazeDimensions() {
        int newRows = (Integer) rowsSpinner.getValue();
        int newCols = (Integer) colsSpinner.getValue();
        
        // Preserve existing maze data where possible
        Cell[][] newMaze = new Cell[newRows][newCols];
        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                if (i < rows && j < cols) {
                    newMaze[i][j] = maze[i][j];
                } else {
                    newMaze[i][j] = new Cell(TileType.GRASS, 0);
                }
            }
        }
        
        // Update dimensions
        rows = newRows;
        cols = newCols;
        maze = newMaze;
        
        // Update start and end points if they're now out of bounds
        startPoint.x = Math.min(startPoint.x, rows - 1);

        startPoint.y = Math.min(startPoint.y, cols - 1);

        endPoint.x = Math.min(endPoint.x, rows - 1);

        endPoint.y = Math.min(endPoint.y, cols - 1);
        
        // Reset path visualization
        isPathCalculated = false;
        resetPathVisualization();
        
        // Update UI
        dimensionsLabel.setText("Current size: " + rows + " x " + cols);
        mazePanel.setPreferredSize(new Dimension(cols * CELL_SIZE, rows * CELL_SIZE));
        updateStartEndInfo();
        
        // Repaint and revalidate
        mazePanel.repaint();
        mazePanel.revalidate();
        statusLabel.setText("Maze dimensions updated to " + rows + " x " + cols);
    }
    
    private void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw grid cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                
                // Draw tile based on type
                drawTile(g2d, i, j, x, y);
                
                // Draw grid lines
                g2d.setColor(new Color(200, 200, 200, 150));
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Display elevation number
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                g2d.drawString(Integer.toString(maze[i][j].elevation), x + 5, y + 12);
                
                // Draw path if calculated
                if (isPathCalculated && maze[i][j].isPath) {
                    drawPathMarker(g2d, x, y);
                }
            }
        }
        
        // Highlight start and end points
        highlightPoint(g2d, startPoint.x, startPoint.y, START_COLOR);
        highlightPoint(g2d, endPoint.x, endPoint.y, END_COLOR);
    }
    
    private void drawPathMarker(Graphics2D g2d, int x, int y) {
        g2d.setColor(PATH_COLOR);
        g2d.fillOval(x + CELL_SIZE/4, y + CELL_SIZE/4, CELL_SIZE/2, CELL_SIZE/2);
    }
    
private void drawTile(Graphics2D g2d, int row, int col, int x, int y) {
    // Draw the tile based on type
    switch (maze[row][col].type) {
        case GRASS:
            if (grassImage != null) {
                g2d.drawImage(grassImage, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                g2d.setColor(new Color(144, 238, 144)); // Light green
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
            break;
        case WATER:
            if (waterImage != null) {
                g2d.drawImage(waterImage, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                g2d.setColor(new Color(135, 206, 250)); // Light blue
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
            break;
        case OBSTACLE:
            if (obstacleImage != null) {
                g2d.drawImage(obstacleImage, x, y, CELL_SIZE, CELL_SIZE, null);
            } else {
                g2d.setColor(new Color(139, 69, 19)); // Brown
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
            return; // Skip elevation for obstacles
    }
    
    // Apply elevation color overlay (darker for higher elevation)
    int elevation = maze[row][col].elevation;
    if (elevation > 0) {
        float alpha = elevation / 30.0f;
        g2d.setColor(new Color(0, 0, 0, alpha));
        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
    }
}   
    private void highlightPoint(Graphics2D g2d, int row, int col, Color color) {
        int x = col * CELL_SIZE;
        int y = row * CELL_SIZE;
        

        
        // Draw the point marker
        if (color == START_COLOR && startImage != null) {
            g2d.drawImage(startImage, x, y, CELL_SIZE, CELL_SIZE, null);
        } else if (color == END_COLOR && endImage != null) {
            g2d.drawImage(endImage, x, y, CELL_SIZE, CELL_SIZE, null);
        } else {
            // Draw a colored border
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4, 10, 10);
            
            // Draw text label
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            
            String label = (color == START_COLOR) ? "S" : "E";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = x + (CELL_SIZE - fm.stringWidth(label)) / 2;
            int textY = y + (CELL_SIZE + fm.getAscent() - fm.getDescent()) / 2;
            
            g2d.drawString(label, textX, textY);
        }
    }
    
    private void generateRandomMaze() {
        Random random = new Random();

        // Reset path visualization
        isPathCalculated = false;
        resetPathVisualization();

        // Generate random tiles
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Random tile type: 60% grass, 20% water, 20% obstacle
                int typeRandom = random.nextInt(100);
                TileType type;
                if (typeRandom < 60) {
                    type = TileType.GRASS;
                } else if (typeRandom < 80) {
                    type = TileType.WATER;
                } else {
                    type = TileType.OBSTACLE;
                }

                // Random elevation between 0 and 10 (0 for obstacles)
                int elevation = (type == TileType.OBSTACLE) ? 0 : random.nextInt(11);

                maze[i][j] = new Cell(type, elevation);
            }
        }
        
        // Random start and end points

        startPoint.x = random.nextInt(rows);

        startPoint.y = random.nextInt(cols);

        

        do {

            endPoint.x = random.nextInt(rows);

            endPoint.y = random.nextInt(cols);
        } while (endPoint.x == startPoint.x && endPoint.y == startPoint.y); // Ensure end != start
        
        // Make sure start and end tiles are grass
        maze[startPoint.x][startPoint.y].type = TileType.GRASS;
        maze[endPoint.x][endPoint.y].type = TileType.GRASS;
        maze[startPoint.x][startPoint.y].elevation = 0;
        maze[endPoint.x][endPoint.y].elevation = 0;
        
        updateStartEndInfo();
        mazePanel.repaint();
        statusLabel.setText("Random maze generated.");
    }
    
    private void clearMaze() {
        // Reset all tiles to grass with elevation 0
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = new Cell(TileType.GRASS, 0);
            }
        }
        
        // Reset start and end points
        startPoint.y = 0; // y=row
        startPoint.x = 0; // x=col
        endPoint.y = rows - 1;
        endPoint.x = cols - 1;
        
        // Reset elevation slider
        elevationSlider.setValue(0);
        
        // Reset path visualization
        isPathCalculated = false;
        resetPathVisualization();
        
        updateStartEndInfo();
        mazePanel.repaint();
        statusLabel.setText("Maze cleared.");
    }
    
    private void resetPathVisualization() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j].isPath = false;
            }
        }
    }
    
    private void saveMaze() {
        StringBuilder mazeData = new StringBuilder();
        mazeData.append("Maze Size: ").append(rows).append(" x ").append(cols).append("\n");
        mazeData.append("Start Point: (").append(startPoint.x).append(", ").append(startPoint.y).append(")\n");
        mazeData.append("End Point: (").append(endPoint.x).append(", ").append(endPoint.y).append(")\n\n");
        
        mazeData.append("Maze Data (Type,Elevation):\n");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String typeString = "";
                switch (maze[i][j].type) {
                    case GRASS: typeString = "G"; break;
                    case WATER: typeString = "W"; break;
                    case OBSTACLE: typeString = "O"; break;
                }
                mazeData.append(typeString).append(",").append(maze[i][j].elevation);
                if (j < cols - 1) mazeData.append("\t");
            }
            mazeData.append("\n");
        }
        
        JTextArea textArea = new JTextArea(mazeData.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Maze Data", JOptionPane.INFORMATION_MESSAGE);
        statusLabel.setText("Maze data displayed. In a real app, this would be saved to a file.");
    }
    
    private void updateStatusMessage() {
        if (isPlacingStart) {
            statusLabel.setText("Click on the maze to place the START point.");
        } else if (isPlacingEnd) {
            statusLabel.setText("Click on the maze to place the END point.");
        } else {
            String tileTypeText = "";
            switch (currentTileType) {
                case GRASS: tileTypeText = "GRASS"; break;
                case WATER: tileTypeText = "WATER"; break;
                case OBSTACLE: tileTypeText = "OBSTACLE"; break;
            }
            statusLabel.setText("Placing " + tileTypeText + " with elevation " + currentElevation + ".");
        }
    }
    
private void executeProject() {
    // Reset previous path visualization
    resetPathVisualization();
    
    // Show processing message
    statusLabel.setText("Run: Classifying tiles and finding path...");
    
    // Create and train the perceptron
    try {
        // Load training data
        String filePath = "src/main/resources/Data.xlsx";
        List<TrainingData> rawData = MazePerceptron.loadTrainingData(filePath);
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();
        
        for (TrainingData data : rawData) {
            features.add(MazePerceptron.normalizeFeatures(data.terrain, data.elevation, data.obstacleDist));
            labels.add(data.label);
        }
        
        // Create and train perceptron
//        MazePerceptron perceptron = new MazePerceptron(0.01, 1000);
MazePerceptron perceptron = new MazePerceptron(0.1, 1, 5000);

        perceptron.train(features, labels);
        
        // Create pathfinder and find path
        MazePathfinder pathfinder = new MazePathfinder(maze, rows, cols, perceptron);
        List<Point> path = pathfinder.findPath(startPoint, endPoint);
        
        // Visualize the path
        if (path.isEmpty()) {
            statusLabel.setText("No safe path found! Try adjusting the maze or changing start/end points.");
        } else {
            // Mark path tiles
            for (Point p : path) {
                // Skip start and end points
                if ((p.x == startPoint.x && p.y == startPoint.y) || 
                    (p.x == endPoint.x && p.y == endPoint.y)) {
                    continue;
                }
                maze[p.x][p.y].isPath = true;
            }
            
            isPathCalculated = true;
            mazePanel.repaint();
            statusLabel.setText("Path Found! Safe path highlighted in yellow. Path length: " + path.size());
        }
        
    } catch (IOException e) {
        statusLabel.setText("Error: Could not load training data - " + e.getMessage());
    }
}   
 
private class MazeClickListener extends MouseAdapter {
    @Override
    public void mouseClicked(MouseEvent e) {
        int col = e.getX() / CELL_SIZE;
        int row = e.getY() / CELL_SIZE;
        
        System.out.printf("Raw click: x=%d, y=%d | Converted to: row=%d, col=%d\n", 
            e.getX(), e.getY(), row, col);
        
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return; // Out of bounds
        }
        
        if (isPlacingStart) {
            System.out.printf("Attempting to place START at row=%d, col=%d\n", row, col);
                        
            // Don't allow placing start on obstacles
            if (maze[row][col].type != TileType.OBSTACLE) {

                startPoint.x = row;

                startPoint.y = col;

                statusLabel.setText("Start point placed at (" + row + "," + col + ").");
                updateStartEndInfo();
                isPathCalculated = false;
                resetPathVisualization();
            } else {
                statusLabel.setText("Cannot place start point on an obstacle!");
            }
        } else if (isPlacingEnd) {
            System.out.printf("Attempting to place START at row=%d, col=%d\n", row, col);

            // Don't allow placing end on obstacles
            if (maze[row][col].type != TileType.OBSTACLE) {
        endPoint.x = row; 
        endPoint.y = col; 
        statusLabel.setText("End point placed at (" + row + "," + col + ").");
                updateStartEndInfo();
                isPathCalculated = false;
                resetPathVisualization();
            } else {
                statusLabel.setText("Cannot place end point on an obstacle!");
            }
        } else {
            // Prevent placing obstacles on start/end points
            if (currentTileType == TileType.OBSTACLE && 
                ((row == startPoint.x && col == startPoint.y) || 
                 (row == endPoint.x && col == endPoint.y))) {
                statusLabel.setText("Cannot place obstacle on start/end points!");
                return;
            }
            
            // Update tile properties
            maze[row][col].type = currentTileType;
            if (currentTileType == TileType.OBSTACLE) {
                maze[row][col].elevation = 0; // Reset elevation for obstacles
            } else {
                maze[row][col].elevation = currentElevation;
            }
            statusLabel.setText("Updated tile at (" + row + "," + col + ").");
            isPathCalculated = false;
            resetPathVisualization();
        }
        
        mazePanel.repaint();
    }
}  
}