import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonOutput
import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har;


WebUI.closeBrowser()

Har har = GlobalVariable.proxy.getHar()

StringWriter sw = new StringWriter()
har.writeTo(sw)

def pp = JsonOutput.prettyPrint(sw.toString())
File f = new File("sample.har")
f.text = pp


GlobalVariable.proxy.stop()
