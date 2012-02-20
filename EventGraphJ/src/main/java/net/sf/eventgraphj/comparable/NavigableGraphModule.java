package net.sf.eventgraphj.comparable;

import java.util.NavigableMap;

import net.sf.eventgraphj.comparable.GraphProvider.SparseGraphProvider;
import net.sf.eventgraphj.comparable.MapProvider.MyMapProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class NavigableGraphModule extends AbstractModule {
	public static final Injector EDGE_NAVIGABLE = Guice.createInjector(new EdgeNavigableModule());
	public static final Injector NODE_NAVIGABLE = Guice.createInjector(new NodeNavigableModule());

	public static final Injector BASIC_NAVIGABLE = Guice.createInjector(new BasicNavigableModule());

	
	public static class EdgeNavigableModule extends NavigableGraphModule {
		@Override
		protected void configure() {
			super.configure();
			this.bind(NavigableGraph.class).to(DyadNavigableGraph.class);
		}
	}

	public static class NodeNavigableModule extends NavigableGraphModule {
		@Override
		protected void configure() {
			super.configure();
			this.bind(NavigableGraph.class).to(NodeNavigableGraph.class);
		}
	}

	public static class BasicNavigableModule extends NavigableGraphModule {
		@Override
		protected void configure() {
			super.configure();
			this.bind(NavigableGraph.class).to(BasicNavigableGraph.class);
		}
	}

	@Override
	protected void configure() {
		this.bind(NavigableMap.class).to(MyTreeMap.class);
		this.bind(Graph.class).annotatedWith(Names.named("EdgeGraph")).to(SparseGraph.class);
		this.bind(GraphProvider.class).annotatedWith(Names.named("EdgeGraph")).to(SparseGraphProvider.class);
		this.bind(MapProvider.class).to(MyMapProvider.class);
	}

	/*public <K, V> NavigableMap<K, V> createTreeMap() {
		return new MyTreeMap<K, V>();
	}*/
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new EdgeNavigableModule());

		/*Provider<Graph<Integer, Integer>> provides = injector
				.getProvider(Graph.class);*/

		NavigableGraph<Integer, Integer, Integer> test = injector.getInstance(NavigableGraph.class);
		test.addEdge(1, 2, 3);

	}

}
