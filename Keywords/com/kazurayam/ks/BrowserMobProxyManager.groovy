package com.kazurayam.ks

import org.openqa.selenium.Proxy

import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.annotation.Keyword

import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.proxy.CaptureType

public class BrowserMobProxyManager {

	@Keyword
	def startupBmpServer(BrowserMobProxyServer) {
		//
		BrowserMobProxyServer bmpServer = new BrowserMobProxyServer()
		bmpServer.start(0)
		bmpServer.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
		bmpServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT,
				CaptureType.RESPONSE_CONTENT,
				CaptureType.REQUEST_HEADERS,
				CaptureType.RESPONSE_HEADERS);
		bmpServer.newHar("request")
		//
		String proxyStr = "localhost:" + bmpServer.getPort();
		Proxy seleniumProxy = new Proxy()
		seleniumProxy.setHttpProxy(proxyStr);
		seleniumProxy.setSslProxy(proxyStr);
		//
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities()
		desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		desiredCapabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		desiredCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		//
		return [
			bmpServer,
			desiredCapabilities
		]
	}
}
