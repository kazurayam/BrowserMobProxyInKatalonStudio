import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.webui.driver.DriverFactory

import internal.GlobalVariable
import net.lightbody.bmp.BrowserMobProxyServer

BrowserMobProxyServer bmpServer
DesiredCapabilities desiredCapabilities

// start up BrowserMob Proxy Server;
// the reference to the BrowserMob Proxy Server and DesiredCapability will be returned
(bmpServer, desiredCapabilities) = 
	CustomKeywords.'com.kazurayam.ks.BrowserMobProxyManager.startupBmpServer'()

GlobalVariable.BrowserMobProxyServer = bmpServer
	
// launch Chrome browser with desired capabilities so that it communicates through the BMPServer
System.setProperty("webdriver.chrome.driver", DriverFactory.getChromeDriverPath())
ChromeOptions options = new ChromeOptions()
options.addArguments("--headless")           // use Chrome Headless browser
options.merge(desiredCapabilities)
WebDriver driver = new ChromeDriver(options)
DriverFactory.changeWebDriver(driver)
