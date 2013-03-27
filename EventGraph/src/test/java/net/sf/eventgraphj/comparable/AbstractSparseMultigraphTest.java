package net.sf.eventgraphj.comparable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Factory;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class AbstractSparseMultigraphTest {

	protected Integer v0 = 0;
	protected Integer v1 = 1;
	protected Integer v2 = 2;
	protected Number e01 = .1;
	protected Number e10 = .2;
	protected Number e12 = .3;
	protected Number e21 = .4;

	protected Factory<Number> vertexFactory = new Factory<Number>() {
		int v = 0;

		@Override
		public Number create() {
			return this.v++;
		}
	};
	protected Factory<Number> edgeFactory = new Factory<Number>() {
		int e = 0;

		@Override
		public Number create() {
			return this.e++;
		}
	};

	protected Graph<Number, Number> graph;
	protected int vertexCount = 50;
	protected Graph<Integer, Number> smallGraph;

	@Test
	public void testGetEdges() {
		assertEquals(this.smallGraph.getEdgeCount(), 4);
	}

	@Test
	public void testGetVertices() {
		assertEquals(this.smallGraph.getVertexCount(), 3);
	}

	@Test
	public void testAddVertex() {
		int count = this.graph.getVertexCount();
		this.graph.addVertex(count);
		assertEquals(this.graph.getVertexCount(), count + 1);
	}

	@Test
	public void testRemoveEndVertex() {
		int vertexCount = this.graph.getVertexCount();
		int edgeCount = this.graph.getEdgeCount();
		Collection<Number> incident = this.graph.getIncidentEdges(vertexCount - 1);
		this.graph.removeVertex(vertexCount - 1);
		assertEquals(vertexCount - 1, this.graph.getVertexCount());
		assertEquals(edgeCount - incident.size(), this.graph.getEdgeCount());
	}

	@Test
	public void testRemoveMiddleVertex() {
		int vertexCount = this.graph.getVertexCount();
		int edgeCount = this.graph.getEdgeCount();
		Collection<Number> incident = this.graph.getIncidentEdges(vertexCount / 2);
		this.graph.removeVertex(vertexCount / 2);
		assertEquals(vertexCount - 1, this.graph.getVertexCount());
		assertEquals(edgeCount - incident.size(), this.graph.getEdgeCount());
	}

	@Test
	public void testAddEdge() {
		int edgeCount = this.graph.getEdgeCount();
		this.graph.addEdge(this.edgeFactory.create(), 0, 9);
		assertEquals(this.graph.getEdgeCount(), edgeCount + 1);
	}

	@Test
	public void testNullEndpoint() {
		try {
			this.graph.addEdge(this.edgeFactory.create(), new Pair<Number>(1, null));
			fail("should not be able to add an edge with a null endpoint");
		} catch (IllegalArgumentException e) {
			// all is well
		}
	}

	@Test
	public void testRemoveEdge() {
		List<Number> edgeList = new ArrayList<Number>(this.graph.getEdges());
		int edgeCount = this.graph.getEdgeCount();
		this.graph.removeEdge(edgeList.get(edgeList.size() / 2));
		assertEquals(this.graph.getEdgeCount(), edgeCount - 1);
	}

	@Test
	public void testGetInOutEdges() {
		for (Number v : this.graph.getVertices()) {
			Collection<Number> incident = this.graph.getIncidentEdges(v);
			Collection<Number> in = this.graph.getInEdges(v);
			Collection<Number> out = this.graph.getOutEdges(v);
			assertTrue(incident.containsAll(in));
			assertTrue(incident.containsAll(out));
			for (Number e : in) {
				if (out.contains(e)) {
					assertTrue(this.graph.getEdgeType(e) == EdgeType.UNDIRECTED);
				}
			}
			for (Number e : out) {
				if (in.contains(e)) {
					assertTrue(this.graph.getEdgeType(e) == EdgeType.UNDIRECTED);
				}
			}
		}

		assertEquals(this.smallGraph.getInEdges(this.v1).size(), 4);
		assertEquals(this.smallGraph.getOutEdges(this.v1).size(), 3);
		assertEquals(this.smallGraph.getOutEdges(this.v0).size(), 2);
	}

	@Test
	public void testGetPredecessors() {
		assertTrue(this.smallGraph.getPredecessors(this.v0).containsAll(Collections.singleton(this.v1)));
	}

	@Test
	public void testGetSuccessors() {
		assertTrue(this.smallGraph.getPredecessors(this.v1).contains(this.v0));
		assertTrue(this.smallGraph.getPredecessors(this.v1).contains(this.v2));
	}

	@Test
	public void testGetNeighbors() {
		Collection<Integer> neighbors = this.smallGraph.getNeighbors(this.v1);
		assertTrue(neighbors.contains(this.v0));
		assertTrue(neighbors.contains(this.v2));
	}

	@Test
	public void testGetGraphNeighbors() {
		Collection<Number> neighbors = this.graph.getNeighbors(0);
		assertTrue(neighbors.contains(1));
		assertTrue(neighbors.contains(2));
		assertTrue(neighbors.contains(5));
		assertEquals(4, neighbors.size());
	}

	@Test
	public void testGetIncidentEdges() {
		assertEquals(this.smallGraph.getIncidentEdges(this.v0).size(), 2);
	}

	@Test
	public void testFindEdge() {
		Number edge = this.smallGraph.findEdge(this.v1, this.v2);
		assertTrue(edge == this.e12 || edge == this.e21);
	}

	@Test
	public void testGetEndpoints() {
		Pair<Integer> endpoints = this.smallGraph.getEndpoints(this.e01);
		assertTrue((endpoints.getFirst() == this.v0 && endpoints.getSecond() == this.v1)
		        || endpoints.getFirst() == this.v1 && endpoints.getSecond() == this.v0);
	}

	@Test
	public void testIsDirected() {
		for (Number edge : this.smallGraph.getEdges()) {
			if (edge == this.e21) {
				assertEquals(this.smallGraph.getEdgeType(edge), EdgeType.DIRECTED);
			} else {
				assertEquals(this.smallGraph.getEdgeType(edge), EdgeType.UNDIRECTED);
			}
		}
	}
}
