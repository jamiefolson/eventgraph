package net.sf.eventgraphj.comparable;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public class EdgeEntry<K extends Comparable<K>,V> implements Entry<K,V>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final K key;
	final V value;
	public EdgeEntry(K key, V value){
		this.key = key;
		this.value = value;
	}
	public EdgeEntry(K key){
		this(key, null);
	}
	
	public K getKey(){
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object o) {
    	return this==o;
    	/*if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?,?> e = (Map.Entry<?,?>)o;

        return valEquals(key,e.getKey()) && valEquals(value,e.getValue());*/
    }

    public int hashCode() {
        /*int keyHash = (key==null ? 0 : key.hashCode());
        int valueHash = (value==null ? 0 : value.hashCode());
        return keyHash ^ valueHash;*/
    	return super.hashCode();
    }

    public String toString() {
        return key + "=" + value;
    }
    /**
     * Test two values for equality.  Differs from o1.equals(o2) only in
     * that it copes with <tt>null</tt> o1 properly.
     */
    final static boolean valEquals(Object o1, Object o2) {
        return (o1==null ? o2==null : o1.equals(o2));
    }

}
