package net.sourceforge.eventgraphj.analysis.iterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eventgraphj.analysis.AbstractNetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.NetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * A single {@code NetworkAnalysis} that simply combines multiple other analyses
 * into a single analysis.
 * 
 * Child analyses, added through {@code addAnalysis(...)} have their analysis
 * once for each subGraph analyzed. However, "coAnalysis" objects of type
 * {@code IterableNetworkAnalysis} instead have their {@code subAnalysis(..)}
 * run for each subGraph.
 * 
 * @author jfolson
 * 
 * @param <K>
 * @param <V>
 * @param <E>
 */
public class CompoundIterableNetworkAnalysis<K extends Comparable<K>, V, E> extends
        IterableNetworkAnalysis<K, V, E, Object> {

	List<NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>>> analyses;
	List<IterableNetworkAnalysis<K, V, E, ?>> coAnalyses;
	List<IterableNetworkAnalysis<K, V, E, ?>> subAnalyses;

	public CompoundIterableNetworkAnalysis(Iterable<Interval<K>> iterator) {
		super(iterator, null);
		analyses = new ArrayList<NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>>>();
		coAnalyses = new ArrayList<IterableNetworkAnalysis<K, V, E, ?>>();
		subAnalyses = new ArrayList<IterableNetworkAnalysis<K, V, E, ?>>();
	}

	@Override
	protected Object doSubAnalysis(NavigableGraph<K, V, E> graph, K start, K stop) {
		for (NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>> analysis : analyses) {
			analysis.analyze(graph);
		}
		for (IterableNetworkAnalysis<K, V, E, ?> analysis : subAnalyses) {
			analysis.doAnalysis(graph);
		}
		for (IterableNetworkAnalysis<K, V, E, ?> subAnalysis : coAnalyses) {
			subAnalysis.subAnalyze(graph, start, stop);
		}
		return null;
	}

	public void addAnalysis(NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>> analysis) {
		analyses.add(analysis);
	}

	public void addCoAnalysis(IterableNetworkAnalysis<K, V, E, ?> subAnalysis) {
		coAnalyses.add(subAnalysis);
	}

	public void close() throws IOException {
		if (output != null) {
			output.close();
		}
		for (NetworkAnalysis<V, EdgeEntry<K, E>, Graph<V, EdgeEntry<K, E>>> analysis : analyses) {
			if (analysis instanceof AbstractNetworkAnalysis) {
				((AbstractNetworkAnalysis) analysis).close();
			}
		}
		for (IterableNetworkAnalysis<K, V, E, ?> analysis : coAnalyses) {
			analysis.close();
		}
	}

	public void addAnalysis(IterableNetworkAnalysis<K, V, E, ?> analysis) {
		subAnalyses.add(analysis);
	}
}
