package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public abstract class AbstractNavigableCachedGraph<K extends Comparable<K>, V, E> extends BaseNavigableGraph<K, V, E>
        implements MultiGraph<V, EdgeEntry<K, V, E>>, Graph<V, EdgeEntry<K, V, E>>, NavigableGraph<K, V, E>,
        Serializable {
	

	protected Graph<V, EdgeEntry<K, V, E>> cachedGraph;

	public AbstractNavigableCachedGraph(Graph<V, EdgeEntry<K, V, E>> cachedGraph) {
		super();
		this.cachedGraph = cachedGraph;
	}

	public AbstractNavigableCachedGraph(Graph<V, EdgeEntry<K, V, E>> cachedGraph, K lowerBound, K upperBound) {
		super(lowerBound, upperBound);
		this.cachedGraph = cachedGraph;
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges()
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> getEdges() {
		return this.cachedGraph.getEdges();
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getInEdges(java.lang.Object)
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> getInEdges(V vertex) {
		Collection<EdgeEntry<K, V, E>> result = this.cachedGraph.getInEdges(vertex);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertices()
	 */
	@Override
	public Collection<V> getVertices() {
		Collection<V> result = this.cachedGraph.getVertices();
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getOutEdges(java.lang.Object)
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> getOutEdges(V vertex) {
		Collection<EdgeEntry<K, V, E>> result = this.cachedGraph.getOutEdges(vertex);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsVertex(java.lang.Object)
	 */
	@Override
	public boolean containsVertex(V vertex) {
		return this.cachedGraph.containsVertex(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getPredecessors(java.lang.Object)
	 */
	@Override
	public Collection<V> getPredecessors(V vertex) {
		Collection<V> result = this.cachedGraph.getPredecessors(vertex);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#containsEdge(java.lang.Object)
	 */
	@Override
	public boolean containsEdge(EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.containsEdge(edge);
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount()
	 */
	@Override
	public int getEdgeCount() {
		return this.cachedGraph.getEdgeCount();
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSuccessors(java.lang.Object)
	 */
	@Override
	public Collection<V> getSuccessors(V vertex) {
		Collection<V> result = this.cachedGraph.getSuccessors(vertex);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getVertexCount()
	 */
	@Override
	public int getVertexCount() {
		return this.cachedGraph.getVertexCount();
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighbors(java.lang.Object)
	 */
	@Override
	public Collection<V> getNeighbors(V vertex) {
		Collection<V> result = this.cachedGraph.getNeighbors(vertex);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#inDegree(java.lang.Object)
	 */
	@Override
	public int inDegree(V vertex) {
		return this.cachedGraph.inDegree(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentEdges(java.lang.Object)
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> getIncidentEdges(V vertex) {
		Collection<EdgeEntry<K, V, E>> result = this.cachedGraph.getIncidentEdges(vertex);
		if (result == null) {
			result = new ArrayList<EdgeEntry<K, V, E>>();
		}

		return result;
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#outDegree(java.lang.Object)
	 */
	@Override
	public int outDegree(V vertex) {
		return this.cachedGraph.outDegree(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentVertices(java.lang.Object)
	 */
	@Override
	public Collection<V> getIncidentVertices(EdgeEntry<K, V, E> edge) {
		Collection<V> result = this.cachedGraph.getIncidentVertices(edge);
		if (result == null) {
			result = Collections.emptyList();
		}
		return result;
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isPredecessor(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isPredecessor(V v1, V v2) {
		return this.cachedGraph.isPredecessor(v1, v2);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isSuccessor(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isSuccessor(V v1, V v2) {
		return this.cachedGraph.isSuccessor(v1, v2);
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#findEdge(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public EdgeEntry<K, V, E> findEdge(V v1, V v2) {
		return this.cachedGraph.findEdge(v1, v2);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getPredecessorCount(java.lang.Object)
	 */
	@Override
	public int getPredecessorCount(V vertex) {
		return this.cachedGraph.getPredecessorCount(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSuccessorCount(java.lang.Object)
	 */
	@Override
	public int getSuccessorCount(V vertex) {
		return this.cachedGraph.getSuccessorCount(vertex);
	}

	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getSource(java.lang.Object)
	 */
	/*@Override
	public V getSource(EdgeEntry<K, V, E> directed_edge) {
		return this.cachedGraph.getSource(directed_edge);
	}*/

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#findEdgeSet(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> findEdgeSet(V v1, V v2) {
		return this.cachedGraph.findEdgeSet(v1, v2);
	}

	/**
	 * @param directed_edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getDest(java.lang.Object)
	 */
	/*
	@Override
	public V getDest(EdgeEntry<K, V, E> directed_edge) {
	return this.cachedGraph.getDest(directed_edge);
	}*/

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isSource(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isSource(V vertex, EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.isSource(vertex, edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#addVertex(java.lang.Object)
	 */
	@Override
	public boolean addVertex(V vertex) {
		if (this.cachedGraph.addVertex(vertex)) {
			if (this.addVertexData(vertex)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean addVertexData(V vertex);

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#isDest(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isDest(V vertex, EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.isDest(vertex, edge);
	}

	@Override
	protected boolean addEdge(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {

		Pair<V> new_endpoints = this.getValidatedEndpoints(edge, endpoints);
		if (new_endpoints == null) {
			return false;
		}

		V v1 = new_endpoints.getFirst();
		V v2 = new_endpoints.getSecond();

		if (!this.containsVertex(v1)) {
			this.addVertex(v1);
		}

		if (!this.containsVertex(v2)) {
			this.addVertex(v2);
		}

		// don't add duplicates
		if (this.containsEdge(edge)) {
			return false;
		}

		// this could potentially fail, but shouldn't in current implementations
		if (this.addEdgeData(edge, endpoints, edgeType)) {
			// add edge to cached graph second
			if (this.cachedGraph.addEdge(edge, endpoints.getFirst(), endpoints.getSecond(), edgeType)) {
				return true;
			}
		}//
		return false;
	}

	protected abstract boolean addEdgeData(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType);

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeVertex(java.lang.Object)
	 */
	@Override
	public boolean removeVertex(V vertex) {
		if (this.cachedGraph.removeVertex(vertex)) {
			if (this.removeVertexData(vertex)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean removeVertexData(V vertex);

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getEndpoints(java.lang.Object)
	 */
	@Override
	public Pair<V> getEndpoints(EdgeEntry<K, V, E> edge) {
		return new Pair<V>(edge.from, edge.to);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Graph#getOpposite(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public V getOpposite(V vertex, EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.getOpposite(vertex, edge);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#removeEdge(java.lang.Object)
	 */
	@Override
	public boolean removeEdge(EdgeEntry<K, V, E> edge) {

		if (!this.containsEdge(edge)) {
			return false;
		}

		Pair<V> endpoints = this.getEndpoints(edge);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		if (this.removeEdgeData(v1, v2, edge)) {
			if (this.cachedGraph.removeEdge(edge)) {
				return true;
			}
		}
		return false;
	}

	protected abstract boolean removeEdgeData(V first, V second, EdgeEntry<K, V, E> edge);

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#isNeighbor(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isNeighbor(V v1, V v2) {
		return this.cachedGraph.isNeighbor(v1, v2);
	}

	/**
	 * @param vertex
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#isIncident(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isIncident(V vertex, EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.isIncident(vertex, edge);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#degree(java.lang.Object)
	 */
	@Override
	public int degree(V vertex) {
		return this.cachedGraph.degree(vertex);
	}

	/**
	 * @param vertex
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getNeighborCount(java.lang.Object)
	 */
	@Override
	public int getNeighborCount(V vertex) {
		return this.cachedGraph.getNeighborCount(vertex);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getIncidentCount(java.lang.Object)
	 */
	@Override
	public int getIncidentCount(EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.getIncidentCount(edge);
	}

	/**
	 * @param edge
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeType(java.lang.Object)
	 */
	@Override
	public EdgeType getEdgeType(EdgeEntry<K, V, E> edge) {
		return this.cachedGraph.getEdgeType(edge);
	}

	/**
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getDefaultEdgeType()
	 */
	@Override
	public EdgeType getDefaultEdgeType() {
		return this.cachedGraph.getDefaultEdgeType();
	}

	/**
	 * @param edge_type
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdges(edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	public Collection<EdgeEntry<K, V, E>> getEdges(EdgeType edge_type) {
		return this.cachedGraph.getEdges(edge_type);
	}

	/**
	 * @param edge_type
	 * @return
	 * @see edu.uci.ics.jung.graph.Hypergraph#getEdgeCount(edu.uci.ics.jung.graph.util.EdgeType)
	 */
	@Override
	public int getEdgeCount(EdgeType edge_type) {
		return this.cachedGraph.getEdgeCount(edge_type);
	}
	
	public Collection<Pair<V>> getPairs(){
		Collection<Pair<V>> allPairs = new HashSet<Pair<V>>();
		for (V from : cachedGraph.getVertices()){
			for (V to : cachedGraph.getNeighbors(from)){
				allPairs.add(new Pair<V>(from,to));
			}
		}
		
		return allPairs;
	}
}
