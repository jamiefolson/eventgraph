package net.sourceforge.eventgraphj.analysis;

import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.Graph;

/**
 * Simple utility class for computing centralization scores. Will probably be
 * moved elsewhere.
 * 
 * @author jfolson
 * 
 */
public class ScoreCentralization {
	/**
	 * Returns a naive version of the centralization score for an arbitrary
	 * vertex scorer. This returns the total deviation over all nodes' scores in
	 * {@code graph} from the maximum observed score.
	 * 
	 * @param <V>
	 *            vertex class
	 * @param <E>
	 *            edge class
	 * @param <R>
	 *            score class
	 * @param scorer
	 * @param graph
	 * @return
	 */
	public static <V, E, R extends Number> double getCentralization(VertexScorer<V, R> scorer, Graph<V, E> graph) {
		double maxVal = Double.NEGATIVE_INFINITY;
		for (V vertex : graph.getVertices()) {
			R score = scorer.getVertexScore(vertex);
			assert (!Double.isNaN(score.doubleValue()));
			assert (score.doubleValue() != Double.POSITIVE_INFINITY);
			assert (score.doubleValue() >= 0);
			maxVal = Math.max(maxVal, score.doubleValue());
		}
		double centralization = 0;
		for (V vertex : graph.getVertices()) {
			R score = scorer.getVertexScore(vertex);
			centralization += maxVal - score.doubleValue();
		}
		return centralization;
	}
}
