package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

import net.sf.eventgraphj.comparable.NavigableGraphModule.EdgeNavigableModule;

import org.apache.commons.collections15.Factory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class DyadNavigableGraph<K extends Comparable<K>, V, E> extends AbstractDyadNavigableGraph<K, V, E> implements
        Graph<V, EdgeEntry<K, V, E>>, Serializable {

	private static final long serialVersionUID = 1L;
	protected HashMap<EdgeEntry<K, V, E>, Pair<V>> directed_edges; // Map of
	// directed
	// edges to
	// incident
	// vertex
	// sets
	protected HashMap<EdgeEntry<K, V, E>, Pair<V>> undirected_edges; // Map of

	// undirected
	// edges
	// to
	// incident
	// vertex
	// sets
	@Inject
	public DyadNavigableGraph(MapProvider mapProvider, @Named("EdgeGraph") GraphProvider graphProvider) {
		super(mapProvider, graphProvider);
		this.mapGraph = graphProvider.get();
		this.directed_edges = new HashMap<EdgeEntry<K, V, E>, Pair<V>>();
		this.undirected_edges = new HashMap<EdgeEntry<K, V, E>, Pair<V>>();
	}

	@Override
	public EdgeType getDefaultEdgeType() {
		// TODO Auto-generated method stub
		return EdgeType.DIRECTED;
	}

	/*
		@Override
		public EdgeType getEdgeType(EdgeEntry<K, V, E> edge) {
			if (this.directed_edges.containsKey(edge)) {
				return EdgeType.DIRECTED;
			} else if (this.undirected_edges.containsKey(edge)) {
				return EdgeType.UNDIRECTED;
			} else {
				return null;
			}
		}

		@Override
		public V getSource(EdgeEntry<K, V, E> directed_edge) {
			if (this.getEdgeType(directed_edge) == EdgeType.DIRECTED) {
				return this.directed_edges.get(directed_edge).getFirst();
			} else {
				return null;
			}
		}

		@Override
		public V getDest(EdgeEntry<K, V, E> directed_edge) {
			if (this.getEdgeType(directed_edge) == EdgeType.DIRECTED) {
				return this.directed_edges.get(directed_edge).getSecond();
			} else {
				return null;
			}
		}*/

	@Override
	public boolean isSource(V vertex, EdgeEntry<K, V, E> edge) {
		if (!this.containsVertex(vertex)) {
			return false;
		}

		V source = this.getSource(edge);
		if (source != null) {
			return source.equals(vertex);
		} else {
			return false;
		}
	}

	@Override
	public Pair<V> getEndpoints(EdgeEntry<K, V, E> edge) {
		Pair<V> endpoints;
		endpoints = this.directed_edges.get(edge);
		if (endpoints == null) {
			return this.undirected_edges.get(edge);
		} else {
			return endpoints;
		}
	}

	@Override
	public boolean isDest(V vertex, EdgeEntry<K, V, E> edge) {
		if (!this.containsVertex(vertex)) {
			return false;
		}

		V dest = this.getDest(edge);
		if (dest != null) {
			return dest.equals(vertex);
		} else {
			return false;
		}
	}

	@Override
	public boolean isPredecessor(V v1, V v2) {
		for (EdgeEntry<K, V, E> edge : this.findEdgeSet(v1, v2)) {
			if (this.getEdgeType(edge) == EdgeType.UNDIRECTED) {
				return true;
			}
			if (this.getSource(edge).equals(v2)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSuccessor(V v1, V v2) {
		for (EdgeEntry<K, V, E> edge : this.findEdgeSet(v1, v2)) {
			if (this.getEdgeType(edge) == EdgeType.UNDIRECTED) {
				return true;
			}
			if (this.getSource(edge).equals(v1)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean addEdgeMetadata(EdgeEntry<K, V, E> edge, Pair<? extends V> endpoints, EdgeType edgeType) {
		if (edgeType == EdgeType.DIRECTED) {
			Pair<V> oldEdge = this.directed_edges.put(edge, (Pair<V>) endpoints);
			if (oldEdge != null) {
				System.err.println("overwriting a directed edge at: " + edge.getKey().toString() + " with value "
				        + edge.getValue() + " from " + oldEdge.getFirst() + " to " + oldEdge.getSecond()
				        + "\n\twith new edge from " + endpoints.getFirst() + " to " + endpoints.getSecond());
			}
		} else {
			Pair<V> oldEdge = this.undirected_edges.put(edge, (Pair<V>) endpoints);
			if (oldEdge != null) {
				System.err.println("overwriting a undirected edge at: " + edge.getKey().toString() + " with value "
				        + edge.getValue() + " from " + oldEdge.getFirst() + " to " + oldEdge.getSecond()
				        + "\n\twith new edge from " + endpoints.getFirst() + " to " + endpoints.getSecond());
			}
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
				if (arg1 == null) {
					return 0;
				}
				return -1;
			}
			if (arg1 == null) {
				return 1;
			}
			return arg0.compareTo(arg1);
		}
	}

	public static class SimpleComparableEdgeGraphFactory<K extends Comparable<K>, V, E> implements
	        Factory<Graph<V, EdgeEntry<K, V, E>>> {
		@Override
		public Graph<V, EdgeEntry<K, V, E>> create() {
			return new DyadNavigableGraph<K, V, E>(injector.getInstance(MapProvider.class),
			        injector.getInstance(GraphProvider.class));
		}
	}

	/*public static <V, K extends Comparable<K>, E extends ComparableEdge<K>> Factory<Graph<V, E>> getFactory() {
		return new SimpleComparableEdgeGraphFactory<V, K, E>();
	}*/

	private static Injector injector = Guice.createInjector(new EdgeNavigableModule());

	public static <V, K extends Comparable<K>, E> Factory<NavigableGraph<K, V, E>> getFactory() {
		return new Factory<NavigableGraph<K, V, E>>() {

			@Override
			public NavigableGraph<K, V, E> create() {
				return new DyadNavigableGraph<K, V, E>(injector.getInstance(MapProvider.class),
				        injector.getInstance(GraphProvider.class));
			}
		};
	}

}
