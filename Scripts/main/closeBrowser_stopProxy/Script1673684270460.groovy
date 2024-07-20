import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.jsonflyweight.JsonFlyweightPrettyPrinter as PP
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har

// close the browser
WebUI.closeBrowser()

if (GlobalVariable.BrowserMobProxyServer != null) {
	
	// get the HAR content out of the Browser MobProxy Server, save it into a temporary file
	Har har = GlobalVariable.BrowserMobProxyServer.getHar()
	Path tempFile = Files.createTempFile("har", ".tmp")
	har.writeTo(Files.newOutputStream(tempFile))

	// pretty-print the json, save it into a planned file
	Path f = Paths.get("work/sample.har")
	Files.createDirectories(f.getParent())
	PP.prettyPrint(Files.newInputStream(tempFile), Files.newOutputStream(f))
	WebUI.comment("wrote the work/sample.har file")

	// stop the BrowserMob Proxy Server gracefully
	GlobalVariable.BrowserMobProxyServer.stop()
	GlobalVariable.BrowserMobProxyServer = null
}
