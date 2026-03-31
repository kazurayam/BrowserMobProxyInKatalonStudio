package com.kms.katalon.core.configuration

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.network.ProxyInformation

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RunConfigurationTest {

	@Test
	public void test_getProxyInformation() {
		ProxyInformation proxyInfo = RunConfiguration.getProxyInformation()
		println "[RunConfigurationTest#test_getProxyInformation] proxyInformation: " + proxyInfo.toString()
		// proxyInformation: ProxyInformation { proxyOption=MANUAL, proxyServerType=HTTP, username=, password=********, proxyServerAddress=localhost, proxyServerPort=9999, executionList="", isApplyToDesiredCapabilities=true }

	}
}
