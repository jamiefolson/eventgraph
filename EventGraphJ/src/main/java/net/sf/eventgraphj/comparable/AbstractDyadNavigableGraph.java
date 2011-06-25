package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.SortedMap;

import net.sf.eventgraphj.comparable.DyadNavigableGraph.NullComparator;

import com.google.inject.Inject;

import edu.uci.ics.jung.graph.Graph;
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
public abstract class AbstractDyadNavigableGraph<K extends Comparable<K>, V, E> extends BaseNavigableGraph<K, V, E>
        implements NavigableGraph<K, V, E>, Graph<V, EdgeEntry<K, V, E>>, Serializable {
	public static class DyadEdgeMap<K extends Comparable<K>, V, E> implements Serializable {
		private static final long serialVersionUID = 1L;
		protected final NavigableMap<K, EdgeEntry<K, V, E>> map;
		protected final V from, to;

		public DyadEdgeMap(V from, V to, NavigableMap<K, EdgeEntry<K, V, E>> map) {
			this.from = from;
			this.to = to;
			this.map = map;
		}

		@Override
		public int hashCode() {
			return this.from.hashCode() + this.to.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof DyadEdgeMap) {
				try {
					DyadEdgeMap<K, V, E> edge = (DyadEdgeMap<K, V, E>) other;
					if (this.from.equals(this.from) && this.to.equals(this.to) && this.map.equals(edge.map)) {
						return true;
					}
				} catch (ClassCastException e) {

				}
			}
			return false;
		}
	}

	public static class DyadNavigableSubGraph<K extends Comparable<K>, V, E> extends
	        AbstractDyadNavigableGraph<K, V, E> {
		private static final long serialVersionUID = 1L;
		protected final AbstractDyadNavigableGraph<K, V, E> parent;
		final K start, stop;

		public DyadNavigableSubGraph(AbstractDyadNavigableGraph<K, V, E> parent, K start, K stop) {
			super(parent.mapProvider, parent.graphProvider, start, stop);
			this.parent = parent;
			this.start = start;
			this.stop = stop;
			this.mapGraph = parent.graphProvider.get();

			for (DyadEdgeMap<K, V, E> edge : parent.mapGraph.getEdges()) {
				if (!parent.mapGraph.containsEdge(edge)) {
					System.err.println("skipping " + edge.toString() + "\n\t" + edge.hashCode());
					continue;
				}
				Pair<V> endpoints = parent.mapGraph.getEndpoints(edge);
				EdgeType edgetype = parent.mapGraph.getEdgeType(edge);
				NavigableMap<K, EdgeEntry<K, V, E>> map = edge.map;
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
				assert (this.mapGraph.addEdge(new DyadEdgeMap<K, V, E>(edge.from, edge.to, map), endpoints, edgetype));
			}
		}

		@Override
		protected boolean addEdgeMetadata(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
			return this.parent.addEdgeMetadata(edge, endpoints, edgeType);
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1l;
	protected MapProvider mapProvider;
	protected GraphProvider graphProvider;
	protected Graph<V, DyadEdgeMap<K, V, E>> mapGraph;
	protected final Comparator<K> comparator = new NullComparator<K>();

	@Inject
	public AbstractDyadNavigableGraph(MapProvider mapProvider, GraphProvider graphProvider) {
		super();
		this.mapProvider = mapProvider;
		this.graphProvider = graphProvider;
	}

	protected AbstractDyadNavigableGraph(MapProvider mapProvider, GraphProvider graphProvider, K lowerBound,
	        K upperBound) {
		super(lowerBound, upperBound);
		this.mapProvider = mapProvider;
		this.graphProvider = graphProvider;
	}

	/**
	 * Adds {@code edge} to this graph with the specified {@code endpoints} and
	 * {@code EdgeType}.
	 * 
	 * @return {@code} true iff the graph was modified as a result of this call
	 */
	@Override
	protected boolean addEdge(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		if (!this.mapGraph.containsVertex(v1)) {
			this.mapGraph.addVertex(v1);
		}
		if (!this.mapGraph.containsVertex(v2)) {
			this.mapGraph.addVertex(v2);
		}

		DyadEdgeMap<K, V, E> edgeMap = null;
		for (DyadEdgeMap<K, V, E> edgeMapItem : this.mapGraph.findEdgeSet(v1, v2)) {
			if (edgeType.equals(this.mapGraph.getEdgeType(edgeMapItem))) {
				edgeMap = edgeMapItem;
			}
		}
		if (edgeMap == null) {
			edgeMap = this.createEdgeNavigableMap(v1, v2, edgeType);
			this.mapGraph.addEdge(edgeMap, v1, v2, edgeType);
		}
		/*
		 * if (edge.getComparable()==null){
		 * System.out.println("null comparable"); }
		 */
		if (!edgeMap.map.containsKey(edge.getKey())) {
			edgeMap.map.put(edge.getKey(), edge);
			// System.out.println("added comparable: "+edge.getComparable()+" ? "+
			// map.containsKey(edge.getComparable()));
			/*
			 * for (Entry<K,E> entry : map.tailMap(null).entrySet()){
			 * System.out.println(entry.getKey() + " -> "+ entry.getValue()); }
			 */
			this.addEdgeMetadata(edge, endpoints, edgeType);
			return true;
		}
		return false;
	}

	protected abstract boolean addEdgeMetadata(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType);

	protected DyadEdgeMap<K, V, E> createEdgeNavigableMap(V v1, V v2, EdgeType edgeType) {
		NavigableMap<K, EdgeEntry<K, V, E>> map = this.mapProvider.get();
		DyadEdgeMap<K, V, E> edge = new DyadEdgeMap<K, V, E>(v1, v2, map);
		return edge;
	}

	@Override
	public boolean addVertex(V vertex) {
		return this.getMapGraph().addVertex(vertex);
	}

	@Override
	public boolean containsEdge(EdgeEntry<K, V, E> edge) {
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.findEdgeSet(this.getSource(edge), this.getDest(edge));
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				if (map.map.containsValue(edge)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsVertex(V vertex) {
		return this.mapGraph.containsVertex(vertex);
	}

	@Override
	public int degree(V vertex) {
		return this.getIncidentEdges(vertex).size();
	}

	@Override
	public EdgeEntry<K, V, E> findEdge(V v1, V v2) {
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.findEdgeSet(v1, v2);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				if (map.map.size() > 0) {
					return map.map.values().iterator().next();
				}
			}
		}
		return null;
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> findEdgeSet(V v1, V v2) {
		Collection<EdgeEntry<K, V, E>> neighbors = new ArrayList<EdgeEntry<K, V, E>>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.findEdgeSet(v1, v2);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				if (map == null) {
					continue;
				}
				Collection<EdgeEntry<K, V, E>> vals = map.map.values();
				if (vals == null || vals.isEmpty()) {
					continue;
				}
				neighbors.addAll(vals);
			}
		}
		return neighbors;
	}

	@Override
	public int getEdgeCount() {
		int count = 0;
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			count += map.map.size();
		}
		return count;
	}

	@Override
	public int getEdgeCount(EdgeType edge_type) {
		int count = 0;
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getEdges(edge_type);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				count += map.map.size();
			}
		}
		return count;
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> getEdges(EdgeType edge_type) {
		Collection<EdgeEntry<K, V, E>> edges = new ArrayList<EdgeEntry<K, V, E>>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getEdges(edge_type);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				edges.addAll(map.map.values());
			}
		}
		return edges;
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> getEdges() {
		Collection<EdgeEntry<K, V, E>> edges = new ArrayList<EdgeEntry<K, V, E>>();
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			assert (map != null);
			// System.out.println("start: "+start+"\tstop: "+stop);
			// System.out.println(map.get(map.firstKey()));

			edges.addAll(map.map.values());
		}
		return edges;
	}

	@Override
	public EdgeEntry<K, V, E> getFirstEdge() {
		EdgeEntry<K, V, E> firstEdge = null;
		K first = null;
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			assert (map != null);
			Iterator<Entry<K, EdgeEntry<K, V, E>>> iterator = map.map.entrySet().iterator();
			K thisFirst = null;
			Entry<K, EdgeEntry<K, V, E>> thisEntry = null;
			while (thisFirst == null && iterator.hasNext()) {
				thisEntry = iterator.next();
				thisFirst = thisEntry.getKey();
			}
			if (thisFirst == null) {
				continue;
			} else if (first == null) {
				first = thisFirst;
				firstEdge = thisEntry.getValue();
			} else if (this.comparator.compare(first, thisFirst) > 0) {
				first = thisFirst;
				firstEdge = thisEntry.getValue();
			}
		}
		return firstEdge;
	}

	@Override
	public K getFirstKey() {
		K first = null;
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			assert (map != null);
			Iterator<K> iterator = map.map.keySet().iterator();
			K thisFirst = null;
			while (thisFirst == null && iterator.hasNext()) {
				thisFirst = iterator.next();
			}
			if (thisFirst == null) {
				continue;
			} else if (first == null) {
				first = thisFirst;
			} else if (this.comparator.compare(first, thisFirst) > 0) {
				first = thisFirst;
			}
		}
		return first;
	}

	@Override
	public int getIncidentCount(EdgeEntry<K, V, E> edge) {
		Pair<V> incident = this.getEndpoints(edge);
		if (incident == null) {
			return 0;
		}
		if (incident.getFirst() == incident.getSecond()) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> getIncidentEdges(V vertex) {
		return this.getIncidentEdges(vertex, null, null);
	}

	public Collection<EdgeEntry<K, V, E>> getIncidentEdges(V vertex, K start, K stop) {
		Collection<EdgeEntry<K, V, E>> neighbors = new ArrayList<EdgeEntry<K, V, E>>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getIncidentEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				neighbors.addAll((start != null && stop != null) ? map.map.subMap(start, stop).values() : map.map
				        .values());
			}
		}
		return neighbors;
	}

	@Override
	public Collection<V> getIncidentVertices(EdgeEntry<K, V, E> edge) {
		Pair<V> endpoints = this.getEndpoints(edge);
		Collection<V> incident = new ArrayList<V>();
		incident.add(endpoints.getFirst());
		incident.add(endpoints.getSecond());

		return Collections.unmodifiableCollection(incident);
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> getInEdges(V vertex) {
		Collection<EdgeEntry<K, V, E>> neighbors = new ArrayList<EdgeEntry<K, V, E>>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getInEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				neighbors.addAll(map.map.values());
			}
		}
		return neighbors;
	}

	@Override
	public EdgeEntry<K, V, E> getLastEdge() {
		EdgeEntry<K, V, E> lastEdge = null;
		K last = null;
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			assert (map != null);
			Iterator<Entry<K, EdgeEntry<K, V, E>>> iterator = map.map.descendingMap().entrySet().iterator();
			K thislast = null;
			Entry<K, EdgeEntry<K, V, E>> thisEntry = null;
			while (thislast == null && iterator.hasNext()) {
				thisEntry = iterator.next();
				thislast = thisEntry.getKey();
			}
			if (thislast == null) {
				continue;
			} else if (last == null) {
				last = thislast;
				lastEdge = thisEntry.getValue();
			} else if (this.comparator.compare(last, thislast) < 0) {
				last = thislast;
				lastEdge = thisEntry.getValue();
			}
		}
		return lastEdge;
	}

	@Override
	public K getLastKey() {
		K last = null;
		for (DyadEdgeMap<K, V, E> map : this.mapGraph.getEdges()) {
			assert (map != null);
			Iterator<K> iterator = map.map.descendingKeySet().iterator();
			K thislast = null;
			while (thislast == null && iterator.hasNext()) {
				thislast = iterator.next();
			}
			if (thislast == null) {
				continue;
			} else if (last == null) {
				last = thislast;
			} else if (this.comparator.compare(last, thislast) < 0) {
				last = thislast;
			}
		}
		return last;
	}

	public Graph<V, DyadEdgeMap<K, V, E>> getMapGraph() {
		return this.mapGraph;
	}

	@Override
	public int getNeighborCount(V vertex) {
		return this.getNeighbors(vertex).size();
	}

	@Override
	public Collection<V> getNeighbors(V vertex) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getIncidentEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {

				for (Entry<K, EdgeEntry<K, V, E>> entry : map.map.entrySet()) {
					neighbors.add(this.getOpposite(vertex, entry.getValue()));
				}
			}
		}
		return neighbors;
	}

	@Override
	public V getOpposite(V vertex, EdgeEntry<K, V, E> edge) {
		Pair<V> incident = this.getEndpoints(edge);
		V first = incident.getFirst();
		V second = incident.getSecond();
		if (vertex.equals(first)) {
			return second;
		} else if (vertex.equals(second)) {
			return first;
		} else {
			throw new IllegalArgumentException(vertex + " is not incident to " + edge + " in this graph");
		}
	}

	@Override
	public Collection<EdgeEntry<K, V, E>> getOutEdges(V vertex) {
		Collection<EdgeEntry<K, V, E>> neighbors = new ArrayList<EdgeEntry<K, V, E>>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getOutEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				neighbors.addAll(map.map.values());
			}
		}
		return neighbors;
	}

	@Override
	public int getPredecessorCount(V vertex) {
		return this.getPredecessors(vertex).size();
	}

	@Override
	public Collection<V> getPredecessors(V vertex) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getInEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				for (EdgeEntry<K, V, E> edge : map.map.values()) {
					neighbors.add(this.getOpposite(vertex, edge));
				}
			}
		}
		return neighbors;
	}

	@Override
	public int getSuccessorCount(V vertex) {
		return this.getSuccessors(vertex).size();
	}

	@Override
	public Collection<V> getSuccessors(V vertex) {
		Collection<V> neighbors = new HashSet<V>();
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.getOutEdges(vertex);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				for (EdgeEntry<K, V, E> edge : map.map.values()) {
					neighbors.add(this.getOpposite(vertex, edge));
				}
			}
		}
		return neighbors;
	}

	@Override
	protected Pair<V> getValidatedEndpoints(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints) {
		if (edge == null) {
			throw new IllegalArgumentException("input edge may not be null");
		}

		if (endpoints == null) {
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
	public int getVertexCount() {
		return this.getVertices().size();
	}

	@Override
	public Collection<V> getVertices() {
		return this.mapGraph.getVertices();
	}

	@Override
	public int inDegree(V vertex) {
		return this.getInEdges(vertex).size();
	}

	@Override
	public boolean isIncident(V vertex, EdgeEntry<K, V, E> edge) {
		if (!this.containsVertex(vertex) || !this.containsEdge(edge)) {
			throw new IllegalArgumentException("At least one of these not in this graph: " + vertex + ", " + edge);
		}
		return this.getIncidentEdges(vertex, null, null).contains(edge);
	}

	@Override
	public boolean isNeighbor(V v1, V v2) {
		Collection<DyadEdgeMap<K, V, E>> maps = this.mapGraph.findEdgeSet(v1, v2);
		if (maps != null) {
			for (DyadEdgeMap<K, V, E> map : maps) {
				SortedMap<K, EdgeEntry<K, V, E>> submap = map.map;
				if (submap.size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int outDegree(V vertex) {
		return this.getOutEdges(vertex).size();
	}

	@Override
	public boolean removeEdge(EdgeEntry<K, V, E> edge) {

		Pair<V> endpoints = this.getEndpoints(edge);
		V v1 = endpoints.getFirst();
		V v2 = endpoints.getSecond();

		if (this.getEdgeType(edge) == EdgeType.DIRECTED) {
			v1 = this.getSource(edge);
			v2 = this.getDest(edge);
		}
		DyadEdgeMap<K, V, E> map = this.mapGraph.findEdge(v1, v2);
		if (map == null) {
			return false;
		}
		return (map.map.remove(edge.getKey()) != null);
	}

	@Override
	public boolean removeVertex(V vertex) {
		// TODO Test this
		return this.mapGraph.removeVertex(vertex);
	}

	@Override
	public String toString() {
		String out = "NavigableGraph : ";
		for (EdgeEntry<K, V, E> entry : this.getEdges()) {
			out = out + "\n\t" + this.getSource(entry).toString() + "-->" + this.getDest(entry).toString() + " : "
			        + entry.getKey();
		}
		return out;

	}

	@Override
	public NavigableGraph<K, V, E> subNetwork(K start, K stop) {
		return new DyadNavigableSubGraph<K, V, E>(this, start, stop);
	}

	@Override
	public NavigableGraph<K, V, E> tailNetwork(K start) {
		return new DyadNavigableSubGraph<K, V, E>(this, start, null);
	}

	@Override
	public NavigableGraph<K, V, E> headNetwork(K stop) {
		return new DyadNavigableSubGraph<K, V, E>(this, null, stop);
	}
}
