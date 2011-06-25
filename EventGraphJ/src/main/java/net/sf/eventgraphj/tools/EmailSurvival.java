package net.sf.eventgraphj.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import net.sf.eventgraphj.comparable.DyadNavigableGraph;
import net.sf.eventgraphj.comparable.EdgeEntry;
import net.sf.eventgraphj.comparable.EdgePair;
import net.sf.eventgraphj.comparable.NavigableGraph;
import net.sf.eventgraphj.comparable.NavigableGraphModule;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;

public class EmailSurvival {
	static NavigableGraph<Long, Integer, Integer> GRAPH;
	static String SERIALIZED_FILE = "/home/jfolson/Data/eu_email/eu_email.serialized";

	public static String[] FILENAMES = new String[] { "/home/jfolson/Data/eu_email/eu_email.csv" };// "/home/jfolson/Data/data/eu_email.csv"

	static final boolean CREATE_GRAPH = false;

	public static NavigableGraph<Long, Integer, Integer> getGraph() {
		if (GRAPH == null) {
			if (CREATE_GRAPH) {
				NavigableGraph<Long, Integer, Integer> graph = NavigableGraphModule.EDGE_NAVIGABLE
				        .getInstance(NavigableGraph.class);
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
				DyadNavigableGraph<Long, Integer, Integer> graph;
				try {
					fis = new FileInputStream(SERIALIZED_FILE);
					in = new ObjectInputStream(fis);
					graph = (DyadNavigableGraph<Long, Integer, Integer>) in.readObject();
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

		// System.out.println("first: " + firstDate + "\tlast: " + lastDate);
		// System.out.println("edgecount: " + comparableGraph.getEdgeCount());

		// System.out.println("first half edgecount: "
		// + comparableGraph.headNetwork((long) (firstDate + (lastDate -
		// firstDate) / 2.0)).getEdgeCount());
		// System.out.println("test head edgecount: " +
		// comparableGraph.headNetwork(12705l).getEdgeCount());
		// System.out.println("test tail edgecount: " +
		// comparableGraph.tailNetwork(-8895l).getEdgeCount());
		HashSet<Pair<Integer>> checked = new HashSet<Pair<Integer>>();
		for (EdgeEntry<Long, Integer, Integer> edge : comparableGraph.getEdges()) {
			Pair<Integer> pair = comparableGraph.getEndpoints(edge);
			checked.add(pair);
		}
		// System.out.println(checked.size() + " pairs");

		final List<Integer> nodes = new ArrayList<Integer>(comparableGraph.getVertices());
		Long lastTime = null, newTime = null;
		for (Integer from : nodes) {
			for (Integer to : comparableGraph.getSuccessors(from)) {
				lastTime = null;
				for (EdgePair<Long, ?> edge : comparableGraph.findEdgeSet(from, to)) {
					if (lastTime != null) {
						newTime = edge.getKey();
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
