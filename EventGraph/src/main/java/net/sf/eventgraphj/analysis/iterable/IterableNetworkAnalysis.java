package net.sf.eventgraphj.analysis.iterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.eventgraphj.analysis.NetworkAnalysis;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.NavigableGraph;

/**
 * Performs the specified analysis for each aggregate "snapshots" for subGraphs
 * of the target {@code NavigableGraph} generated for {@code Interval<K>}s
 * produced by an {@code Iterable<Interval<K>>}.
 * 
 * @author jfolson
 * 
 * @param <K>
 * @param <V>
 * @param <E>
 * @param <R>
 */
public abstract class IterableNetworkAnalysis<K extends Comparable<K>, V, E, R> implements
        NetworkAnalysis<V, EdgeEntry<K, V, E>, NavigableGraph<K, V, E>, List<R>> {
	Iterable<Interval<K>> iterable;
	NavigableGraph<K, V, E> graph;

	// protected List<R> results;

	public IterableNetworkAnalysis(Iterable<Interval<K>> iterator) {
		this.iterable = iterator;
	}

	@Override
	public List<R> analyze(NavigableGraph<K, V, E> graph) {
		List<R> results = new ArrayList<R>();
		Iterator<Interval<K>> iterator = this.iterable.iterator();
		K graphStart = graph.getLowerBound();
		K graphStop = graph.getUpperBound();
		K start, finish;
		Interval<K> interval = null;
		interval = iterator.next();
		if (graphStart != null) {
			while (interval.getFinish().compareTo(graphStart) <= 0) {
				interval = iterator.next();
			}
		}
		start = interval.getStart();
		finish = interval.getFinish();
		if (graphStart != null && start.compareTo(graphStart) < 0) {
			start = graphStart;
		}

		while (finish != null && ((graphStop == null) || (start.compareTo(graphStop) < 0))) {
			if (graphStop != null && finish.compareTo(graphStop) > 0) {
				finish = graphStop;
			}
			// System.out.println("Compare " + start + "-" + finish + " within "
			// + graphStart + "-" + graphStop);
			long t1 = System.nanoTime();
			R result = this.doSubAnalysis(graph.subNetwork(start, finish), start, finish);
			long t2 = System.nanoTime();
			// System.out.println((t2 - t1) / 1000000000.0);
			results.add(result);
			interval = iterator.next();

			start = interval.getStart();
			finish = interval.getFinish();
		}
		return results;
	}

	/**
	 * The analysis to perform on each subGraph "snapshot"
	 * 
	 * @param graph
	 * @param start
	 * @param stop
	 * @return
	 */
	protected abstract R doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop);

	/*public static void main(String[] args) {
		Injector injector = Guice.createInjector(new EdgeNavigableModule());
		Factory<NavigableGraph<Integer, Integer, Integer>> graphFactory = DyadNavigableGraph.getFactory();
		Factory<Integer> vertexFactory = new Factory<Integer>() {
			int current = 0;

			@Override
			public Integer create() {
				return new Integer(this.current++);
			}
		};
		Factory<EdgeEntry<Integer, Integer, Integer>> edgeFactory = new Factory<EdgePair<Integer, Integer>>() {
			@Override
			public EdgeEntry<Integer, Integer, Integer> create() {
				return new EdgePair<Integer, Integer>((int) (Math.random() * 10));
			}

		};
		int numVertices = 100;
		double p = .25;
		KleinbergSmallWorldGenerator<Integer, EdgeEntry<Integer, Integer, Integer>> generator = new KleinbergSmallWorldGenerator<Integer, EdgeEntry<Integer, Integer, Integer>>(
		        graphFactory, vertexFactory, edgeFactory, 10, 10, .25);
		NavigableGraph<Integer, Integer, Integer> graph = (NavigableGraph<Integer, Integer, Integer>) generator
		        .create();
		final Integer firstDate = graph.getFirstKey();
		final Integer lastDate = graph.getLastKey();
		System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		Iterable<Interval<Integer>> iterable = IntervalWindowIterable.fromInteger(
		        IncrementIterable.fromIntegers(firstDate, lastDate, 1), 2);
		IterableNetworkAnalysis<Integer, Integer, Integer, Integer> analyze = new IterableNetworkAnalysis<Integer, Integer, Integer, Integer>(
		        iterable) {
			@Override
			public Integer doSubAnalysis(NavigableGraph<Integer, Integer, Integer> graph, Integer start, Integer stop) {
				System.out.println("interval: " + start + " -- " + stop + " : " + graph.getEdgeCount());
				return graph.getEdgeCount();
			}
		};
		List<Integer> results = analyze.analyze(graph);
		System.out.println("total " + graph.getEdgeCount());
		System.out.println("pre 5 " + graph.headNetwork(5).getEdgeCount());
		System.out.println("post 5 " + graph.tailNetwork(5).getEdgeCount());
		System.out.println("3-7 " + graph.subNetwork(3, 7).getEdgeCount());

		for (int numEdges : results) {
			System.out.print(numEdges + ", ");
		}
	}*/
}
