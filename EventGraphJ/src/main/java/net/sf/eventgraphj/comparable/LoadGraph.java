package net.sf.eventgraphj.comparable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import net.sf.eventgraphj.comparable.NavigableGraphModule.EdgeNavigableModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.uci.ics.jung.graph.util.EdgeType;

public class LoadGraph {

	private static Injector injector = Guice
			.createInjector(new EdgeNavigableModule());

	public static <V, K extends Comparable<K>, E> net.sf.eventgraphj.comparable.NavigableGraph<K, V, E> loadBinaryJungGraph(
			String file, Class<K> keyType, Class<V> vertexType,
			Class<E> edgeType) {

		FileInputStream fis = null;
		ObjectInputStream in = null;
		NavigableGraph<K, V, E> graph;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			graph = (NavigableGraph<K, V, E>) in.readObject();
			in.close();
			System.out.println("loaded graph: " + graph.getVertexCount()
					+ " vertices, " + graph.getEdgeCount() + " edges");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
		// print out restored time

		return graph;
	}

	public static NavigableGraph loadBinaryJungGraph(String file) {

		FileInputStream fis = null;
		ObjectInputStream in = null;
		NavigableGraph graph;
		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			graph = (NavigableGraph) in.readObject();
			in.close();
			System.out.println("loaded graph: " + graph.getVertexCount()
					+ " vertices, " + graph.getEdgeCount() + " edges");
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
		// print out restored time

		return graph;
	}

	public static <V> net.sf.eventgraphj.comparable.NavigableGraph<Long, V, String> loadCsvGraph(
			String filename, int fromColumn, int toColumn, int dateColumn,
			SimpleDateFormat dateFormat, Class<V> vertexType,
			boolean hasHeader, String separatorStr)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			FileNotFoundException, SecurityException, NoSuchMethodException,
			ParseException {
		return loadCsvGraph(injector, filename, fromColumn, toColumn,
				dateColumn, dateFormat, vertexType, hasHeader, separatorStr);
	}

	public static <V> net.sf.eventgraphj.comparable.NavigableGraph<Long, V, String> loadCsvGraph(
			Injector inject, String filename, int fromColumn, int toColumn,
			int dateColumn, SimpleDateFormat dateFormat, Class<V> vertexType,
			boolean hasHeader, String separatorStr)
			throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			FileNotFoundException, SecurityException, NoSuchMethodException,
			ParseException {
		NavigableGraph<Long, V, String> graph = inject
				.getInstance(NavigableGraph.class);

		Constructor<V> contructVertex = vertexType.getConstructor(String.class);
		int count = 0;
		long thisTime = 0, lastTime = System.currentTimeMillis();
		Scanner input;
		input = new Scanner(new File(filename));
		if (hasHeader) {
			input.nextLine();
		}

		while (input.hasNext()) {
			if (count % 1000 == 0) {
				thisTime = System.currentTimeMillis();
				// System.out.println("added " + count + "\t" + (thisTime -
				// lastTime) / 1000.);
				lastTime = thisTime;
				// if (count == 5000)
				// break;
			}
			String[] vals = input.nextLine().split(separatorStr);
			V from = contructVertex.newInstance(vals[fromColumn]);
			V to = contructVertex.newInstance(vals[toColumn]);
			// int size = input.nextInt();
			String dateString = vals[dateColumn];
			Long dateLong = null;
			if (dateFormat != null) {
				try {
					Date date = dateFormat.parse(dateString);
					dateLong = date.getTime();
				} catch (ParseException e) {
					throw new ParseException("Failed to parse value '"
							+ dateString + "' as Date on line: " + count, count);
				}
			} else {
				dateLong = Long.parseLong(dateString);
				if (dateLong == null) {
					throw new ParseException("Failed to parse value '"
							+ dateString + "' as long on line: " + count, count);
				}
			}

			// long epoch = input.nextInt();// / (60 * 60);
			// int weekday = input.nextInt();
			// int multiplicity = input.nextInt();
			// System.out.println(from + ", " + to + ", " + dateLong + ", " +
			// lastTime);

			graph.addEdge(dateLong, from, to, EdgeType.DIRECTED);
			count++;
		}

		return graph;
	}

	public static <V, K extends Comparable<K>, E> net.sf.eventgraphj.comparable.NavigableGraph<K, V, E> loadDynetmlGraph(
			String file, Class<K> keyType, Class<V> vertexType,
			Class<E> edgeType) {
		return null;
	}
}
