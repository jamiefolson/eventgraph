package net.sourceforge.eventgraphj.comparable;

import java.util.Iterator;

public abstract class IntervalWindowIterator<K extends Comparable<K>> implements Iterator<Interval<K>> {
	K midpoint;
	final Iterator<K> child;
	private K width;

	public IntervalWindowIterator(Iterable<K> childIterable, K width) {
		this.child = childIterable.iterator();
		this.width = width;
	}

	@Override
	public boolean hasNext() {
		return child.hasNext();
	}

	public abstract Interval<K> constructInterval(K midpoint);

	@Override
	public Interval<K> next() {
		this.midpoint = child.next();
		if (midpoint == null)
			return null;
		return constructInterval(midpoint);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {
	/*Iterator<Interval<Double>> iter = new IntervalWindowIterator<Integer>(IncrementIterable.fromIntegers(0, 10, 1),
	        2);
	while (iter.hasNext()) {
		System.out.println(iter.next());
	}*/
	}
}
