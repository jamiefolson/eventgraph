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
import net.sf.eventgraphj.comparable.LoadGraph;
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

public class BenchmarkStatistics {

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

	public static final String HELP = "help";
	public static final String FILE = "f";
	public static final String TIMESCALE = "scale";
	public static final String OUTPUT = "output";
	public static final String HEADER = "header";
	public static final String FROM_COLUMN = "from";
	public static final String TO_COLUMN = "to";
	public static final String DATE_COLUMN = "date";
	public static final String VERTEX_TYPE = "vtype";
	public static final String DATE_FORMAT = "dateformat";
	public static final String LONG_DATE = "longdate";
	public static final String OUTPUT_FILE = "output";
	public static final String FIELD_SEPARATOR = "sep";
	public static final String CACHE_TYPE = "dyad";
	public static final int WAIT_TIME = 100;

	public static void main(String[] args) throws IOException {
		Options options = new Options();
		Option helpOption = new Option(HELP, "print this message");
		Option headerOption = new Option(HEADER, "graph file has a header");
		Option graphfile = OptionBuilder.withArgName("file").hasArg()
		        .withDescription("use given file to construct graph").create(FILE);
		Option fromColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column for from vertex").create(FROM_COLUMN);
		Option toColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column for to vertex").create(TO_COLUMN);
		Option edgeColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column for event/edge date").create(DATE_COLUMN);
		Option vertexTypeOption = OptionBuilder.withArgName("typename").hasArg()
		        .withDescription("use given fully qualified java class as vertex type").create(VERTEX_TYPE);
		Option longOption = new Option(LONG_DATE, "parse date column as a Long or Integer (default)");
		Option dateFormatOption = OptionBuilder
		        .withArgName("format")
		        .hasArg()
		        .withDescription(
		                "parse date column as Date using given string as a date format for the event ordering ")
		        .create(DATE_FORMAT);
		Option separator = OptionBuilder.withArgName("\"string\"").hasArg()
		        .withDescription("field separator in graph file").create(FIELD_SEPARATOR);

		options.addOption(helpOption);
		options.addOption(graphfile);
		options.addOption(headerOption);
		options.addOption(fromColumnOption);
		options.addOption(toColumnOption);
		options.addOption(edgeColumnOption);
		options.addOption(vertexTypeOption);
		options.addOption(longOption);
		options.addOption(dateFormatOption);
		options.addOption(separator);

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

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine line;
		try {
			// parse the command line arguments
			line = parser.parse(options, args);

			if (line.hasOption(HELP)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("BenchmarkStatistics -f <file> -from <column> -to <column> -event <column> [...]",
				        options);
			} else if (!line.hasOption(FROM_COLUMN) || !line.hasOption(TO_COLUMN) || !line.hasOption(DATE_COLUMN)
			        || !line.hasOption(FILE)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("EventGraphFromCSV -f <file> -from <column> -to <column> -date <column> [...]",
				        options);
			} else {
				String filename = line.getOptionValue(FILE);
				String outputFilename = line.getOptionValue(OUTPUT_FILE);
				int fromColumn = Integer.parseInt(line.getOptionValue(FROM_COLUMN)) - 1;
				int toColumn = Integer.parseInt(line.getOptionValue(TO_COLUMN)) - 1;
				int dateColumn = Integer.parseInt(line.getOptionValue(DATE_COLUMN)) - 1;
				Class vertexType = String.class;
				Class edgeType = Long.class;
				String strValue = line.getOptionValue(VERTEX_TYPE);
				SimpleDateFormat dateFormat = null;
				if (strValue != null) {
					vertexType = Class.forName(strValue);
				}
				strValue = line.getOptionValue(DATE_FORMAT);
				if (strValue != null) {
					dateFormat = new SimpleDateFormat(strValue);
				}
				String separatorStr = line.getOptionValue(FIELD_SEPARATOR);
				if (separatorStr == null) {
					separatorStr = ",";
				}
				System.out.println("separator: " + separatorStr);

				Long timescale = Long.parseLong(line.getOptionValue(TIMESCALE));

				String outputBase = filename.substring(0, filename.lastIndexOf('.'));

				if (line.hasOption(OUTPUT))
					outputBase = line.getOptionValue(OUTPUT);
				FileWriter queryOutput = new FileWriter(new File(outputBase + "-queryTime.txt"));
				FileWriter constructOutput = new FileWriter(new File(outputBase + "-constructionTime.txt"));
				NavigableGraph<Long, ?, String> graph;

				for (Implementation type : Implementation.values()) {
					long constructionTime = System.nanoTime();

					graph = LoadGraph.loadCsvGraph(type.injector(), filename, fromColumn, toColumn, dateColumn,
					        dateFormat, vertexType, line.hasOption(HEADER), separatorStr);
					constructionTime = System.nanoTime() - constructionTime;

					System.out.println("Successfully loaded graph of type: " + graph.getClass().getName() + " with "
					        + graph.getVertexCount() + " vertices, and " + graph.getEdgeCount() + " edges in "
					        + constructionTime / 1000 + " ms");

					computeQueries(graph, timescale, queryOutput);
				}
			}
		} catch (ParseException exp) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not find specified class: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			System.err.println("Illegal argument: " + e.getMessage());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to read graph file: " + e.getMessage());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.err.println("Security exception: " + e.getMessage());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to create objects of specified vertex type: " + e.getMessage());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to access constructor for specified vertex typ: " + e.getMessage());
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to create objects of specified vertex type: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			System.err.println("No suitable contructor for the specified vertex type: " + e.getMessage());
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Error parsing graph file: " + e.getMessage());
		}
		System.exit(0);
	}

	public static <V, E> void computeQueries(NavigableGraph<Long, V, E> comparableGraph, Long timescale,
	        FileWriter output) throws IOException {

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

		String thisImplementation = "edge";
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
				output.write(thisinterval + ", " + thisImplementation + ", " + queryTime + "\n");
			}
		}
	}

	public static void writeResults(Object value, Writer writer) throws IOException {
		if (value instanceof Collection) {
			boolean first = true;
			for (Object item : (Collection) value) {
				if (!first) {
					writer.write(", ");
				}
				writeResults(item, writer);
				first = false;
			}
		} else {
			writer.write(value.toString());
		}
	}
}
