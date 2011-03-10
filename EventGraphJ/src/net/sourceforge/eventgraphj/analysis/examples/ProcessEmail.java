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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import net.sourceforge.eventgraphj.analysis.SimpleNetworkAnalysis;
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
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class ProcessEmail {
	static NavigableGraph<Long, Integer, Integer> GRAPH;
	static String SERIALIZED_FILE = "/home/jfolson/Data/eu_email/eu_email.serialized";

	public static String[] FILENAMES = new String[] { "/home/jfolson/Data/eu_email/eu_email.csv" };// "/home/jfolson/Data/data/eu_email.csv"

	static final boolean CREATE_GRAPH = false;

	public static NavigableGraph<Long, Integer, Integer> getGraph() {
		if (GRAPH == null) {
			if (CREATE_GRAPH) {
				SimpleNavigableGraph<Long, Integer, Integer> graph = new SimpleNavigableGraph<Long, Integer, Integer>();
				try {

					int count = 0;
					long thisTime = 0, lastTime = System.currentTimeMillis();
					for (String filename : FILENAMES) {
						Scanner input;
						input = new Scanner(new File(filename));
						input.nextLine();

						while (input.hasNext()) {
							if (count % 1000 == 0) {
								thisTime = System.currentTimeMillis();
								System.out.println("added " + count + "\t" + (thisTime - lastTime) / 1000.);
								lastTime = thisTime;
								// if (count == 5000)
								// break;
							}
							int from = input.nextInt();
							int to = input.nextInt();
							int size = input.nextInt();
							String dateString = input.next();
							long epoch = input.nextInt();// / (60 * 60);
							int weekday = input.nextInt();
							int multiplicity = input.nextInt();
							graph.addEdge(epoch, from, to, EdgeType.DIRECTED);
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
				SimpleNavigableGraph<Long, Integer, Integer> graph;
				try {
					fis = new FileInputStream(SERIALIZED_FILE);
					in = new ObjectInputStream(fis);
					graph = (SimpleNavigableGraph<Long, Integer, Integer>) in.readObject();
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
			/*Integer start = GRAPH.getFirstKey();
			Integer stop = GRAPH.getLastKey();
			stop = start + (stop - start) / 20;
			GRAPH = GRAPH.subNetwork(start, stop);*/
		}
		return GRAPH;
	}

	// };

	public static void main(String[] args) throws IOException {

		final NavigableGraph<Long, Integer, Integer> comparableGraph = getGraph();
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
		HashSet<Pair<Integer>> checked = new HashSet<Pair<Integer>>();
		for (EdgeEntry<Long, Integer> edge : comparableGraph.getEdges()) {
			Pair<Integer> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		System.out.println(checked.size() + " pairs");

		VertexScorer<Integer, Integer> scorer = new DegreeScorer<Integer>(comparableGraph);
		for (Integer v : comparableGraph.getVertices()) {
			Integer score = scorer.getVertexScore(v);
			System.out.print(v + ":" + score + ", ");
		}
		System.out.println();

		Long interval = lastDate - firstDate;
		final long scale = 60 * 60;
		final long smallestInterval = 6 * scale;

		Iterable<Interval<Long>> innerIterable = new IntervalWrapperIterable<Long>(IncrementIterable.fromLong(
		        firstDate, lastDate, smallestInterval));

		final double prior = (((double) M) / (N * N)) / (interval / scale);
		final double priorStrength = 0.10;

		NetworkComparison<Integer, EdgeEntry<Long, Integer>, NavigableGraph<Long, Integer, Integer>> infoLossCompare = new PoissonNetworkInformation<Long, Integer, Integer>(
		        scale, prior, priorStrength);

		NetworkComparison<Integer, EdgeEntry<Integer, Integer>, NavigableGraph<Integer, Integer, Integer>> mseCompare = new SquaredError<Integer, Integer, Integer>(
		        scale, prior, priorStrength);

		ArrayList<Long> intervals = new ArrayList<Long>();
		// intervals.add(6 * scale);
		intervals.add(12 * scale);
		intervals.add(24 * scale);
		intervals.add(48 * scale);
		intervals.add(72 * scale);
		intervals.add(7 * 24 * scale);
		intervals.add(14 * 24 * scale);
		intervals.add(21 * 24 * scale);
		intervals.add(28 * 24 * scale);
		intervals.add(42 * 24 * scale);
		// intervals.add(interval);

		Writer output;
		for (final Long thisinterval : intervals) {
			System.out.println("binning " + thisinterval + " [" + (thisinterval / scale) + "]");
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

			CompoundIterableNetworkAnalysis<Long, Integer, Integer> allAnalyses = new CompoundIterableNetworkAnalysis<Long, Integer, Integer>(
			        iterable);

			allAnalyses.addAnalysis(new SimpleNetworkAnalysis(null) {

				@Override
				protected Object doAnalysis(Graph graph) {
					//System.out.println(graph.getEdgeCount() + " edges");

					/*VertexScorer<Integer, Integer> scorer = new DegreeScorer<Integer>(graph);
					for (Integer v : comparableGraph.getVertices()) {
						Integer score = scorer.getVertexScore(v);
						System.out.print(v + ":" + score + ", ");
					}
					System.out.println();*/

					return null;
				}

			});

			output = new FileWriter(new File("/home/jfolson/Data/eu_email/eu_email-bin_" + (thisinterval / scale)
			        + "-infoloss.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<Long, Integer, Integer, List<Double>> infoAnalyze = new AggregationComparison<Long, Integer, Integer>(
			        innerIterable, iterable, infoLossCompare, output);

			//allAnalyses.addSubAnalysis(infoAnalyze);
			allAnalyses.addCoAnalysis(infoAnalyze);

			/*output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval / scale) + "-mse.txt"));
			// output = new PrintWriter(System.out);

			IterableNetworkAnalysis<Integer, Long, Integer, List<Double>> mseAnalyze = new AggregationComparison<Integer, Long, Integer>(
			        iterable, innerIterable, infoLossCompare, output);

			allAnalyses.addSubAnalysis(mseAnalyze);

			final List<Integer> nodes = new ArrayList<Integer>(comparableGraph.getVertices());

			SimpleNetworkAnalysis<Integer, EdgeEntry<Long, Integer>, ?> analysis;
			IterableNetworkAnalysis<Integer, Long, Integer, List> iterAnalyze;

			output = new PrintWriter(System.out);
			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval / scale) + "-degree.txt"));

			analysis = new VertexScoreAnalysis<Integer, EdgeEntry<Long, Integer>, Integer>(output, nodes) {
				@Override
				public VertexScorer<Integer, Integer> createScorer(Graph<Integer, EdgeEntry<Long, Integer>> graph) {
					return new DegreeScorer<Integer>(graph);
				}
			};
			iterAnalyze = new SimpleIterableNetworkAnalysis<Integer, Integer, Integer, List>(
					(SimpleNetworkAnalysis<Integer, ComparableEdge<Integer, Integer>, List>) analysis,iterable, null);
			System.out.println("analyzing degree");
			iterAnalyze.analyze(comparableGraph);
			

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval / scale) + "-closeness.txt"));

			analysis = new VertexScoreAnalysis<Integer, EdgeEntry<Long, Integer>, Double>(output, nodes) {
				@Override
				public VertexScorer<Integer, Double> createScorer(Graph<Integer, EdgeEntry<Long, Integer>> graph) {
					return new InverseDistanceCentralityScorer<Integer, EdgeEntry<Long, Integer>>(graph, true);
				}
			};

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval / scale) + "-betweenness.txt"));
			analysis = new VertexScoreAnalysis<Integer, EdgeEntry<Long, Integer>, Double>(output, nodes) {
				@Override
				public VertexScorer<Integer, Double> createScorer(Graph<Integer, EdgeEntry<Long, Integer>> graph) {
					return new BetweennessCentrality<Integer, EdgeEntry<Long, Integer>>(graph);
				}

			};

			allAnalyses.addAnalysis(analysis);

			output = new FileWriter(new File("data/eu_email-bin_" + (thisinterval / scale) + "-pagerank.txt"));
			analysis = new VertexScoreAnalysis<Integer, EdgeEntry<Long, Integer>, Double>(output, nodes) {
				@Override
				public VertexScorer<Integer, Double> createScorer(Graph<Integer, EdgeEntry<Long, Integer>> graph) {
					return new PageRank<Integer, EdgeEntry<Long, Integer>>(graph, .15);
				}
			};

			allAnalyses.addAnalysis(analysis);
			*/
			allAnalyses.analyze(comparableGraph);

			allAnalyses.close();

			// interval = interval / 2;
		}
	}
}
