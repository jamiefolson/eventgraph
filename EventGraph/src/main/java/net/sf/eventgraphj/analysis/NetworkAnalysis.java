package net.sf.eventgraphj.analysis;

import edu.uci.ics.jung.graph.Graph;

/**
 * Basic interface for analyzing a network. Used to enable easy repetition of an
 * analysis procedure over many different networks or many different subgraphs
 * in a single network over time.
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <E>
 * @param <G>
 */
public interface NetworkAnalysis<V, E, G extends Graph<V, E>, R> {

	public R analyze(G graph);

}
