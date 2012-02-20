package net.sf.eventgraphj.comparable;

import java.util.Collection;
import java.util.Map.Entry;

import net.sourceforge.jannotater.RJava;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * An extension of the Graph API to accommodate edges that are ordered by some
 * key with the specific goal of allowing operations and computations on
 * subsets/intervals of those keys. To enable this, the standard
 * getEdges/Neighbors/Successors/Predecessors have been extended to accept an
 * interval of keys. In addition subNetwork, headNetwork, and tailNetwork
 * methods have been added to allow for retrieving a complete subset of the
 * network.
 * 
 * Duplicate edge data <E> is allowed, as well as duplicate keys <K>, provided
 * that no single pair has multiple edges of the same type with the same key
 * <K>.
 * 
 * @author jfolson
 * 
 * @param <V>
 *            the vertex type
 * @param <K>
 *            the key type
 * @param <E>
 *            the edge type
 */
public interface NavigableGraph<K extends Comparable<K>, V, E> extends MultiGraph<V, EdgeEntry<K, V, E>>,
        Graph<V, EdgeEntry<K, V, E>>, Hypergraph<V, EdgeEntry<K, V, E>> {

	/**
	 * Returns a view of all edges in this graph between <code>start</code> and
	 * <code>stop</code>. In general, this obeys the <code>Collection</code>
	 * contract, and therefore makes no guarantees about the ordering of the
	 * vertices within the set. However, parallel edges are sorted by their key.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of all edges in this graph
	 */
	// Collection<EdgeEntry<K, E>> getEdges(K start, K stop);

	/**
	 * Returns a view of all vertices in this graph between <code>start</code>
	 * and <code>stop</code>. In general, this obeys the <code>Collection</code>
	 * contract, and therefore makes no guarantees about the ordering of the
	 * vertices within the set.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of all vertices in this graph
	 */
	// Collection<V> getVertices(K start, K stop);

	/**
	 * Returns true if this graph's vertex collection contains
	 * <code>vertex</code> between <code>start</code> and <code>stop</code>.
	 * Equivalent to <code>getVertices().contains(vertex)</code>.
	 * 
	 * @param vertex
	 *            the vertex whose presence is being queried
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return true iff this graph contains a vertex <code>vertex</code>
	 */
	// boolean containsVertex(V vertex, K start, K stop);

	/**
	 * Returns true if this graph's edge collection contains <code>edge</code>
	 * between <code>start</code> and <code>stop</code>. Equivalent to
	 * <code>getEdges().contains(edge)</code>.
	 * 
	 * @param edge
	 *            the edge whose presence is being queried
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return true iff this graph contains an edge <code>edge</code>
	 */
	// boolean containsEdge(EdgeEntry<K, E> edge, K start, K stop);

	/**
	 * Returns the number of edges in this graph between <code>start</code> and
	 * <code>stop</code>.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of edges in this graph
	 */
	// int getEdgeCount(K start, K stop);

	/**
	 * Returns the number of vertices in this graph between <code>start</code>
	 * and <code>stop</code>.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of vertices in this graph
	 */
	// int getVertexCount(K start, K stop);

	/**
	 * Returns the collection of vertices which are connected to
	 * <code>vertex</code> via any edges in this graph between
	 * <code>start</code> and <code>stop</code>. If <code>vertex</code> is
	 * connected to itself with a self-loop, then it will be included in the
	 * collection returned.
	 * 
	 * @param vertex
	 *            the vertex whose neighbors are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the collection of vertices which are connected to
	 *         <code>vertex</code>, or <code>null</code> if <code>vertex</code>
	 *         is not present
	 */
	// Collection<V> getNeighbors(V vertex, K start, K stop);

	/**
	 * Returns the collection of edges in this graph which are connected to
	 * <code>vertex</code> between <code>start</code> and <code>stop</code>.
	 * 
	 * @param vertex
	 *            the vertex whose incident edges are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the collection of edges which are connected to
	 *         <code>vertex</code>, or <code>null</code> if <code>vertex</code>
	 *         is not present
	 */
	// Collection<EdgeEntry<K, E>> getIncidentEdges(V vertex, K start, K stop);

	/**
	 * Returns the collection of vertices in this graph which are connected to
	 * <code>edge</code> between <code>start</code> and <code>stop</code>. Note
	 * that for some graph types there are guarantees about the size of this
	 * collection (i.e., some graphs contain edges that have exactly two
	 * endpoints, which may or may not be distinct). Implementations for those
	 * graph types may provide alternate methods that provide more convenient
	 * access to the vertices.
	 * 
	 * @param edge
	 *            the edge whose incident vertices are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the collection of vertices which are connected to
	 *         <code>edge</code>, or <code>null</code> if <code>edge</code> is
	 *         not present
	 */
	// Collection<V> getIncidentVertices(E edge,K start, K stop); // not
	// meaningful

	/**
	 * Returns an edge that connects <code>v1</code> to <code>v2</code> between
	 * <code>start</code> and <code>stop</code>. If this edge is not uniquely
	 * defined (that is, if the graph contains more than one edge connecting
	 * <code>v1</code> to <code>v2</code>), any of these edges may be returned.
	 * <code>findEdgeSet(v1, v2)</code> may be used to return all such edges.
	 * Returns null if either of the following is true:
	 * <ul>
	 * <li/><code>v2</code> is not connected to <code>v1</code>
	 * <li/>either <code>v1</code> or <code>v2</code> are not present in this
	 * graph
	 * </ul>
	 * <p>
	 * <b>Note</b>: for purposes of this method, <code>v1</code> is only
	 * considered to be connected to <code>v2</code> via a given <i>directed</i>
	 * edge <code>e</code> if
	 * <code>v1 == getSource(e) && v2 == getDest(e)</code> evaluates to
	 * <code>true</code>. (<code>v1</code> and <code>v2</code> are connected by
	 * an undirected edge <code>u</code> if <code>u</code> is incident to both
	 * <code>v1</code> and <code>v2</code>.)
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return an edge that connects <code>v1</code> to <code>v2</code>, or
	 *         <code>null</code> if no such edge exists (or either vertex is not
	 *         present)
	 * @see Hypergraph#findEdgeSet(Object, Object)
	 */
	// EdgeEntry<K, E> findEdge(V v1, V v2, K start, K stop);

	/**
	 * Returns all edges that connects <code>v1</code> to <code>v2</code>
	 * between <code>start</code> and <code>stop</code>. If this edge is not
	 * uniquely defined (that is, if the graph contains more than one edge
	 * connecting <code>v1</code> to <code>v2</code>), any of these edges may be
	 * returned. <code>findEdgeSet(v1, v2)</code> may be used to return all such
	 * edges. Returns null if <code>v2</code> is not connected to
	 * <code>v1</code>. <br/>
	 * Returns an empty collection if either <code>v1</code> or <code>v2</code>
	 * are not present in this graph.
	 * 
	 * <p>
	 * <b>Note</b>: for purposes of this method, <code>v1</code> is only
	 * considered to be connected to <code>v2</code> via a given <i>directed</i>
	 * edge <code>d</code> if
	 * <code>v1 == d.getSource() && v2 == d.getDest()</code> evaluates to
	 * <code>true</code>. (<code>v1</code> and <code>v2</code> are connected by
	 * an undirected edge <code>u</code> if <code>u</code> is incident to both
	 * <code>v1</code> and <code>v2</code>.)
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a collection containing all edges that connect <code>v1</code> to
	 *         <code>v2</code>, or <code>null</code> if either vertex is not
	 *         present
	 * @see Hypergraph#findEdge(Object, Object)
	 */
	// Collection<EdgeEntry<K, E>> findEdgeSet(V v1, V v2, K start, K stop);

	/**
	 * Returns <code>true</code> if <code>v1</code> and <code>v2</code> share an
	 * incident edge between <code>start</code> and <code>stop</code>.
	 * Equivalent to <code>getNeighbors(v1).contains(v2)</code>.
	 * 
	 * @param v1
	 *            the first vertex to test
	 * @param v2
	 *            the second vertex to test
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return <code>true</code> if <code>v1</code> and <code>v2</code> share an
	 *         incident edge
	 */
	// boolean isNeighbor(V v1, V v2, K start, K stop);

	/**
	 * Returns <code>true</code> if <code>vertex</code> and <code>edge</code>
	 * are incident to each other between <code>start</code> and
	 * <code>stop</code>. Equivalent to
	 * <code>getIncidentEdges(vertex).contains(edge)</code> and to
	 * <code>getIncidentVertices(edge).contains(vertex)</code>.
	 * 
	 * @param vertex
	 * @param edge
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return <code>true</code> if <code>vertex</code> and <code>edge</code>
	 *         are incident to each other
	 */
	// boolean isIncident(V vertex, E edge,K start, K stop);

	/**
	 * Returns the number of edges incident to <code>vertex</code> between
	 * <code>start</code> and <code>stop</code>. Special cases of interest:
	 * <ul>
	 * <li/>Incident self-loops are counted once.
	 * <li>If there is only one edge that connects this vertex to each of its
	 * neighbors (and vice versa), then the value returned will also be equal to
	 * the number of neighbors that this vertex has (that is, the output of
	 * <code>getNeighborCount</code>).
	 * <li>If the graph is directed, then the value returned will be the sum of
	 * this vertex's indegree (the number of edges whose destination is this
	 * vertex) and its outdegree (the number of edges whose source is this
	 * vertex), minus the number of incident self-loops (to avoid
	 * double-counting).
	 * </ul>
	 * <p>
	 * Equivalent to <code>getIncidentEdges(vertex).size()</code>.
	 * 
	 * @param vertex
	 *            the vertex whose degree is to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the degree of this node
	 * @see Hypergraph#getNeighborCount(Object)
	 */
	// int degree(V vertex, K start, K stop);

	/**
	 * Returns the number of vertices that are adjacent to <code>vertex</code>
	 * between <code>start</code> and <code>stop</code> (that is, the number of
	 * vertices that are incident to edges in <code>vertex</code>'s incident
	 * edge set).
	 * 
	 * <p>
	 * Equivalent to <code>getNeighbors(vertex).size()</code>.
	 * 
	 * @param vertex
	 *            the vertex whose neighbor count is to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of neighboring vertices
	 */
	// int getNeighborCount(V vertex, K start, K stop);

	/**
	 * Returns the number of vertices that are incident to <code>edge</code>
	 * between <code>start</code> and <code>stop</code>. For hyperedges, this
	 * can be any nonnegative integer; for edges this must be 2 (or 1 if
	 * self-loops are permitted).
	 * 
	 * <p>
	 * Equivalent to <code>getIncidentVertices(edge).size()</code>.
	 * 
	 * @param edge
	 *            the edge whose incident vertex count is to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of vertices that are incident to <code>edge</code>.
	 */
	// int getIncidentCount(E edge,K start, K stop);

	/**
	 * Returns the collection of edges in this graph between <code>start</code>
	 * and <code>stop</code> which are of type <code>edge_type</code>.
	 * 
	 * @param edge_type
	 *            the type of edges to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the collection of edges which are of type <code>edge_type</code>,
	 *         or <code>null</code> if the graph does not accept edges of this
	 *         type
	 * @see EdgeType
	 */
	// Collection<EdgeEntry<K, E>> getEdges(EdgeType edge_type, K start, K
	// stop);

	/**
	 * Returns the number of edges of type <code>edge_type</code> in this graph
	 * between <code>start</code> and <code>stop</code>.
	 * 
	 * @param edge_type
	 *            the type of edge for which the count is to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of edges of type <code>edge_type</code> in this graph
	 */
	// int getEdgeCount(EdgeType edge_type, K start, K stop);

	/**
	 * Returns a <code>Collection</code> view of the incoming edges incident to
	 * <code>vertex</code> in this graph between <code>start</code> and
	 * <code>stop</code>.
	 * 
	 * @param vertex
	 *            the vertex whose incoming edges are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of the incoming edges incident to
	 *         <code>vertex</code> in this graph
	 */
	// Collection<EdgeEntry<K, E>> getInEdges(V vertex, K start, K stop);

	/**
	 * Returns a <code>Collection</code> view of the outgoing edges incident to
	 * <code>vertex</code> in this graph between <code>start</code> and
	 * <code>stop</code>.
	 * 
	 * @param vertex
	 *            the vertex whose outgoing edges are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of the outgoing edges incident to
	 *         <code>vertex</code> in this graph
	 */
	// Collection<EdgeEntry<K, E>> getOutEdges(V vertex, K start, K stop);

	/**
	 * Returns the number of incoming edges incident to <code>vertex</code>
	 * between <code>start</code> and <code>stop</code>. Equivalent to
	 * <code>getInEdges(vertex).size()</code>.
	 * 
	 * @param vertex
	 *            the vertex whose indegree is to be calculated
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of incoming edges incident to <code>vertex</code>
	 */
	// int inDegree(V vertex, K start, K stop);

	/**
	 * Returns the number of outgoing edges incident to <code>vertex</code>
	 * between <code>start</code> and <code>stop</code>. Equivalent to
	 * <code>getOutEdges(vertex).size()</code>.
	 * 
	 * @param vertex
	 *            the vertex whose outdegree is to be calculated
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return the number of outgoing edges incident to <code>vertex</code>
	 */
	// int outDegree(V vertex, K start, K stop);

	/**
	 * Returns a <code>Collection</code> view of the predecessors of
	 * <code>vertex</code> in this graph between <code>start</code> and
	 * <code>stop</code>. A predecessor of <code>vertex</code> is defined as a
	 * vertex <code>v</code> which is connected to <code>vertex</code> by an
	 * edge <code>e</code>, where <code>e</code> is an outgoing edge of
	 * <code>v</code> and an incoming edge of <code>vertex</code>.
	 * 
	 * @param vertex
	 *            the vertex whose predecessors are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of the predecessors of
	 *         <code>vertex</code> in this graph
	 */
	// Collection<V> getPredecessors(V vertex, K start, K stop);

	/**
	 * Returns a <code>Collection</code> view of the successors of
	 * <code>vertex</code> in this graph between <code>start</code> and
	 * <code>stop</code>. A successor of <code>vertex</code> is defined as a
	 * vertex <code>v</code> which is connected to <code>vertex</code> by an
	 * edge <code>e</code>, where <code>e</code> is an incoming edge of
	 * <code>v</code> and an outgoing edge of <code>vertex</code>.
	 * 
	 * @param vertex
	 *            the vertex whose predecessors are to be returned
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return a <code>Collection</code> view of the successors of
	 *         <code>vertex</code> in this graph
	 */
	// Collection<V> getSuccessors(V vertex, K start, K stop);

	/**
	 * Returns the edge that is "first", that is the edge whose key is found to
	 * be less than that of all other edges. It there are multiple edges with
	 * the same "first" key, an edge is chosen arbitrarily among them.
	 * 
	 * @return
	 */
	EdgePair<K, E> getFirstEdge();

	/**
	 * Returns the edge that is "last", that is the edge whose key is found to
	 * be greater than that of all other edges. It there are multiple edges with
	 * the same "last" key, an edge is chosen arbitrarily among them.
	 * 
	 * @return
	 */
	EdgePair<K, E> getLastEdge();

	/**
	 * Returns the "first" key for any edge or node in the graph.
	 * 
	 * @return
	 */
	@RJava
	K getFirstKey();

	/**
	 * Returns the "last" key for any edge or node in the graph.
	 * 
	 * @return
	 */
	@RJava
	K getLastKey();

	/**
	 * Returns the subset of the network consisting of all node and edge events
	 * between <code>start</code> and <code>stop</code>.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @param stop
	 *            The end of the desired subset
	 * @return
	 */
	@RJava
	NavigableGraph<K, V, E> subNetwork(K start, K stop);

	/**
	 * Returns the subset of the network consisting of all node and edge events
	 * occuring after <code>start</code>.
	 * 
	 * @param start
	 *            The beginning of the desired subset
	 * @return
	 */
	@RJava
	NavigableGraph<K, V, E> tailNetwork(K start);

	/**
	 * Returns the subset of the network consisting of all node and edge events
	 * occuring before <code>stop</code>.
	 * 
	 * @param stop
	 *            The beginning of the desired subset
	 * @return
	 */
	@RJava
	NavigableGraph<K, V, E> headNetwork(K stop);

	/**
	 * Does the network have fixed, strict bounds on the range it represents. In
	 * general a sub-network of a larger network will have such bounds.
	 * 
	 * @return whether or not the network is strictly bounded in the keys it
	 *         contains
	 */
	boolean isBounded();

	/**
	 * If the network has a strict lower bound, such that it cannot contain any
	 * keys less that that, it returns said bound, otherwise returns null.
	 * 
	 * @return
	 */
	@RJava
	K getLowerBound();

	/**
	 * If the network has a strict upper bound, such that it cannot contain any
	 * keys greater that that, it returns said bound, otherwise returns null.
	 * 
	 * @return
	 */
	@RJava
	K getUpperBound();

	public boolean addEdge(K key, V v1, V v2);

	public boolean addEdge(K key, V v1, V v2, EdgeType edge_type);

	public boolean addEdge(K key, V v1, V v2, E edgevalue);

	public boolean addEdge(K key, V v1, V v2, E edgevalue, EdgeType edge_type);
	
	public Collection<Pair<V>> getPairs();

}
