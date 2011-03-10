package net.sourceforge.eventgraphj.analysis.iterable;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.eventgraphj.analysis.AbstractNetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.NetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.IncrementIterable;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.IntervalWindowIterable;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;
import net.sourceforge.eventgraphj.comparable.SimpleNavigableGraph;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.random.KleinbergSmallWorldGenerator;

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
public abstract class IterableNetworkAnalysis<K extends Comparable<K>, V, E, R> extends
        AbstractNetworkAnalysis<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>, List<R>> implements
        NetworkAnalysis<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> {
	Iterable<Interval<K>> iterable;
	NavigableGraph<K, V, E> graph;

	// protected List<R> results;

	public IterableNetworkAnalysis(Iterable<Interval<K>> iterator, Writer output) {
		super(output);
		this.iterable = iterator;
	}

	public IterableNetworkAnalysis(Iterable<Interval<K>> iterator) {
		this(iterator, null);
	}

	protected List<R> doAnalysis(NavigableGraph<K, V, E> graph) {
		List<R> results = new ArrayList<R>();
		Iterator<Interval<K>> iterator = iterable.iterator();
		K start, finish;
		Interval<K> interval = null;
		while (iterator.hasNext()) {
			interval = iterator.next();
			// System.out.println(interval);
			//System.out.println("Compare Interval: " + interval.toString());
			start = interval.getStart();
			finish = interval.getFinish();
			long t1 = System.nanoTime();
			R result = doSubAnalysis(graph.subNetwork(start, finish), start, finish);
			long t2 = System.nanoTime();
			//System.out.println((t2 - t1) / 1000000000.0);
			results.add(result);

		}
		return results;
	}

	final protected void writeResult(List<R> result, Writer output) throws IOException {
		for (R val : result) {
			writeResultInstance(val, output);
			output.write("\n");
		}
	}

	protected void writeResultInstance(R result, Writer output) throws IOException {
		if (result instanceof Collection) {
			String prepend = "";
			for (Object val : (Collection) result) {
				output.write(prepend + val.toString());
				prepend = ", ";
			}
		} else {
			output.write(result.toString());
		}
	}

	public List<R> getResult() {
		return result;
	}

	public R subAnalyze(NavigableGraph<K, V, E> graph, K start, K stop) {
		R result = doSubAnalysis(graph, start, stop);
		if (result != null && output != null) {
			try {
				writeResultInstance(result, output);
				output.write("\n");
				output.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
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

	public static void main(String[] args) {
		Factory<NavigableGraph<Integer, Integer, Integer>> graphFactory = SimpleNavigableGraph.getFactory();
		Factory<Integer> vertexFactory = new Factory<Integer>() {
			int current = 0;

			public Integer create() {
				return new Integer(current++);
			}
		};
		Factory<EdgeEntry<Integer, Integer>> edgeFactory = new Factory<EdgeEntry<Integer, Integer>>() {
			public EdgeEntry<Integer, Integer> create() {
				return new EdgeEntry<Integer, Integer>((int) (Math.random() * 10));
			}

		};
		int numVertices = 100;
		double p = .25;
		KleinbergSmallWorldGenerator<Integer, EdgeEntry<Integer, Integer>> generator = new KleinbergSmallWorldGenerator<Integer, EdgeEntry<Integer, Integer>>(
		        graphFactory, vertexFactory, edgeFactory, 10, 10, .25);
		NavigableGraph<Integer, Integer, Integer> graph = (NavigableGraph<Integer, Integer, Integer>) generator
		        .create();
		final Integer firstDate = graph.getFirstKey();
		final Integer lastDate = graph.getLastKey();
		System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		Iterable<Interval<Integer>> iterable = IntervalWindowIterable.fromInteger(IncrementIterable.fromIntegers(
		        firstDate, lastDate, 1), 2);
		IterableNetworkAnalysis<Integer, Integer, Integer, Integer> analyze = new IterableNetworkAnalysis<Integer, Integer, Integer, Integer>(
		        iterable) {
			@Override
			public Integer doSubAnalysis(NavigableGraph<Integer, Integer, Integer> graph, Integer start, Integer stop) {
				System.out.println("interval: " + start + " -- " + stop + " : " + graph.getEdgeCount());
				return graph.getEdgeCount();
			}
		};
		analyze.analyze(graph);
		System.out.println("total " + graph.getEdgeCount());
		System.out.println("pre 5 " + graph.headNetwork(5).getEdgeCount());
		System.out.println("post 5 " + graph.tailNetwork(5).getEdgeCount());
		System.out.println("3-7 " + graph.subNetwork(3, 7).getEdgeCount());

		for (int numEdges : analyze.getResult()) {
			System.out.print(numEdges + ", ");
		}
	}
}
