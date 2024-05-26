import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomophilyTest {
    private Map<Integer, Set<Integer>> graph = new HashMap<>();
    private Map<Integer, Set<Integer>> features = new HashMap<>();
    private Map<Integer, String> featureNames = new HashMap<>();
    private static final int EGO_NODE_ID = 1684;

    public void loadEdges(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int node1 = Integer.parseInt(parts[0]);
            int node2 = Integer.parseInt(parts[1]);

            graph.computeIfAbsent(node1, k -> new HashSet<>()).add(node2);
            graph.computeIfAbsent(node2, k -> new HashSet<>()).add(node1);
        }
        reader.close();
    }

    public void loadFeatures(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int nodeId = Integer.parseInt(parts[0]);
            Set<Integer> nodeFeatures = new HashSet<>();
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].equals("1")) {
                    nodeFeatures.add(i - 1);
                }
            }
            features.put(nodeId, nodeFeatures);
        }
        reader.close();
    }

    public void loadEgoFeatures(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        if ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            Set<Integer> egoNodeFeatures = new HashSet<>();
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("1")) {
                    egoNodeFeatures.add(i);
                }
            }
            features.put(EGO_NODE_ID, egoNodeFeatures);
        }
        reader.close();
    }

    public void loadFeatureNames(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int featureId = Integer.parseInt(parts[0]);
            featureNames.put(featureId, parts[1]);
        }
        reader.close();
    }

    public void homophilyTest(PrintWriter writer, int featureIdForTest) throws IOException {
        // Count the nodes with each value of the feature
        int countValue1 = 0, countValue2 = 0;
        for (Set<Integer> nodeFeatures : features.values()) {
            if (nodeFeatures.contains(featureIdForTest)) {
                countValue1++;
            } else {
                countValue2++;
            }
        }
    
        //Calculate proportions p and q
        double p = (double) countValue1 / (countValue1 + countValue2);
        double q = 1 - p; //since there are only two values
    
        int crossFeatureEdges = 0;
        int totalEdges = 0;
    
        for (Map.Entry<Integer, Set<Integer>> entry : graph.entrySet()) {
            int nodeId = entry.getKey();
            Set<Integer> neighbors = entry.getValue();
            boolean nodeHasFeature = features.get(nodeId).contains(featureIdForTest);
    
            for (int neighborId : neighbors) {
                totalEdges++;
                boolean neighborHasFeature = features.containsKey(neighborId) && features.get(neighborId).contains(featureIdForTest);
                if (nodeHasFeature != neighborHasFeature) {
                    crossFeatureEdges++;
                }
            }
        }
        crossFeatureEdges /= 2; // each edge counted twice
        totalEdges /= 2;
    
        double expectedCrossFeatureEdges = 2 * p * q * totalEdges;
        
        
        writer.println(crossFeatureEdges + " " + expectedCrossFeatureEdges);
    }

    public static void main(String[] args) {
        HomophilyTest network = new HomophilyTest();
        try {
            network.loadEdges("..\\..\\161101071\\dataset\\facebook\\1684.edges");
            network.loadFeatures("..\\..\\161101071\\dataset\\facebook\\1684.feat");
            network.loadEgoFeatures("..\\..\\161101071\\dataset\\facebook\\1684.egofeat");
            network.loadFeatureNames("..\\..\\161101071\\dataset\\facebook\\1684.featnames");

            PrintWriter writer = new PrintWriter("output.txt");

            // Iterate over all feature IDs
            for (int featureId : network.featureNames.keySet()) {
                network.homophilyTest(writer, featureId);
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
