package net.sf.eventgraphj.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.uci.ics.jung.graph.Graph;

public class EventGraphFromCSV {
	public static final String HELP = "help";
	public static final String FILE = "f";
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

	public static void main(String[] args) {
		Options options = new Options();
		Option helpOption = new Option(HELP, "print this message");
		Option headerOption = new Option(HEADER, "graph file has a header");
		Option graphfile = OptionBuilder.withArgName("file").hasArg()
		        .withDescription("use given file to construct graph").create(FILE);
		Option fromColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column number(1 indexed) for \"from\" vertex").create(FROM_COLUMN);
		Option toColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column number(1 indexed) for \"to\" vertex").create(TO_COLUMN);
		Option edgeColumnOption = OptionBuilder.withArgName("column").hasArg()
		        .withDescription("use given column number(1 indexed) for event/edge date").create(DATE_COLUMN);
		Option vertexTypeOption = OptionBuilder.withArgName("typename").hasArg()
		        .withDescription("use given fully qualified java class as vertex type").create(VERTEX_TYPE);
		Option longOption = new Option(LONG_DATE, "parse date column as a Long or Integer (default)");
		Option dateFormatOption = OptionBuilder
		        .withArgName("format")
		        .hasArg()
		        .withDescription(
		                "parse date column as Date using given string as a date format for the event ordering ")
		        .create(DATE_FORMAT);
		Option outputfile = OptionBuilder.withArgName("file").hasArg()
		        .withDescription("store constructed graph to a binary at the specified location").create(OUTPUT_FILE);
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
		options.addOption(outputfile);
		options.addOption(separator);

		// create the parser
		CommandLineParser parser = new GnuParser();
		CommandLine line;
		try {
			// parse the command line arguments
			line = parser.parse(options, args);

			if (line.hasOption(HELP)) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("EventGraphFromCSV -f <file> -from <column> -to <column> -date <column> [...]",
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
				System.out.println("separator: \"" + separatorStr + "\"");
				Graph graph;
				graph = LoadGraph.loadCsvGraph(filename, fromColumn, toColumn, dateColumn, dateFormat, vertexType,
				        line.hasOption(HEADER), separatorStr);

				System.out.println("Successfully loaded graph with " + graph.getVertexCount() + " vertices, and "
				        + graph.getEdgeCount() + " edges");
				if (outputFilename != null) {
					FileOutputStream fos = null;
					ObjectOutputStream out = null;
					try {
						fos = new FileOutputStream(outputFilename);
						out = new ObjectOutputStream(fos);
						out.writeObject(graph);
						out.close();
						System.out.println("Successfully serialized graph to " + outputFilename);
					} catch (IOException ex) {
						ex.printStackTrace();
					}
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
	}
}
