package net.sf.eventgraphj.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.eventgraphj.analysis.CompoundNetworkAnalysis;
import net.sf.eventgraphj.analysis.NetworkAnalysis;
import net.sf.eventgraphj.analysis.VertexScoreAnalysis;
import net.sf.eventgraphj.analysis.compare.PoissonNetworkInformation;
import net.sf.eventgraphj.analysis.iterable.AggregationComparison;
import net.sf.eventgraphj.analysis.iterable.SimpleAggregationComparison;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.IncrementIterable;
import net.sf.eventgraphj.comparable.Interval;
import net.sf.eventgraphj.comparable.IntervalWrapperIterable;
import net.sf.eventgraphj.comparable.LoadGraph;
import net.sf.eventgraphj.comparable.NavigableGraph;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealVector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class SnapshotStatistics {

	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String TIMESCALE = "scale";
	public static final String OUTPUT = "output";
	public static final String POOL_SIZE = "poolSize";
	public static final int WAIT_TIME = 100;

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(new Option(HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
		        .withDescription("use given file to construct graph").create(FILE));
		options.addOption(OptionBuilder
		        .withArgName("length")
		        .hasArg()
		        .withDescription(
		                "use given length of time as the basis for constructing exponentially larger temporal snapshots")
		        .create(TIMESCALE));
		options.addOption(OptionBuilder
		        .withArgName("path")
		        .hasArg()
		        .withDescription(
		                "put generated files into the given path, files contained there will be overwritten on collisions")
		        .create(OUTPUT));
		options.addOption(OptionBuilder.withArgName("n").hasArg()
		        .withDescription("use the given number of threads to run analysis").create(POOL_SIZE));

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine line;
		// parse the command line arguments
		try {
			line = parser.parse(options, args);

			if (line.hasOption(HELP)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SnapshotStatistics -f <file>  [...]", options);
			} else if (!line.hasOption(FILE)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SnapshotStatistics -f <file>  [...]", options);
			} else {
				String filename = line.getOptionValue(FILE);
				Long timescale = Long.parseLong(line.getOptionValue(TIMESCALE));

				String output = filename.substring(0, filename.lastIndexOf('.'));

				if (line.hasOption(OUTPUT))
					output = line.getOptionValue(OUTPUT);

				Integer poolSize = 0;
				if (line.hasOption(POOL_SIZE)) {
					poolSize = Integer.parseInt(line.getOptionValue(POOL_SIZE));
				}
				final NavigableGraph<Long, ?, ?> comparableGraph = LoadGraph.loadBinaryJungGraph(filename);
				computeStatistics(comparableGraph, output, timescale, poolSize);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static <V, E> void computeStatistics(final NavigableGraph<Long, V, E> comparableGraph, String outputBase,
	        Long timescale, Integer poolSize) throws IOException {

		ThreadPoolExecutor exec = null;
		CompletionService<Map<String, RealVector>> completionService = null;
		if ((poolSize != null) && (poolSize > 0))
			exec = new ThreadPoolExecutor(poolSize, poolSize, Long.MAX_VALUE, TimeUnit.NANOSECONDS,
			        new LinkedBlockingQueue<Runnable>());
		completionService = new ExecutorCompletionService<Map<String, RealVector>>(exec);
		//(ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);

		//System.out.println(exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
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
		//System.out.println(checked.size() + " pairs");

		/*VertexScorer<V, Integer> scorer = new DegreeScorer<V>(comparableGraph);
		for (V v : comparableGraph.getVertices()) {
			Integer score = scorer.getVertexScore(v);
			System.out.print(v + ":" + score + ", ");
		}
		System.out.println();*/

		Long interval = lastDate - firstDate;
		final long smallestInterval = timescale;

		Iterable<Interval<Long>> innerIterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(
		        firstDate, lastDate, smallestInterval));

		PoissonNetworkInformation<Long, V, E> infoLossCompare = PoissonNetworkInformation.forGraph(comparableGraph,
		        timescale);

		/*		NetworkComparison<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> mseCompare = new SquaredError<Long, V, E>(
				        timescale, prior, priorStrength);*/

		for (long thisinterval = smallestInterval; thisinterval < interval; thisinterval *= 2) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / timescale) + "]");

			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, thisinterval));

			/*Iterable<Interval<Long>> iterable = IntervalWindowIterable.fromLong(IncrementIterable.fromLong(firstDate,
			        lastDate, smallestInterval), thisinterval);*/

			final CompoundNetworkAnalysis<V, EdgeEntry<Long, V, E>, NavigableGraph<Long, V, E>> allAnalyses = new CompoundNetworkAnalysis<V, EdgeEntry<Long, V, E>, NavigableGraph<Long, V, E>>();

			final HashMap<String, Writer> outMap = new HashMap<String, Writer>();

			NetworkAnalysis<V, EdgeEntry<Long, V, E>, NavigableGraph<Long, V, E>, RealVector> infoAnalyze = new SimpleAggregationComparison<Long, V, E>(
			        innerIterable, infoLossCompare);

			allAnalyses.addAnalysis("InformationLoss", infoAnalyze);
			outMap.put("InformationLoss", new FileWriter(new File(outputBase + "_" + (thisinterval) + "_infoloss.txt")));

			//Writer mseoutput = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-mse.txt"));
			// output = new PrintWriter(System.out);

			NetworkAnalysis<V, EdgeEntry<Long, V, E>, NavigableGraph<Long, V, E>, List<RealVector>> mseAnalyze = new AggregationComparison<Long, V, E>(
			        iterable, innerIterable, infoLossCompare);

			//allAnalyses.addSubAnalysis(mseAnalyze);

			final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());

			NetworkAnalysis<V, EdgeEntry<Long, V, E>, Graph<V, EdgeEntry<Long, V, E>>, RealVector> analysis;

			analysis = VertexScoreAnalysis.newDegreeAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Degree", analysis);
			outMap.put("Degree", new FileWriter(new File(outputBase + "_" + (thisinterval) + "_degree.txt")));

			analysis = VertexScoreAnalysis.newClosenessAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Closeness", analysis);
			outMap.put("Closeness", new FileWriter(new File(outputBase + "_" + (thisinterval) + "_closeness.txt")));

			analysis = VertexScoreAnalysis.newBetweennessAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Betweenness", analysis);
			outMap.put("Betweenness", new FileWriter(new File(outputBase + "_" + (thisinterval) + "_betweenness.txt")));

			analysis = VertexScoreAnalysis.newPageRankAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Pagerank", analysis);
			outMap.put("Pagerank", new FileWriter(new File(outputBase + "_" + (thisinterval) + "_pagerank.txt")));

			int futureCount = 0;
			for (Interval<Long> currInterval : iterable) {

				final Long start = currInterval.getStart();
				final Long stop = currInterval.getFinish();
				Callable<Map<String, RealVector>> worker = new Callable<Map<String, RealVector>>() {

					@Override
					public HashMap<String, RealVector> call() {
						final NavigableGraph<Long, V, E> subNet = comparableGraph.subNetwork(start, stop);
						System.out.println("sub network from " + start + " to " + stop + " has "
						        + subNet.getEdgeCount() + " edges");

						HashMap<String, RealVector> results = allAnalyses.analyze(subNet);
						/*for (Entry<String, ?> entry : results.entrySet()) {
						    //System.out.println("printing");
						    Writer output = outMap.get(entry.getKey());
						    synchronized (output) {
						        try {
							        output.write(entry.getKey() + ", " + start + ", " + stop + ", ");
							        writeResults(entry.getValue(), output);
							        output.write("\n");
							        output.flush();
						        } catch (IOException e) {
							        // TODO Auto-generated catch block
							        e.printStackTrace();
						        }
						        //System.out.println("printed");

						    }

						}*/
						return results;

					}
				};
				if (completionService == null) {
					Map<String, RealVector> results;
					try {
						results = worker.call();

						for (Entry<String, RealVector> entry : results.entrySet()) {
							//System.out.println("printing");
							Writer output = outMap.get(entry.getKey());
							try {
								//output.write(entry.getKey() + ", ");// + start + ", " + stop + ", ");
								writeResults(entry.getValue(), output);
								output.write("\n");
								output.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//System.out.println("printed");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} else {
					try {
						completionService.submit(worker);
						futureCount++;
					} catch (RejectedExecutionException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("Executing: " + currInterval);

			}
			while (futureCount > 0) {
				Future<Map<String, RealVector>> resultsFuture;
				try {
					resultsFuture = completionService.take();
					futureCount--;
					if (resultsFuture.isCancelled()) {
						System.err.println("Task cancelled");
						continue;
					}
					Map<String, RealVector> results = resultsFuture.get();
					for (Entry<String, RealVector> entry : results.entrySet()) {
						//System.out.println("printing");
						Writer output = outMap.get(entry.getKey());
						try {
							//output.write(entry.getKey() + ", ");// + start + ", " + stop + ", ");
							writeResults(entry.getValue(), output);
							output.write("\n");
							output.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//System.out.println("printed");

					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			//System.out.println("All threads have finished");
			//System.out.println("closing output");
			for (Writer output : outMap.values()) {
				synchronized (output) {
					output.flush();
					output.close();
				}

			}

		}
	}

	public static void writeResults(Object value, Writer writer) throws IOException {
		if (value instanceof List) {
			boolean first = true;
			for (Object item : (List) value) {
				if (!first)
					writer.write(", ");
				writeResults(item, writer);
				first = false;
			}
		} else if (value instanceof ArrayRealVector) {
			boolean first = true;
			for (double item : ((ArrayRealVector) value).getData()) {
				if (!first)
					writer.write(", ");
				writer.write("" + item);
				first = false;
			}
		} else {
			writer.write(value.toString());
		}
	}
}
