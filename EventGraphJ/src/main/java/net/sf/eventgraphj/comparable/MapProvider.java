package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.NavigableMap;

public interface MapProvider {
	public <K, V> NavigableMap<K, V> get();

	public static class MyMapProvider implements MapProvider, Serializable {

		@Override
		public <K, V> NavigableMap<K, V> get() {
			return new MyTreeMap<K, V>();
		}

	}
}
