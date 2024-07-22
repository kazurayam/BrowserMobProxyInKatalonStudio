// Test Cases/post-process/select_entries_of_jquery.min.js

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

// Input
Path projectDir = Paths.get(RunConfiguration.getProjectDir());
Path inputHar = projectDir.resolve("work").resolve(GlobalVariable.TestSuiteShortName + ".har")

// specifeis how we use Jayway JsonPath to transform the input HAR
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
	
	// Now select interesting entries out of theHAR to create a much smaller json file
	List<Map<String, Object>> result =
		JsonPath.parse(Files.newInputStream(har))
			.read("\$['log']['entries'][?]", filter)
									// [?(...)] is a filter expression
	return result
	
	// see https://www.baeldung.com/guide-to-jayway-jsonpath for more about Jayway JsonPath
}

// Output
Path output = projectDir.resolve("work").resolve(GlobalVariable.TestSuiteShortName + ".selection.json")
Files.createDirectories(output.getParent())

// apply the templates which drive Jayway JsonPath
WebUI.callTestCase(findTestCase("Test Cases/main/post-process/HAR Transform"),
					[
						"inputHar": inputHar,
						"templates": cls,
						"outputJson": output,
						"shouldBeLessThan": 20
					])
