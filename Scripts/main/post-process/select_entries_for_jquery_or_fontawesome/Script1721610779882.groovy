// Test Cases/post-process/select_entries_of_jquery.min.js

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

import org.apache.commons.io.FileUtils

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

Path outDir = Paths.get(RunConfiguration.getProjectDir())
				.resolve('work')
				.resolve(GlobalVariable.TestSuiteShortName)

// Input
Path inputHar = outDir.resolve("pretty.har")
assert Files.exists(inputHar)

// Output
Path output = outDir.resolve("extract.har")

// specify how I use Jayway JsonPath to transform the input HAR
Closure cls = { Path har ->
	// I am interested in a HTTP request of which URL contains a string ".jquery.min.js"
	Filter filter1 = Filter.filter(
		Criteria.where("request.url")
				.regex(Pattern.compile('.*jquery\\.min\\.js')));
	
	// I am also interested in  "fontawesome-webfont.woff2"	
	Filter filter2 = Filter.filter(
		 Criteria.where("request.url")
		 		.regex(Pattern.compile('.*fontawesome\\-webfont\\.woff2.*')));
	
	Filter filter = filter1.or(filter2)
	
	// Now select interesting entries out of the HAR to create a much smaller json file
	List<Map<String, Object>> result =
		JsonPath.parse(Files.newInputStream(har))
			.read("\$['log']['entries'][?]", filter)
									// [?(...)] is a filter expression
	return result
}

// apply the templates to transform the input into the output
WebUI.callTestCase(findTestCase("Test Cases/main/post-process/HAR Transform"),
					[
						"inputHar": inputHar,
						"templates": cls,
						"outputJson": output,
						"shouldBeLessThan": 20
					])
