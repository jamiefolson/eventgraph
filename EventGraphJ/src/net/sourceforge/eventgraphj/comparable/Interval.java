package net.sourceforge.eventgraphj.comparable;

public class Interval<K> {
	protected K start, finish;

	public Interval(K start, K finish) {
		this.start = start;
		this.finish = finish;
	}

	public K getStart() {
		return start;
	}

	public K getFinish() {
		return finish;
	}

	public String toString() {
		return "Interval: " + start.toString() + " to " + finish.toString();
	}

	public static Interval<Integer> fromMidpoint(int midpoint, int width) {
		return new Interval<Integer>((int) (midpoint - width / 2.0), (int) (midpoint + width / 2.0));
	}

	public static Interval<Long> fromMidpoint(long midpoint, long width) {
		return new Interval<Long>((long) (midpoint - width / 2.0), (long) (midpoint + width / 2.0));
	}

	public static Interval<Double> fromMidpoint(double midpoint, double width) {
		return new Interval<Double>((double) (midpoint - width / 2.0), (double) (midpoint + width / 2.0));
	}
}
