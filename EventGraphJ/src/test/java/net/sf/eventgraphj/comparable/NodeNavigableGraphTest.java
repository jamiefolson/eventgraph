package net.sf.eventgraphj.comparable;

import net.sf.eventgraphj.comparable.NavigableGraphModule.NodeNavigableModule;

import com.google.inject.Guice;

public class NodeNavigableGraphTest extends AbstractNavigableGraphTest {

	public NodeNavigableGraphTest() {
		this.injector = Guice.createInjector(new NodeNavigableModule());
	}
}
