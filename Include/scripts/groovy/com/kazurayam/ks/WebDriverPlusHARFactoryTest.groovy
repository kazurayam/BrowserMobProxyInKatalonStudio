package com.kazurayam.ks

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.BeforeClass;import org.apache.commons.lang.time.StopWatch

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.util.KeywordUtil

@RunWith(JUnit4.class)
public class WebDriverPlusHARFactoryTest {

	private static Path projectDir = Paths.get(RunConfiguration.getProjectDir())
	private static Path outDir

	@BeforeClass
	public static void beforeClass() {
		Path workDir = projectDir.resolve('build/testOutput')
		outDir = workDir.resolve('WebDriverPlusHARFactoryTest')
		//if (Files.exists(outDir)) Files.delete(outDir)
		Files.createDirectories(outDir)
	}

	@Test
	public void testSmoke() {
		WebDriverPlusHARFactory factory = new WebDriverPlusHARFactory()

		factory.openBrowser('')

		StopWatch watch = new StopWatch()
		watch.start()
		long startNavigation = watch.getTime()

		WebUI.navigateToUrl("http://demoaut.katalon.com/")

		long endNavigation = watch.getTime()
		KeywordUtil.logInfo(String.format("[WebDriverPlusHARFactoryTest#testSmoke] endNavigation - startNavigation = %.3fs",
				(endNavigation - startNavigation) / 1000))

		Path outFile = outDir.resolve("pretty.har")
		BufferedWriter bw = Files.newBufferedWriter(outFile)
		factory.closeBrowserExportHAR(bw)

		assert Files.exists(outFile)
		assert outFile.toFile().length()> 0
		
		KeywordUtil.logInfo("[WebDriverPlusHARFactoryTest#testSmoke] outFile: " + "./" + projectDir.relativize(outFile))
	}
}
