package net.sourceforge.eventgraphj.comparable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.Map.Entry;

import net.sourceforge.eventgraphj.comparable.SimpleNavigableGraph.NullComparator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
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
public abstract class AbstractNavigableGraph<K extends Comparable<K>, V, E> implements NavigableGraph<K, V, E>,
        Graph<V, EdgeEntry<K, E>>, Serializable {

	public class EdgeNavigableMap implements Serializable {
		private static final long serialVersionUID = 1L;
		protected final NavigableMap<K, EdgeEntry<K, E>> map;
		protected final V from, to;

		public EdgeNavigableMap(V from, V to, NavigableMap<K, EdgeEntry<K, E>> map) {
			this.from = from;
			this.to = to;
			this.map = map;
		}

		public int hashCode() {
			return from.hashCode() + to.hashCode();
		}

		public boolean equals(Object other) {
			try {
				EdgeNavigableMap edge = (EdgeNavigableMap) other;
				if (from.equals(from) && to.equals(to) && map.equals(edge.map))
					return true;
			} catch (ClassCastException e) {

			}
			return false;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1l;
	protected SparseGraph<V, EdgeNavigableMap> mapGraph;
	protected final Comparator<K> comparator = new NullComparator<K>();

	private final K lowerBound, upperBound;
	private final boolean isBounded;

	public AbstractNavigableGraph() {
		this.lowerBound = null;
		this.upperBound = null;
		this.isBounded = false;
	}

	public AbstractNavigableGraph(K lowerBound, K upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.isBounded = true;
	}

	public boolean addEdge(EdgeEntry<K, E> edge, Collection<? extends V> vertices) {
		return addEdge(edge, vertices, this.getDefaultEdgeType());
	}

	@SuppressWarnings("unchecked")
	public boolean addEdge(EdgeEntry<K, E> edge, Collection<? extends V> vertices, EdgeType edgeType) {
		if (vertices == null)
			throw new IllegalArgumentException("'vertices' parameter must not be null");
		if (vertices.size() == 2)
			return addEdge(edge, vertices instanceof Pair ? (Pair<V>) vertices : new Pair<V>(vertices), edgeType);
		else if (vertices.size() == 1) {
			V vertex = vertices.iterator().next();
			return addEdge(edge, new Pair<V>(vertex, vertex), edgeType);
		} else
			throw new IllegalArgumentException("Graph objects connect 1 or 2 vertices; vertices arg has "
			        + vertices.size());
	}

	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints},
	 * with the default edge type.
	 * 
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	protected boolean addEdge(EdgeEntry<K, E> edge, Pair<? extends V> endpoints) {
		return addEdge(edge, endpoints, this.getDefaultEdgeType());
	}

	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints} and
	 * {@code EdgeType}.
	 * 
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	protected boolean addEdge(EdgeEntry<K, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		if (!mapGraph.containsVertex(v1))
			mapGraph.addVertex(v1);
		if (!mapGraph.containsVertex(v2))
			mapGraph.addVertex(v2);

		NavigableMap<K, EdgeEntry<K, E>> map = null;
		for (EdgeNavigableMap mapEdge : mapGraph.findEdgeSet(v1, v2)) {
			if (edgeType.equals(mapGraph.getEdgeType(mapEdge))) {
				map = mapEdge.map;
			}
		}
		if (map == null) {
			map = this.addNewEdgeMap(v1, v2, edgeType);
		}
		/*if (edge.getComparable()==null){
			System.out.println("null comparable");
		}*/
		if (!map.containsKey(edge.getKey())) {
			map.put(edge.getKey(), edge);
			// System.out.println("added comparable: "+edge.getComparable()+" ? "+
			// map.containsKey(edge.getComparable()));
			/*for (Entry<K,E> entry : map.tailMap(null).entrySet()){
				System.out.println(entry.getKey() + " -> "+ entry.getValue());
			}*/
			this.addEdgeMetadata(edge, endpoints, edgeType);
			return true;
		}
		return false;
	}

	public boolean addEdge(EdgeEntry<K, E> e, V v1, V v2) {
		return addEdge(e, v1, v2, this.getDefaultEdgeType());
	}

	public boolean addEdge(EdgeEntry<K, E> e, V v1, V v2, EdgeType edge_type) {
		return addEdge(e, new Pair<V>(v1, v2), edge_type);
	}

	public boolean addEdge(K key, V v1, V v2) {
		return addEdge(key, v1, v2, this.getDefaultEdgeType());
	}

	public boolean addEdge(K key, V v1, V v2, EdgeType edge_type) {
		return addEdge(new EdgeEntry<K, E>(key), v1, v2, edge_type);
	}

	protected abstract boolean addEdgeMetadata(EdgeEntry<K, E> edge, Pair<? extends V> endpoints, EdgeType edgeType);

	protected abstract NavigableMap<K, EdgeEntry<K, E>> addNewEdgeMap(V v1, V v2, EdgeType edgeType);

	@Override
	public boolean addVertex(V vertex) {
		return this.getMapGraph().addVertex(vertex);
	}

	@Override
	public boolean containsEdge(EdgeEntry<K, E> edge) {
		return this.containsEdge(edge, null, null);
	}

	public boolean containsEdge(EdgeEntry<K, E> edge, K start, K stop) {
		if (start != null && stop != null) {
			Collection<EdgeNavigableMap> maps = mapGraph.findEdgeSet(getSource(edge), getDest(edge));
			if (maps != null)
				for (EdgeNavigableMap map : maps) {
					if (map.map.subMap(start, stop).containsValue(edge))
						return true;
				}
		} else {
			Collection<EdgeNavigableMap> maps = mapGraph.findEdgeSet(getSource(edge), getDest(edge));
			if (maps != null)
				for (EdgeNavigableMap map : maps) {
					if (map.map.containsValue(edge))
						return true;
				}
		}
		return false;
	}

	@Override
	public boolean containsVertex(V vertex) {
		return this.containsVertex(vertex, null, null);
	}

	public boolean containsVertex(V vertex, K start, K stop) {
		return mapGraph.containsVertex(vertex);
	}

	@Override
	public int degree(V vertex) {
		return this.degree(vertex, null, null);
	}

	public int degree(V vertex, K start, K stop) {
		return this.getIncidentEdges(vertex, start, stop).size();
	}

	@Override
	public EdgeEntry<K, E> findEdge(V v1, V v2) {
		return this.findEdge(v1, v2, null, null);
	}

	public EdgeEntry<K, E> findEdge(V v1, V v2, K start, K stop) {
		Collection<EdgeNavigableMap> maps = mapGraph.findEdgeSet(v1, v2);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				SortedMap<K, EdgeEntry<K, E>> submap = (start != null && stop != null) ? map.map.subMap(start, stop)
				        : map.map;
				if (submap.size() > 0)
					return submap.values().iterator().next();
			}
		return null;
	}

	@Override
	public Collection<EdgeEntry<K, E>> findEdgeSet(V v1, V v2) {
		return this.findEdgeSet(v1, v2, null, null);
	}

	public Collection<EdgeEntry<K, E>> findEdgeSet(V v1, V v2, K start, K stop) {
		Collection<EdgeEntry<K, E>> neighbors = new ArrayList<EdgeEntry<K, E>>();
		Collection<EdgeNavigableMap> maps = mapGraph.findEdgeSet(v1, v2);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				if (map == null)
					continue;
				Collection<EdgeEntry<K, E>> vals = (start != null && stop != null) ? map.map.subMap(start, stop)
				        .values() : map.map.values();
				if (vals == null || vals.isEmpty())
					continue;
				neighbors.addAll(vals);
			}
		return neighbors;
	}

	@Override
	public int getEdgeCount() {
		return this.getEdgeCount(null, null);
	}

	@Override
	public int getEdgeCount(EdgeType edge_type) {
		return this.getEdges(edge_type).size();
	}

	public int getEdgeCount(EdgeType edge_type, K start, K stop) {
		return getEdges(edge_type, start, stop).size();
	}

	public int getEdgeCount(K start, K stop) {
		return this.getEdges(start, stop).size();
	}

	@Override
	public Collection<EdgeEntry<K, E>> getEdges() {
		return this.getEdges(null, null);
	}

	@Override
	public Collection<EdgeEntry<K, E>> getEdges(EdgeType edge_type) {
		return this.getEdges(edge_type, null, null);
	}

	public Collection<EdgeEntry<K, E>> getEdges(EdgeType edge_type, K start, K stop) {
		Collection<EdgeEntry<K, E>> edges = new ArrayList<EdgeEntry<K, E>>();
		Collection<EdgeNavigableMap> maps = mapGraph.getEdges(edge_type);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				edges.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map.values());
			}
		return edges;
	}

	public Collection<EdgeEntry<K, E>> getEdges(K start, K stop) {
		Collection<EdgeEntry<K, E>> edges = new ArrayList<EdgeEntry<K, E>>();
		for (EdgeNavigableMap map : mapGraph.getEdges()) {
			assert (map != null);
			// System.out.println("start: "+start+"\tstop: "+stop);
			// System.out.println(map.get(map.firstKey()));

			edges.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map.values());
		}
		return edges;
	}

	@Override
	public EdgeEntry<K, E> getFirstEdge() {
		EdgeEntry<K, E> firstEdge = null;
		K first = null;
		for (EdgeNavigableMap map : mapGraph.getEdges()) {
			assert (map != null);
			Iterator<Entry<K, EdgeEntry<K, E>>> iterator = map.map.entrySet().iterator();
			K thisFirst = null;
			Entry<K, EdgeEntry<K, E>> thisEntry = null;
			while (thisFirst == null && iterator.hasNext()) {
				thisEntry = iterator.next();
				thisFirst = thisEntry.getKey();
			}
			if (thisFirst == null)
				continue;
			else if (first == null) {
				first = thisFirst;
				firstEdge = thisEntry.getValue();
			} else if (comparator.compare(first, thisFirst) > 0) {
				first = thisFirst;
				firstEdge = thisEntry.getValue();
			}
		}
		return firstEdge;
	}

	@Override
	public K getFirstKey() {
		K first = null;
		for (EdgeNavigableMap map : mapGraph.getEdges()) {
			assert (map != null);
			Iterator<K> iterator = map.map.keySet().iterator();
			K thisFirst = null;
			while (thisFirst == null && iterator.hasNext()) {
				thisFirst = iterator.next();
			}
			if (thisFirst == null)
				continue;
			else if (first == null) {
				first = thisFirst;
			} else if (comparator.compare(first, thisFirst) > 0) {
				first = thisFirst;
			}
		}
		return first;
	}

	@Override
	public int getIncidentCount(EdgeEntry<K, E> edge) {
		Pair<V> incident = this.getEndpoints(edge);
		if (incident == null)
			return 0;
		if (incident.getFirst() == incident.getSecond())
			return 1;
		else
			return 2;
	}

	@Override
	public Collection<EdgeEntry<K, E>> getIncidentEdges(V vertex) {
		return this.getIncidentEdges(vertex, null, null);
	}

	public Collection<EdgeEntry<K, E>> getIncidentEdges(V vertex, K start, K stop) {
		Collection<EdgeEntry<K, E>> neighbors = new ArrayList<EdgeEntry<K, E>>();
		Collection<EdgeNavigableMap> maps = mapGraph.getIncidentEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				neighbors.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map
				        .values());
			}
		return neighbors;
	}

	@Override
	public Collection<V> getIncidentVertices(EdgeEntry<K, E> edge) {
		Pair<V> endpoints = this.getEndpoints(edge);
		Collection<V> incident = new ArrayList<V>();
		incident.add(endpoints.getFirst());
		incident.add(endpoints.getSecond());

		return Collections.unmodifiableCollection(incident);
	}

	@Override
	public Collection<EdgeEntry<K, E>> getInEdges(V vertex) {
		return this.getInEdges(vertex, null, null);
	}

	public Collection<EdgeEntry<K, E>> getInEdges(V vertex, K start, K stop) {
		Collection<EdgeEntry<K, E>> neighbors = new ArrayList<EdgeEntry<K, E>>();
		Collection<EdgeNavigableMap> maps = mapGraph.getInEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				neighbors.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map
				        .values());
			}
		return neighbors;
	}

	@Override
	public EdgeEntry<K, E> getLastEdge() {
		EdgeEntry<K, E> lastEdge = null;
		K last = null;
		for (EdgeNavigableMap map : mapGraph.getEdges()) {
			assert (map != null);
			Iterator<Entry<K, EdgeEntry<K, E>>> iterator = map.map.descendingMap().entrySet().iterator();
			K thislast = null;
			Entry<K, EdgeEntry<K, E>> thisEntry = null;
			while (thislast == null && iterator.hasNext()) {
				thisEntry = iterator.next();
				thislast = thisEntry.getKey();
			}
			if (thislast == null)
				continue;
			else if (last == null) {
				last = thislast;
				lastEdge = thisEntry.getValue();
			} else if (comparator.compare(last, thislast) < 0) {
				last = thislast;
				lastEdge = thisEntry.getValue();
			}
		}
		return lastEdge;
	}

	@Override
	public K getLastKey() {
		K last = null;
		for (EdgeNavigableMap map : mapGraph.getEdges()) {
			assert (map != null);
			Iterator<K> iterator = map.map.descendingKeySet().iterator();
			K thislast = null;
			while (thislast == null && iterator.hasNext()) {
				thislast = iterator.next();
			}
			if (thislast == null)
				continue;
			else if (last == null) {
				last = thislast;
			} else if (comparator.compare(last, thislast) < 0) {
				last = thislast;
			}
		}
		return last;
	}

	@Override
	public K getLowerBound() {
		return this.lowerBound;
	}

	public Graph<V, EdgeNavigableMap> getMapGraph() {
		return mapGraph;
	}

	@Override
	public int getNeighborCount(V vertex) {
		return this.getNeighborCount(vertex, null, null);
	}

	public int getNeighborCount(V vertex, K start, K stop) {
		return getNeighbors(vertex, start, stop).size();
	}

	@Override
	public Collection<V> getNeighbors(V vertex) {
		return this.getNeighbors(vertex, null, null);
	}

	public Collection<V> getNeighbors(V vertex, K start, K stop) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<EdgeNavigableMap> maps = mapGraph.getIncidentEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {

				for (Entry<K, EdgeEntry<K, E>> entry : (start != null && stop != null) ? map.map.subMap(start, stop)
				        .entrySet() : map.map.entrySet()) {
					neighbors.add(this.getOpposite(vertex, entry.getValue()));
				}
			}
		return neighbors;
	}

	public V getOpposite(V vertex, EdgeEntry<K, E> edge) {
		Pair<V> incident = this.getEndpoints(edge);
		V first = incident.getFirst();
		V second = incident.getSecond();
		if (vertex.equals(first))
			return second;
		else if (vertex.equals(second))
			return first;
		else
			throw new IllegalArgumentException(vertex + " is not incident to " + edge + " in this graph");
	}

	@Override
	public Collection<EdgeEntry<K, E>> getOutEdges(V vertex) {
		return this.getOutEdges(vertex, null, null);
	}

	public Collection<EdgeEntry<K, E>> getOutEdges(V vertex, K start, K stop) {
		Collection<EdgeEntry<K, E>> neighbors = new ArrayList<EdgeEntry<K, E>>();
		Collection<EdgeNavigableMap> maps = mapGraph.getOutEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				neighbors.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map
				        .values());
			}
		return neighbors;
	}

	@Override
	public int getPredecessorCount(V vertex) {
		return this.getPredecessors(vertex).size();
	}

	public int getPredecessorCount(V vertex, K start, K stop) {
		return this.getPredecessors(vertex, start, stop).size();
	}

	@Override
	public Collection<V> getPredecessors(V vertex) {
		return this.getPredecessors(vertex, null, null);
	}

	public Collection<V> getPredecessors(V vertex, K start, K stop) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<EdgeNavigableMap> maps = mapGraph.getInEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				for (EdgeEntry<K, E> edge : (start != null && stop != null) ? map.map.subMap(start, stop).values()
				        : map.map.values()) {
					neighbors.add(this.getOpposite(vertex, edge));
				}
			}
		return neighbors;
	}

	@Override
	public int getSuccessorCount(V vertex) {
		return this.getSuccessors(vertex).size();
	}

	public int getSuccessorCount(V vertex, K start, K stop) {
		return this.getSuccessors(vertex, start, stop).size();
	}

	@Override
	public Collection<V> getSuccessors(V vertex) {
		return this.getSuccessors(vertex, null, null);
	}

	public Collection<V> getSuccessors(V vertex, K start, K stop) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<EdgeNavigableMap> maps = mapGraph.getOutEdges(vertex);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				for (EdgeEntry<K, E> edge : (start != null && stop != null) ? map.map.subMap(start, stop).values()
				        : map.map.values()) {
					neighbors.add(this.getOpposite(vertex, edge));
				}
			}
		return neighbors;
	}

	@Override
	public K getUpperBound() {
		return this.upperBound;
	}

	protected Pair<V> getValidatedEndpoints(EdgeEntry<K, E> edge, Pair<? extends V> endpoints) {
		if (edge == null)
			throw new IllegalArgumentException("input edge may not be null");

		if (endpoints == null)
			throw new IllegalArgumentException("endpoints may not be null");

		Pair<V> new_endpoints = new Pair<V>(endpoints.getFirst(), endpoints.getSecond());
		if (containsEdge(edge)) {
			Pair<V> existing_endpoints = getEndpoints(edge);
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
	public int getVertexCount() {
		return this.getVertexCount(null, null);
	}

	public int getVertexCount(K start, K stop) {
		return this.getVertices(start, stop).size();
	}

	@Override
	public Collection<V> getVertices() {
		return this.getVertices(null, null);
	}

	public Collection<V> getVertices(K start, K stop) {
		return mapGraph.getVertices();
	}

	@Override
	public int inDegree(V vertex) {
		return this.inDegree(vertex, null, null);
	}

	public int inDegree(V vertex, K start, K stop) {
		return getInEdges(vertex, start, stop).size();
	}

	@Override
	public boolean isBounded() {
		return this.isBounded;
	}

	public boolean isIncident(V vertex, EdgeEntry<K, E> edge) {
		if (!containsVertex(vertex) || !containsEdge(edge))
			throw new IllegalArgumentException("At least one of these not in this graph: " + vertex + ", " + edge);
		return this.getIncidentEdges(vertex, null, null).contains(edge);
	}

	@Override
	public boolean isNeighbor(V v1, V v2) {
		return this.isNeighbor(v1, v2, null, null);
	}

	public boolean isNeighbor(V v1, V v2, K start, K stop) {
		Collection<EdgeNavigableMap> maps = mapGraph.findEdgeSet(v1, v2);
		if (maps != null)
			for (EdgeNavigableMap map : maps) {
				SortedMap<K, EdgeEntry<K, E>> submap = (start != null && stop != null) ? map.map.subMap(start, stop)
				        : map.map;
				if (submap.size() > 0)
					return true;
			}
		return false;
	}

	@Override
	public int outDegree(V vertex) {
		return this.outDegree(vertex, null, null);
	}

	public int outDegree(V vertex, K start, K stop) {
		return getOutEdges(vertex, start, stop).size();
	}

	@Override
	public boolean removeEdge(EdgeEntry<K, E> edge) {

		// TODO FIX THIS!!
		EdgeNavigableMap map = mapGraph.findEdge(getSource(edge), getDest(edge));
		if (map == null)
			return false;
		return (map.map.remove(edge) != null);
	}

	@Override
	public boolean removeVertex(V vertex) {
		// TODO Test this
		return this.mapGraph.removeVertex(vertex);
	}

	public String toString() {
		String out = "NavigableGraph : ";
		for (EdgeEntry entry : this.getEdges()) {
			out = out + "\n\t" + this.getSource(entry).toString() + "-->" + this.getDest(entry).toString() + " : "
			        + entry.getKey();
		}
		return out;

	}
}
