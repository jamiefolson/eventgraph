package net.sourceforge.eventgraphj.comparable;

import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class SparseGraphTest {

	public static void main(String[] args) {
		SparseGraph<Integer, String> mapGraph = new SparseGraph<Integer, String>();
		mapGraph.addEdge("a", 1, 2, EdgeType.DIRECTED);
		mapGraph.addEdge("b", 1, 3, EdgeType.DIRECTED);
		mapGraph.addEdge("a", 1, 4, EdgeType.DIRECTED);
		mapGraph.addEdge("d", 1, 5, EdgeType.DIRECTED);
		mapGraph.addEdge("e", 1, 6, EdgeType.UNDIRECTED);
		for (String edge : mapGraph.getEdges()) {
			if (!mapGraph.containsEdge(edge)) {
				System.err.println("missing: " + edge);
				continue;
			}
			Pair<Integer> endpoints = mapGraph.getEndpoints(edge);
			System.out.println(edge + " : " + endpoints.getFirst() + " --> " + endpoints.getSecond());
		}
	}
}
