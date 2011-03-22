package net.sf.eventgraphj.comparable;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public abstract class IncrementIterable<K extends Comparable<K>> implements Iterable<K> {
	final K start, finish, increment;

	public IncrementIterable(K start, K finish, K increment) {
		this.start = start;
		this.finish = finish;
		this.increment = increment;
	}

	public abstract K add(K init, K increment);

	@Override
	public Iterator<K> iterator() {
		return new IncrementIterator<K>(start, finish, increment) {

			@Override
			public K add(K init, K increment) {
				return IncrementIterable.this.add(init, increment);
			}

		};
	}

	public static IncrementIterable<Integer> fromIntegers(Integer start, Integer stop, Integer increment) {
		return new IncrementIterable<Integer>(start, stop, increment) {
			@Override
			public Integer add(Integer init, Integer increment) {
				return init + increment;
			}
		};
	}

	public static IncrementIterable<Long> fromLong(Long start, Long stop, Long increment) {
		return new IncrementIterable<Long>(start, stop, increment) {
			@Override
			public Long add(Long init, Long increment) {
				return init + increment;
			}
		};
	}

	public static IncrementIterable<Double> fromDouble(Double start, Double stop, Double increment) {
		return new IncrementIterable<Double>(start, stop, increment) {
			@Override
			public Double add(Double init, Double increment) {
				return init + increment;
			}
		};
	}

	public static IncrementIterable<Date> fromDate(Date start, Date stop, final int incrementAmount,
	        final int incrementField) {
		return new IncrementIterable<Date>(start, stop, null) {
			@Override
			public Date add(Date init, Date increment) {
				Calendar next = Calendar.getInstance();
				next.setTime(init);
				next.add(incrementField, incrementAmount);
				return next.getTime();
			}
		};
	}

	public static void main(String[] args) {
		IncrementIterable<Long> test = IncrementIterable.fromLong(10l, 100l, 25l);
		for (Long t : test) {
			System.out.println(t);
		}
	}
}
