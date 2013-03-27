package net.sf.eventgraphj.comparable;

import net.sf.eventgraphj.comparable.NavigableGraphModule.EdgeNavigableModule;

import com.google.inject.Guice;

public class EdgeNavigableGraphTest extends AbstractNavigableGraphTest {

	public EdgeNavigableGraphTest() {
		this.injector = Guice.createInjector(new EdgeNavigableModule());
	}
}
