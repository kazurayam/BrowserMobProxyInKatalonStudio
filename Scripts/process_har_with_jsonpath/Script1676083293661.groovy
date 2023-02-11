import com.kms.katalon.core.configuration.RunConfiguration
import java.nio.file.Path
import java.nio.file.Paths
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext

/**
 * This Test Case post-processes the '<projectDir>/sample.har' JSON file 
 * using JayWay JSONPath library.
 * 
 * See https://github.com/json-path/JsonPath
 */

Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("sample.har")
ReadContext ctx = JsonPath.parse(har.toFile())

// verify if the HAR file contains a request of which url ends with "jquery.min.js"
String jsonpath = '$.log.entries[?(@.request.url =~ /.*jquery\\.min\\.js/)]'
List<String> requests = ctx.read(jsonpath)
assert 1 == requests.size()
