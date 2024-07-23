import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

import com.kazurayam.jsonflyweight.FlyPrettyPrinter as PP
import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable
import net.lightbody.bmp.core.har.Har

Path projectDir = Paths.get(RunConfiguration.getProjectDir())

// close the browser
WebUI.closeBrowser()

if (GlobalVariable.BrowserMobProxyServer != null) {
	
	Measurement m1 = 
		new Measurement.Builder("Acquiring a HAR file from BrowserMob Proxy",["Case"])
				.build()
	
	// get the HAR content out of the Browser MobProxy Server, save it into a temporary file
	m1.before(["Case": "get HAR from BrowserMob Proxy"])
	Har har = GlobalVariable.BrowserMobProxyServer.getHar()
	Path sourceFile = projectDir.resolve("work")
						.resolve(GlobalVariable.TestSuiteShortName + ".har")
	har.writeTo(Files.newOutputStream(sourceFile))
	m1.after()
	
	KeywordUtil.logInfo("[closeBrowser_stopProxy] getting the original HAR took " 
		+ m1.getLastRecordDuration().toMillis() + " msecs")
	KeywordUtil.logInfo(String.format("[closeBrowser_stopProxy] source HAR size = %,8d bytes", 
										sourceFile.toFile().length()))
	
	// pretty-print the json, save it into a planned file
	m1.before(["Case": "pretty-print the HAR"])
	Path ppFile = projectDir.resolve("work")
						.resolve(GlobalVariable.TestSuiteShortName + ".pp.har")
	Files.createDirectories(ppFile.getParent())
	int numLines = PP.prettyPrint(Files.newInputStream(sourceFile), Files.newOutputStream(ppFile))
	m1.after()
	
	KeywordUtil.logInfo "[closeBrowser_stopProxy] wrote the ${GlobalVariable.TestSuiteShortName}.pp.har file"
	KeywordUtil.logInfo String.format("[closeBrowser_stopProxy] pretty-printing the HAR took %,6d msecs", 
										m1.getLastRecordDuration().toMillis())
	KeywordUtil.logInfo String.format("[closeBrowser_stopProxy] #lines of HAR = %,8d lines", numLines)
	
	// write the report into a local file *.timekeepr.md
	Timekeeper tk = new Timekeeper.Builder()
						.table(new Table.Builder(m1).build())
						.build()
	Path reportFile = projectDir.resolve("work").resolve("${GlobalVariable.TestSuiteShortName}.aquiring.md") 
	tk.report(reportFile)
	
	// stop the BrowserMob Proxy Server gracefully
	GlobalVariable.BrowserMobProxyServer.stop()
	GlobalVariable.BrowserMobProxyServer = null
}
