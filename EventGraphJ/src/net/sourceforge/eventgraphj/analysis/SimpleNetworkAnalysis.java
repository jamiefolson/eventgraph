package net.sourceforge.eventgraphj.analysis;

import java.io.Writer;

import edu.uci.ics.jung.graph.Graph;

/**
 * This class exists merely as an indicator that a NetworkAnalysis object
 * accepts any Graph<V,E> rather than requiring a specific graph class
 * 
 * @author jfolson
 * 
 * @param <V>
 *            vertex class
 * @param <E>
 *            edge class
 * @param <R>
 *            result class
 */
public abstract class SimpleNetworkAnalysis<V, E, R> extends AbstractNetworkAnalysis<V, E, Graph<V, E>, R> implements
        NetworkAnalysis<V, E, Graph<V, E>> {

	public SimpleNetworkAnalysis(Writer output) {
		super(output);
	}

	public R getResult() {
		return result;
	}
}
