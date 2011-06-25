package net.sf.eventgraphj.comparable;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class GuiceFun {
	public static interface GProvider {
		public <K, V> Graph<K, V> get();
	}

	public static class SGProvider implements GProvider{
		public <K, V> Graph<K, V> get() {
			return new SparseGraph<K, V>();
		}
	}

	public static class A<K, V> {
		@Inject
		GProvider gprovides;
		Graph<K, V> graph;

		@Inject
		public A(GProvider provides) {
			this.gprovides = provides;
			this.graph = provides.get();
		}

	}

	public static class FunModule extends AbstractModule {

		@Override
		protected void configure() {
			this.bind(Graph.class).to(SparseGraph.class);
			this.bind(GProvider.class).to(SGProvider.class);
		}
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new FunModule());

		Graph<Integer, Integer> test = injector.getInstance(Graph.class);

		System.out.println(test.getClass());

		A a = injector.getInstance(A.class);

		System.out.println(a.graph.getClass());
	}

}
