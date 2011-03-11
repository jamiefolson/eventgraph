package net.sourceforge.eventgraphj.analysis;

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
public class CompoundNetworkAnalysis<V, E, G extends Graph<V, E>> implements
        NetworkAnalysis<V, E, G, HashMap<String, Object>> {

	HashMap<String, NetworkAnalysis<V, E, ? super G, ?>> analyses;

	public CompoundNetworkAnalysis() {
		analyses = new HashMap<String, NetworkAnalysis<V, E, ? super G, ?>>();
	}

	@Override
	public HashMap<String, Object> analyze(G graph) {
		HashMap<String, Object> compoundResults = new HashMap<String, Object>();
		for (Entry<String, NetworkAnalysis<V, E, ? super G, ?>> entry : analyses.entrySet()) {
			NetworkAnalysis<V, E, ? super G, ?> analysis = entry.getValue();
			Object result = analysis.analyze(graph);
			compoundResults.put(entry.getKey(), result);
		}
		return compoundResults;
	}

	public void addAnalysis(String analysisId, NetworkAnalysis<V, E, ? super G, ?> analysis) {
		analyses.put(analysisId, analysis);
	}

}
