package net.sourceforge.eventgraphj.analysis;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
public abstract class VertexScoreAnalysis<V, E, R> extends SimpleNetworkAnalysis<V, E, List<R>> {

	final private List<V> nodes;
	private boolean headerIsPrinted = false;

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
	public VertexScoreAnalysis(Writer output, List<V> nodes) {
		super(output);
		this.nodes = nodes;
	}

	/**
	 * Same as {@code VertexScoreAnalysis(null output, nodes)}
	 * 
	 * @param nodes
	 */
	public VertexScoreAnalysis(List<V> nodes) {
		this(null, nodes);
	}

	/**
	 * Uses the set of vertices in {@code graph} to determine the population for
	 * which to compute scores, and {@code null} for {@code output}.
	 * 
	 * @param graph
	 */
	public VertexScoreAnalysis(Graph<V, E> graph) {
		this(null, new ArrayList<V>(graph.getVertices()));
	}

	@Override
	protected List<R> doAnalysis(Graph<V, E> graph) {
		VertexScorer<V, R> scorer = createScorer(graph);
		List<R> values = new ArrayList<R>();
		for (V vertex : nodes) {

			R score = null;
			try {
				score = scorer.getVertexScore(vertex);
			} catch (IllegalArgumentException e) { // thrown if you ask for the
				// score of a vertex not
				// present in the target
				// graph
				e.printStackTrace();
			}
			values.add(score);
		}

		return values;
	}

	public void printHeader(Writer output) throws IOException {
		if (headerIsPrinted)
			System.err.println("Warning: Header printed twice");
		int i = 0;
		output.write(nodes.get(i).toString());
		for (i = 1; i < nodes.size(); i++) {
			output.write(", " + nodes.get(i).toString());
		}
		output.write("\n");
		this.headerIsPrinted = true;

	}

	protected void writeResult(List<R> result, Writer output) throws IOException {
		if (!headerIsPrinted) {
			printHeader(output);

		}
		String prepend = "";
		for (R val : result) {
			if (val == null)
				output.write(prepend + "NULL");
			else
				output.write(prepend + val.toString());
			prepend = ", ";
		}
		output.write("\n");

	}

	public abstract VertexScorer<V, R> createScorer(Graph<V, E> graph);

	public static <V, E> VertexScoreAnalysis<V, E, Double> newBetweennessAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E, Double>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new BetweennessCentrality<V, E>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E, Integer> newDegreeAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E, Integer>(graph) {
			@Override
			public VertexScorer<V, Integer> createScorer(Graph<V, E> graph) {
				return new DegreeScorer<V>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E, Double> newClosenessAnalysis(Graph<V, E> graph) {
		return new VertexScoreAnalysis<V, E, Double>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new InverseDistanceCentralityScorer<V, E>(graph);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E, Double> newPageRankAnalysis(Graph<V, E> graph,
	        final double restartProbability) {
		return new VertexScoreAnalysis<V, E, Double>(graph) {
			@Override
			public VertexScorer<V, Double> createScorer(Graph<V, E> graph) {
				return new PageRank<V, E>(graph, restartProbability);
			}

		};
	}

	public static <V, E> VertexScoreAnalysis<V, E, Double> newPageRankAnalysis(Graph<V, E> graph) {
		return newPageRankAnalysis(graph, .15);
	}
}
