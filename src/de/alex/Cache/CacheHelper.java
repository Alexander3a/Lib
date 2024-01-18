package de.alex.Cache;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CacheHelper<T> { // I wish I could include this like in c++ a header file in different files not like this

	public CacheHelper() {
		cache= new HashMap<>();
	}

	public CacheHelper(HashMap<String, Map.Entry<Long, T>> cache) {
		this.cache = cache;
	}

	private final transient HashMap<String, Map.Entry<Long,T>> cache;
	public T tryAndGetFromCache(String path, Long expire){
		return tryAndGetFromCache(path,expire,()-> null);
	}
	public T tryAndGetFromCache(String path, Long expire, Supplier<T> orGetFunction){
		if(cache.containsKey(path)){
			Map.Entry<Long,T> cacheEntry = cache.get(path);
			if(System.currentTimeMillis()-expire-cacheEntry.getKey()<0){
				System.out.println("returned cached value");
				return cacheEntry.getValue();
			}
//                System.out.println("cache expired "+(System.currentTimeMillis()-expire-cacheEntry.getKey())+"ms ago");
		}
		T orGet;
		try {
			orGet = orGetFunction.get();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		if(orGet!=null){
			addToCache(path,orGet);
		}
		return orGet;
	}
	public void addToCache(String path, T value){
		cache.put(path,new AbstractMap.SimpleEntry<>(System.currentTimeMillis(),value));
	}
}
