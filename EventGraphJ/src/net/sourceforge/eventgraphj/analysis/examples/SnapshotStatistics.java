package net.sourceforge.eventgraphj.analysis.examples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.eventgraphj.analysis.SimpleNetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.VertexScoreAnalysis;
import net.sourceforge.eventgraphj.analysis.compare.NetworkComparison;
import net.sourceforge.eventgraphj.analysis.compare.PoissonNetworkInformation;
import net.sourceforge.eventgraphj.analysis.iterable.AggregationComparison;
import net.sourceforge.eventgraphj.analysis.iterable.CompoundIterableNetworkAnalysis;
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
import edu.uci.ics.jung.graph.util.Pair;

public class SnapshotStatistics {

	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String TIMESCALE = "scale";

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
				computeSurvivals(comparableGraph, timescale);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static <V, E> void computeSurvivals(NavigableGraph<Long, V, E> comparableGraph, Long timescale)
	        throws IOException {
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

		Writer output;
		for (long thisinterval = smallestInterval; thisinterval < interval; thisinterval *= 2) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / timescale) + "]");
			/*Iterable<Integer> iterable = new Iterable<Integer>() {
				public Iterator<Integer> iterator() {
					return new IntervalIterator<Integer>(firstDate, lastDate, thisinterval) {
						@Override
						public Integer add(Integer init, Integer increment) {
							return init + increment;
						}
					};
				}
			};*/
			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, thisinterval));

			/*Iterable<Interval<Long>> iterable = IntervalWindowIterable.fromLong(IncrementIterable.fromLong(firstDate,
			        lastDate, smallestInterval), thisinterval);*/

			CompoundIterableNetworkAnalysis<Long, V, E> allAnalyses = new CompoundIterableNetworkAnalysis<Long, V, E>(
			        iterable);

			output = new FileWriter(new File("/home/jfolson/Data/eu_email/eu_email-bin_" + (thisinterval)
			        + "-infoloss.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<Long, V, E, List<Double>> infoAnalyze = new AggregationComparison<Long, V, E>(
			        innerIterable, iterable, infoLossCompare, output);

			allAnalyses.addCoAnalysis(infoAnalyze);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-mse.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<Long, V, E, List<Double>> mseAnalyze = new AggregationComparison<Long, V, E>(
			        iterable, innerIterable, infoLossCompare, output);

			//allAnalyses.addSubAnalysis(mseAnalyze);

			final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());

			SimpleNetworkAnalysis<V, EdgeEntry<Long, E>, ?> analysis;
			IterableNetworkAnalysis<Long, V, E, List> iterAnalyze;

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-degree.txt"));

			analysis = VertexScoreAnalysis.newDegreeAnalysis(comparableGraph);

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-closeness.txt"));

			analysis = VertexScoreAnalysis.newClosenessAnalysis(comparableGraph);

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-betweenness.txt"));
			analysis = VertexScoreAnalysis.newBetweennessAnalysis(comparableGraph);

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval) + "-pagerank.txt"));
			analysis = VertexScoreAnalysis.newPageRankAnalysis(comparableGraph);

			allAnalyses.addAnalysis(analysis);

			allAnalyses.analyze(comparableGraph);

			allAnalyses.close();

			// interval = interval / 2;
		}
	}
}
