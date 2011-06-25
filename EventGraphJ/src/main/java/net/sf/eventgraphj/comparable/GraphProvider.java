package net.sf.eventgraphj.comparable;

import java.io.Serializable;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public interface GraphProvider {
	public <V, E> Graph<V, E> get();

	public static class SparseGraphProvider implements GraphProvider, Serializable {

		@Override
		public <V, E> Graph<V, E> get() {
			return new SparseGraph<V, E>();
		}

	}
}
