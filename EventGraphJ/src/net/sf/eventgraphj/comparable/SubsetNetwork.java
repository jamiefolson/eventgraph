package net.sf.eventgraphj.comparable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SubsetNetwork {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		NavigableGraph<Long, ?, ?> graph;
		fis = new FileInputStream(args[0]);
		in = new ObjectInputStream(fis);
		graph = (SimpleNavigableGraph<Long, Integer, Integer>) in.readObject();
		in.close();
		System.out.println("loaded graph: " + graph.getVertexCount() + " vertices, " + graph.getEdgeCount() + " edges");

		final Long firstDate = graph.getFirstKey();
		final Long lastDate = graph.getLastKey();

		System.out.println("first: " + firstDate + "\tlast: " + lastDate);

		Long start = Long.parseLong(args[1]);
		Long stop = Long.parseLong(args[2]);

		graph = graph.subNetwork(start, stop);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(args[3]);
			out = new ObjectOutputStream(fos);
			out.writeObject(graph);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("wrote graph");

	}
}
