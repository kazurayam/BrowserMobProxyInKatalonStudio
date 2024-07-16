import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonOutput
import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har;

// close the browser
WebUI.closeBrowser()

// get the HAR content
Har har = GlobalVariable.BrowserMobProxyServer.getHar()

StringWriter sw = new StringWriter()
har.writeTo(sw)

// save the json into a file
def pp = JsonOutput.prettyPrint(sw.toString())
File f = new File("sample.har")
f.text = pp
WebUI.comment("wrote sample.har file")

// terminate the proxy process
GlobalVariable.BrowserMobProxyServer.stop()
