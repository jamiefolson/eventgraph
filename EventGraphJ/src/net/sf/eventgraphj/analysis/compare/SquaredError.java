package net.sf.eventgraphj.analysis.compare;

import java.util.Collection;

import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.NavigableGraph;


import edu.uci.ics.jung.graph.util.Pair;

/**
 * Computes the mean squared error of one graph from the other.
 * 
 * @author jfolson
 * 
 * @param <K>
 * @param <V>
 * @param <E>
 */
public class SquaredError<K extends Number & Comparable<K>, V, E> extends
        DyadicComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> implements
        NetworkComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> {
	double scaling = 1;
	final double prior;
	final double priorStrength;

	public SquaredError(double scaling, double prior, double priorStrength) {
		super();
		this.scaling = scaling;
		this.prior = prior;
		this.priorStrength = priorStrength;
	}

	public SquaredError(double scaling) {
		this(scaling, 0.0, 0.0);
	}

	@Override
	public double compareDyads(NavigableGraph<K, V, E> thisGraph, NavigableGraph<K, V, E> otherGraph, Pair<V> pair) {
		double alpha = prior;
		double beta = priorStrength;
		double thisRate = 0;
		double thisStart = thisGraph.isBounded() ? thisGraph.getLowerBound().doubleValue() : thisGraph.getFirstKey()
		        .doubleValue();
		double thisStop = thisGraph.isBounded() ? thisGraph.getUpperBound().doubleValue() : thisGraph.getLastKey()
		        .doubleValue();
		double thisDuration = thisStop - thisStart;
		Collection<?> edges = thisGraph.findEdgeSet(pair.getFirst(), pair.getSecond());
		if (edges != null) {
			thisRate = (alpha + edges.size()) / (beta + thisDuration / scaling);
		}
		double otherRate = 0;
		double otherStart = otherGraph.isBounded() ? otherGraph.getLowerBound().doubleValue() : otherGraph
		        .getFirstKey().doubleValue();
		double otherStop = otherGraph.isBounded() ? otherGraph.getUpperBound().doubleValue() : otherGraph.getLastKey()
		        .doubleValue();
		double otherDuration = otherStop - otherStart;
		edges = otherGraph.findEdgeSet(pair.getFirst(), pair.getSecond());
		if (edges != null) {
			otherRate = (alpha + edges.size()) / (beta + otherDuration / scaling);
		}
		assert (thisRate >= 0);
		assert (otherRate >= 0);
		return (otherRate - thisRate) * (otherRate - thisRate);
	}

}
