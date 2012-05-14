package net.sf.eventgraphj.rjava;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.JRI.JRIEngine;

import cern.colt.Arrays;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.Lists;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.util.Pair;

import net.sf.eventgraphj.comparable.DyadNavigableGraph;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.IncrementIterable;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.IntervalWrapperIterable;
import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sf.eventgraphj.comparable.NavigableGraphModule;
import net.sf.eventgraphj.comparable.GraphProvider;
import net.sf.eventgraphj.comparable.MapProvider;
import net.sf.eventgraphj.comparable.NavigableGraphModule.EdgeNavigableModule;
import net.sf.eventgraphj.tools.LoadGraph;
import net.sf.eventgraphj.tools.BenchmarkStatistics.Implementation;
import net.sourceforge.jannotater.RJava;

public class EventGraph extends DyadNavigableGraph<Long, Long, Long> implements
		IEventGraph {

	public static class EventGraphModule extends NavigableGraphModule {
		@Override
		protected void configure() {
			super.configure();
			this.bind(NavigableGraph.class).to(EventGraph.class);
		}
	}

	
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
		public void addEdge(double key, double from, double to) {
			super.addEdge((long)key,(long) from,(long) to);
		}

		@Override
		public void addVertex(double vertex) {
			super.addVertex((long)vertex);
		}

		@Override
		public IEventGraph subNetwork(double start, double stop) {
			return new EventSubGraph(this, (long)start, (long)stop);
		}

		@Override
		public IEventGraph tailNetwork(double start) {
			return new EventSubGraph(this,(long) start, null);
		}

		@Override
		public IEventGraph headNetwork(double stop) {
			return new EventSubGraph(this, null,(long) stop);
		}

		@Override
		public double[][] getPairArray() {
			Collection<Pair<Long>> objPairs = this.getPairs();
			double[][] pairMatrix = new double[objPairs.size()][2];
			int pairIdx = 0;
			for (Pair<Long> pair : objPairs){
				pairMatrix[pairIdx][0] = pair.getFirst();
				pairMatrix[pairIdx][1] = pair.getSecond();
				pairIdx++;
			}
			return pairMatrix;
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
	
	public EventGraph(REXP data,REXP from,REXP to,REXP time){
		this();
		try {
			this.addEvents(data, from, to, time);
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addEdge(double key, double from, double to) {
		super.addEdge((long)key,(long) from,(long) to);
	}

	@Override
	public void addVertex(double vertex) {
		super.addVertex((long)vertex);
	}

	@Override
	public IEventGraph subNetwork(double start, double stop) {
		return new EventSubGraph(this, (long)start, (long)stop);
	}

	@Override
	public IEventGraph tailNetwork(double start) {
		return new EventSubGraph(this,(long) start, null);
	}

	@Override
	public IEventGraph headNetwork(double stop) {
		return new EventSubGraph(this, null,(long) stop);
	}

	@Override
	public double[][] getPairArray() {
		Collection<Pair<Long>> objPairs = this.getPairs();
		double[][] pairMatrix = new double[objPairs.size()][2];
		int pairIdx = 0;
		for (Pair<Long> pair : objPairs){
			pairMatrix[pairIdx][0] = pair.getFirst();
			pairMatrix[pairIdx][1] = pair.getSecond();
			pairIdx++;
		}
		return pairMatrix;
	}
	
	
	
	@RJava
	public static EventGraph fromCSV(String filename, int fromColumn, int toColumn, int dateColumn,boolean hasHeader,
			SimpleDateFormat dateFormat,
			String separatorStr) throws IllegalArgumentException, FileNotFoundException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException{
		return (EventGraph) LoadGraph.loadCsvGraph(Guice
				.createInjector(new EventGraphModule()),filename, fromColumn, toColumn, dateColumn, dateFormat, Long.class,
		        hasHeader, separatorStr);
	}
	
	@RJava
	public void addEvents(REXP data,REXP from,REXP to,REXP time) throws REXPMismatchException {
		int[] fromData = getVector(data,from).asIntegers();
		int[] toData = getVector(data,to).asIntegers();
		int[] timeData = getVector(data,time).asIntegers();
		
		
		addEvents(fromData, toData, timeData);
	}

	private void addEvents(int[] fromData, int[] toData, int[] timeData) {
		int n = fromData.length;
		
		for (int i=0;i<n;i++){
			addEdge((long)timeData[i], (long)fromData[i], (long)toData[i]);
		}
	}
	
	
	@RJava
	public void addEvent(REXP from,REXP to,REXP time) throws REXPMismatchException {
		
			addEdge((long)time.asInteger(), (long)from.asInteger(), (long)to.asInteger());
			System.out.println("adding edge: "+time.asInteger()+", "+from.asInteger() +"-->"+to.asInteger());
	}

	private static REXPVector getVector(REXP data, REXP key) {
		try {
			REXPVector vector = null;
			if (data.isList()) {
				RList dataList = null;
				dataList = data.asList();
				if (dataList.isNamed()) {
					if (key.isString()) {
						vector = (REXPVector) dataList.at(key.asString());
						return vector;//.asDoubles();
					}
				} 
				
				vector = (REXPVector) dataList.at(key.asInteger()-1);
				return vector;//.asDoubles();
			} else if (data.isVector()) {
/*				try {
					RList dataList = null;
					dataList = data.asList();
					vector = dataList.at(key.asInteger());
					return vector.asDoubles();
				} catch (REXPMismatchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
*/				/*double[][] matrix = data.asDoubleMatrix();
				return matrix[key.asInteger()-1];*/
			}else{
				System.out.println("Not a vector or list, what is it? "
						+ data.getClass());

			}
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@RJava(postCall=".robj")
	public REXP benchmark(REXP numRepsREXP,REXP scaleREXP) throws REXPMismatchException, REngineException{
		int numReps = numRepsREXP.asInteger();
		int scale = scaleREXP.asInteger();
		
		System.out.println("reps: "+numReps+"\nscale: "+scale);
		DataFrameBenchmark benchmark = new DataFrameBenchmark(this,Long.class);
		benchmark.benchmark((int)numReps, (long)scale);
		REXP result = new REXPList(benchmark.close());
		REngine engine = new JRIEngine(Rengine.getMainEngine());
		engine.assign(".robj", result);
		return result;
	}
	
	public static void computeBenchmarks(REXP data,REXP from,REXP to,REXP time,REXP nreps,REXP scale) throws REXPMismatchException, IOException {
		int[] fromData = getVector(data,from).asIntegers();
		int[] toData = getVector(data,to).asIntegers();
		int[] timeData = getVector(data,time).asIntegers();
		
		int numReps = nreps.asInteger();

		
		int runIdx = 0;
		double[] construction = new double[numReps*Implementation.values().length];
		double[] query = new double[numReps*Implementation.values().length];
		String[] types = new String[numReps*Implementation.values().length];
		int n = construction.length;
		RList allQueries = new RList();
		for (int repetition = 0; repetition < numReps; repetition++) {
			for (Implementation type : Implementation.values()) {

				long constructionTime = System.nanoTime();

				NavigableGraph<Long, Long, Long> graph = type.injector().getInstance(NavigableGraph.class);

				for (int i=0;i<n;i++){
					graph.addEdge((long)timeData[i], (long)fromData[i], (long)toData[i]);
				}
				constructionTime = System.nanoTime() - constructionTime;

				construction[runIdx] = constructionTime;
				types[runIdx] = type.name();

				System.out.println("Successfully loaded graph of type: " + graph.getClass().getName()
				        + " with " + graph.getVertexCount() + " vertices, and " + graph.getEdgeCount()
				        + " edges in " + constructionTime / 1000 + " ms");

				RList queries = computeQueries(graph, scale.asDouble());
				if (allQueries == null){
					allQueries = queries;
				}else{
					allQueries.at("bins");
				}
			}
		}
	}

	private static NavigableGraph<Long, ?, String> buildGraph(
			Injector injector, int[] fromData, int[] toData, int[] timeData) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <V, E> RList computeQueries(NavigableGraph<Long, V, E> comparableGraph,
	        double timescale) throws IOException, REXPMismatchException {

		// System.out.println(exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		HashSet<Pair<V>> checked = new HashSet<Pair<V>>();
		for (EdgeEntry<Long, V, E> edge : comparableGraph.getEdges()) {
			Pair<V> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		
		Long interval = lastDate - firstDate;
		final double smallestInterval = timescale;

		/*		NetworkComparison<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> mseCompare = new SquaredError<Long, V, E>(
				        timescale, prior, priorStrength);*/
		List<Long> bins = new ArrayList<Long>();
		List<Long> times = new ArrayList<Long>();
		for (double thisinterval = smallestInterval; thisinterval < interval; thisinterval *= 2) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / timescale) + "]");

			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, (long)thisinterval));

			/*Iterable<Interval<Long>> iterable = IntervalWindowIterable.fromLong(IncrementIterable.fromLong(firstDate,
			        lastDate, smallestInterval), thisinterval);*/

			final HashMap<String, Writer> outMap = new HashMap<String, Writer>();
			double sumTime = 0;
			final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());
			for (Interval<Long> currInterval : iterable) {
				final Long start = currInterval.getStart();
				final Long stop = currInterval.getFinish();
				long queryTime = System.nanoTime();
				final NavigableGraph<Long, V, E> subNet = comparableGraph.subNetwork(start, stop);
				queryTime = System.nanoTime() - queryTime;
				sumTime += queryTime;
			}
			bins.add((long)thisinterval);
			times.add((long)sumTime);
		}
		Collection<REXP> list = new ArrayList<REXP>();
		double[] vals = new double[bins.size()];
		for (int idx = 0;idx<vals.length;idx++){
			vals[idx] = bins.get(idx);
		}
		list.add(new REXPDouble(vals));
		for (int idx = 0;idx<vals.length;idx++){
			vals[idx] = times.get(idx);
		}
		list.add(new REXPDouble(vals));
		Collection<String> names = new ArrayList<String>();
		names.add("bins");
		names.add("times");
		return new RList(list,names);
	}
}
