/*
 * Created on Jul 10, 2007
 * 
 * Copyright (c) 2007, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package net.sf.eventgraphj.analysis;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.DistanceCentralityScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.Distance;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Hypergraph;

/**
 * Assigns scores to vertices based on their distances to each other vertex in
 * the graph.
 * 
 * 
 * Similar to the {@code DistanceCentralityScorer}, this returns the total or
 * average of all inverted distances (1/distance). Instead of returning null for
 * disconnected graphs, this substitutes {@code Double.POSITIVE_INFINITY} for
 * missing distances, which are then inverted, effectively adding 0 to the ego
 * node's centrality.
 * 
 * This class normalizes its results so that the value returned for vertex v is
 * <average over all other vertices, u, of>: 1 / (distance from v to u). This is
 * sometimes called <i>inverse closeness centrality</i>.
 * 
 * 
 * @see DistanceCentralityScorer
 */
public class InverseDistanceCentralityScorer<V, E> extends
		DistanceCentralityScorer<V, E> implements VertexScorer<V, Double> {

	/**
	 * Creates an instance with the specified graph, distance metric, and
	 * averaging behavior.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 * @param distance
	 *            The metric to use for specifying the distance between pairs of
	 *            vertices.
	 * @param ignore_self_distances
	 *            Specifies whether distances from a vertex to itself should be
	 *            included in its score.
	 */
	public InverseDistanceCentralityScorer(Hypergraph<V, E> graph,
			Distance<V> distance, boolean ignore_self_distances) {
		super(graph, distance, true, true, ignore_self_distances);
	}

	/**
	 * Creates an instance with the specified graph and averaging behavior whose
	 * vertex distances are calculated based on the specified edge weights.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 * @param edge_weights
	 *            The edge weights to use for specifying the distance between
	 *            pairs of vertices.
	 * @param ignore_self_distances
	 *            Specifies whether distances from a vertex to itself should be
	 *            included in its score.
	 */
	public InverseDistanceCentralityScorer(Hypergraph<V, E> graph,
			Transformer<E, ? extends Number> edge_weights,
			boolean ignore_self_distances) {
		this(graph, new DijkstraDistance<V, E>(graph, edge_weights),
				ignore_self_distances);
	}

	/**
	 * Equivalent to <code>this(graph, edge_weights, true)</code>.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 * @param edge_weights
	 *            The edge weights to use for specifying the distance between
	 *            pairs of vertices.
	 */
	public InverseDistanceCentralityScorer(Hypergraph<V, E> graph,
			Transformer<E, ? extends Number> edge_weights) {
		this(graph, new DijkstraDistance<V, E>(graph, edge_weights), true);
	}

	/**
	 * Equivalent to <code>this(graph, true)</code>.
	 * 
	 * @param graph
	 *            The graph on which the vertex scores are to be calculated.
	 */
	public InverseDistanceCentralityScorer(Hypergraph<V, E> graph) {
		this(graph, new UnweightedShortestPath<V, E>(graph), true);
	}

	/**
	 * Calculates the score for the specified vertex. Returns {@code null} if
	 * there are missing distances and such are not ignored by this instance.
	 */
	@Override
	public Double getVertexScore(V v) {
		Double value = this.output.get(v);
		if (value != null) {
			if (value < 0) {
				return null;
			}
			return value;
		}

		Map<V, Number> v_distances = new HashMap<V, Number>(
				this.distance.getDistanceMap(v));
		if (this.ignore_self_distances) {
			v_distances.remove(v);
		}

		// if we don't ignore missing distances and there aren't enough
		// distances, output null (shortcut)
		if (!this.ignore_missing) {
			int num_dests = this.graph.getVertexCount()
					- (this.ignore_self_distances ? 1 : 0);
			if (v_distances.size() != num_dests) {
				this.output.put(v, -1.0);
				return null;
			}
		}

		Double sum = 0.0;
		int count = 0;
		for (V w : this.graph.getVertices()) {
			if (w.equals(v) && this.ignore_self_distances) {
				continue;
			}
			Number w_distance = v_distances.get(w);
			if (w_distance == null) {
				if (this.ignore_missing) {
					w_distance = Double.POSITIVE_INFINITY;// graph.getVertexCount();
				} else {
					this.output.put(v, -1.0);
					return null;
				}
			}
			sum += 1 / w_distance.doubleValue();
			count++;
		}
		value = sum;
		if (this.averaging) {
			value /= count;
		}
		this.output.put(v, value);
		if (Double.isNaN(value)) {
			System.err.println(value);
		}
		// assert (!Double.isNaN(value));
		return value;
	}

	public Double getMaximumDeviation() {
		int N = this.graph.getVertexCount();
		return (double) ((N - 1) * (N - 1));
	}
}
