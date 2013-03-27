package net.sf.eventgraphj.comparable;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class AbstractNavigableSubGraph<K extends Comparable<K>, V, E> extends BaseNavigableGraph<K, V, E> {
	protected final NavigableGraph<K, V, E> parent;
	final K start, stop;

	public AbstractNavigableSubGraph(NavigableGraph<K, V, E> parent, K start, K stop) {
		super(start, stop);
		this.parent = parent;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public V getDest(EdgeEntry<K, V, E> directedEdge) {
		return this.parent.getDest(directedEdge);
	}

	@Override
	public Pair<V> getEndpoints(EdgeEntry<K, V, E> edge) {
		return this.parent.getEndpoints(edge);
	}

	@Override
	public V getSource(EdgeEntry<K, V, E> directedEdge) {
		return this.parent.getSource(directedEdge);
	}

	@Override
	public boolean isDest(V vertex, EdgeEntry<K, V, E> edge) {
		return this.parent.isDest(vertex, edge);
	}

	@Override
	public boolean isPredecessor(V v1, V v2) {
		return this.parent.isPredecessor(v1, v2);
	}

	@Override
	public boolean isSource(V vertex, EdgeEntry<K, V, E> edge) {
		return this.parent.isSource(vertex, edge);
	}

	@Override
	public boolean isSuccessor(V v1, V v2) {
		return this.parent.isSuccessor(v1, v2);
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		return this.parent.getDefaultEdgeType();
	}

	@Override
	public EdgeType getEdgeType(EdgeEntry<K, V, E> edge) {
		return this.parent.getEdgeType(edge);
	}

	@Override
	public K getFirstKey() {
		return this.start;
	}

	@Override
	public K getLastKey() {
		return this.stop;
	}

}
