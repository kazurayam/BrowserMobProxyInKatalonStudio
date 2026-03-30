// Test Cases/post-process/select_entries_that_contains_Dockerfile

import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.apache.commons.io.FileUtils

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

Path outDir = Paths.get(RunConfiguration .getProjectDir())
				.resolve('work')
				.resolve(GlobalVariable.TestSuiteShortName)

// Input
Path inputHar = outDir.resolve("pretty.har")
assert Files.exists(inputHar)

// Output
Path output = outDir.resolve("extract.har")

// specifeis how we use Jayway JsonPath to transform the input HAR
Closure cls = { Path har ->
	
	// I am interested in a HTTP response of which status is 301 Moved Permanently
	Filter filter = Filter.filter(
		Criteria.where("response.status").eq(301));
	
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
