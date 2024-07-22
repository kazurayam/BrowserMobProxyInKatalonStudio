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
	
	Timekeeper tk = new Timekeeper()
	Measurement m1 = new Measurement.Builder("Processing a HAR file", ["Case"]).build()
	tk.add(new Table.Builder(m1).build())
	
	// get the HAR content out of the Browser MobProxy Server, save it into a temporary file
		LocalDateTime beforeAction = LocalDateTime.now()
	Har har = GlobalVariable.BrowserMobProxyServer.getHar()
	Path tempFile = Files.createTempFile("har", ".tmp")
	har.writeTo(Files.newOutputStream(tempFile))
		LocalDateTime afterAction = LocalDateTime.now()
		m1.recordDuration(["Case": "get HAR from BrowserMob Proxy"],
			beforeAction, afterAction)

	// pretty-print the json, save it into a planned file
		beforeAction = LocalDateTime.now()
	Path f = projectDir.resolve("work")
						.resolve(GlobalVariable.TestSuiteShortName + ".har")
	Files.createDirectories(f.getParent())
	int numLines = PP.prettyPrint(Files.newInputStream(tempFile), Files.newOutputStream(f))
	KeywordUtil.logInfo "wrote the ${GlobalVariable.TestSuiteShortName}.har file"
	KeywordUtil.logInfo String.format("#lines of HAR = %,8d lines", numLines)
	
		afterAction = LocalDateTime.now()
		m1.recordDuration(["Case": "pretty-print the HAR"],
			beforeAction, afterAction)

	// stop the BrowserMob Proxy Server gracefully
	GlobalVariable.BrowserMobProxyServer.stop()
	GlobalVariable.BrowserMobProxyServer = null
	
	tk.report(projectDir.resolve("work").resolve("${GlobalVariable.TestSuiteShortName}.timekeeper.md"))
}
