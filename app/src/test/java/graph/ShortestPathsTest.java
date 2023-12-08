package graph;

 /* Author: Kieran Rapo
 * Date: 12/06/2023
 * Description: Test Cases for ShortestPath and associated methods */

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.LinkedList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortestPathsTest {

    /* Performs the necessary gradle-related incantation to get the
       filename of a graph text file in the src/test/resources directory at
       test time.*/
    private String getGraphResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return resource.getPath();
    }

    /* Returns the Graph loaded from the file with filename fn located in
     * src/test/resources at test time. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        String filePath = getGraphResource(fn);
        try {
          result = ShortestPaths.parseGraph("basic", filePath);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Dummy test case demonstrating syntax to create a graph from scratch.
     * Write your own tests below. */
    @Test
    public void test00Nothing() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 1);
        ShortestPaths sp = new ShortestPaths();
        sp.compute(a);
        // sample assertion statements:
        assertTrue(true);
        assertEquals(2+2, 4);
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test01Simple0() {
        Graph g = loadBasicGraph("Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);
    }
    /** Test case to go from path A to S in Simple1.txt */
    @Test
    public void test02Simple1() {
        Graph g = loadBasicGraph("Simple1.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node s = g.getNode("S");
        LinkedList<Node> asPath = sp.shortestPath(s);
        assertEquals(asPath.size(), 4);
        assertEquals(asPath.getFirst(), a);
        assertEquals(asPath.getLast(),  s);
        assertEquals(sp.shortestPathLength(s), 5.0, 1e-6);
    }

    /** Test case to run through A to I in Simple2.txt, as well it tests the shortest path part */
    @Test
    public void test03Simple2() {
        Graph g = loadBasicGraph("Simple2.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node i = g.getNode("I");
        LinkedList<Node> aiPath = sp.shortestPath(i);
        assertEquals(aiPath.size(), 4);
        assertEquals(aiPath.getFirst(), a);
        assertEquals(aiPath.getLast(),  i);
        assertEquals(sp.shortestPathLength(i), 5.0, 1e-6);
    }

    /** Test case to check a non-path due to the directed nature of the graph */
    @Test
    public void test04NoPathDirection() {
        Graph g = loadBasicGraph("Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node b = g.getNode("B");
        sp.compute(b);
        Node a = g.getNode("A");
        LinkedList<Node> baPath = sp.shortestPath(a);
        assertEquals(baPath, null);
        assertEquals(sp.shortestPathLength(a), Double.POSITIVE_INFINITY, 1e-6);
    }

    /** Test case to check a destination of the origin point.*/
    @Test
    public void test05OriginEater() {
        Graph g = loadBasicGraph("Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        LinkedList<Node> abPath = sp.shortestPath(a);
        assertEquals(abPath.size(), 1);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(), a);
        assertEquals(sp.shortestPathLength(a), 0.0, 1e-6);
    }

    /** Test case to check a new Simple3.txt file */
    @Test
    public void test06Simple3() {
        Graph g = loadBasicGraph("Simple3.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node e = g.getNode("E");
        LinkedList<Node> aePath = sp.shortestPath(e);
        assertEquals(aePath.size(), 5);
        assertEquals(aePath.getFirst(),a);
        assertEquals(aePath.getLast(), e);
        assertEquals(sp.shortestPathLength(e), 10.0, 1e-6);

    }

    /** Test case to check Nodes in different components. */
    @Test
    public void test07DifferentComponents() {
        Graph g = loadBasicGraph("Simple3.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node y = g.getNode("Y");
        LinkedList<Node> ayPath = sp.shortestPath(y);
        assertEquals(ayPath, null);
        assertEquals(sp.shortestPathLength(y), Double.POSITIVE_INFINITY, 1e-6);
    }

    /** Test case to go through the FakeCanada.txt file */
    @Test 
    public void test08FakeCanada() {
        Graph g = loadBasicGraph("FakeCanada.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node montreal = g.getNode("YUL");
        sp.compute(montreal);
        Node vancouver = g.getNode("YVR");
        LinkedList<Node> mvPath = sp.shortestPath(vancouver);
        assertEquals(mvPath.size(), 5);
        assertEquals(mvPath.getFirst(), montreal);
        assertEquals(mvPath.getLast(), vancouver);
        assertEquals(sp.shortestPathLength(vancouver), 2423.0, 1e-6);
    }



    /* Pro tip: unless you include @Test on the line above your method header,
     * gradle test will not run it! This gets me every time. */
}
