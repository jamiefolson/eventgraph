package net.eventgraphj.rjava;

import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sourceforge.jannotater.RJava;

public interface IEventGraph extends NavigableGraph<Long, Long, Long> {
	@RJava
	public void addEdge(long key, long from, long to);

	@RJava
	public void addVertex(long vertex);

	@RJava
	public IEventGraph subNetwork(long start, long stop);

	@RJava
	public IEventGraph tailNetwork(long start);

	@RJava
	public IEventGraph headNetwork(long stop);

	@RJava
	long[][] getPairs();
}
