package net.sf.eventgraphj.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.EdgePair;
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

import edu.uci.ics.jung.graph.util.Pair;

public class SurvivalTimes {
	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String PAIRWISE = "pairwise";
	public static final String SENDER = "sender";
	public static final String RECEIVER = "receiver";

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(new Option(HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
		        .withDescription("use given file to construct graph").create(FILE));
		options.addOption(new Option(PAIRWISE, "produce pairwise survival (inter-arrival) times"));
		options.addOption(new Option(SENDER, "produce sender survival (inter-arrival) times"));
		options.addOption(new Option(RECEIVER, "produce receiver survival (inter-arrival) times"));

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

				final NavigableGraph<Long, ?, ?> comparableGraph = LoadGraph.loadBinaryJungGraph(filename);
				computeSurvivals(comparableGraph);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static <V, E> void computeSurvivals(NavigableGraph<Long, V, E> comparableGraph) {
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		//System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		//System.out.println("edgecount: " + comparableGraph.getEdgeCount());

		//System.out.println("first half edgecount: "
		//        + comparableGraph.headNetwork((long) (firstDate + (lastDate - firstDate) / 2.0)).getEdgeCount());
		//System.out.println("test head edgecount: " + comparableGraph.headNetwork(12705l).getEdgeCount());
		//System.out.println("test tail edgecount: " + comparableGraph.tailNetwork(-8895l).getEdgeCount());
		HashSet<Pair<V>> checked = new HashSet<Pair<V>>();
		for (EdgeEntry<Long, V, E> edge : comparableGraph.getEdges()) {
			Pair<V> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		//System.out.println(checked.size() + " pairs");

		final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());
		Long lastTime = null, newTime = null;
		for (V from : nodes) {
			for (V to : comparableGraph.getSuccessors(from)) {
				lastTime = null;
				for (EdgePair<Long, E> edge : comparableGraph.findEdgeSet(from, to)) {
					newTime = edge.getKey();
					if (lastTime != null) {
						long diff = newTime - lastTime;
						assert (diff >= 0);
						System.out.println(from.toString() + "," + to.toString() + "," + diff);
					}
					lastTime = newTime;
				}
			}
		}
	}
}
