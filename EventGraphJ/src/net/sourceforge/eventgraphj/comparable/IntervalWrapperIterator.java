package net.sourceforge.eventgraphj.comparable;

import java.util.Iterator;

public class IntervalWrapperIterator<K extends Comparable<K>> implements Iterator<Interval<K>> {
	K next, current;
	final Iterator<K> child;

	public IntervalWrapperIterator(Iterable<K> childIterable) {
		this.child = childIterable.iterator();
		this.current = child.next();
		this.next = child.next();
	}

	@Override
	public boolean hasNext() {
		return child.hasNext();
	}

	@Override
	public Interval<K> next() {
		this.current = this.next;
		this.next = child.next();
		return new Interval<K>(current, next);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {
		Iterator<Interval<Integer>> iter = new IntervalWrapperIterator<Integer>(
		        new IncrementIterable<Integer>(0, 10, 1) {

			        @Override
			        public Integer add(Integer init, Integer increment) {
				        return init + increment;
			        }

		        });
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
}
