package net.sf.eventgraphj.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.EdgePair;
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
	public static final String TYPE = "type";
	public static final String SHOW_PAIR = "showpair";

	public static enum Type {
		pairwise, sender, any;
	}

	/*
	 * public static final String PAIRWISE = "pairwise"; public static final
	 * String SENDER = "sender"; public static final String RECEIVER =
	 * "receiver";
	 */
	public static void main(String[] args) throws IOException {
		Options options = new Options();
		options.addOption(new Option(HELP, "print this message"));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given file to construct graph")
				.create(FILE));
		options.addOption(new Option(TYPE,
				"produce \"pairwise\" \"sender\" or \"any\" survival(inter-arrival) times"));
		// options.addOption(new Option(SENDER,
		// "produce sender survival (inter-arrival) times"));
		// options.addOption(new Option(RECEIVER,
		// "produce receiver survival (inter-arrival) times"));

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

				boolean showpairs = false;
				
				if (line.hasOption(SHOW_PAIR)){
					showpairs= true;
				}
				final NavigableGraph<Long, ?, ?> comparableGraph = LoadGraph
						.loadBinaryJungGraph(filename);
				Type survivalType = Type.sender;
				if (line.hasOption(TYPE)) {
					survivalType = Type.valueOf(line.getOptionValue(TYPE));
				}

				printSurvivals(comparableGraph, survivalType,showpairs);

			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static <V, E> void printSurvivals(
			NavigableGraph<Long, V, E> comparableGraph, Type survivalType,boolean showpairs) {
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		Long lastTime = null, newTime = null;

		if (survivalType == Type.any) {
			ArrayList<EdgeEntry<Long, V, E>> allEdges = new ArrayList<EdgeEntry<Long, V, E>>(
					comparableGraph.getEdges());
			Collections.sort(allEdges, new Comparator<EdgeEntry<Long, V, E>>() {
				@Override
				public int compare(EdgeEntry<Long, V, E> arg0,
						EdgeEntry<Long, V, E> arg1) {
					return arg0.getKey().compareTo(arg1.getKey());
				}
			});

			printTimeDiffs(allEdges, showpairs);
			return;
		}

		final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());
		for (V from : nodes) {
			if (survivalType == Type.sender) {
				lastTime = null;
				ArrayList<EdgeEntry<Long, V, E>> allEdges = new ArrayList<EdgeEntry<Long, V, E>>(
						comparableGraph.getOutEdges(from));
				Collections.sort(allEdges,
						new Comparator<EdgeEntry<Long, V, E>>() {
							@Override
							public int compare(EdgeEntry<Long, V, E> arg0,
									EdgeEntry<Long, V, E> arg1) {
								return arg0.getKey().compareTo(arg1.getKey());
							}
						});

				printTimeDiffs(allEdges, showpairs);

			} else {
				assert (survivalType == Type.pairwise);
				for (V to : comparableGraph.getSuccessors(from)) {
					printTimeDiffs(comparableGraph.findEdgeSet(
							from, to), showpairs);
				}
			}
		}
	}
	
	
	

	public static <V, E> long[][] getSurvivals(
			NavigableGraph<Long, V, E> comparableGraph, Type survivalType) {
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = comparableGraph.getFirstKey();
		final Long lastDate = comparableGraph.getLastKey();

		long[][] survivals;
		
		
		if (survivalType == Type.any) {
			survivals = new long[1][];
			ArrayList<EdgeEntry<Long, V, E>> allEdges = new ArrayList<EdgeEntry<Long, V, E>>(
					comparableGraph.getEdges());
			Collections.sort(allEdges, new Comparator<EdgeEntry<Long, V, E>>() {
				@Override
				public int compare(EdgeEntry<Long, V, E> arg0,
						EdgeEntry<Long, V, E> arg1) {
					return arg0.getKey().compareTo(arg1.getKey());
				}
			});
			survivals[0] = getTimeDiffs(allEdges);
			return survivals;
		}

		final List<V> nodes = new ArrayList<V>(comparableGraph.getVertices());
		if (survivalType == Type.sender) {
			survivals = new long[nodes.size()][];
		}else{
			survivals = new long[comparableGraph.getPairs().size()][];
		}
		int arrayIdx = 0;
		for (V from : nodes) {
			
			if (survivalType == Type.sender) {
				ArrayList<EdgeEntry<Long, V, E>> allEdges = new ArrayList<EdgeEntry<Long, V, E>>(
						comparableGraph.getOutEdges(from));
				Collections.sort(allEdges,
						new Comparator<EdgeEntry<Long, V, E>>() {
							@Override
							public int compare(EdgeEntry<Long, V, E> arg0,
									EdgeEntry<Long, V, E> arg1) {
								return arg0.getKey().compareTo(arg1.getKey());
							}
						});
				survivals[arrayIdx] = getTimeDiffs(allEdges);
				arrayIdx++;
			} else {
				assert (survivalType == Type.pairwise);
				for (V to : comparableGraph.getSuccessors(from)) {
					survivals[arrayIdx] = getTimeDiffs(comparableGraph.findEdgeSet(
							from, to));
						arrayIdx++;
						
				}
			}
		}
		return survivals;
	}
	
	private static <V,E> long[] getTimeDiffs(Collection<EdgeEntry<Long, V, E>> allEdges){
		Long lastTime = null, newTime = null;
		int idx = 0;
		long[] survivals = new long[allEdges.size()-1];
		for (EdgeEntry<Long, V, E> edge : allEdges) {
			newTime = edge.getKey();
			if (lastTime != null) {
				long diff = newTime - lastTime;
				assert (diff >= 0);
				survivals[idx] = diff;
			}
			lastTime = newTime;

		}
		return survivals;
	}
	
	private static <V,E> void printTimeDiffs(Collection<EdgeEntry<Long, V, E>> allEdges,boolean showpairs){
		Long lastTime = null, newTime = null;
		for (EdgeEntry<Long, V, E> edge : allEdges) {
			newTime = edge.getKey();
			if (lastTime != null) {
				long diff = newTime - lastTime;
				assert (diff >= 0);
				System.out.println(edge.getFrom().toString() + ","
						+ edge.getTo().toString() + "," + diff);
			}
			lastTime = newTime;

		}
	}
	
	
}
