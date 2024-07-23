import org.openqa.selenium.WebElement

import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

TestObject makeTestObject(String id, String xpath) {
	TestObject tObj = new TestObject(id)
	tObj.addProperty("xpath", ConditionType.EQUALS, xpath)
	return tObj
}

String searchEngine = 'https://github.com/search?q=browsermob%20proxy&type=repositories'
String prefix = '//div[@data-testid="results-list"]'

WebUI.navigateToUrl(searchEngine)
WebUI.verifyElementPresent(makeTestObject("result-list", prefix), 10)

List<WebElement> h3List = WebUI.findWebElements(makeTestObject("h3s", prefix + '/div//h3'), 10)

20.times() {  // repeat many times just to make the HAR file big enough
	for (int i = 1; i <= h3List.size(); i++) {
		WebUI.navigateToUrl(searchEngine)
		TestObject tObj = makeTestObject("div[${i}]//h3", prefix + "/div[${i}]//h3")
		if (WebUI.waitForElementPresent(tObj, 10)) {
			WebUI.click(tObj)
			WebUI.waitForPageLoad(5)
			WebUI.delay(3)  // insert a pause to be gentle to the server
		}
	}
}

WebUI.closeBrowser()