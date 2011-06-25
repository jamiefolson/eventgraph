package net.sf.eventgraphj.comparable;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class SparseMultigraphTest extends AbstractSparseMultigraphTest {

	@Before
	public void setUp() throws Exception {
		Set<Number> seeds = new HashSet<Number>();
		seeds.add(1);
		seeds.add(5);
		this.graph = new SparseMultigraph<Number, Number>();
		// this.graph = new SparseGraph<Number, Number>();
		this.graph.addEdge(1, 0, 1);
		this.graph.addEdge(2, 1, 2);
		this.graph.addEdge(3, 0, 2);
		this.graph.addEdge(4, 2, 1);
		this.graph.addEdge(5, 3, 1);
		this.graph.addEdge(6, 0, 4, EdgeType.DIRECTED);
		this.graph.addEdge(7, 0, 5, EdgeType.DIRECTED);
		this.graph.addEdge(8, 5, 1, EdgeType.DIRECTED);
		this.graph.addEdge(9, 6, 1, EdgeType.DIRECTED);
		this.graph.addEdge(10, 4, 3, EdgeType.DIRECTED);
		this.graph.addEdge(11, 2, 7);
		this.graph.addEdge(12, 1, 5);
		this.graph.addEdge(13, 2, 6);
		this.graph.addEdge(14, 6, 4);
		this.graph.addEdge(15, 7, 8);
		this.graph.addEdge(16, 8, 3);
		this.graph.addEdge(17, 5, 7);

		this.smallGraph = new SparseMultigraph<Integer, Number>();
		// this.smallGraph = new SparseGraph<Integer, Number>();
		this.smallGraph.addVertex(this.v0);
		this.smallGraph.addVertex(this.v1);
		this.smallGraph.addVertex(this.v2);
		this.smallGraph.addEdge(this.e01, this.v0, this.v1);
		this.smallGraph.addEdge(this.e10, this.v1, this.v0);
		this.smallGraph.addEdge(this.e12, this.v1, this.v2);
		this.smallGraph.addEdge(this.e21, this.v2, this.v1, EdgeType.DIRECTED);

	}

}
