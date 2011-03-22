package net.sf.eventgraphj.comparable;

import java.util.Iterator;

public abstract class IncrementIterator<K extends Comparable<K>> implements Iterator<K> {
	final K start, finish, increment;
	K next, iterate;

	public IncrementIterator(K start, K finish, K increment) {
		this.start = start;
		this.finish = finish;
		this.increment = increment;
		this.iterate = start;
		this.next = this.iterate;
	}

	public abstract K add(K init, K increment);

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public K next() {
		K last = next;
		next = null;
		if (iterate.compareTo(finish) < 0) {
			iterate = add(iterate, increment);
			if (iterate.compareTo(finish) > 0) {
				iterate = finish;
			}
			next = iterate;
		}
		return last;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static void main(String[] args) {
		Iterator<Integer> iter = new IncrementIterator<Integer>(0, 10, 1) {

			@Override
			public Integer add(Integer init, Integer increment) {
				return init + increment;
			}

		};
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
}
