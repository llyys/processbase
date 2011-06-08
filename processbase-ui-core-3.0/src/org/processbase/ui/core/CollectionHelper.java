package org.processbase.ui.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class CollectionHelper<T> {
	
	public Map<String, List<T>> groupBy(Collection<T> items, GroupKey<T> compare ){
		Map<String, List<T>> groups = new HashMap<String, List<T>>();
	    for (T t : items) {
			String key = compare.key(t);
			List<T> group=groups.get(key);
			if(group==null){
				group=new ArrayList<T>();
				groups.put(key, group);
			}
			group.add(t);
		}   
		return groups;		
	}
	
	public interface GroupKey<T>{
		public String key(T source);
	}
	
	
}
