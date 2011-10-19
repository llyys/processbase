package org.processbase.bonita.services.impl;
import java.io.File;
import org.apache.log4j.Logger;
import org.ow2.bonita.util.Command;
import org.processbase.ui.core.Constants;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheUtil {
    private static Logger LOGGER = Logger.getLogger(CacheUtil.class);

    protected static CacheManager CACHE_MANAGER = null;
	 static {
	    	try {
	    		final String theBonitaHome = System.getProperty(Constants.BONITA_HOME);
	    		final String pathToCacheConfigFile = new File(theBonitaHome+"/client/web/common/conf").getAbsolutePath()+"/cache-config.xml";
				CACHE_MANAGER = CacheManager.create(pathToCacheConfigFile);
	    	} catch (final Exception e) {
	    		LOGGER.error("Unable to retrieve the path of the cache configuration file.", e);
	    		CACHE_MANAGER = CacheManager.create();
			}
	    }
	 
	 protected static synchronized Cache createCache(final String cacheName, final String diskStorePath) {
			
			//Double-check
			Cache cache = CACHE_MANAGER.getCache(cacheName);		
			if (cache == null) {
				CACHE_MANAGER.addCache(cacheName);
				cache = CACHE_MANAGER.getCache(cacheName);
			}
			if (diskStorePath != null) {
				cache.getCacheConfiguration().setDiskStorePath(diskStorePath);
			}
			return cache;
		}
		
		public static void store(final String cacheName, final String diskStorePath, final Object key, final Object value) {

			Cache cache = CACHE_MANAGER.getCache(cacheName);
			if (cache == null) {
				cache = createCache(cacheName, diskStorePath);
			}

			final Element element = new Element(key, value);
			cache.put(element);
		}

		public static Object get(final String cacheName, final Object key) {

			Object value = null;
			final Cache cache = CACHE_MANAGER.getCache(cacheName);
			if (cache != null) {
				final Element element = cache.get(key);
				if (element != null) {
					value = element.getValue();
				}
			} else {
				LOGGER.debug("Cache with name " + cacheName
						+ " doesn't exists or wasn't created yet.");
			}
			return value;
		}
	  
		public static void clear(final String cacheName) {
			final Cache cache = CACHE_MANAGER.getCache(cacheName);
			if (cache != null) {
				cache.removeAll();
			}
		}
		
		public static void remove(final String cacheName, final Object key) {
			final Cache cache = CACHE_MANAGER.getCache(cacheName);
			if (cache != null) {
				cache.remove(key);
			}
		}
		
		public static <T extends Object> T getOrCache(final String cacheName, final Object key, ICacheDelegate<T> cmd) throws Exception{
			T result=null;
			try {
			result=(T) get(cacheName, key);
			
			if(result==null){
				
					result=cmd.execute();
					store(cacheName, null, key, result);
					return result;
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				clear(cacheName);
				
				//store(cacheName, null, key, result);
			}
			return (T)cmd.execute();
		}
	 
}
