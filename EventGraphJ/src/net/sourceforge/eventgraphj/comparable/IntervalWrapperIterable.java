package net.sourceforge.eventgraphj.comparable;

import java.util.Iterator;

public class IntervalWrapperIterable<K extends Comparable<K>> implements Iterable<Interval<K>> {
	final Iterable<K> child;

	public IntervalWrapperIterable(Iterable<K> child) {
		this.child = child;
	}

	@Override
	public Iterator<Interval<K>> iterator() {
		return new IntervalWrapperIterator<K>(child);
	}

}
