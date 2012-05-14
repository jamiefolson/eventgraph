package net.sf.eventgraphj.rjava;

import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sourceforge.jannotater.RJava;

public interface IEventGraph extends NavigableGraph<Long, Long, Long> {
	@RJava
	public void addEdge(double key, double from, double to);

	@RJava
	public void addVertex(double vertex);

	@RJava
	public IEventGraph subNetwork(double start, double stop);

	@RJava
	public IEventGraph tailNetwork(double start);

	/**
	 * @param stop
	 * @return
	 */
	@RJava
	public IEventGraph headNetwork(double stop);

	@RJava(rName = "getPairs",postCall = "{t(sapply("+RJava.RETURN_VARIABLE+",.jevalArray))}")
	double[][] getPairArray();
	
	
}
