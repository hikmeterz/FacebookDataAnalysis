package Q3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;


public class SchellingModel {
    private int n;
    private Agent[][] grid;
    private int numTypes;
    private int[] typeCounts;
    private int threshold;
    private Random random = new Random();

    public SchellingModel(int n, int numTypes, int[] typeCounts, int threshold) {
        this.n = n;
        this.numTypes = numTypes;
        this.typeCounts = typeCounts;
        this.threshold = threshold;
        grid = new Agent[n][n];
        initializeGrid();
    }

    private void initializeGrid() {
        List<Agent> allAgents = new ArrayList<>();
        for (int i = 0; i < numTypes; i++) {
            for (int j = 0; j < typeCounts[i]; j++) {
                allAgents.add(new Agent(i, -1, -1)); 
            }
        }

        Collections.shuffle(allAgents);
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (index < allAgents.size()) {
                    Agent agent = allAgents.get(index++);
                    agent.x = i;
                    agent.y = j;
                    grid[i][j] = agent;
                } else {
                    grid[i][j] = null; 
                }
            }
        }
    }

    private void move(Agent agent) {
        int x, y;
        do {
            x = random.nextInt(n);
            y = random.nextInt(n);
        } while (grid[x][y] != null);

        grid[agent.x][agent.y] = null;
        agent.x = x;
        agent.y = y;
        grid[x][y] = agent;
    }

    private boolean calculateUtility(Agent agent) {
        int similarNeighbors = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 || dy != 0) {
                    int neighborX = agent.x + dx;
                    int neighborY = agent.y + dy;
                    if (neighborX >= 0 && neighborX < n && neighborY >= 0 && neighborY < n && grid[neighborX][neighborY] != null) {
                        Agent neighbor = grid[neighborX][neighborY];
                        if (neighbor.type == agent.type) {
                            similarNeighbors++;
                        }
                    }
                }
            }
        }
        return similarNeighbors >= threshold;
    }

    public void runSimulation() {
        int maxIterations = 1000; 
        for (int i = 0; i < maxIterations; i++) {
            boolean moved = false;
            List<Agent> agents = getAgentsInRandomOrder();
            for (Agent agent : agents) {
                if (!calculateUtility(agent)) {
                    move(agent);
                    moved = true;
                }
            }
            if (!moved) {
                break; 
            }
            visualize(); 
        }
    }
    public void visualize() {
    JFrame frame = new JFrame("Schelling Model Visualization");
    frame.setSize(800, 800);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (grid[i][j] != null) {
                        if (grid[i][j].type == 0) {
                            g.setColor(Color.RED);
                        } else if (grid[i][j].type == 1) {
                            g.setColor(Color.BLUE);
                        } else {
                            g.setColor(Color.GREEN); 
                        }
                        g.fillRect(j * 4, i * 4, 4, 4); 
                    }
                }
            }
        }
    };

    frame.add(panel);
    frame.setVisible(true);
}


    private List<Agent> getAgentsInRandomOrder() {
        List<Agent> agents = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] != null) {
                    agents.add(grid[i][j]);
                }
            }
        }
        Collections.shuffle(agents);
        return agents;
    }

    public static void main(String[] args) {
        int n = 200; // Grid size
        int numTypes = 2; // Number of types
        int[] typeCounts = {15000, 15000}; // Number of agents for each type
        int threshold = 3; // Threshold for satisfaction

        SchellingModel model = new SchellingModel(n, numTypes, typeCounts, threshold);
        model.runSimulation();

        // For additional experiments, create new instances of SchellingModel with different parameters
    }
}

class Agent {
    int type;
    int x;
    int y;

    public Agent(int type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }
}
