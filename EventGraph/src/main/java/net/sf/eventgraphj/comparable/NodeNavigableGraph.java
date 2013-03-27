package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.eventgraphj.comparable.DyadNavigableGraph.NullComparator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * default implementation of the <code>NavigableMap</code> interface. It is
 * implemented as a <code>SparseGraph</code>, ie <code>mapGraph</code> where
 * edges in the <code>SparseGraph</code> are themselves
 * <code>NavigableMap<K,EdgeEntry<K,E>></code> objects. Edge queries (and pretty
 * much everything is ultimately an edge query) first retrieves any
 * <code>NavigableMap</code> edges from the <code>mapGraph</code> with a
 * computational cost depending on the SparseGraph implementation and then any
 * <code>EdgeEntry<K,E></code>s in the desired interval are retrieved from the
 * <code>NavigableMap</code>. Using <code>TreeMap</code>s as in
 * <code>SimpleNavigableMap</code> will make this a log N operation, where N is
 * the <code>size()</code> of the <code>TreeMap</code> (the number of edges of a
 * particular type connecting two vertices).
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <K>
 * @param <E>
 */
public class NodeNavigableGraph<K extends Comparable<K>, V, E> extends
		AbstractNavigableCachedGraph<K, V, E> implements
		NavigableGraph<K, V, E>, Graph<V, EdgeEntry<K, V, E>>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1l;
	protected final Comparator<K> comparator = new NullComparator<K>();
	protected final Map<V, MultiNavigableMap<K, EdgeEntry<K, V, E>>> map = new HashMap<V, MultiNavigableMap<K, EdgeEntry<K, V, E>>>();

	public NodeNavigableGraph() {
		this(new SparseMultigraph<V, EdgeEntry<K, V, E>>());
	}

	public NodeNavigableGraph(K lowerBound, K upperBound) {
		this(new SparseMultigraph<V, EdgeEntry<K, V, E>>(), lowerBound,
				upperBound);
	}

	public NodeNavigableGraph(Graph<V, EdgeEntry<K, V, E>> graph, K lowerBound,
			K upperBound) {
		super(graph, lowerBound, upperBound);
	}

	public NodeNavigableGraph(Graph<V, EdgeEntry<K, V, E>> graph) {
		super(graph);
	}

	public NodeNavigableGraph(NodeNavigableGraph<K, V, E> parent,
			Graph<V, EdgeEntry<K, V, E>> graph, K lowerBound, K upperBound) {
		this(graph, lowerBound, upperBound);
		for (Entry<V, MultiNavigableMap<K, EdgeEntry<K, V, E>>> nodeEdges : parent.map
				.entrySet()) {
			V fromNode = nodeEdges.getKey();
			Collection<EdgeEntry<K, V, E>> edges = new ArrayList<EdgeEntry<K, V, E>>();

			if (lowerBound != null) {
				if (upperBound != null) {
					edges = nodeEdges.getValue().subMap(lowerBound, upperBound)
							.values();
				} else {
					edges = nodeEdges.getValue().tailMap(lowerBound).values();
				}
			} else {
				if (upperBound != null) {
					edges = nodeEdges.getValue().headMap(upperBound).values();
				}
			}

			for (EdgeEntry<K, V, E> edge : edges) {
				this.cachedGraph
						.addEdge(edge, fromNode, edge.to, edge.edgetype);
			}
		}

	}

	public NodeNavigableGraph(NodeNavigableGraph<K, V, E> parent, K lowerBound,
			K upperBound) {
		this(parent, new SparseMultigraph<V, EdgeEntry<K, V, E>>(), lowerBound,
				upperBound);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.eventgraphj.comparable.NavigableGraph#getFirstEdge()
	 */
	@Override
	public EdgeEntry<K, V, E> getFirstEdge() {
		K firstKey = null;
		EdgeEntry<K, V, E> firstEdge = null;
		for (MultiNavigableMap<K, EdgeEntry<K, V, E>> edges : this.map.values()) {
			if ((firstKey == null)
					|| (edges.firstKey().compareTo(firstKey) < 0)) {
				firstEdge = edges.firstEntry().getValue().iterator().next();
				firstKey = edges.firstKey();
			}

		}

		return firstEdge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.eventgraphj.comparable.NavigableGraph#getLastEdge()
	 */
	@Override
	public EdgeEntry<K, V, E> getLastEdge() {
		K lastKey = null;
		EdgeEntry<K, V, E> lastEdge = null;
		for (MultiNavigableMap<K, EdgeEntry<K, V, E>> edges : this.map.values()) {
			if ((lastKey == null) || (edges.lastKey().compareTo(lastKey) > 0)) {
				lastEdge = edges.lastEntry().getValue().iterator().next();
				lastKey = edges.lastKey();
			}

		}

		return lastEdge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.eventgraphj.comparable.NavigableGraph#getFirstKey()
	 */
	@Override
	public K getFirstKey() {
		K firstKey = null;
		for (MultiNavigableMap<K, EdgeEntry<K, V, E>> edges : this.map.values()) {
			if ((firstKey == null)
					|| (!edges.isEmpty() && edges.firstKey()
							.compareTo(firstKey) < 0)) {
				firstKey = edges.firstKey();
			}

		}

		return firstKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.eventgraphj.comparable.NavigableGraph#getLastKey()
	 */
	@Override
	public K getLastKey() {
		K lastKey = null;
		for (MultiNavigableMap<K, EdgeEntry<K, V, E>> edges : this.map.values()) {
			if ((lastKey == null)
					|| (!edges.isEmpty() && edges.lastKey().compareTo(lastKey) > 0)) {
				lastKey = edges.lastKey();
			}

		}

		return lastKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.eventgraphj.comparable.NavigableGraph#subNetwork(java.lang.Comparable
	 * , java.lang.Comparable)
	 */
	@Override
	public NavigableGraph<K, V, E> subNetwork(K start, K stop) {
		return new NodeNavigableGraph<K, V, E>(this, start, stop);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.eventgraphj.comparable.NavigableGraph#tailNetwork(java.lang.Comparable
	 * )
	 */
	@Override
	public NavigableGraph<K, V, E> tailNetwork(K start) {
		return new NodeNavigableGraph<K, V, E>(this, start, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.eventgraphj.comparable.NavigableGraph#headNetwork(java.lang.Comparable
	 * )
	 */
	@Override
	public NavigableGraph<K, V, E> headNetwork(K stop) {
		return new NodeNavigableGraph<K, V, E>(this, null, stop);
	}

	@Override
	protected boolean addVertexData(V vertex) {
		if (this.map
				.put(vertex, new MultiNavigableMap<K, EdgeEntry<K, V, E>>()) != null) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean addEdgeData(EdgeEntry<K, V, E> edge,
			Pair<? extends V> endpoints, EdgeType edgeType) {

		EdgeEntry<K, V, E> entry = new EdgeEntry<K, V, E>(endpoints.getFirst(),
				endpoints.getSecond(), edge.getKey(), edge.getValue(), edgeType);

		// this should never fail for a multi-map
		if (this.map.get(endpoints.getFirst()).put(edge.getKey(), entry) == null) {
			// this means the edge was a duplicate and no new edge was added
			throw new IllegalArgumentException(
					"Duplicate edge was added, but went undetected.  This shouldn't happen");
		}

		return true;
	}

	@Override
	protected boolean removeVertexData(V vertex) {
		if (this.map.remove(vertex) != null) {
			return true;
		}
		return false;

	}

	@Override
	protected boolean removeEdgeData(V first, V second, EdgeEntry<K, V, E> edge) {
		if (this.map.get(first).remove(
				edge.getKey(),
				new EdgeEntry<K, V, E>(first, second, edge.getKey(), edge
						.getValue(), this.getEdgeType(edge))) != null) {
			return true;
		}
		return false;
	}

}
