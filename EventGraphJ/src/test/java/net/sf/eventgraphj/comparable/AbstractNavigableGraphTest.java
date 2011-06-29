package net.sf.eventgraphj.comparable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;

import edu.uci.ics.jung.graph.util.EdgeType;

public abstract class AbstractNavigableGraphTest {
	protected NavigableGraph<Integer, Integer, Integer> graph;
	protected int m_VertexCount;
	protected int m_EdgeCount;
	protected Injector injector;

	/*
		public static void assertEquals(int expected, int actual, String message) {
			Assert.assertEquals(message, expected, actual);
		}

		public static void assertEquals(int expected, int actual) {
			Assert.assertEquals(expected, actual);
		}

		public static void assertEquals(long expected, long actual, String message) {
			Assert.assertEquals(message, expected, actual);
		}

		public static void assertEquals(long expected, long actual) {
			Assert.assertEquals(expected, actual);
		}

		public static void assertEquals(double expected, double actual,
				String message) {
			Assert.assertEquals(message, expected, actual);
		}

		public static void assertEquals(double expected, double actual) {
			Assert.assertEquals(expected, actual);
		}
	
	public static <T> void assertEquals(String message, T expected, T actual) {
		Assert.assertSame(message, expected, actual);
	}

	public static <T> void assertEquals(T expected, T actual) {
		Assert.assertSame(expected, actual);
	}

	public static <T> void assertTrue(String message, boolean condition) {
		Assert.assertTrue(message, condition);
	}

	public static <T> void assertTrue(boolean condition) {
		Assert.assertTrue(condition);
	}
	*/

