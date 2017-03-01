import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;


public class MyGraphTest {

    @Test
    public void testMyGraph() {
	MyGraph test = new MyGraph();
	assertEquals("numvertices = 0", test.vertices(), 0);
	assertEquals("numedges = 0", test.edges(), 0);
    }

    @Test
    public void testVertices() {
	MyGraph test = new MyGraph();
	test.insertVertex("1");
	test.insertVertex("4");
	test.insertVertex("5");
	assertEquals("NumVertices = 3", test.vertices(), 3);
    }

    @Test
    public void testEdges() {
	MyGraph test = new MyGraph();
	test.insertVertex("1");
	test.insertVertex("2");
	test.insertEdge("1", "2", 1);
	assertEquals("NumEdges = 1", test.edges(), 1);
    }

    @Test
    public void testAreAdjacent() {
	MyGraph test = new MyGraph();
	test.insertVertex("1");
	test.insertVertex("2");
	assertFalse("1 and 2 should not be adjacent", test.areAdjacent("1", "2"));
	test.insertEdge("1", "2", 1);
	assertTrue("1 and 2 should be adjacent", test.areAdjacent("1", "2"));
	test.insertVertex("3");
	test.insertEdge("2", "3", 1);
	assertFalse("1 and 3 should not be adjacent, even though they are connected", test.areAdjacent("1", "3"));
    }

    @Test
    public void testRemoveEdge() {
	MyGraph test = new MyGraph();
	test.insertVertex("Kevin");
	test.insertVertex("Carl");
	test.insertVertex("Stacy");
	test.insertEdge("Kevin", "Stacy", 1);
	System.out.println(test);
	assertTrue("Should remove edge between kevin and stacy", test.removeEdge("Kevin", "Stacy"));
	assertEquals("NumEdges = 0", test.edges(), 0);
	System.out.println(test);
    }

    @Test
    public void testRemoveVertex() {
	MyGraph test = new MyGraph();
	test.insertVertex("Kevin");
	test.insertVertex("Carl");
	test.insertVertex("Stacy");
	test.insertEdge("Kevin", "Stacy", 1);
	test.insertEdge("Kevin", "Carl", 1);
	System.out.println(test);
	assertTrue("Should remove vertex kevin", test.removeVertex("Kevin"));
	System.out.println(test);
	assertEquals("Num Edges should be 0", test.edges(), 0);
	assertEquals("Num vertices should be 2", test.vertices(), 2);
    }

    @Test
    public void testInsertEdge() {
	MyGraph test = new MyGraph();
	test.insertVertex("X");
	test.insertVertex("Y");
	test.insertVertex("Z");
	System.out.println(test);
	test.insertEdge("X", "Z", 5);
	assertEquals("NumEdges should be 1", test.edges(), 1);
	System.out.println(test);
	assertSame("insert edge from x to w should return null", test.insertEdge("X", "W", 1), null);
    }

    @Test
    public void testInsertVertex() {
	MyGraph test = new MyGraph();
	test.insertVertex("X");
	test.insertVertex("Y");
	test.insertVertex("Z");
	assertEquals("Numvertices should be 3", test.vertices(), 3);
    }

    @Test
    public void testAdjacentVertices() {
	MyGraph test = new MyGraph();
	test.insertVertex("K");
	test.insertVertex("W");
	test.insertVertex("X");
	test.insertVertex("Y");
	test.insertVertex("Z");
	test.insertEdge("K", "W", 1);
	test.insertEdge("K", "X", 1);
	test.insertEdge("K", "Y", 1);
	System.out.println(test);
	LinkedList<MyGraph.Vertex> v = test.adjacentVertices("K");
	System.out.println("Vertices adjacent to K: ");
	for (int i = 0; i < v.size(); i++) {
	    System.out.println(v.get(i));
	}
    }

    @Test
    public void testGetDists() {
	MyGraph test = new MyGraph();
	test.insertVertex("A");
	test.insertVertex("B");
	test.insertVertex("C");
	test.insertVertex("D");
	test.insertVertex("E");
	test.insertVertex("F");
	test.insertVertex("G");
	test.insertEdge("A", "B", 2);
	test.insertEdge("A", "D", 1);
	test.insertEdge("B", "D", 3);
	test.insertEdge("B", "E", 10);
	test.insertEdge("C", "A", 4);
	test.insertEdge("C", "F", 5);
	test.insertEdge("D", "E", 2);
	test.insertEdge("D", "G", 4);
	test.insertEdge("D", "F", 8);
	test.insertEdge("D", "C", 2);
	test.insertEdge("E", "G", 6);
	test.insertEdge("G", "F", 1);
	
	System.out.println(test.getDists("B"));
    }

    @Test
    public void testDupes(){
    	MyGraph test = new MyGraph();
    	test.insertVertex("A");
    	test.insertVertex("B");
    	System.out.println(test);
    	test.insertVertex("A");
    	System.out.println(test);
    	test.insertEdge("A", "B", 1);
    	System.out.println(test);
    	test.insertEdge("A", "B", 2);
    	System.out.println(test);
    }
}
