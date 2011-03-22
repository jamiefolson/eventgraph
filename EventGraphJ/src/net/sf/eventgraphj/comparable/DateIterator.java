package net.sf.eventgraphj.comparable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class DateIterator implements Iterator<Date> {
	public static DateFormat format = new SimpleDateFormat();
	final Calendar start, finish, iter;
	Date next = null;
	final int incrementField;

	public DateIterator(Date start, Date finish, int incrementField) {
		this.start = Calendar.getInstance();
		this.start.setTime(start);
		this.finish = Calendar.getInstance();
		this.finish.setTime(finish);
		this.iter = Calendar.getInstance();
		this.iter.setTime(start);
		this.incrementField = incrementField;
		next = iter.getTime();
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public Date next() {
		Date last = next;
		next = null;
		if (iter.before(finish)) {
			iter.add(this.incrementField, 1);
			if (iter.after(finish)) {
				iter.setTime(finish.getTime());
			}
			next = iter.getTime();
		}
		return last;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public static class DateIntervalIterator implements Iterator<Interval<Date>> {
		final Iterator<Date> iterator;
		Date next = null;

		public DateIntervalIterator(Iterator<Date> iterator) {
			this.iterator = iterator;
			this.next = iterator.next();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Interval<Date> next() {
			Date last = next;
			next = iterator.next();
			return new Interval<Date>(last, next);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public static void main(String[] args) {

		Calendar calendar = Calendar.getInstance();

		calendar.set(90 + 1900, 6, 1);
		Date start = calendar.getTime();
		System.out.println("start: " + format.format(start));
		calendar.set(91 + 1900, 6, 1, 12, 0);
		Date stop = calendar.getTime();
		System.out.println("stop: " + format.format(stop));
		Iterator<Date> test = new DateIterator(start, stop, Calendar.MONTH);
		while (test.hasNext()) {
			System.out.println(format.format(test.next()));
		}
	}

}
