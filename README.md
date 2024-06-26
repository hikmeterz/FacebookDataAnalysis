﻿# FacebookDataAnalysis
This repository contains Java implementations for analyzing Facebook social network data. The analyses cover various aspects of the network such as homophily, segregation models, and network processing.

## Implementation Details

### Homophily Test 

1. **Load the Graph**: The program reads the Facebook dataset file and constructs an adjacency list representation of the graph.
2. **Find Giant Component**: Using BFS, the program identifies the largest connected component in the graph.
3. **Calculate Distances**: It computes the shortest path distances between every pair of vertices within the giant component.
4. **Generate Histogram**: The program generates a histogram of these distances and saves it in `output.txt`.

### Segregation Model 

1. **Initialize the Grid**: The program initializes a grid representing a neighborhood with different agents based on Facebook data.
2. **Simulate Segregation**: The program simulates the movement of agents based on their satisfaction with their neighbors.
3. **Display Results**: The program displays the final state of the grid after the simulation.

### Network Processing 

1. **Load the Graph**: The program reads the Facebook dataset file and constructs an adjacency list representation of the graph.
2. **Analyze Network**: The program performs various analyses on the network, such as calculating centrality measures.
3. **Display Results**: The program displays the results of the analyses in the console.
