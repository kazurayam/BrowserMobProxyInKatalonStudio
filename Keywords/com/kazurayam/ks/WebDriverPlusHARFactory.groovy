package com.kazurayam.ks

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

import org.apache.commons.lang.time.StopWatch

import com.kazurayam.jsonflyweight.JsonFlyweight
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.IDriverType
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.core.har.Har
import net.lightbody.bmp.proxy.CaptureType

/**
 * Manage web browsers (Chrome, FF, Edge)  and BrowserMob Proxy integrated.
 * Let you get HTTP Archieve (HAR) that contains the payload HTTP Requests and Responses recorded during your test.
 * 
 * Is a Singleton class which lives in a Test Suite scope.
 */
public class WebDriverPlusHARFactory {

	private static BrowserMobProxyServer bmpServer = null;

	/**
	 * The constructor should have been declared private, but Katalon Studio requires it to be public 
	 */
	public WebDriverPlusHARFactory() {}

	/**
	 * Call WebUI.openBrowser keyword to start WebDriver, 
	 * Start BrowserMob Proxy background if the the browser type is one of Forefox, Chrome or Edge on the desktop.
	 * Remote browser and browsers on moble device is not supported.
	 */
	@Keyword
	public static void openBrowser(String url) {
		Objects.requireNonNull(url)
		long startTime, bmpLaunched, browserOpened, endTime
		StopWatch  watch = new StopWatch()
		watch.start()
		startTime = watch.getTime()
		if (bmpServer == null) {
			// check the browser-type and configure the proxy
			IDriverType driverType = DriverFactory.getExecutedBrowser()
			switch (driverType) {
				case WebUIDriverType.FIREFOX_DRIVER:
				case WebUIDriverType.FIREFOX_HEADLESS_DRIVER:
				case WebUIDriverType.CHROME_DRIVER:
				case WebUIDriverType.HEADLESS_DRIVER:
				case WebUIDriverType.EDGE_CHROMIUM_DRIVER:
					// start the BrowserMob Proxy Server
					bmpServer = startBmpServer()
					bmpLaunched = watch.getTime()
					break;
				default:
					KeywordUtil.markWarning("Exporting HAR for " + driverType.name + " is not supported");
			}
		}
		// now we open a browser, which is connected to the BrowserMob Proxy server
		WebUI.openBrowser(url)
		browserOpened = watch.getTime()
		endTime = watch.getTime()
		if (bmpLaunched != 0) {
			KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#openBrowser] bmpLaunched - startTime: %.3fs', (bmpLaunched - startTime) / 1000))
			KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#openBrowser] browserOpened - bmpLaunched: %.3fs', (browserOpened - bmpLaunched) / 1000))
		}
		KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#openBrowser] endTime - startTime: %.3fs', (endTime - startTime) / 1000))
		watch.stop()
	}

	def static startBmpServer() {
		BrowserMobProxyServer server = new BrowserMobProxyServer()
		server.start(0)
		server.setHarCaptureTypes(CaptureType.getAllContentCaptureTypes());
		server.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT,
				CaptureType.RESPONSE_CONTENT,
				CaptureType.REQUEST_HEADERS,
				CaptureType.RESPONSE_HEADERS);
		server.newHar("request")
		// Modify the RunConfiguration object dynamically using Groovy's Meta-programming technique
		RunConfigurationModifier.apply()
		// pass the port number that Proxy listed on, which WebDriver should connect to.
		// Thus the browser communicates the target URL via the Proxy.
		RunConfiguration.setHttpProxyInformation('localhost', server.getPort())
		// set the preference "acceptInsecureCerts" to be true
		RunConfiguration.setAcceptInsecureCerts(true)
		return server
	}

	/**
	 * call WebUI.closeBrowser keyword, export HTTP Archive from BrowserMob Proxy,
	 * serialize into the file, stop the proxy 
	 */
	@Keyword
	public static void closeBrowserExportHAR(String filepath) {
		Objects.requireNonNull(filepath)
		File outFile = new File(filepath)
		closeBrowserExportHAR(filepath)
	}
	
	public static void closeBrowserExportHAR(File outFile) {
		Objects.requireNonNull(outFile)	
		Path path = outFile.toPath()
		BufferedWriter bw = Files.newBufferedWriter(path)
		closeBrowserExportHAR(bw)
	}
	
	/**
	 * call WebUI.closeBrowser keyword, export HTTP Archive from BrowserMob Proxy, 
	 * serialize it into the Writer, stop the proxy
	 * @param bw Writer into which the HAR will be serialized
	 */
	public static void closeBrowserExportHAR(Writer wr) {
		Objects.requireNonNull(wr)
		StopWatch watch = new StopWatch()
		long startTime, browserClosedTime, harAquiredTime, harExportedTime, endTime;
		watch.start()
		WebUI.closeBrowser()
		browserClosedTime = watch.getTime()
		//
		if (bmpServer != null) {
			// get the HAR content out of the BrowserMob Proxy Server, save it into a temp file
			Har har = bmpServer.getHar()
			Path sourceFile = Files.createTempFile('WebDriverPlusHARFactory-source-', '.json')
			har.writeTo(Files.newBufferedWriter(sourceFile, StandardOpenOption.TRUNCATE_EXISTING))
			harAquiredTime = watch.getTime()
			KeywordUtil.logInfo(String.format("[WebDriverPlusHARFactory#closeBrowserExportHAR] source HAR size = %,8d bytes",
					sourceFile.toFile().length()))
			// pretty-print the json, save it into the target
			BufferedReader br = Files.newBufferedReader(sourceFile)
			BufferedWriter bw = new BufferedWriter(wr)
			int numLines = JsonFlyweight.prettyPrint(br, bw)
			KeywordUtil.logInfo(String.format("[WebDriverPlusHARFactory#closeBrowserExportHAR] number of lines in pretty-printed HAR = %,8d lines", numLines))
			harExportedTime = watch.getTime()
			// now we are done
			bmpServer.stop()
			// prepare for the next invocation just in case
			bmpServer == null
		}
		endTime = watch.getTime()
		watch.stop()
		KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#closeBrowserExportHAR] browserClosedTime - startTime = %.3fs', (browserClosedTime - startTime) / 1000))
		KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#closeBrowserExportHAR] harAquiredTime - browserClosedTime = %.3fs', (harAquiredTime - browserClosedTime) / 1000))
		KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#closeBrowserExportHAR] harExportedTime - harAquiredTime = %.3fs', (harExportedTime - harAquiredTime) / 1000))
		KeywordUtil.logInfo(String.format('[WebDriverPlusHARFactory#closeBrowserExportHAR] endTime - startTime = %.3fs', (endTime - startTime) / 1000))
	}
}
