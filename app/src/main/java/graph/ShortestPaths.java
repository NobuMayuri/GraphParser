package graph;

 /* Author: Kieran Rapo
 * Date: 12/06/2023
 * Description: This class implemets Dijkstra's Algorithm. Can calculate all the shortest paths from an origin node, the shortest path to a destination node
 * As well, can provide a linked list of all nodes on that pathway to a destination node.
 * Class also contains the main method. */

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
        // sets up paths, seen, unprocessed for the algorithm.
        paths = new HashMap<Node, PathData>();
        Set<Node> seen = new HashSet<Node>();
        Heap unProcessed = new Heap();
        PathData og = new PathData(0, null);
        seen.add(origin);
        unProcessed.add(origin,0);
        paths.put(origin,og);
        // while there are still unprocessed nodes, run through each checking every neighbour and comparing them.
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
                        paths.get(neighbor).distance = paths.get(current).distance + current.getNeighbors().get(neighbor);
                        paths.get(neighbor).previous = current;
                    }
                }

            }
            // remove the processed node.
            unProcessed.poll();
        }

    }

    /**
     * Returns the length of the shortest path from the origin to destination. If no path exists, return
     * Double.POSITIVE_INFINITY. Precondition: destination is a node in the graph, and compute(origin) has been called.
     */
    public double shortestPathLength(Node destination) {
        if (paths.get(destination) == null)
            return Double.POSITIVE_INFINITY;
        return paths.get(destination).distance;
    }
    /*
    * Returns paths due to it being private, used in main for going through every node
    */
    public HashMap<Node, PathData> getPaths(){
        return paths;
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
        if (shortestPathLength(destination) == Double.POSITIVE_INFINITY)
            return null;
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
        // sets up the shortestpaths object and does Dijkastra's algo on them.
        ShortestPaths sp = new ShortestPaths();
        Node origNode = graph.getNode(origCode);
        sp.compute(origNode);
        HashMap<Node, PathData> mapping = sp.getPaths(); // importing path data from sp.
        // if no destination, run through each node available.
        if (destCode == null){
            System.out.println("Shortest Paths from A:");
                for (Node current : mapping.keySet()){
                    System.out.println(current.getId() + ": " + mapping.get(current).distance);
                }
        }
        // otherwise, run through to the destination or tells you there is no paths if there is none.
        else{
            Node destNode = graph.getNode(destCode);
                if (sp.shortestPathLength(destNode) != Double.POSITIVE_INFINITY){ // checks if there is a connection.
                    LinkedList<Node> bob = sp.shortestPath(destNode);
                        for(Node runner : bob){
                            System.out.print(runner.getId() + " ");
                        }
                    System.out.println(sp.shortestPathLength(destNode));
                }
                else{
                    System.out.println("No Path Exists");
                }
        }

    }
}
