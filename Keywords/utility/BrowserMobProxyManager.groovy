package utility

import org.openqa.selenium.Proxy

import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.annotation.Keyword

import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.proxy.CaptureType

public class BrowserMobProxyManager {

	@Keyword
	def initProxy() {
		BrowserMobProxyServer initProxy = new BrowserMobProxyServer()
		DesiredCapabilities InitDesCapa = new DesiredCapabilities()
		Proxy initSeleniumProxy = new Proxy()
		return [
			initProxy,
			InitDesCapa,
			initSeleniumProxy
		]
	}

	@Keyword
	def setUpProxy(BrowserMobProxyServer proxy, DesiredCapabilities desiredCapabilities, Proxy seleniumProxy) {
		proxy.start(0)
		String proxyStr = "localhost:" + proxy.getPort();
		seleniumProxy.setHttpProxy(proxyStr);
		seleniumProxy.setSslProxy(proxyStr);
		desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		desiredCapabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		desiredCapabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
		return [
			proxy,
			desiredCapabilities
		]
	}

	@Keyword
	BrowserMobProxyServer setProxy(BrowserMobProxyServer proxy) {
		proxy.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
		proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);
		proxy.newHar("request")
		return proxy
	}
}
