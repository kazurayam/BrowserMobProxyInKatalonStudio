// Test Cases/post-process/select_entries_that_contains_Dockerfile

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

// Input
Path projectDir = Paths.get(RunConfiguration.getProjectDir());
Path inputHar = projectDir.resolve("work").resolve(GlobalVariable.TestSuiteShortName + "-pretty.har")

// Output
Path output = projectDir.resolve("work").resolve(GlobalVariable.TestSuiteShortName + "-selection.har")
Files.createDirectories(output.getParent())

// specifeis how we use Jayway JsonPath to transform the input HAR
Closure cls = { Path har ->
	
	// I am interested in a HTTP request of which Response contains a string "Dockerfile" somewhere in the content text
	//Filter filter = Filter.filter(
	//	Criteria.where("response.content.text")
	//			.regex(Pattern.compile('.*html.*')));
	Filter filter = Filter.filter(
		Criteria.where("response.status").eq(302));
	
	// Now select interesting entries out of theHAR to create a much smaller json file
	List<Map<String, Object>> result =
		JsonPath.parse(Files.newInputStream(har))
			.read("\$['log']['entries'][?]", filter)
									// [?(...)] is a filter expression					 
	return result
}

// apply the templates which drive Jayway JsonPath
WebUI.callTestCase(findTestCase("Test Cases/main/post-process/HAR Transform"),
					[
						"inputHar": inputHar,
						"templates": cls,
						"outputJson": output,
						"shouldBeLessThan": 20
					])

