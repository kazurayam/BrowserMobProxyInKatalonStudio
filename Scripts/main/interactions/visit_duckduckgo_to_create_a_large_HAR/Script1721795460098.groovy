import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

TestObject makeTestObject(String id, String xpath) {
	TestObject tObj = new TestObject(id)
	tObj.addProperty("xpath", ConditionType.EQUALS, xpath)
	return tObj
}

String searchUrl = 'https://duckduckgo.com/?q=browsermob+proxy&ia=web'
WebUI.navigateToUrl(searchUrl)

String prefix = '//div[@id="react-layout"]/div/div/div/div/section[@data-testid="mainline"]'
WebUI.verifyElementPresent(makeTestObject("mainline", prefix), 10)
WebUI.verifyElementPresent(makeTestObject("mainline", prefix + '/ol'), 10)
WebUI.verifyElementPresent(makeTestObject("mainline", prefix + '/ol/li'), 10)

TestObject anchorsTO = makeTestObject("anchors", prefix + '/ol/li/article/div[2]/h2/a')
WebUI.verifyElementPresent(anchorsTO, 10)

List<WebElement> anchors = WebUI.findWebElements(anchorsTO, 10)
assert anchors.size() > 0
List<String> hrefs = []
anchors.forEach { anchor ->
	hrefs.add(anchor.getAttribute("href"))
}

hrefs.forEach { href ->
	WebUI.navigateToUrl(href)
	WebUI.delay(5)                // insert a pause to be gentle to the server(s)
}

WebUI.closeBrowser()