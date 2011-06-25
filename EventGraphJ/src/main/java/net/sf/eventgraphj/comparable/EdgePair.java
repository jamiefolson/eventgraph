package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.Map.Entry;

public class EdgePair<K extends Comparable<K>, V> implements Entry<K, V>,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final K key;
	final V value;
	final boolean isUnique;

	public EdgePair(K key, V value) {
		this(key, value, false);
	}

	public EdgePair(K key) {
		this(key, null, false);
	}

	public EdgePair(K key, boolean isUnique) {
		this(key, null, isUnique);
	}

	public EdgePair(K key, V value, boolean isUnique) {
		this.key = key;
		this.value = value;
		this.isUnique = isUnique;
	}

	@Override
	public K getKey() {
		return this.key;
	}

	@Override
	public V getValue() {
		return this.value;
	}

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object o) {
		if (this.isUnique) {
			return this == o;
		} else if (o instanceof EdgePair) {

			EdgePair e = (EdgePair) o;
			return this.safeEquals(this.key, e.getKey())
					&& this.safeEquals(this.value, e.getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {

		if (this.isUnique) {
			return super.hashCode();
		}
		int keyHash = (this.key == null ? 0 : this.key.hashCode());
		int valueHash = (this.value == null ? 0 : this.value.hashCode());
		return keyHash ^ valueHash;

	}

	@Override
	public String toString() {
		return this.key + "=" + this.value;
	}

	/**
	 * Test two values for equality. Differs from o1.equals(o2) only in that it
	 * copes with <tt>null</tt> o1 properly.
	 */
	final boolean safeEquals(Object o1, Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}

}
