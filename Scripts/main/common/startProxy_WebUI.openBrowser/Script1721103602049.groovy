import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.DriverType
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.driver.WebUIDriverType
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import net.lightbody.bmp.BrowserMobProxyServer

// startProxy_WebUI.openBrowser

BrowserMobProxyServer bmpServer
DesiredCapabilities desiredCapabilities

// start up BrowserMob Proxy Server;
// the reference to the BrowserMob Proxy Server and DesiredCapability will be returned
(bmpServer, desiredCapabilities) =
	CustomKeywords.'com.kazurayam.ks.BrowserMobProxyManager.startupBmpServer'()

GlobalVariable.BrowserMobProxyServer = bmpServer


DriverType driverType = DriverFactory.getExecutedBrowser()
switch (driverType) {
	case WebUIDriverType.CHROME_DRIVER:
	case WebUIDriverType.HEADLESS_DRIVER:
		// pass the desired capabilities so that the browser communicates through the BMPServer
		mergeDesiredCapabilities(desiredCapabilities)
		WebUI.openBrowser('')
		break;
	default:
		throw new IllegalStateException("Only Chrome or Chrome (headless) are supported");
}

/*
 * See https://katalon-studio-8-x--docs-production-katalon.netlify.app/docs/create-tests/manage-projects/project-settings/desired-capabilities/pass-desired-capabilities-at-runtime-in-katalon-studio
 */
def mergeDesiredCapabilities(DesiredCapabilities capabilities) {
	capabilities.asMap().forEach { key, value ->
		RunConfiguration.setWebDriverPreferencesProperty(key, value)
	}
}
