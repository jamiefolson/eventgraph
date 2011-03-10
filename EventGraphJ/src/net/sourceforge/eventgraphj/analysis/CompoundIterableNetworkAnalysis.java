package net.sourceforge.eventgraphj.analysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

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
public class CompoundIterableNetworkAnalysis<V, E, G extends Graph<V, E>> extends
        AbstractNetworkAnalysis<V, E, G, HashMap> {

	HashMap<String, NetworkAnalysis<V, E, G>> analyses;

	public CompoundIterableNetworkAnalysis() {
		super(null);
		analyses = new HashMap<String, NetworkAnalysis<V, E, G>>();
	}

	@Override
	protected HashMap doAnalysis(G graph) {
		HashMap compoundResults = new HashMap();
		for (Entry<String, NetworkAnalysis<V, E, G>> entry : analyses.entrySet()) {
			NetworkAnalysis<V, E, G> analysis = entry.getValue();
			analysis.analyze(graph);
		}
		return null;
	}

	public void addAnalysis(String analysisId, NetworkAnalysis<V, E, G> analysis) {
		analyses.add(analysis);
	}

	public void close() throws IOException {
		if (output != null) {
			output.close();
		}
		for (NetworkAnalysis<V, E, G> analysis : analyses) {
			if (analysis instanceof AbstractNetworkAnalysis) {
				((AbstractNetworkAnalysis) analysis).close();
			}
		}
	}

}
