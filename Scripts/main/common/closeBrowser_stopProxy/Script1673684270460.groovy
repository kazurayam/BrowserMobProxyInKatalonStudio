import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

import com.kazurayam.jsonflyweight.JsonFlyweight
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har

Path projectDir = Paths.get(RunConfiguration.getProjectDir())

// close the browser
WebUI.closeBrowser()

if (GlobalVariable.BrowserMobProxyServer != null) {
		
	// get the HAR content out of the Browser MobProxy Server, save it into a temporary file
	Har har = GlobalVariable.BrowserMobProxyServer.getHar()
	Path sourceFile = projectDir.resolve("work")
						.resolve(GlobalVariable.TestSuiteShortName + "-source.har")
	har.writeTo(Files.newOutputStream(sourceFile))
	
	KeywordUtil.logInfo(String.format("[closeBrowser_stopProxy] source HAR size = %,8d bytes", 
										sourceFile.toFile().length()))
	
	// pretty-print the json, save it into a planned file
	Path ppFile = projectDir.resolve("work")
						.resolve(GlobalVariable.TestSuiteShortName + "-pretty.har")
	Files.createDirectories(ppFile.getParent())
	int numLines = JsonFlyweight.prettyPrint(Files.newInputStream(sourceFile), Files.newOutputStream(ppFile))
	
	KeywordUtil.logInfo "[closeBrowser_stopProxy] wrote the ${GlobalVariable.TestSuiteShortName}.pp.har file"
	KeywordUtil.logInfo String.format("[closeBrowser_stopProxy] #lines of HAR = %,8d lines", numLines)
	
	// stop the BrowserMob Proxy Server gracefully
	GlobalVariable.BrowserMobProxyServer.stop()
	GlobalVariable.BrowserMobProxyServer = null
}
