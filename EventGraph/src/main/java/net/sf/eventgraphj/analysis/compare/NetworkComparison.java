package net.sf.eventgraphj.analysis.compare;

import edu.uci.ics.jung.graph.Graph;

/**
 * A generic class for performing an analysis comparing two networks.
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <E>
 * @param <G>
 */
public interface NetworkComparison<V, E, G extends Graph<V, E>> {

	/**
	 * Compares two networks and returns a numeric value. Where such a
	 * distinction is meaningful, The {@code graph} is interpreted as the true
	 * network and {@code otherGraph} is interpreted as an
	 * estimate/approximation.
	 * 
	 * @param graph
	 * @param otherGraph
	 * @return
	 */
	public abstract double compare(G graph, G otherGraph);

}
