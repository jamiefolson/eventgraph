package net.sf.eventgraphj.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.Graph;

/**
 * This class enables consistent evaluation of multiple graphs by reporting the
 * {@code VertexScorer} results for a specific set of nodes.
 * 
 * Because, for example, a sequence of networks may vary slightly in the
 * population of vertices, this class uses an ordered list of nodes to ensure
 * that the resulting {@code List<R>} of scores has a consistent ordering such
 * that the same position for different network analysis results refers to the
 * same vertex.
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <E>
 * @param <R>
 */
public abstract class VertexScoreAnalysis<V, E> implements NetworkAnalysis<V, E, Graph<V, E>, RealVector> {

	final private List<V> nodes;

	/**
	 * Creates a network analysis that computes the vertex scores for vertices
	 * in {@code nodes} and records the results in {@code output}.
	 * 
	 * A value of {@code null} for {@code output} will simply compute the
	 * analysis and return the result using {@code getResult()}.
	 * 
	 * @param output
	 * @param nodes
	 */
	public VertexScoreAnalysis(List<V> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Uses the set of vertices in {@code graph} to determine the population for
	 * which to compute scores, and {@code null} for {@code output}.
	 * 
	 * @param graph
	 */
	public VertexScoreAnalysis(Graph<V, E> graph) {
		this(new ArrayList<V>(graph.getVertices()));
	}

	@Override
	public RealVector analyze(Graph<V, E> graph) {
		VertexScorer<V, ? extends Number> scorer = createScorer(graph);
		RealVector values = new ArrayRealVector(nodes.size());
		int nodeIdx = 0;
		for (V vertex : nodes) {

			Number score = null;
			try {
				score = scorer.getVertexScore(vertex);
			} catch (IllegalArgumentException e) { // thrown if you ask for the
				// score of a vertex not
				// present in the target
				// graph
				e.printStackTrace();
			}
			if (score == null) {
				values.setEntry(nodeIdx, 0);
			} else {
				//System.out.println(score + ", ");
				values.setEntry(nodeIdx, score.doubleValue());
			}
			nodeIdx++;
		}

		return values;
	}

	public abstract VertexScorer<V, ? extends Number> createScorer(Graph<V, E> graph);

	public static <V, E> VertexScoreAnalysis<V, E> newBetweennessAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new BetweennessCentrality<V, E>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E> newDegreeAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E>(graph) {
			@Override
			public VertexScorer<V, Integer> createScorer(Graph<V, E> graph) {
				return new DegreeScorer<V>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E> newClosenessAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new InverseDistanceCentralityScorer<V, E>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E> newPageRankAnalysis(Graph<V, E> graph,
	        final double restartProbability) {
		return new VertexScoreAnalysis<V, E>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new PageRank<V, E>(graph, restartProbability);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E> newPageRankAnalysis(Graph<V, E> graph) {
		return newPageRankAnalysis(graph, .15);
	}
}
