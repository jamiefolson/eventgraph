package net.sf.eventgraphj.analysis.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.eventgraphj.analysis.NetworkAnalysis;
import net.sf.eventgraphj.analysis.compare.NetworkComparison;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.NavigableGraph;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

/**
 * Compares aggregate "snapshots" at a long time-scale to snapshots over the
 * same interval but at a smaller time-scale.
 * 
 * 
 * Proceeds through the longer time-scale and for each {@code Interval<K>}
 * compares it to each overlapping interval at the smaller time-scale.
 * 
 * @author jfolson
 * 
 * @param <K>
 * @param <V>
 * @param <E>
 */
public class SimpleAggregationComparison<K extends Comparable<K>, V, E> implements
        NetworkAnalysis<V, EdgeEntry<K, V, E>, NavigableGraph<K, V, E>, RealVector> {
	protected Iterable<Interval<K>> smallIntervalIterable;

	protected NetworkComparison<V, EdgeEntry<K, V, E>, NavigableGraph<K, V, E>> compare;

	// protected List<R> results;

	/**
	 * 
	 * @param smallIntervals
	 *            Defines the smaller time-scale
	 * @param largeIntervals
	 *            Defines the longer time-scale
	 * @param compare
	 *            The method of {@code NetworkComparison}
	 * @param output
	 *            Destination to record the results or {@code null} for no
	 *            recording
	 */
	public SimpleAggregationComparison(Iterable<Interval<K>> smallIntervals,
	        NetworkComparison<V, EdgeEntry<K, V, E>, NavigableGraph<K, V, E>> compare) {
		//super(largeIntervals);
		this.smallIntervalIterable = smallIntervals;
		this.compare = compare;
	}

	@Override
	public RealVector analyze(NavigableGraph<K, V, E> graph) {
		Iterator<Interval<K>> smallIntervalIterator;
		K start = graph.getLowerBound(), stop = graph.getUpperBound();
		K stopSmall, startSmall;
		Interval<K> smallInterval;
		smallIntervalIterator = this.smallIntervalIterable.iterator();
		smallInterval = smallIntervalIterator.next();
		while (smallInterval.getFinish().compareTo(start) <= 0) { // skip ahead to get to the intervals where this graph exists
			smallInterval = smallIntervalIterator.next();
		}
		startSmall = smallInterval.getStart();
		stopSmall = smallInterval.getFinish();
		if (startSmall.compareTo(start) < 0) // if next interval starts too soon, move it back to avoid errors
			startSmall = start;
		List<Double> comparisons = new ArrayList<Double>();
		while (stopSmall != null && startSmall.compareTo(stop) < 0) {
			if (stopSmall.compareTo(stop) > 0) // if next interval stops too late, move it to avoid errors
				stopSmall = stop;
			//System.out.println("SubCompare Interval: " + smallInterval.toString());
			NavigableGraph<K, V, E> smallGraph = graph.subNetwork(startSmall, stopSmall);
			Double compareResult = compare.compare(graph, smallGraph);
			//System.out.println("Compare Result: " + compareResult);
			comparisons.add(compareResult);
			//System.out.println("Compare Results: " + comparisons.toString());
			smallInterval = smallIntervalIterator.next();
			startSmall = smallInterval.getStart();
			stopSmall = smallInterval.getFinish();
		}
		RealVector result = new ArrayRealVector(comparisons.toArray(new Double[0]));

		//System.out.println("Compare Results: " + comparisons.toString());
		return result;
	}

}
