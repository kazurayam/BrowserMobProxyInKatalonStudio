import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

TestObject makeTestObject(String id, String xpath) {
	TestObject tObj = new TestObject(id)
	tObj.addProperty("xpath", ConditionType.EQUALS, xpath)
	return tObj	
}

String duckduckgo = 'https://duckduckgo.com/?q=BrowserMob+Proxy&ia=web'

WebUI.navigateToUrl(duckduckgo)

WebUI.verifyElementPresent(
	makeTestObject("section",
		'//*[@id="react-layout"]/div/div/div/div/section[@data-testid="mainline"]'), 10)

WebUI.verifyElementPresent(
	makeTestObject("ol",
		'//*[@id="react-layout"]/div/div/div/div/section[@data-testid="mainline"]/ol'), 10)

List<WebElement> anchors = WebUI.findWebElements(
	makeTestObject('anchors to external URLs', 
		'//*[@id="react-layout"]/div/div/div/div/section[@data-testid="mainline"]/ol/li/article/div/div/a'), 10)

println "anchors.size() = " + anchors.size()

anchors.forEach { anchor ->
	WebUI.navigateToUrl(anchor.getAttribute('href'))
	WebUI.delay(3)
	WebUI.back()	
}

WebUI.closeBrowser()