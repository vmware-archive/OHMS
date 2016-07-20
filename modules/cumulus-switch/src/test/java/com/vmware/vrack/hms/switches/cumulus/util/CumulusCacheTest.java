package com.vmware.vrack.hms.switches.cumulus.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.vmware.vrack.hms.common.switches.api.SwitchNode;

public class CumulusCacheTest {
	@Before
	public void setupBefore() {
		s1 = new SwitchNode("S1", "SSH", "1.1.1.1", 21, "cumulus", "root123");
		s2 = new SwitchNode("S2", "SSH", "2.2.2.2", 21, "cumulus", "root123");
	}

	@Test
	public void testCumulusCache() {
		CumulusCache<String> stringCache = new CumulusCache<String>();
		assertNotNull(stringCache);
		
		stringCache.set(s1, "Cached item");
		assertFalse(stringCache.isStale(s1));
		
		assertNotNull(stringCache.get(s1));
		assertNull(stringCache.get(s2));
		
		stringCache.setExpiry(1200);
		stringCache.setStale(s1);
		assertTrue (stringCache.isStale(s1));
	}

	@Test
	public void testCumulusCacheLong() {
		CumulusCache<String> stringCache = new CumulusCache<String>(5);
		assertNotNull(stringCache);
		
		assertEquals(stringCache.getExpiry(), 5);
		stringCache.set(s2, "Cached data");
		
		try {
			Thread.sleep(10 * 1000);
			assertTrue(stringCache.isStale(s2));
		} catch (InterruptedException ie) {
		}
	}

	private SwitchNode s1, s2;
}
