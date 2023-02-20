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

(proxyForHar, desiredCapabilities, seleniumProxy) = 
    CustomKeywords.'utility.BrowserMobProxyManager.initProxy'()

(proxyForHar, desiredCapabilities) =
    CustomKeywords.'utility.BrowserMobProxyManager.setUpProxy'(proxyForHar, desiredCapabilities, seleniumProxy)

System.setProperty("webdriver.chrome.driver", DriverFactory.getChromeDriverPath())
ChromeOptions options = new ChromeOptions()
options.addArguments("--headless")           // use Chrome Headless browser
options.merge(desiredCapabilities)
WebDriver driver = new ChromeDriver(options)

proxyForHar = CustomKeywords.'utility.BrowserMobProxyManager.setProxy'(proxyForHar)
GlobalVariable.proxy = proxyForHar

DriverFactory.changeWebDriver(driver)

WebUI.navigateToUrl(GlobalVariable.url)