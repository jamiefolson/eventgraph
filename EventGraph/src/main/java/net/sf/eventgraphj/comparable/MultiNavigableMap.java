package net.sf.eventgraphj.comparable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections15.MultiMap;

public class MultiNavigableMap<K, V> implements MultiMap<K, V> {

	public MultiNavigableMap() {
		this.m_CollectionMap = new TreeMap<K, Collection<V>>();
	}

	public MultiNavigableMap(NavigableMap<K, Collection<V>> map) {
		this.m_CollectionMap = map;
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#ceilingEntry(java.lang.Object)
	 */

	public Entry<K, Collection<V>> ceilingEntry(K key) {
		return this.m_CollectionMap.ceilingEntry(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#ceilingKey(java.lang.Object)
	 */

	public K ceilingKey(K key) {
		return this.m_CollectionMap.ceilingKey(key);
	}

	/**
	 * 
	 * @see java.util.TreeMap#clear()
	 */

	@Override
	public void clear() {
		this.m_CollectionMap.clear();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#comparator()
	 */

	public Comparator<? super K> comparator() {
		return this.m_CollectionMap.comparator();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#containsKey(java.lang.Object)
	 */

	@Override
	public boolean containsKey(Object key) {
		return this.m_CollectionMap.containsKey(key);
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.TreeMap#containsValue(java.lang.Object)
	 */

	@Override
	public boolean containsValue(Object value) {
		for (Entry<K, Collection<V>> entry : this.m_CollectionMap.entrySet()) {
			if (entry.getValue().contains(value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 * @see java.util.TreeMap#descendingKeySet()
	 */

	public NavigableSet<K> descendingKeySet() {
		return this.m_CollectionMap.descendingKeySet();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#descendingMap()
	 */

	public NavigableMap<K, Collection<V>> descendingMap() {
		return this.m_CollectionMap.descendingMap();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#entrySet()
	 */

	@Override
	public Set<Entry<K, Collection<V>>> entrySet() {
		return this.m_CollectionMap.entrySet();
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */

	@Override
	public boolean equals(Object other) {
		return this.m_CollectionMap.equals(((MultiNavigableMap<K, V>) other)
				.map());
	}

	/**
	 * @return
	 * @see java.util.TreeMap#firstEntry()
	 */

	public Entry<K, Collection<V>> firstEntry() {
		return this.m_CollectionMap.firstEntry();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#firstKey()
	 */

	public K firstKey() {
		return this.m_CollectionMap.firstKey();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#floorEntry(java.lang.Object)
	 */

	public Entry<K, Collection<V>> floorEntry(K key) {
		return this.m_CollectionMap.floorEntry(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#floorKey(java.lang.Object)
	 */

	public K floorKey(K key) {
		return this.m_CollectionMap.floorKey(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#get(java.lang.Object)
	 */

	@Override
	public Collection<V> get(Object key) {
		return this.m_CollectionMap.get(key);
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#hashCode()
	 */

	@Override
	public int hashCode() {
		return this.m_CollectionMap.hashCode();
	}

	/**
	 * @param toKey
	 * @param inclusive
	 * @return
	 * @see java.util.TreeMap#headMap(java.lang.Object, boolean)
	 */

	public MultiNavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return new MultiNavigableMap<K, V>(this.m_CollectionMap.headMap(toKey,
				inclusive));
	}

	/**
	 * @param toKey
	 * @return
	 * @see java.util.TreeMap#headMap(java.lang.Object)
	 */

	public MultiNavigableMap<K, V> headMap(K toKey) {
		return this.headMap(toKey, false);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#higherEntry(java.lang.Object)
	 */

	public Entry<K, Collection<V>> higherEntry(K key) {
		return this.m_CollectionMap.higherEntry(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#higherKey(java.lang.Object)
	 */

	public K higherKey(K key) {
		return this.m_CollectionMap.higherKey(key);
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#isEmpty()
	 */

	@Override
	public boolean isEmpty() {
		return this.m_CollectionMap.isEmpty();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#keySet()
	 */

	@Override
	public Set<K> keySet() {
		return this.m_CollectionMap.keySet();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#lastEntry()
	 */

	public Entry<K, Collection<V>> lastEntry() {
		return this.m_CollectionMap.lastEntry();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#lastKey()
	 */

	public K lastKey() {
		return this.m_CollectionMap.lastKey();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#lowerEntry(java.lang.Object)
	 */

	public Entry<K, Collection<V>> lowerEntry(K key) {
		return this.m_CollectionMap.lowerEntry(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#lowerKey(java.lang.Object)
	 */

	public K lowerKey(K key) {
		return this.m_CollectionMap.lowerKey(key);
	}

	/**
	 * @return
	 * @see java.util.TreeMap#navigableKeySet()
	 */

	public NavigableSet<K> navigableKeySet() {
		return this.m_CollectionMap.navigableKeySet();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.TreeMap#remove(java.lang.Object)
	 */

	@Override
	public Collection<V> remove(Object key) {
		return this.m_CollectionMap.remove(key);
	}

	/**
	 * @return
	 * @see java.util.TreeMap#size()
	 */

	@Override
	public int size() {
		return this.m_CollectionMap.size();
	}

	/**
	 * @param fromKey
	 * @param fromInclusive
	 * @param toKey
	 * @param toInclusive
	 * @return
	 * @see java.util.TreeMap#subMap(java.lang.Object, boolean,
	 *      java.lang.Object, boolean)
	 */

	public MultiNavigableMap<K, V> subMap(K fromKey, boolean fromInclusive,
			K toKey, boolean toInclusive) {
		return new MultiNavigableMap<K, V>(this.m_CollectionMap.subMap(fromKey,
				fromInclusive, toKey, toInclusive));
	}

	/**
	 * @param fromKey
	 * @param toKey
	 * @return
	 * @see java.util.TreeMap#subMap(java.lang.Object, java.lang.Object)
	 */

	public MultiNavigableMap<K, V> subMap(K fromKey, K toKey) {
		return this.subMap(fromKey, true, toKey, false);
	}

	/**
	 * @param fromKey
	 * @param inclusive
	 * @return
	 * @see java.util.TreeMap#tailMap(java.lang.Object, boolean)
	 */

	public MultiNavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return new MultiNavigableMap<K, V>(this.m_CollectionMap.tailMap(
				fromKey, inclusive));
	}

	/**
	 * @param fromKey
	 * @return
	 * @see java.util.TreeMap#tailMap(java.lang.Object)
	 */

	public MultiNavigableMap<K, V> tailMap(K fromKey) {
		return this.tailMap(fromKey, true);
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#toString()
	 */

	@Override
	public String toString() {
		return this.m_CollectionMap.toString();
	}

	/**
	 * @return
	 * @see java.util.TreeMap#values()
	 */

	@Override
	public Collection<V> values() {
		Collection<V> allValues = new ArrayList<V>();
		for (Collection<V> values : this.m_CollectionMap.values()) {
			allValues.addAll(values);
		}
		return allValues;
	}

	private final NavigableMap<K, Collection<V>> m_CollectionMap;

	@Override
	public boolean containsValue(Object key, Object value) {
		Collection edges = this.m_CollectionMap.get(key);
		if (edges == null) {
			return false;
		}

		return edges.contains(value);
	}

	@Override
	public Iterator<V> iterator(Object key) {
		return this.m_CollectionMap.get(key).iterator();
	}

	@Override
	public Map<K, Collection<V>> map() {
		return this.m_CollectionMap;
	}

	@Override
	public V put(K key, V value) {
		Collection<V> edges = this.m_CollectionMap.get(key);
		if (edges == null) {
			edges = new ArrayList<V>();
			this.m_CollectionMap.put(key, edges);
		}
		if (edges.add(value)) { // returns true if changes
			return value; // if it changed, return the object as per api
		}
		return null; // if it didn't change, return null as per api
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> other) {
		for (Entry<? extends K, ? extends V> entry : other.entrySet()) {
			this.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void putAll(MultiMap<? extends K, ? extends V> other) {
		for (Entry<? extends K, ?> entry : other.entrySet()) {
			this.putAll(entry.getKey(),
					(Collection<? extends V>) entry.getValue());
		}

	}

	@Override
	public boolean putAll(K key, Collection<? extends V> valueCollection) {
		Collection<V> edges = this.m_CollectionMap.get(key);
		if (edges == null) {
			edges = new ArrayList<V>();
			this.m_CollectionMap.put(key, edges);
		}
		return edges.addAll(valueCollection);
	}

	@Override
	public V remove(Object key, Object value) {
		Collection<V> edges = this.m_CollectionMap.get(key);
		if (edges == null) {
			return null;
		}
		if (edges.remove(value)) {
			return (V) value;
		}
		return null;
	}

	@Override
	public int size(Object key) {
		Collection<V> edges = this.m_CollectionMap.get(key);
		if (edges == null) {
			return 0;
		}
		return edges.size();

	}

	public Entry<K, Collection<V>> pollFirstEntry() {
		return this.m_CollectionMap.pollFirstEntry();
	}

	public Entry<K, Collection<V>> pollLastEntry() {
		return this.m_CollectionMap.pollLastEntry();
	}

}