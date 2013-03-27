package net.sf.eventgraphj.analysis.iterable;

import net.sf.eventgraphj.analysis.NetworkAnalysis;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.NavigableGraph;
import edu.uci.ics.jung.graph.Graph;

public class SimpleIterableNetworkAnalysis<K extends Comparable<K>, V, E, R> extends
        IterableNetworkAnalysis<K, V, E, R> {
	NetworkAnalysis<V, EdgeEntry<K, V, E>, Graph<V, EdgeEntry<K, V, E>>, R> analysis;

	public SimpleIterableNetworkAnalysis(
	        NetworkAnalysis<V, EdgeEntry<K, V, E>, Graph<V, EdgeEntry<K, V, E>>, R> analysis,
	        Iterable<Interval<K>> iterator) {
		super(iterator);
		this.analysis = analysis;
	}

	@Override
	protected R doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop) {
		return analysis.analyze((Graph<V, EdgeEntry<K, V, E>>) graph);
	}

}
