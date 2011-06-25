package net.eventgraphj.rjava;

import net.sf.eventgraphj.comparable.DyadNavigableGraph;
import net.sf.eventgraphj.comparable.NavigableGraphModule;
import net.sf.eventgraphj.comparable.GraphProvider;
import net.sf.eventgraphj.comparable.MapProvider;
import net.sourceforge.jannotater.RJava;

public class EventGraph extends DyadNavigableGraph<Long, Long, Long> implements
		IEventGraph {

	public static class EventSubGraph extends
			DyadNavigableSubGraph<Long, Long, Long> implements IEventGraph {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public EventSubGraph(EventGraph parent, Long start, Long stop) {
			super(parent, start, stop);
		}

		public EventSubGraph(EventSubGraph parent, Long start, Long stop) {
			super(parent, start, stop);
		}

		@Override
		public void addEdge(long key, long from, long to) {
			super.addEdge(key, from, to);
		}

		@Override
		public void addVertex(long vertex) {
			super.addVertex(vertex);
		}

		@Override
		public IEventGraph subNetwork(long start, long stop) {
			return new EventSubGraph(this, start, stop);
		}

		@Override
		public IEventGraph tailNetwork(long start) {
			return new EventSubGraph(this, start, null);
		}

		@Override
		public IEventGraph headNetwork(long stop) {
			return new EventSubGraph(this, null, stop);
		}

		@Override
		public long[][] getPairs() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@RJava
	public EventGraph() {
		super(NavigableGraphModule.EDGE_NAVIGABLE.getInstance(MapProvider.class),
				NavigableGraphModule.EDGE_NAVIGABLE
						.getInstance(GraphProvider.class));
	}

	@Override
	public void addEdge(long key, long from, long to) {
		super.addEdge(key, from, to);
	}

	@Override
	public void addVertex(long vertex) {
		super.addVertex(vertex);
	}

	@Override
	public IEventGraph subNetwork(long start, long stop) {
		return new EventSubGraph(this, start, stop);
	}

	@Override
	public IEventGraph tailNetwork(long start) {
		return new EventSubGraph(this, start, null);
	}

	@Override
	public IEventGraph headNetwork(long stop) {
		return new EventSubGraph(this, null, stop);
	}

	@Override
	public long[][] getPairs() {
		// TODO Auto-generated method stub
		return null;
	}

}
