package net.sourceforge.eventgraphj.analysis.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sourceforge.eventgraphj.analysis.CompoundNetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.NetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.VertexScoreAnalysis;
import net.sourceforge.eventgraphj.analysis.compare.NetworkComparison;
import net.sourceforge.eventgraphj.analysis.compare.PoissonNetworkInformation;
import net.sourceforge.eventgraphj.analysis.iterable.AggregationComparison;
import net.sourceforge.eventgraphj.analysis.iterable.IterableNetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.IncrementIterable;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.IntervalWrapperIterable;
import net.sourceforge.eventgraphj.comparable.LoadGraph;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

public class SnapshotStatistics {

	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String TIMESCALE = "scale";
	public static final int POOL_SIZE = 3;
	public static final int WAIT_TIME = 100;

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(new Option(HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("file").hasArg().withDescription(
		        "use given file to construct graph").create(FILE));
		options.addOption(OptionBuilder.withArgName("length").hasArg().withDescription(
		        "use given length of time as the basis for constructing exponentially larger temporal snapshots")
		        .create(TIMESCALE));

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine line;
		// parse the command line arguments
		try {
			line = parser.parse(options, args);

			if (line.hasOption(HELP)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SurvivalTimes -f <file>  [...]", options);
			} else if (!line.hasOption(FILE)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("SurvivalTimes -f <file>  [...]", options);
			} else {
				String filename = line.getOptionValue(FILE);
				Long timescale = Long.parseLong(line.getOptionValue(TIMESCALE));
				final NavigableGraph<Long, ?, ?> comparableGraph = LoadGraph.loadBinaryJungGraph(filename);
				computeSurvivals(comparableGraph, filename.substring(0, filename.lastIndexOf('.')), timescale);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static <V, E> void computeSurvivals(NavigableGraph<Long, V, E> comparableGraph, String outputBase,
	        Long timescale) throws IOException {

		ThreadPoolExecutor exec = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, Long.MAX_VALUE, TimeUnit.NANOSECONDS,
		        new SynchronousQueue<Runnable>());
		//(ThreadPoolExecutor) Executors.newFixedThreadPool(POOL_SIZE);

		//System.out.println(exec.getKeepAliveTime(TimeUnit.NANOSECONDS));
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		System.out.println("edgecount: " + comparableGraph.getEdgeCount());

		System.out.println("first half edgecount: "
		        + comparableGraph.headNetwork((long) (firstDate + (lastDate - firstDate) / 2.0)).getEdgeCount());
		System.out.println("test head edgecount: " + comparableGraph.headNetwork(12705l).getEdgeCount());
		System.out.println("test tail edgecount: " + comparableGraph.tailNetwork(-8895l).getEdgeCount());
		HashSet<Pair<V>> checked = new HashSet<Pair<V>>();
		for (EdgeEntry<Long, E> edge : comparableGraph.getEdges()) {
			Pair<V> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		System.out.println(checked.size() + " pairs");

		VertexScorer<V, Integer> scorer = new DegreeScorer<V>(comparableGraph);
		for (V v : comparableGraph.getVertices()) {
			Integer score = scorer.getVertexScore(v);
			System.out.print(v + ":" + score + ", ");
		}
		System.out.println();

		Long interval = lastDate - firstDate;
		final long smallestInterval = timescale;

		Iterable<Interval<Long>> innerIterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(
		        firstDate, lastDate, smallestInterval));

		NetworkComparison<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> infoLossCompare = PoissonNetworkInformation
		        .forGraph(comparableGraph, timescale);

		/*		NetworkComparison<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> mseCompare = new SquaredError<Long, V, E>(
				        timescale, prior, priorStrength);*/

		for (long thisinterval = smallestInterval; thisinterval < interval; thisinterval *= 2) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / timescale) + "]");

			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, thisinterval));

			/*Iterable<Interval<Long>> iterable = IntervalWindowIterable.fromLong(IncrementIterable.fromLong(firstDate,
			        lastDate, smallestInterval), thisinterval);*/

			final CompoundNetworkAnalysis<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>> allAnalyses = new CompoundNetworkAnalysis<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>>();

			final HashMap<String, Writer> outMap = new HashMap<String, Writer>();

			NetworkAnalysis<V, EdgeEntry<Long, E>, NavigableGraph<Long, V, E>, ?> infoAnalyze = new AggregationComparison<Long, V, E>(
			        innerIterable, iterable, infoLossCompare);

			allAnalyses.addAnalysis("InformationLoss", infoAnalyze);
			outMap
			        .put("InformationLoss", new FileWriter(
			                new File(outputBase + "_" + (thisinterval) + "-infoloss.txt")));

			//Writer mseoutput = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-mse.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<Long, V, E, List<Double>> mseAnalyze = new AggregationComparison<Long, V, E>(
			        iterable, innerIterable, infoLossCompare);

			//allAnalyses.addSubAnalysis(mseAnalyze);

			final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());

			NetworkAnalysis<V, EdgeEntry<Long, E>, Graph<V, EdgeEntry<Long, E>>, ?> analysis;
			IterableNetworkAnalysis<Long, V, E, List> iterAnalyze;

			analysis = VertexScoreAnalysis.newDegreeAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Degree", analysis);
			outMap.put("Degree", new FileWriter(new File(outputBase + "_" + (thisinterval) + "-degree.txt")));

			analysis = VertexScoreAnalysis.newClosenessAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Closeness", analysis);
			outMap.put("Closeness", new FileWriter(new File(outputBase + "_" + (thisinterval) + "-closeness.txt")));

			analysis = VertexScoreAnalysis.newBetweennessAnalysis(comparableGraph);
			allAnalyses.addAnalysis("Betweenness", analysis);
			outMap.put("Betweenness", new FileWriter(new File(outputBase + "_" + (thisinterval) + "-betweenness.txt")));

			analysis = VertexScoreAnalysis.newPageRankAnalysis(comparableGraph);
			//allAnalyses.addAnalysis("Pagerank", analysis);
			outMap.put("Pagerank", new FileWriter(new File(outputBase + "_" + (thisinterval) + "-pagerank.txt")));

			int numWorkers = 0;
			final Object lock = new Object();
			for (Interval<Long> currInterval : iterable) {
				numWorkers++;
				final Long start = currInterval.getStart();
				final Long stop = currInterval.getFinish();
				final NavigableGraph<Long, V, E> subNet = comparableGraph.subNetwork(start, stop);
				Runnable worker = new Runnable() {

					@Override
					public void run() {
						HashMap<String, ?> results = allAnalyses.analyze(subNet);
						for (Entry<String, ?> entry : results.entrySet()) {
							synchronized (lock) {
								Writer output = outMap.get(entry.getKey());
								try {
									output.write(entry.getKey() + ", " + start + ", " + stop + ", ");
									writeResults(entry.getValue(), output);
									output.write("\n");
									output.flush();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						}

					}

				};
				boolean submitted = false;
				while (!submitted) {
					try {
						exec.execute(worker);
						submitted = true;
					} catch (RejectedExecutionException e) {
						try {
							Thread.sleep(WAIT_TIME);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				//System.out.println("Executing: " + currInterval);

			}
			while (((ThreadPoolExecutor) exec).getActiveCount() > 0) {
				try {
					Thread.sleep(WAIT_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Writer output : outMap.values()) {
				output.close();
			}

		}
	}

	public static void writeResults(Object value, Writer writer) throws IOException {
		if (value instanceof Collection) {
			boolean first = true;
			for (Object item : (Collection) value) {
				if (!first)
					writer.write(", ");
				writeResults(item, writer);
				first = false;
			}
		} else {
			writer.write(value.toString());
		}
	}
}
