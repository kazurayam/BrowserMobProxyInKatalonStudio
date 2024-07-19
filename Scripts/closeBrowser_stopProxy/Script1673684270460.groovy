import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import groovy.json.JsonOutput
import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har

// close the browser
WebUI.closeBrowser()

if (GlobalVariable.BrowserMobProxyServer != null) {
	// get the HAR content out of the Browser MobProxy Server
	Har har = GlobalVariable.BrowserMobProxyServer.getHar()

	StringWriter sw = new StringWriter()
	har.writeTo(sw)

	// save the json into a file
	def pp = JsonOutput.prettyPrint(sw.toString())
	
	Path f = Paths.get("work/sample.har")
	Files.createDirectories(f.getParent())
	Files.writeString(f, pp)
	WebUI.comment("wrote sample.har file")

	// stop the BrowserMob Proxy Server gracefully
	GlobalVariable.BrowserMobProxyServer.stop()
	GlobalVariable.BrowserMobProxyServer = null
}
