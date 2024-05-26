package Q2;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class SlashdotNetworkProcessor {

    static int crossFeatureEdges=0;
    public static void main(String[] args) {
        String datasetPath = "..\\..\\161101071\\dataset\\soc-sign-Slashdot090221.txt";
        SlashdotNetworkProcessor.Graph undirectedGraph = processSlashdotData(datasetPath);
    
        // Output the total number of edges
        crossFeatureEdges = undirectedGraph.countCrossFeatureEdges();
        //System.out.println("Total number of edges: " + totalEdges);
    
        boolean isBalanced = undirectedGraph.isBalanced();
        //System.out.println("The graph is balanced: " + isBalanced);
    
        if (!isBalanced) {
            List<Integer> minNegativeCycle = undirectedGraph.findMinNegativeCycle();
            if (minNegativeCycle != null) {
                //System.out.println("Cycle with the smallest number of negative edges: " + minNegativeCycle);
                try {
                    undirectedGraph.writeCyclesToFile(Collections.singletonList(minNegativeCycle), "output.txt");
                    //System.out.println("Cycle written to output.txt");
                } catch (IOException e) {
                    System.err.println("Error writing to file: " + e.getMessage());
                }
            } else {
                System.out.println("No such cycle found.");
            }
        }
    }

    private static Graph processSlashdotData(String datasetPath) {
        Graph graph = new Graph(); 
        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                int source = Integer.parseInt(parts[0]);
                int target = Integer.parseInt(parts[1]);
                int sign = Integer.parseInt(parts[2]);

                
                graph.addEdge(source, target, sign);
                graph.addEdge(target, source, sign); 
            }
        } catch (IOException e) {
            System.err.println("Error reading dataset: " + e.getMessage());
        }
        return graph;
    }
    

     
    static class Graph {
        Map<Integer, Map<Integer, Integer>> adjacencyList = new HashMap<>();
        private List<List<Integer>> allCycles;
    
        public Graph() {
            this.allCycles = new ArrayList<>(); 
        }
        void addEdge(int source, int target, int sign) {
            adjacencyList.putIfAbsent(source, new HashMap<>());
            adjacencyList.putIfAbsent(target, new HashMap<>());
            adjacencyList.get(source).put(target, sign);
            adjacencyList.get(target).put(source, sign); 
        }
        public int countCrossFeatureEdges() {
            int crossFeatureEdges = 0;
            for (Map.Entry<Integer, Map<Integer, Integer>> entry : adjacencyList.entrySet()) {
                for (Integer sign : entry.getValue().values()) {
                    
                    if (sign < 0) {
                        crossFeatureEdges++;
                    }
                }
            }
            // Since the graph is undirected and each edge is counted twice, divide by 2
            return crossFeatureEdges / 2;
        }
        public boolean isBalanced() {
            Map<Integer, Integer> group = new HashMap<>();
            for (int node : adjacencyList.keySet()) {
                if (!group.containsKey(node)) {
                    group.put(node, 1);
                    Queue<Integer> queue = new LinkedList<>();
                    queue.add(node);

                    while (!queue.isEmpty()) {
                        int current = queue.poll();

                        for (Map.Entry<Integer, Integer> entry : adjacencyList.get(current).entrySet()) {
                            int neighbor = entry.getKey();
                            int sign = entry.getValue();

                            if (!group.containsKey(neighbor)) {
                                
                                group.put(neighbor, sign == 1 ? group.get(current) : -group.get(current));
                                queue.add(neighbor);
                            } else {
                                
                                if (group.get(neighbor) != (sign == 1 ? group.get(current) : -group.get(current))) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        public List<Integer> findMinNegativeCycle() {
            findAllCycles(3); 

            List<Integer> minNegativeCycle = null;
            int minNegatives = Integer.MAX_VALUE;

            for (List<Integer> cycle : allCycles) {
                int negativeCount = 0;
                for (int i = 0; i < cycle.size(); i++) {
                    int node = cycle.get(i);
                    int nextNode = cycle.get((i + 1) % cycle.size());
                    if (adjacencyList.get(node).get(nextNode) < 0) {
                        negativeCount++;
                    }
                }
                if (negativeCount % 2 != 0 && negativeCount < minNegatives) {
                    minNegatives = negativeCount;
                    minNegativeCycle = cycle;
                }
            }

            return minNegativeCycle;
        }
        private void dfsFindCycles(int start, Set<Integer> visited, int maxCycleSize) {
            Deque<NodeDepthPair> stack = new ArrayDeque<>();
            stack.push(new NodeDepthPair(start, 0, -1)); 
            Map<Integer, Integer> parent = new HashMap<>();
        
            while (!stack.isEmpty()) {
                NodeDepthPair pair = stack.pop();
                int node = pair.node;
                int depth = pair.depth;
                int parentNode = pair.parent;
        
                if (!visited.contains(node)) {
                    visited.add(node);
                    parent.put(node, parentNode);
        
                    for (int neighbor : adjacencyList.get(node).keySet()) {
                        if (!visited.contains(neighbor) && depth + 1 <= maxCycleSize) {
                            stack.push(new NodeDepthPair(neighbor, depth + 1, node));
                        } else if (neighbor != parentNode && depth + 1 <= maxCycleSize) {
                           
                            List<Integer> cycle = new ArrayList<>();
                            int current = node;
                            while (current != -1) {
                                cycle.add(current);
                                current = parent.getOrDefault(current, -1);
                                if (current == neighbor) {
                                    cycle.add(neighbor); 
                                    Collections.reverse(cycle);
                                    allCycles.add(cycle);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        private static class NodeDepthPair {
            int node;
            int depth;
            int parent;
        
            NodeDepthPair(int node, int depth, int parent) {
                this.node = node;
                this.depth = depth;
                this.parent = parent;
            }
        }
        
        private void findAllCycles(int maxCycleSize) {
            allCycles.clear();
            Set<Integer> visited = new HashSet<>();
            for (int startNode : adjacencyList.keySet()) {
                if (!visited.contains(startNode)) {
                    dfsFindCycles(startNode, visited, maxCycleSize);
                }
            }
        }
        
        public void writeCyclesToFile(List<List<Integer>> cycles, String filePath) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (List<Integer> cycle : cycles) {
                    Collections.sort(cycle); 
                    writer.write(crossFeatureEdges + "\n");
                    for (int node : cycle) {
                        writer.write(node + "\n"); 
                    }
                    writer.write("--\n"); 
                }
            }
        }

    }
}
