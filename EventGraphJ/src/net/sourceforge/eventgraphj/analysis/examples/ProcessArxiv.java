package net.sourceforge.eventgraphj.analysis.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.sourceforge.eventgraphj.analysis.compare.NetworkComparison;
import net.sourceforge.eventgraphj.analysis.compare.PoissonNetworkInformation;
import net.sourceforge.eventgraphj.analysis.compare.SquaredError;
import net.sourceforge.eventgraphj.analysis.iterable.AggregationComparison;
import net.sourceforge.eventgraphj.analysis.iterable.CompoundIterableNetworkAnalysis;
import net.sourceforge.eventgraphj.analysis.iterable.IterableNetworkAnalysis;
import net.sourceforge.eventgraphj.comparable.EdgeEntry;
import net.sourceforge.eventgraphj.comparable.IncrementIterable;
import net.sourceforge.eventgraphj.comparable.Interval;
import net.sourceforge.eventgraphj.comparable.IntervalWrapperIterable;
import net.sourceforge.eventgraphj.comparable.NavigableGraph;
import net.sourceforge.eventgraphj.comparable.SimpleNavigableGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ProcessArxiv {
	public static String[] FILENAMES = new String[] { "/home/jfolson/Data/arxiv_hep-ph/hep_ph.all.tsv" };// "/home/jfolson/Data/data/hep_ph.all.tsv"
	// };
	static DateFormat DATE_FORMAT = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss'Z'");
	static SimpleNavigableGraph<Long, String, String> GRAPH;
	static String SERIALIZED_FILE = "/home/jfolson/Data/arxiv_hep-ph/hep_ph.serialized";
	static final boolean CREATE_GRAPH = true;

	public static SimpleNavigableGraph<Long, String, String> getGraph() throws ParseException {
		if (GRAPH == null) {
			if (CREATE_GRAPH) {
				SimpleNavigableGraph<Long, String, String> graph = new SimpleNavigableGraph<Long, String, String>();
				try {

					int count = 0;
					long thisTime = 0, lastTime = System.currentTimeMillis();
					for (String filename : FILENAMES) {
						Scanner input;
						input = new Scanner(new File(filename));
						input.useDelimiter("[\t\n]");
						input.nextLine();

						while (input.hasNext()) {
							if (count % 1000 == 0) {
								thisTime = System.currentTimeMillis();
								System.out.println("added " + count + "\t" + (thisTime - lastTime) / 1000.);
								lastTime = thisTime;
								// if (count == 5000)break;
							}
							String dateString = input.next();
							Date date = DATE_FORMAT.parse(dateString);
							String from = input.next();
							String to = input.next();
							graph.addEdge(new EdgeEntry<Long, String>(date.getTime(), dateString), from, to,
							        EdgeType.DIRECTED);
							count++;
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return null;
				}

				FileOutputStream fos = null;
				ObjectOutputStream out = null;
				try {
					fos = new FileOutputStream(SERIALIZED_FILE);
					out = new ObjectOutputStream(fos);
					out.writeObject(graph);
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				System.out.println("wrote graph");

				System.out.println("done loading: " + graph.getVertexCount() + " vertices, " + graph.getEdgeCount()
				        + " edges");
				GRAPH = graph;

			} else {
				FileInputStream fis = null;
				ObjectInputStream in = null;
				SimpleNavigableGraph<Long, String, String> graph;
				try {
					fis = new FileInputStream(SERIALIZED_FILE);
					in = new ObjectInputStream(fis);
					graph = (SimpleNavigableGraph<Long, String, String>) in.readObject();
					in.close();
					System.out.println("loaded graph: " + graph.getVertexCount() + " vertices, " + graph.getEdgeCount()
					        + " edges");
				} catch (IOException ex) {
					ex.printStackTrace();
					return null;
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
					return null;
				}
				// print out restored time
				GRAPH = graph;
				// print out the current time
			}

		}
		return GRAPH;
	}

	public static void main(String[] args) throws ParseException, IOException {

		SimpleNavigableGraph<Long, String, String> graph = getGraph();
		final SimpleNavigableGraph<Long, String, String> comparableGraph = graph;
		int N = comparableGraph.getVertexCount();
		int M = comparableGraph.getEdgeCount();
		final Long firstDate = graph.getFirstKey();
		final Long lastDate = graph.getLastKey();
		System.out.println("first: " + DATE_FORMAT.format(new Date(firstDate)) + "\tlast: "
		        + DATE_FORMAT.format(new Date(lastDate)));
		Iterator<Long> iterate;

		HashSet<Pair<String>> checked = new HashSet<Pair<String>>();
		for (EdgeEntry<Long, String> edge : comparableGraph.getEdges()) {
			Pair<String> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		System.out.println(checked.size() + " pairs");

		long longestInterval = lastDate - firstDate;
		final long scale = 1000 * 60 * 60 * 24 * 7;
		final Long smallestInterval = 2 * scale;
		final double prior = (((double) M) / (N * N)) / (longestInterval / scale);
		final double priorStrength = 0.10;

		ArrayList<Long> intervals = new ArrayList<Long>();
		intervals.add(1 * scale);
		intervals.add(2 * scale);
		intervals.add(4 * scale);
		intervals.add(8 * scale);
		intervals.add(16 * scale);
		intervals.add(32 * scale);
		intervals.add(52 * scale);
		intervals.add(54 * scale);
		intervals.add(108 * scale);
		intervals.add(216 * scale);

		Iterable<Interval<Long>> innerIterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(
		        firstDate, lastDate, smallestInterval));

		NetworkComparison<String, EdgeEntry<Long, String>, NavigableGraph<Long, String, String>> infoLossCompare = new PoissonNetworkInformation<Long, String, String>(
		        scale, prior, priorStrength);

		NetworkComparison<String, EdgeEntry<Long, String>, NavigableGraph<Long, String, String>> mseCompare = new SquaredError<Long, String, String>(
		        scale, prior, priorStrength);
		Writer output;
		// while (interval > smallestInterval) {
		for (long interval : intervals) {
			System.out.println("binning " + (interval / scale));
			final long thisinterval = interval;
			Iterable<Interval<Long>> iterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(firstDate,
			        lastDate, thisinterval));
			CompoundIterableNetworkAnalysis<Long, String, String> allAnalyses = new CompoundIterableNetworkAnalysis<Long, String, String>(
			        iterable);

			output = new FileWriter(new File("/home/jfolson/Data/arxiv_hep-ph/hep_ph-bin_" + (interval / scale)
			        + "-infoloss.txt"));
			IterableNetworkAnalysis<Long, String, String, List<Double>> infoAnalyze = new AggregationComparison<Long, String, String>(
			        innerIterable, iterable, infoLossCompare, output);

			allAnalyses.addCoAnalysis(infoAnalyze);

			/*output = new FileWriter(new File("data/hep_ph-bin_" + (thisinterval / scale) + "-mse.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<String, Long, String, List<Double>> mseAnalyze = new AggregationComparison<String, Long, String>(
			        iterable, innerIterable, mseCompare, output);

			allAnalyses.addSubAnalysis(mseAnalyze);

			final List<String> nodes = new ArrayList<String>(comparableGraph.getVertices());

			SimpleNetworkAnalysis<String, EdgeEntry<Long, String>, ?> analysis;

			output = new FileWriter(new File("data/hep_ph-bin_" + (interval / scale) + "-degree.txt"));
			analysis = new VertexScoreAnalysis<String, EdgeEntry<Long, String>, Integer>(output, nodes) {
				@Override
				public VertexScorer<String, Integer> createScorer(Graph<String, EdgeEntry<Long, String>> graph) {
					return new DegreeScorer<String>(graph);
				}
			};

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/hep_ph-bin_" + (interval / scale) + "-closeness.txt"));
			analysis = new VertexScoreAnalysis<String, EdgeEntry<Long, String>, Double>(output, nodes) {
				@Override
				public VertexScorer<String, Double> createScorer(Graph<String, EdgeEntry<Long, String>> graph) {
					return new InverseDistanceCentralityScorer<String, EdgeEntry<Long, String>>(graph, true);
				}
			};

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/hep_ph-bin_" + (interval / scale) + "-betweenness.txt"));
			analysis = new VertexScoreAnalysis<String, EdgeEntry<Long, String>, Double>(output, nodes) {
				@Override
				public VertexScorer<String, Double> createScorer(Graph<String, EdgeEntry<Long, String>> graph) {
					return new BetweennessCentrality<String, EdgeEntry<Long, String>>(graph);
				}
			};

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/hep_ph-bin_" + (interval / scale) + "-pagerank.txt"));
			analysis = new VertexScoreAnalysis<String, EdgeEntry<Long, String>, Double>(output, nodes) {
				@Override
				public VertexScorer<String, Double> createScorer(Graph<String, EdgeEntry<Long, String>> graph) {
					return new PageRank<String, EdgeEntry<Long, String>>(graph, .15);
				}
			};

			allAnalyses.addAnalysis(analysis);*/

			allAnalyses.analyze(comparableGraph);
			allAnalyses.close();
			interval = interval / 2;
		}
	}
}
