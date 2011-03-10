package net.sourceforge.eventgraphj.analysis.compare;

import java.util.Collection;

import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;


import edu.uci.ics.jung.graph.util.Pair;

/**
 * This comparison uses the observed intervals for the two networks and
 * estimates the rate parameter for homogeneous Poisson processes describing the
 * pair for each network and then compute the information divergence between the
 * two processes.
 * 
 * 
 * This {@code NetworkComparison} interprets the {@code E} edge objects as
 * events spread through time, represented by the {@code K} keys. The the
 * {@code Pair<V>} queried, it then estimates the rate parameter for homogeneous
 * Poisson processes describing the pair for each network, taking for each
 * network the difference between the first and last {@code K} keys as the
 * elapsed time.
 * 
 * Estimates can be considered Bayesian maximum a posteriori estimates using a
 * Beta prior with alpha = {@code prior * priorStrength}, beta = {@code
 * (1-prior)*priorStrength}. Although this makes things awkward from a
 * statistical perspective, it allows the more intuitive interpretation as a
 * smoothing of the estimate, with {@code prior} being a defuault value and
 * {@code priorStrength} controlling the extent of the smoothing.
 * 
 * For the two estimated homogeneous Poisson processes, the information
 * divergence (gain/loss) between them is calulated. This information divergence
 * is calculated for an interval of length {@code scaling}. For homogeneous
 * Poisson processes, this is equivalent to calculating the information
 * divergence in the counting process over the time period.
 * 
 * @author jfolson
 * 
 * @param <K>
 * @param <V>
 * @param <E>
 */
public class PoissonNetworkInformation<K extends Number & Comparable<K>, V, E> extends
        DyadicComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> implements
        NetworkComparison<V, EdgeEntry<K, E>, NavigableGraph<K, V, E>> {
	double scaling = 1;
	final double prior;
	final double priorStrength;

	public PoissonNetworkInformation(double scaling, double prior, double priorStrength) {
		super();
		this.scaling = scaling;
		this.prior = prior;
		this.priorStrength = priorStrength;
	}

	public PoissonNetworkInformation() {
		this(1.0, 0.01, 0.10);
	}

	@Override
	public double compareDyads(NavigableGraph<K, V, E> thisGraph, NavigableGraph<K, V, E> otherGraph, Pair<V> pair) {
		double alpha = prior;
		double beta = priorStrength;
		double thisRate = 0;
		double thisStart = thisGraph.isBounded() ? thisGraph.getLowerBound().doubleValue() : thisGraph.getFirstKey()
		        .doubleValue();
		double thisStop = thisGraph.isBounded() ? thisGraph.getUpperBound().doubleValue() : thisGraph.getLastKey()
		        .doubleValue();
		double thisDuration = thisStop - thisStart;
		Collection<?> edges = thisGraph.findEdgeSet(pair.getFirst(), pair.getSecond());
		if (edges != null) {
			thisRate = (alpha + edges.size()) / (beta + thisDuration / scaling);
		}
		double otherRate = 0;
		double otherStart = otherGraph.isBounded() ? otherGraph.getLowerBound().doubleValue() : otherGraph
		        .getFirstKey().doubleValue();
		double otherStop = otherGraph.isBounded() ? otherGraph.getUpperBound().doubleValue() : otherGraph.getLastKey()
		        .doubleValue();
		double otherDuration = otherStop - otherStart;
		edges = otherGraph.findEdgeSet(pair.getFirst(), pair.getSecond());
		if (edges != null) {
			otherRate = (alpha + edges.size()) / (beta + otherDuration / scaling);
		}
		assert (thisRate > 0);
		assert (otherRate > 0);
		return otherRate - thisRate + thisRate * Math.log(thisRate / otherRate);

	}

	/**
	 * Generates an instance of the PoissonNetworkInformation comparison using
	 * the average over all pairs of nodes and all observed time to choose a
	 * value of {@code prior}.
	 * 
	 * For a graph with N vertices and M edges over time T, prior is chosen to
	 * be [M/(N*(N-1))]/[T/scale].
	 * 
	 * The {@code scale} parameter controls the numeric range of the parameter
	 * estimates, allowing users to limit problems of numerical precision. It
	 * also linearly scales the values of the information divergence, but does
	 * NOT change any relative values.
	 * 
	 * @param <K>
	 * @param <V>
	 * @param <E>
	 * @param comparableGraph
	 * @param scale
	 * @return
	 */
	public static <K extends Number & Comparable<K>, V, E> PoissonNetworkInformation<K, V, E> forGraph(
	        NavigableGraph<K, V, E> comparableGraph, K scale) {
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final K firstDate = comparableGraph.getFirstKey();
		final K lastDate = comparableGraph.getLastKey();

		Double interval = lastDate.doubleValue() - firstDate.doubleValue();
		final double prior = (((double) M) / (N * (N - 1))) / (interval / scale.doubleValue());
		final double priorStrength = 0.10;
		return new PoissonNetworkInformation<K, V, E>(scale.doubleValue(), prior, priorStrength);
	}

}
