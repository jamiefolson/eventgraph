package net.sf.eventgraphj.comparable;

import java.io.Serializable;

import edu.uci.ics.jung.graph.util.EdgeType;

public class EdgeEntry<K extends Comparable<K>, V, E> extends EdgePair<K, E> implements Serializable {
	private static final long serialVersionUID = 1L;
	protected final V from, to;

	public V getFrom() {
		return from;
	}

	public V getTo() {
		return to;
	}

	public EdgeType getEdgetype() {
		return edgetype;
	}

	protected final EdgeType edgetype;

	public EdgeEntry(V from, V to, K key, E edge, EdgeType edgeType) {
		super(key, edge);
		this.from = from;
		this.to = to;
		this.edgetype = edgeType;
	}

	@Override
	public int hashCode() {
		int result =super.hashCode(); 
		 result = 37*result + this.from.hashCode();
		 result = 37*result + this.to.hashCode();
		 result = 37*result + this.edgetype.hashCode();
		 return result;
	}

	@Override
	public boolean equals(Object other) {
		if (super.equals(other)) {
			try {
				EdgeEntry<K, V, E> edge = (EdgeEntry<K, V, E>) other;
				if (this.edgetype == edge.edgetype && this.from.equals(edge.from) && this.to.equals(edge.to)) {
					return true;
				}
			} catch (ClassCastException e) {

			}
		}
		return false;
	}

}