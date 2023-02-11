import com.kms.katalon.core.configuration.RunConfiguration
import java.nio.file.Path
import java.nio.file.Paths
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import com.kms.katalon.core.util.KeywordUtil

/**
 * This Test Case post-processes the '<projectDir>/sample.har' JSON file 
 * using JayWay JSONPath library.
 * 
 * See https://github.com/json-path/JsonPath
 */
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("sample.har")
Path harRelative = projectDir.relativize(har)

// verify if the HAR file contains a request of which url ends with "jquery.min.js"
String pattern = '/.*jquery\\.min\\.js/'   // a RegEx to match the URL of my interest
String jsonpath = '$.log.entries[?(@.request.url =~ ' + pattern + ')]' // JsonPath instance
ReadContext ctx = JsonPath.parse(har.toFile())
List<String> requests = ctx.read(jsonpath)

if (requests.size() == 0) {
	KeywordUtil.markFailedAndStop(
		"${harRelative} contains no URL that matches ${pattern}")
} else if (requests.size() == 1) {
	KeywordUtil.logInfo("${harRelative} is fine")	
} else {
	KeywordUtil.markFailedAndStop(
		"${harRelative} contains 2 or more URLs that matches ${pattern}")
}
