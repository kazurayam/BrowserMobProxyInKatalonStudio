import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.ks.WebDriverPlusHARFactory
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil
import internal.GlobalVariable

Path workDir = Paths.get(RunConfiguration.getProjectDir()).resolve("work")
Path outDir = workDir.resolve(GlobalVariable.TestSuiteShortName)
Files.createDirectories(outDir)
Path harFile = outDir.resolve("pretty.har")
Writer bw = Files.newBufferedWriter(harFile)

WebDriverPlusHARFactory.closeBrowserExportHAR(bw)

assert Files.exists(harFile)
assert harFile.toFile().length() > 0
KeywordUtil.logInfo('[closeBrowser_stopProxy] harFile: ' + harFile.toString() + ", " + harFile.toFile().length() + "bytes")
