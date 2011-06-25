package net.sf.eventgraphj.comparable;

import java.util.Iterator;

public abstract class IntervalWindowIterable<K extends Comparable<K>> implements Iterable<Interval<K>> {
	final Iterable<K> child;
	private K width;

	public IntervalWindowIterable(Iterable<K> child, K width) {
		this.child = child;
		this.width = width;
	}

	public abstract Interval<K> constructInterval(K midpoint);

	@Override
	public Iterator<Interval<K>> iterator() {
		return new IntervalWindowIterator<K>(child, width) {
			public Interval<K> constructInterval(K midpoint) {
				return IntervalWindowIterable.this.constructInterval(midpoint);
			}

		};
	}

	public static IntervalWindowIterable<Integer> fromInteger(Iterable<Integer> iterator, final Integer width) {
		return new IntervalWindowIterable<Integer>(iterator, width) {
			@Override
			public Interval<Integer> constructInterval(Integer midpoint) {
				Interval<Integer> ret = new Interval<Integer>(
				        (int) (midpoint.doubleValue() - width.doubleValue() / 2.0),
				        (int) (midpoint.doubleValue() + width.doubleValue() / 2.0));
				return ret;
			}
		};
	}

	public static IntervalWindowIterable<Long> fromLong(Iterable<Long> iterator, final Long width) {
		return new IntervalWindowIterable<Long>(iterator, width) {
			@Override
			public Interval<Long> constructInterval(Long midpoint) {
				return new Interval<Long>((long) (midpoint.doubleValue() - width.doubleValue() / 2.0), (long) (midpoint
				        .doubleValue() + width.doubleValue() / 2.0));
			}
		};
	}

	public static IntervalWindowIterable<Double> fromDouble(Iterable<Double> iterator, final Double width) {
		return new IntervalWindowIterable<Double>(iterator, width) {
			@Override
			public Interval<Double> constructInterval(Double midpoint) {
				return new Interval<Double>((midpoint.doubleValue() - width.doubleValue() / 2.0), (midpoint
				        .doubleValue() + width.doubleValue() / 2.0));
			}
		};
	}

	public static void main(String[] args) {
		Iterator<Interval<Integer>> iter = IntervalWindowIterable.fromInteger(IncrementIterable.fromIntegers(0, 10, 1),
		        2).iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}

}
