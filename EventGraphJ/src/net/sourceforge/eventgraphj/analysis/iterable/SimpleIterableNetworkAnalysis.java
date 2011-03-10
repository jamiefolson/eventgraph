package net.sourceforge.eventgraphj.analysis.iterable;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eventgraphj.analysis.SimpleNetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;
import edu.uci.ics.jung.graph.Graph;

public class SimpleIterableNetworkAnalysis<K extends Comparable<K>, V, E, R> extends
        IterableNetworkAnalysis<K, V, E, R> {
	List<V> nodes;
	SimpleNetworkAnalysis<V, EdgeEntry<K, E>, R> analysis;

	public SimpleIterableNetworkAnalysis(SimpleNetworkAnalysis<V, EdgeEntry<K, E>, R> analysis,
	        Iterable<Interval<K>> iterator, Writer output) {
		super(iterator, output);
		this.analysis = analysis;
	}

	public SimpleIterableNetworkAnalysis(SimpleNetworkAnalysis<V, EdgeEntry<K, E>, R> analysis,
	        Iterable<Interval<K>> iterator) {
		this(analysis, iterator, null);
	}

	protected List<R> doAnalysis(NavigableGraph<K, V, E> graph) {
		this.nodes = new ArrayList<V>(graph.getVertices());
		return super.doAnalysis(graph);
	}

	@Override
	protected R doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop) {
		analysis.analyze((Graph<V, EdgeEntry<K, E>>) graph);

		return analysis.getResult();
	}

}
