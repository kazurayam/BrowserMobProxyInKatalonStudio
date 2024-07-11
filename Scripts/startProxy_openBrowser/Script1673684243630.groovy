import org.openqa.selenium.Proxy
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import net.lightbody.bmp.BrowserMobProxyServer

BrowserMobProxyServer proxyForHar
DesiredCapabilities desiredCapabilities
Proxy seleniumProxy

// Start up a process of BrowserMob Proxy
(proxyForHar, desiredCapabilities, seleniumProxy) = 
    CustomKeywords.'utility.BrowserMobProxyManager.initProxy'()

// prepare the DesiredCapability for browser to communicate to the proxy 
(proxyForHar, desiredCapabilities) =
    CustomKeywords.'utility.BrowserMobProxyManager.setUpProxy'(proxyForHar, desiredCapabilities, seleniumProxy)

System.setProperty("webdriver.chrome.driver", DriverFactory.getChromeDriverPath())
ChromeOptions options = new ChromeOptions()
options.addArguments("--headless")           // use Chrome Headless browser
options.merge(desiredCapabilities)

// launche the browser with custom configuration
WebDriver driver = new ChromeDriver(options)

// memorize the handle to the proxy
proxyForHar = CustomKeywords.'utility.BrowserMobProxyManager.setProxy'(proxyForHar)
GlobalVariable.proxy = proxyForHar

// let WebUI.* keywords to talk to the custom browser
DriverFactory.changeWebDriver(driver)

// navigate to the target URL
WebUI.navigateToUrl(GlobalVariable.url)