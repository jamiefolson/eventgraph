package net.sourceforge.eventgraphj.comparable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.NavigableMap;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class SimpleNavigableGraph<K extends Comparable<K>, V, E> extends AbstractNavigableGraph<K, V, E> implements
        Graph<V, EdgeEntry<K, E>>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap<EdgeEntry<K, E>, Pair<V>> directed_edges; // Map of
	// directed
	// edges to
	// incident
	// vertex
	// sets
	protected HashMap<EdgeEntry<K, E>, Pair<V>> undirected_edges; // Map of

	// undirected
	// edges
	// to
	// incident
	// vertex
	// sets

	public SimpleNavigableGraph() {
		super();
		this.mapGraph = new SparseGraph<V, EdgeNavigableMap>();
		directed_edges = new HashMap<EdgeEntry<K, E>, Pair<V>>();
		undirected_edges = new HashMap<EdgeEntry<K, E>, Pair<V>>();
	}

	@Override
	public EdgeType getEdgeType(EdgeEntry<K, E> edge) {
		if (directed_edges.containsKey(edge))
			return EdgeType.DIRECTED;
		else if (undirected_edges.containsKey(edge))
			return EdgeType.UNDIRECTED;
		else
			return null;
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		// TODO Auto-generated method stub
		return EdgeType.DIRECTED;
	}

	public V getSource(EdgeEntry<K, E> directed_edge) {
		if (getEdgeType(directed_edge) == EdgeType.DIRECTED)
			return directed_edges.get(directed_edge).getFirst();
		else
			return null;
	}

	public V getDest(EdgeEntry<K, E> directed_edge) {
		if (getEdgeType(directed_edge) == EdgeType.DIRECTED)
			return directed_edges.get(directed_edge).getSecond();
		else
			return null;
	}

	public boolean isSource(V vertex, EdgeEntry<K, E> edge) {
		if (!containsVertex(vertex))
			return false;

		V source = getSource(edge);
		if (source != null)
			return source.equals(vertex);
		else
			return false;
	}

	public boolean isDest(V vertex, EdgeEntry<K, E> edge) {
		if (!containsVertex(vertex))
			return false;

		V dest = getDest(edge);
		if (dest != null)
			return dest.equals(vertex);
		else
			return false;
	}

	@Override
	public boolean isPredecessor(V v1, V v2) {
		for (EdgeEntry<K, E> edge : this.findEdgeSet(v1, v2)) {
			if (this.getEdgeType(edge) == EdgeType.UNDIRECTED)
				return true;
			if (this.getSource(edge).equals(v2))
				return true;
		}
		return false;
	}

	@Override
	public boolean isSuccessor(V v1, V v2) {
		for (EdgeEntry<K, E> edge : this.findEdgeSet(v1, v2)) {
			if (this.getEdgeType(edge) == EdgeType.UNDIRECTED)
				return true;
			if (this.getSource(edge).equals(v1))
				return true;
		}
		return false;
	}

	public Pair<V> getEndpoints(EdgeEntry<K, E> edge) {
		Pair<V> endpoints;
		endpoints = directed_edges.get(edge);
		if (endpoints == null)
			return undirected_edges.get(edge);
		else
			return endpoints;
	}

	@Override
	protected boolean addEdgeMetadata(EdgeEntry<K, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
		if (edgeType == EdgeType.DIRECTED) {
			if (directed_edges.put(edge, (Pair<V>) endpoints) != null)
				System.err.println("overwriting an edge's type");
		} else {
			if (undirected_edges.put(edge, (Pair<V>) endpoints) != null)
				System.err.println("overwriting an edge's type");
		}
		return true;
	}

	public static class NullComparator<K extends Comparable<K>> implements Comparator<K>, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(K arg0, K arg1) {
			if (arg0 == null) {
				if (arg1 == null)
					return 0;
				return -1;
			}
			if (arg1 == null)
				return 1;
			return arg0.compareTo(arg1);
		}
	}

	@Override
	protected NavigableMap<K, EdgeEntry<K, E>> addNewEdgeMap(V v1, V v2, EdgeType edgeType) {
		NavigableMap<K, EdgeEntry<K, E>> map = new MyTreeMap<K, EdgeEntry<K, E>>(comparator);
		EdgeNavigableMap edge = new EdgeNavigableMap(v1, v2, map);
		this.mapGraph.addEdge(edge, v1, v2, edgeType);
		return map;
	}

	public static class SimpleComparableEdgeGraphFactory<K extends Comparable<K>, V, E> implements
	        Factory<Graph<V, EdgeEntry<K, E>>> {

		@Override
		public Graph<V, EdgeEntry<K, E>> create() {
			return new SimpleNavigableGraph<K, V, E>();
		}
	}

	/*public static <V, K extends Comparable<K>,E extends ComparableEdge<K>> Factory<Graph<V,E>> getFactory(){
		return new SimpleComparableEdgeGraphFactory<V,K,E>();
	}*/
	public static <V, K extends Comparable<K>, E> Factory<NavigableGraph<K, V, E>> getFactory() {
		return new Factory<NavigableGraph<K, V, E>>() {

			public NavigableGraph<K, V, E> create() {
				return new SimpleNavigableGraph<K, V, E>();
			}
		};
	}

	public NavigableGraph<K, V, E> subNetwork(K start, K stop) {
		return new EdgeSubTreeGraph<K, V, E>(this, start, stop);
	}

	public NavigableGraph<K, V, E> tailNetwork(K start) {
		return new EdgeSubTreeGraph<K, V, E>(this, start, null);
	}

	public NavigableGraph<K, V, E> headNetwork(K stop) {
		return new EdgeSubTreeGraph<K, V, E>(this, null, stop);
	}

	public static class EdgeSubTreeGraph<K extends Comparable<K>, V, E> extends AbstractNavigableGraph<K, V, E> {
		protected final AbstractNavigableGraph<K, V, E> parent;
		final K start, stop;

		public EdgeSubTreeGraph(AbstractNavigableGraph<K, V, E> parent, K start, K stop) {
			super(start, stop);
			this.parent = parent;
			this.start = start;
			this.stop = stop;
			this.mapGraph = new SparseGraph<V, EdgeNavigableMap>();

			for (EdgeNavigableMap edge : parent.mapGraph.getEdges()) {
				if (!parent.mapGraph.containsEdge(edge)) {
					System.err.println("skipping " + edge.toString() + "\n\t" + edge.hashCode());
					continue;
				}
				Pair<V> endpoints = parent.mapGraph.getEndpoints(edge);
				EdgeType edgetype = parent.mapGraph.getEdgeType(edge);
				NavigableMap<K, EdgeEntry<K, E>> map = edge.map;
				if (start != null) {
					if (stop != null) {
						map = edge.map.subMap(start, true, stop, false);
					} else {
						map = edge.map.tailMap(start, true);
					}
				} else {
					if (stop != null) {
						map = edge.map.headMap(stop, false);
					}
				}
				assert (edge != null);
				assert (endpoints != null);
				this.mapGraph.addEdge(new EdgeNavigableMap(edge.from, edge.to, map), endpoints, edgetype);
			}
		}

		@Override
		protected boolean addEdgeMetadata(EdgeEntry<K, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
			return parent.addEdgeMetadata(edge, endpoints, edgeType);
		}

		@Override
		protected NavigableMap<K, EdgeEntry<K, E>> addNewEdgeMap(V v1, V v2, EdgeType edgeType) {
			return parent.addNewEdgeMap(v1, v2, edgeType);
		}

		@Override
		public V getDest(EdgeEntry<K, E> directedEdge) {
			return parent.getDest(directedEdge);
		}

		@Override
		public Pair<V> getEndpoints(EdgeEntry<K, E> edge) {
			return parent.getEndpoints(edge);
		}

		@Override
		public V getSource(EdgeEntry<K, E> directedEdge) {
			return parent.getSource(directedEdge);
		}

		@Override
		public boolean isDest(V vertex, EdgeEntry<K, E> edge) {
			return parent.isDest(vertex, edge);
		}

		@Override
		public boolean isPredecessor(V v1, V v2) {
			return parent.isPredecessor(v1, v2);
		}

		@Override
		public boolean isSource(V vertex, EdgeEntry<K, E> edge) {
			return parent.isSource(vertex, edge);
		}

		@Override
		public boolean isSuccessor(V v1, V v2) {
			return parent.isSuccessor(v1, v2);
		}

		@Override
		public EdgeType getDefaultEdgeType() {
			return parent.getDefaultEdgeType();
		}

		@Override
		public EdgeType getEdgeType(EdgeEntry<K, E> edge) {
			return parent.getEdgeType(edge);
		}

		public NavigableGraph<K, V, E> subNetwork(K start, K stop) {
			return new EdgeSubTreeGraph<K, V, E>(this, start, stop);
		}

		public NavigableGraph<K, V, E> tailNetwork(K start) {
			return new EdgeSubTreeGraph<K, V, E>(this, start, null);
		}

		public NavigableGraph<K, V, E> headNetwork(K stop) {
			return new EdgeSubTreeGraph<K, V, E>(this, null, stop);
		}

		public K getFirstKey() {
			return start;
		}

		public K getLastKey() {
			return stop;
		}

	}
}
