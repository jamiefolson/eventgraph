package net.sf.eventgraphj.comparable;

import net.sf.eventgraphj.comparable.NavigableGraphModule.BasicNavigableModule;

import com.google.inject.Guice;

public class BasicNavigableGraphTest extends AbstractNavigableGraphTest {

	public BasicNavigableGraphTest() {
		this.injector = Guice.createInjector(new BasicNavigableModule());
	}
}
