package net.sourceforge.eventgraphj.analysis.iterable;

import net.sourceforge.eventgraphj.analysis.NetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;
import edu.uci.ics.jung.graph.Graph;

public class SimpleIterableNetworkAnalysis<K extends Comparable<K>, V, E, R> extends
        IterableNetworkAnalysis<K, V, E, R> {
	NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>, R> analysis;

	public SimpleIterableNetworkAnalysis(NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>, R> analysis,
	        Iterable<Interval<K>> iterator) {
		super(iterator);
		this.analysis = analysis;
	}

	@Override
	protected R doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop) {
		return analysis.analyze((Graph<V, EdgeEntry<K, E>>) graph);
	}

}
