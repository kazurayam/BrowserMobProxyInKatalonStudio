package com.kazurayam.ks

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

import org.junit.BeforeClass
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.network.ProxyInformation

@RunWith(JUnit4.class)
public class RunConfigurationModifierTest {

	@BeforeClass
	public static void beforeClass() {
		RunConfigurationModifier.apply()
	}

	@Test
	public void test_toJson() {
		String json = RunConfiguration.toJson()
		//println json
		assertNotNull(json)
		assertTrue(json.length() > 0)
	}

	@Test
	public void test_setHttpProxyInformation() {
		RunConfiguration.setHttpProxyInformation('localhost', 9998)
		String json = RunConfiguration.toJson()
		println json
		ProxyInformation proxyInfo = RunConfiguration.getProxyInformation()
		assertEquals('MANUAL_CONFIG', proxyInfo.getProxyOption())
		assertEquals('HTTP', proxyInfo.getProxyServerType())
		assertEquals('localhost', proxyInfo.getProxyServerAddress())
		assertEquals(9998, proxyInfo.getProxyServerPort())
	}

	@Test
	public void test_setAcceptInsecureCerts() {
		RunConfiguration.setAcceptInsecureCerts(true)
		String json = RunConfiguration.toJson()
		//println json
		assertTrue("RunConfiguration JSON does not contain a string acceptInsecureCerts",
				json.contains('acceptInsecureCerts'))
	}
}
