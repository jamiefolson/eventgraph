package net.sf.eventgraphj.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.IncrementIterable;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.IntervalWrapperIterable;
import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sf.eventgraphj.comparable.NavigableGraphModule;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Injector;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.util.Pair;

public class NavigableGraphBenchmark<V>{

	public static enum Implementation {
		EDGE(NavigableGraphModule.EDGE_NAVIGABLE), NODE(NavigableGraphModule.NODE_NAVIGABLE), BASIC(
		        NavigableGraphModule.BASIC_NAVIGABLE);

		private final Injector injector;

		Implementation(Injector inject) {
			this.injector = inject;
		}

		public Injector injector() {
			return this.injector;
		}
	}

	public static final int WAIT_TIME = 100;
	
	NavigableGraph<Long, V, String> baseGraph;
	private Class<V> vertexType;

	public NavigableGraphBenchmark(NavigableGraph<Long, V, String> baseGraph,Class<V> vertexType) {
		
		this.baseGraph = baseGraph;
		this.vertexType = vertexType;
	}
	
	public void benchmark(int numReps,long timescale){
				NavigableGraph<Long, V, String> graph = null;

				Collection edges = baseGraph.getEdges();

				for (int repetition = 0; repetition < numReps; repetition++) {
					System.out.println("Iteration: "+repetition);
					for (Implementation type : Implementation.values()) {
						System.out.println("\ttype: "+type.name());
						long constructionTime = System.nanoTime();

						try {
							graph = buildGraph(type.injector(), (Collection<EdgeEntry<Long, V, String>>) edges, vertexType);
						

						constructionTime = System.nanoTime() - constructionTime;

						recordConstructionTime(type.name().toLowerCase(), constructionTime);
						
						/*System.out.println("Successfully loaded graph of type: " + graph.getClass().getName()
						        + " with " + graph.getVertexCount() + " vertices, and " + graph.getEdgeCount()
						        + " edges in " + constructionTime / 1000 + " ms");
*/
						computeQueries(type.name().toLowerCase(), graph, timescale);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		
	

	public net.sf.eventgraphj.comparable.NavigableGraph<Long, V, String> buildGraph(Injector inject,
	        Collection<EdgeEntry<Long, V, String>> entries, Class<V> vertexType) throws IllegalArgumentException,
	        InstantiationException, IllegalAccessException, InvocationTargetException, FileNotFoundException,
	        SecurityException, NoSuchMethodException, ParseException {
		NavigableGraph<Long, V, String> graph = inject.getInstance(NavigableGraph.class);

		for (EdgeEntry<Long, V, String> entry : entries) {
			graph.addEdge(entry, entry.getFrom(), entry.getTo(), entry.getEdgetype());
		}

		return graph;
	}

	public <E> void computeQueries(String graphType, NavigableGraph<Long, V, E> comparableGraph,
	        Long timescale) {
		System.out.println("Expecting type: "+graphType+"\n\t"+comparableGraph.getClass());
		// System.out.println(exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		System.out.println("edgecount: " + comparableGraph.getEdgeCount());

		/*System.out.println("first half edgecount: "
		        + comparableGraph.headNetwork((long) (firstDate + (lastDate - firstDate) / 2.0)).getEdgeCount());
		System.out.println("test head edgecount: " + comparableGraph.headNetwork(12705l).getEdgeCount());
		System.out.println("test tail edgecount: " + comparableGraph.tailNetwork(-8895l).getEdgeCount());*/
		HashSet<Pair<V>> checked = new HashSet<Pair<V>>();
		for (EdgeEntry<Long, V, E> edge : comparableGraph.getEdges()) {
			Pair<V> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		// System.out.println(checked.size() + " pairs");

		VertexScorer<V, Integer> scorer = new DegreeScorer<V>(comparableGraph);
		for (V v : comparableGraph.getVertices()) {
			Integer score = scorer.getVertexScore(v);
			// System.out.print(v + ":" + score + ", ");
		}
		// System.out.println();

		Long interval = lastDate - firstDate;
		final long smallestInterval = timescale;

		/*		NetworkComparison<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> mseCompare = new SquaredError<Long, V, E>(
				        timescale, prior, priorStrength);*/

		for (long thisinterval = smallestInterval; thisinterval < interval; thisinterval *= 2) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / timescale) + "]");

			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, thisinterval));

			/*Iterable<Interval<Long>> iterable = IntervalWindowIterable.fromLong(IncrementIterable.fromLong(firstDate,
			        lastDate, smallestInterval), thisinterval);*/

			final HashMap<String, Writer> outMap = new HashMap<String, Writer>();

			final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());

			for (Interval<Long> currInterval : iterable) {
				final Long start = currInterval.getStart();
				final Long stop = currInterval.getFinish();
				long queryTime = System.nanoTime();
				final NavigableGraph<Long, V, E> subNet = comparableGraph.subNetwork(start, stop);
				queryTime = System.nanoTime() - queryTime;
				recordQueryTime(thisinterval,graphType, queryTime );
			}
		}
	}

	protected void recordQueryTime(long thisinterval, String graphType,
			long queryTime) {
		// TODO Auto-generated method stub
		
	}


	protected void recordConstructionTime(String lowerCase, long constructionTime) {
		// TODO Auto-generated method stub
		
	}
}