	@Before
	public void setUpRandomGraph() throws Exception {
		this.graph = this.injector.getInstance(NavigableGraph.class);
		System.out.println(this.graph.getClass().getName());
		this.clearGraph();
		this.m_EdgeCount = 0;
		assertTrue(this.graph.addEdge(1, 0, 1, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(2, 1, 2, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(3, 0, 2, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(4, 2, 1, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(6, 3, 1, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(6, 0, 4, EdgeType.DIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(7, 0, 5, EdgeType.DIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(8, 5, 1, EdgeType.DIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(9, 6, 1, EdgeType.DIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(10, 4, 3, EdgeType.DIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(11, 2, 7, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(12, 1, 5, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(13, 2, 6, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(14, 6, 4, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(15, 7, 8, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(16, 8, 3, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		assertTrue(this.graph.addEdge(17, 5, 7, EdgeType.UNDIRECTED));
		this.m_EdgeCount++;
		this.m_VertexCount = 9;
		// System.out.println("Vertices:" +
		// this.graph.getVertices().toString());
		// System.out.println("Edges:" + this.graph.getEdges().toString());
	}

	@Test
	public void testGetInOutEdgesgraph() {

		assertEquals("to 0", 2, this.graph.getInEdges(0).size());
		assertEquals("from 0", 4, this.graph.getOutEdges(0).size());
		assertEquals("from 4", 2, this.graph.getOutEdges(4).size());
	}

	public void clearGraph() {
		Collection<Integer> vertices = new ArrayList<Integer>(
				this.graph.getVertices());
		for (Integer v : vertices) {
			this.graph.removeVertex(v);
		}
	}

	@Test
	public void testClearGraph() {
		this.clearGraph();
		assertEquals("edge count", 0, this.graph.getEdgeCount());
		assertTrue("edges size", this.graph.getEdges().isEmpty());
		assertEquals("vertex count", 0, this.graph.getVertexCount());
		assertTrue("vertices size", this.graph.getVertices().isEmpty());
	}

	@Test
	public void testCounts() {
		assertEquals("edge count", this.m_EdgeCount, this.graph.getEdgeCount());
		assertEquals("edges size", this.m_EdgeCount, this.graph.getEdges()
				.size());
		assertEquals("vertex count", this.m_VertexCount,
				this.graph.getVertexCount());
		assertEquals("vertices size", this.m_VertexCount, this.graph
				.getVertices().size());
	}

	@Test
	public void testAddVertex() {
		int count = this.graph.getVertexCount();
		this.graph.addVertex(count);
		assertEquals("count + 1", count + 1, this.graph.getVertexCount());
	}

	@Test
	public void testRemoveEndVertex() {
		int vertexCount = this.graph.getVertexCount();
		int edgeCount = this.graph.getEdgeCount();
		Collection<EdgeEntry<Integer, Integer, Integer>> incident = this.graph
				.getIncidentEdges(vertexCount - 1);
		this.graph.removeVertex(vertexCount - 1);
		assertEquals(vertexCount - 1, this.graph.getVertexCount());
		assertEquals(edgeCount - incident.size(), this.graph.getEdgeCount());
	}

	@Test
	public void testRemoveMiddleVertex() {
		int vertexCount = this.graph.getVertexCount();
		int edgeCount = this.graph.getEdgeCount();
		Collection<EdgeEntry<Integer, Integer, Integer>> incident = this.graph
				.getIncidentEdges(vertexCount / 2);
		this.graph.removeVertex(vertexCount / 2);
		assertEquals(vertexCount - 1, this.graph.getVertexCount());
		assertEquals(edgeCount - incident.size(), this.graph.getEdgeCount());
	}

	@Test
	public void testAddEdge() {
		this.graph.addEdge(this.m_EdgeCount + 1, 0, 1);
		this.m_EdgeCount++;
		assertEquals("count + 1", this.m_EdgeCount, this.graph.getEdgeCount());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullEndpoint() {
		this.graph.addEdge(this.m_EdgeCount, 1, null);
	}

	@Test
	public void testRemoveEdge() {
		ArrayList<EdgeEntry<Integer, Integer, Integer>> edgeList = new ArrayList<EdgeEntry<Integer, Integer, Integer>>(
				this.graph.getEdges());
		int edgeCount = this.graph.getEdgeCount();
		this.graph.removeEdge(edgeList.get(edgeList.size() / 2));
		assertEquals("count - 1", edgeCount - 1, this.graph.getEdgeCount());
	}

	@Test
	public void testGetInOutEdgesRandomGraph() {
		for (Integer v : this.graph.getVertices()) {
			Collection<EdgeEntry<Integer, Integer, Integer>> incident = this.graph
					.getIncidentEdges(v);
			Collection<EdgeEntry<Integer, Integer, Integer>> in = this.graph
					.getInEdges(v);
			Collection<EdgeEntry<Integer, Integer, Integer>> out = this.graph
					.getOutEdges(v);
			assertTrue(incident.containsAll(in));
			assertTrue(incident.containsAll(out));
			for (EdgeEntry<Integer, Integer, Integer> e : in) {
				if (out.contains(e)) {
					assertTrue("in edge in out edges but not undirected",
							this.graph.getEdgeType(e) == EdgeType.UNDIRECTED);
				}
			}
			for (EdgeEntry<Integer, Integer, Integer> e : out) {
				if (in.contains(e)) {
					assertTrue("out edge in in edges but not undirected",
							this.graph.getEdgeType(e) == EdgeType.UNDIRECTED);
				}
			}
		}
	}

	@Test
	public void testSubnetwork() {
		NavigableGraph<Integer, Integer, Integer> subgraph = this.graph
				.subNetwork(2, 7);
		assertEquals(5, subgraph.getEdgeCount());
		assertEquals("to 0", 1, subgraph.getInEdges(0).size());
		assertEquals("from 0", 2, subgraph.getOutEdges(0).size());
		assertEquals("from 4", 0, subgraph.getOutEdges(4).size());
	}

	@Test
	public void testTailnetwork() {
		NavigableGraph<Integer, Integer, Integer> subgraph = this.graph
				.tailNetwork(5);
		assertEquals(13, subgraph.getEdgeCount());
		assertEquals("to 0", 0, subgraph.getInEdges(0).size());
		assertEquals("from 0", 2, subgraph.getOutEdges(0).size());
		assertEquals("from 4", 2, subgraph.getOutEdges(4).size());
	}

	@Test
	public void testHeadnetwork() {
		NavigableGraph<Integer, Integer, Integer> subgraph = this.graph
				.headNetwork(5);
		assertEquals(4, subgraph.getEdgeCount());
		assertEquals("to 0", 2, subgraph.getInEdges(0).size());
		assertEquals("from 0", 2, subgraph.getOutEdges(0).size());
		assertEquals("from 4", 0, subgraph.getOutEdges(4).size());
	}

	@Test
	public void testGetKey() {
		assertEquals(1, this.graph.getFirstKey().intValue());
		assertEquals(17, this.graph.getLastKey().intValue());

	}
}
