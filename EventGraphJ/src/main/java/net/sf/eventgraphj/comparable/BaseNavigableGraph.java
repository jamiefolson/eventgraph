package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.Collection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class BaseNavigableGraph<K extends Comparable<K>, V, E> implements NavigableGraph<K, V, E>,
        Graph<V, EdgeEntry<K, V, E>>, Serializable {
	protected final K lowerBound, upperBound;
	protected final boolean isBounded;

	public BaseNavigableGraph() {
		this.lowerBound = null;
		this.upperBound = null;
		this.isBounded = false;
	}

	public BaseNavigableGraph(K lowerBound, K upperBound) {

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isBounded = ((lowerBound != null) || (upperBound != null)) ? true : false;
	}

	@Override
	public final boolean isBounded() {
		return this.isBounded;
	}

	@Override
	public final K getUpperBound() {
		return this.upperBound;
	}

	@Override
	public final K getLowerBound() {
		return this.lowerBound;
	}

	@Override
	public final boolean addEdge(EdgeEntry<K, V, E> edge, Collection<? extends V> vertices) {
		return this.addEdge(edge, vertices, this.getDefaultEdgeType());
	}

	@Override
	@SuppressWarnings("unchecked")
	public final boolean addEdge(EdgeEntry<K, V, E> edge, Collection<? extends V> vertices, EdgeType edgeType) {
		if (vertices == null) {
			throw new IllegalArgumentException("'vertices' parameter must not be null");
		}
		if (vertices.size() == 2) {
			return this.addEdge(edge, vertices instanceof Pair ? (Pair<V>) vertices : new Pair<V>(vertices), edgeType);
		} else if (vertices.size() == 1) {
			V vertex = vertices.iterator().next();
			return this.addEdge(edge, new Pair<V>(vertex, vertex), edgeType);
		} else {
			throw new IllegalArgumentException("Graph objects connect 1 or 2 vertices; vertices arg has "
			        + vertices.size());
		}
	}

	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints},
	 * with the default edge type.
	 * 
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	protected final boolean addEdge(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints) {
		return this.addEdge(edge, endpoints, this.getDefaultEdgeType());
	}

	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints} and
	 * {@code EdgeType}.
	 * 
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	protected abstract boolean addEdge(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType);

	@Override
	public final boolean addEdge(EdgeEntry<K, V, E> e, V v1, V v2) {
		return this.addEdge(e, v1, v2, this.getDefaultEdgeType());
	}

	@Override
	public final boolean addEdge(EdgeEntry<K, V, E> e, V v1, V v2, EdgeType edge_type) {
		return this.addEdge(e, new Pair<V>(v1, v2), edge_type);
	}

	@Override
	public final boolean addEdge(K key, V v1, V v2) {
		return this.addEdge(key, v1, v2, this.getDefaultEdgeType());
	}

	@Override
	public final boolean addEdge(K key, V v1, V v2, EdgeType edge_type) {
		return this.addEdge(new EdgeEntry<K, V, E>(v1, v2, key, null, edge_type), v1, v2, edge_type);
	}

	@Override
	public final boolean addEdge(K key, V v1, V v2, E edgevalue) {
		return this.addEdge(key, v1, v2, edgevalue, this.getDefaultEdgeType());
	}

	@Override
	public final boolean addEdge(K key, V v1, V v2, E edgevalue, EdgeType edge_type) {
		return this.addEdge(new EdgeEntry<K, V, E>(v1, v2, key, edgevalue, edge_type), v1, v2, edge_type);
	}

	protected Pair<V> getValidatedEndpoints(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints) {
		if (edge == null) {
			throw new IllegalArgumentException("input edge may not be null");
		}

		if (endpoints == null || endpoints.getFirst() == null || endpoints.getSecond() == null) {
			throw new IllegalArgumentException("endpoints may not be null");
		}

		Pair<V> new_endpoints = new Pair<V>(endpoints.getFirst(), endpoints.getSecond());
		if (this.containsEdge(edge)) {
			Pair<V> existing_endpoints = this.getEndpoints(edge);
			if (!existing_endpoints.equals(new_endpoints)) {
				throw new IllegalArgumentException("edge " + edge + " already exists in this graph with endpoints "
				        + existing_endpoints + " and cannot be added with endpoints " + endpoints);
			} else {
				return null;
			}
		}
		return new_endpoints;
	}

	@Override
	public EdgeType getEdgeType(EdgeEntry<K, V, E> edge) {
		return edge == null ? null : edge.edgetype;
	}

	@Override
	public V getSource(EdgeEntry<K, V, E> directed_edge) {
		return directed_edge == null ? null : directed_edge.from;
	}

	@Override
	public V getDest(EdgeEntry<K, V, E> directed_edge) {
		return directed_edge == null ? null : directed_edge.from;
	}
}
