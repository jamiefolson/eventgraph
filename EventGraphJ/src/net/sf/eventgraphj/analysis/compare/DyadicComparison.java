package net.sf.eventgraphj.analysis.compare;

import java.util.HashSet;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * A kind of {@code NetworkComparison} that sums up difference values for all
 * {@code Pair<V>} of vertices that exist in either of the two networks.
 * 
 * @author jfolson
 * 
 * @param <V>
 * @param <E>
 * @param <G>
 */
public abstract class DyadicComparison<V, E, G extends Graph<V, E>> implements NetworkComparison<V, E, G> {

	public DyadicComparison() {}

	public double compare(G thisGraph, G otherGraph) {
		double divergence = 0;
		HashSet<Pair<V>> checked = new HashSet<Pair<V>>();
		for (E edge : thisGraph.getEdges()) {
			Pair<V> pair = thisGraph.getEndpoints(edge);
			if (checked.contains(pair))
				continue;

			divergence += this.compareDyads(thisGraph, otherGraph, pair);

			checked.add(pair);
		}

		for (E edge : otherGraph.getEdges()) {
			Pair<V> pair = otherGraph.getEndpoints(edge);
			if (checked.contains(pair))
				continue;

			divergence += this.compareDyads(thisGraph, otherGraph, pair);

			checked.add(pair);
		}
		return divergence;
	}

	public abstract double compareDyads(G thisGraph, G otherGraph, Pair<V> pair);

}
