package graph;

import heap.Heap;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;


/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node, PathData> paths;
    /**
     * Compute the shortest path to all nodes from origin using Dijkstra's algorithm. Fill in the paths field, which
     * associates each Node with its PathData record, storing total distance from the source, and the backpointer to the
     * previous node on the shortest path. Precondition: origin is a node in the Graph.
     */
    public void compute(Node origin) {
        paths = new HashMap<Node, PathData>();
        Set<Node> seen = new HashSet<Node>();
        Heap unProcessed = new Heap();
        PathData og = new PathData(0, null);
        seen.add(origin);
        unProcessed.add(origin,0);
        paths.put(origin,og);
        while (unProcessed.size() != 0){
            Node current = ((Node)unProcessed.peek());
            for (Node neighbor : current.getNeighbors().keySet()){
                if (!seen.contains(neighbor)){
                    // seeing j for the first time, add path details
                     PathData neighborData = new PathData(paths.get(current).distance+current.getNeighbors().get(neighbor), current);
                     paths.put(neighbor,neighborData);
                     paths.get(neighbor).previous = current;
                     seen.add(neighbor);
                     unProcessed.add(neighbor,(int)paths.get(neighbor).distance);
                } else {
                    // we have seen j before then, check if  the path is shorter.
                        if(paths.get(current).distance + current.getNeighbors().get(neighbor) < paths.get(neighbor).distance){
                            System.out.println("inside");
                        paths.get(neighbor).distance = paths.get(current).distance + current.getNeighbors().get(neighbor);
                        paths.get(neighbor).previous = current;
                    }
                }

            }
            unProcessed.poll();
        }

    }

    /**
     * Returns the length of the shortest path from the origin to destination. If no path exists, return
     * Double.POSITIVE_INFINITY. Precondition: destination is a node in the graph, and compute(origin) has been called.
     */
    public double shortestPathLength(Node destination) {
        return paths.get(destination).distance;
    }

    /**
     * Returns a LinkedList of the nodes along the shortest path from origin to destination. This path includes the
     * origin and destination. If origin and destination are the same node, it is included only once. If no path to it
     * exists, return null. Precondition: destination is a node in the graph, and compute(origin) has been called.
     */
    public LinkedList<Node> shortestPath(Node destination) {
        // TODO 3 - implement this method to reconstruct sequence of Nodes
        // along the shortest path from the origin to destination using the
        // paths data computed by Dijkstra's algorithm.
        LinkedList<Node> joe = new LinkedList<Node>();
        joe.add(destination);
        while(paths.get(destination).previous != null){
            joe.add(paths.get(destination).previous);
            destination = paths.get(destination).previous;
        }
        Collections.reverse(joe);
        return joe;
    }


    /**
     * Inner class representing data used by Dijkstra's algorithm in the process of computing shortest paths from a
     * given source node.
     */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /**
         * constructor: initialize distance and previous node
         */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }


    /**
     * Static helper method to open and parse a file containing graph information. Can parse either a basic file or a
     * DB1B CSV file with flight data. See GraphParser, BasicParser, and DB1BParser for more.
     */
    protected static Graph parseGraph(String fileType, String fileName) throws
            FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
        // read command line args
        String fileType = args[0];
        String fileName = args[1];
        String origCode = args[2];

        String destCode = null;
        if (args.length == 4) {
            destCode = args[3];
        }

        // parse a graph with the given type and filename
        Graph graph;
        try {
            graph = parseGraph(fileType, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file " + fileName);
            return;
        }
        graph.report();


        // TODO 4: create a ShortestPaths object, use it to compute shortest
        // paths data from the origin node given by origCode.

        // TODO 5:
        // If destCode was not given, print each reachable node followed by the
        // length of the shortest path to it from the origin.

        // TODO 6:
        // If destCode was given, print the nodes in the path from
        // origCode to destCode, followed by the total path length
        // If no path exists, print a message saying so.
    }
}
