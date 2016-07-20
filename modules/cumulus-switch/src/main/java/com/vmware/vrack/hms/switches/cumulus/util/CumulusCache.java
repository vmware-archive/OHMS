package com.vmware.vrack.hms.switches.cumulus.util;
/**
 * Provides cumulus class the Cumulus Cache object.
 * 
 * Each cumulus-switch object is created with respect to a cumulus based switch. Using the functionality provided,
 * the cumulus object can be configured to provide interactions with the switch. All the functionality is 
 * displayed in the package of classes. 
 */

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.vrack.hms.common.switches.api.SwitchNode;

/**
 * Provides basic implementation for Cumulus's cache.
 * 
 */
public class CumulusCache<T> {
	/**
	 * CumulusCache constructor - returns itself.
	 */
	public CumulusCache () {
		this(0);
	}
	
	/**
	 * Cumulus Cache constructor - creates cache based on a date of expiry for the cache.
	 * 
	 * @param expiry - set expiry date for object
	 */
	public CumulusCache (long expiry) {
		this.expiry = expiry;
		this.cacheMap = new HashMap<SwitchNode, T>();
		this.cachedTimeMap = new HashMap<SwitchNode, Long>();
	}

	/**
	 * Get expiry value for cumulus cache object
	 * 
	 * @return long of expiry date
	 */
	public long getExpiry() {
		return expiry; 
	}
	
	/**
	 * Set expiry date for a cumulus cache object 
	 * 
	 * @param expiry long value for expiry date
	 */
	public void setExpiry(long expiry) {
		this.expiry = expiry;
	}
	
	/**
	 * Set(removes) stale date for provided switch node object.
	 * 
	 * @param node object that contains stale data
	 */
	public void setStale(SwitchNode node) {
		cachedTimeMap.remove(node);
		cacheMap.remove(node);
	}

	/**
	 * Check if provided node is stale, or if it is passed expiry of cache.
	 * 
	 * @param node to check if stale 
	 * @return true/false if Switch node stale
	 */
	public boolean isStale(SwitchNode node) {
		long currentTime = System.currentTimeMillis();
		Long cachedTime = cachedTimeMap.get(node);
		
		if (cachedTime == null)
			return true;
		
		if (expiry == 0) // never expires
			return false;
		else
			return (currentTime - cachedTime.longValue()) > (expiry * 1000);
	}
	
	/**
	 * Get switch node from cache Map
	 * 
	 * @param node object
	 * @return Get the value of the node stored in the CacheMap
	 */
	public T get(SwitchNode node) {
		if (isStale(node)) {
			return null;
		} else {
			return cacheMap.get(node);
		}
	}
	
	/**
	 * Set(add to) the cacheMap and CacheTimeMap with the provided node
	 * 
	 * @param node to add (switch node)
	 * @param data
	 */
	public void set(SwitchNode node, T data) {
		cacheMap.put(node, data);
		cachedTimeMap.put(node, System.currentTimeMillis());
	}

	/** Variables used to monitor the cumulus cache object that stores the Switch Node data and times */
	private long expiry; // Expiry time in seconds; 0 means infinity
	private volatile Map<SwitchNode, T> cacheMap;
	private volatile Map<SwitchNode, Long> cachedTimeMap;
    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(CumulusCache.class);
}
