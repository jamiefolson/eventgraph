package net.sf.eventgraphj.comparable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.NavigableMap;

import net.sf.eventgraphj.comparable.SimpleNavigableGraph.NullComparator;

public class SimpleNavigableGraphTest {

	public static void main(String[] args) {
		Comparator<Integer> compare = new NullComparator<Integer>();
		NavigableMap<Integer, Integer> map1 = new MyTreeMap<Integer, Integer>(compare);
		NavigableMap<Integer, Integer> map2 = new MyTreeMap<Integer, Integer>(compare);
		// System.out.println("problem? " + (map1.hashCode() ==
		// (map2.hashCode())));
		/*NavigableMap<Integer, Integer> map = new MyTreeMap<Integer, Integer>(compare);
		System.out.println("1 vs 2: " + compare.compare(1, 2));
		System.out.println("1 vs null: " + compare.compare(1, null));
		System.out.println("null vs null: " + compare.compare(null, null));

		map.put(null, 1);
		System.out.println("contains? " + map.containsKey(null));
		map.put(1, 2);
		map.put(2, 3);

		System.out.println("first key: " + map.firstKey());
		System.out.println("All entries:");
		for (Entry<Integer, Integer> entry : map.entrySet()) {
			System.out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		}
		System.out.println("All keys:");
		for (Integer key : map.keySet()) {
			System.out.println("\tkey -> " + key);
		}
		System.out.println("subMap entries:");
		for (Entry<Integer, Integer> entry : map.subMap(null, 4).entrySet()) {
			System.out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		}
		System.out.println("headMap entries: isEmpty()? " + map.headMap(4).entrySet().isEmpty());
		for (Entry<Integer, Integer> entry : map.headMap(4).entrySet()) {
			System.out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		}
		System.out.println("headMap entries: hasNext? " + map.headMap(4).entrySet().iterator().hasNext());
		// System.out.println("headMap entries: hasNext? "+
		// map.headMap(4).entrySet().iterator().next());
		System.out.println("headMap entries: size? " + (map.headMap(4).entrySet().size()));
		System.out.println("headMap keys: isEmpty()? " + map.headMap(4).keySet().isEmpty());
		for (Integer key : map.headMap(4).keySet()) {
			System.out.println("\tkey -> " + key);
		}
		System.out.println("descending entries: isEmpty()? " + map.descendingMap().entrySet().isEmpty());
		for (Entry<Integer, Integer> entry : map.descendingMap().entrySet()) {
			System.out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		}*/

		SimpleNavigableGraph<Integer, Integer, Object> graph = new SimpleNavigableGraph<Integer, Integer, Object>() {

		};
		graph.addEdge(1, 1, 2);
		graph.addEdge(2, 2, 3);
		graph.addEdge(3, 1, 3);
		graph.addEdge(4, 1, 5);
		graph.addEdge(6, 1, 4);
		System.out.println("1 has degree: " + graph.degree(1));
		System.out.println("2 has degree: " + graph.degree(2));

		// System.out.println((new Integer(0)).compareTo(null));

		System.out.println("neighbors by 2");
		for (int neighbor : graph.getNeighbors(1, 0, 2)) {
			System.out.println("\t" + neighbor); // 2
		}
		System.out.println("neighbors by 4");
		for (int neighbor : graph.getNeighbors(1, 0, 4)) {
			System.out.println("\t" + neighbor); // 2 3
		}
		System.out.println("neighbors from null");
		for (int neighbor : graph.getNeighbors(1)) {
			System.out.println("\t" + neighbor); // 2 3 5
		}

		System.out.println("total " + graph.getEdgeCount());
		System.out.println(graph.toString());

		System.out.println("subMap 0,2");
		NavigableGraph<Integer, Integer, ?> subgraph = graph.subNetwork(0, 2);
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		subgraph.getVertices();
		/*for (int neighbor : subgraph.getVertices()){
			System.out.println("\t"+neighbor);
		}*/
		for (int neighbor : graph.subNetwork(0, 2).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.subNetwork(0, 4);
		System.out.println("subMap 0,4");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.tailNetwork(1);
		System.out.println("tailMap 1");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.headNetwork(4);
		System.out.println("headMap 4");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		/*System.out.println("subMap null,4");
		for (int neighbor : graph.subNetwork(null, 4).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}*/

		System.out.println("loaded graph: " + graph.getVertexCount() + " vertices, " + graph.getEdgeCount() + " edges");
		String filename = "graph.serialized";
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(graph);
			out.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("wrote graph");
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			graph = (SimpleNavigableGraph<Integer, Integer, Object>) in.readObject();
			ParameterizedType ptype = ((ParameterizedType) graph.getClass().getGenericSuperclass());
			for (Type type : ptype.getActualTypeArguments()) {
				System.out.println("type: " + type);
			}
			Class c = graph.getClass();
			System.out.println("class: " + c.toString());
			for (java.lang.reflect.TypeVariable type : c.getTypeParameters()) {
				System.out.println("type: " + type);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		// print out restored time
		System.out.println("loaded graph: " + graph.getVertexCount() + " vertices, " + graph.getEdgeCount() + " edges");
		// print out the current time

		System.out.println("neighbors by 2");
		for (int neighbor : graph.getNeighbors(1, 0, 2)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("neighbors by 4");
		for (int neighbor : graph.getNeighbors(1, 0, 4)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("neighbors from null");
		for (int neighbor : graph.getNeighbors(1, null, 4)) {
			System.out.println("\t" + neighbor);
		}

		System.out.println("subMap 0,2");
		subgraph = graph.subNetwork(0, 2);
		subgraph.getVertices();
		/*for (int neighbor : subgraph.getVertices()){
			System.out.println("\t"+neighbor);
		}*/
		for (int neighbor : graph.subNetwork(0, 2).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("subMap 0,4");
		for (int neighbor : graph.subNetwork(0, 4).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("tailMap 1");
		for (int neighbor : graph.tailNetwork(1).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("headMap 4");
		for (int neighbor : graph.headNetwork(4).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		System.out.println("subMap null,4");
		for (int neighbor : graph.subNetwork(null, 4).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}

		System.out.println("total " + graph.getEdgeCount());

		System.out.println("subMap 0,2");
		subgraph = graph.subNetwork(0, 2);
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		subgraph.getVertices();
		/*for (int neighbor : subgraph.getVertices()){
			System.out.println("\t"+neighbor);
		}*/
		for (int neighbor : graph.subNetwork(0, 2).getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.subNetwork(0, 4);
		System.out.println("subMap 0,4");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.tailNetwork(1);
		System.out.println("tailMap 1");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
		subgraph = graph.headNetwork(4);
		System.out.println("headMap 4");
		System.out.println("edges " + subgraph.getEdgeCount());
		System.out.println(subgraph.toString());
		for (int neighbor : subgraph.getNeighbors(1)) {
			System.out.println("\t" + neighbor);
		}
	}
}
