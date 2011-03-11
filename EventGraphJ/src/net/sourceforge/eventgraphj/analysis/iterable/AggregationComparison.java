package net.sourceforge.eventgraphj.analysis.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.eventgraphj.analysis.compare.NetworkComparison;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;

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
public class AggregationComparison<K extends Comparable<K>, V, E> extends
        IterableNetworkAnalysis<K, V, E, List<Double>> {
	protected Iterable<Interval<K>> smallIntervalIterable;
	protected Iterable<Interval<K>> largeIntervalIterable;

	protected NetworkComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> compare;

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
	public AggregationComparison(Iterable<Interval<K>> smallIntervals, Iterable<Interval<K>> largeIntervals,
	        NetworkComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> compare) {
		super(largeIntervals);
		this.smallIntervalIterable = smallIntervals;
		this.largeIntervalIterable = largeIntervals;
		this.compare = compare;
	}

	@Override
	protected List<Double> doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop) {
		Iterator<Interval<K>> smallIntervalIterator;
		K stopSmall, startSmall;
		Interval<K> smallInterval;
		smallIntervalIterator = this.smallIntervalIterable.iterator();
		smallInterval = smallIntervalIterator.next();
		while (smallInterval.getFinish().compareTo(start) <= 0) {
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
		//System.out.println("Compare Results: " + comparisons.toString());
		return comparisons;
	}

}
