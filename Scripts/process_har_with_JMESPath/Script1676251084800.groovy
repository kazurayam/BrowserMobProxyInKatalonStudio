import java.nio.file.Path
import java.nio.file.Paths

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil

import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.jackson.JacksonRuntime

/**
 * This Test Case post-processes the '<projectDir>/sample.har' JSON file
 * using JMESPath library.
 *
 * See https://jmespath.org/
 */
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("sample.har")
Path harRelative = projectDir.relativize(har)

// verify if the HAR file contains a request of which url ends with "jquery.min.js"
JmesPath<JsonNode> jmespath = new JacksonRuntime()
ObjectMapper om = new ObjectMapper()
String expr = "log.entries[?contains(to_string(request.url), 'jquery.min.js')]"
Expression<JsonNode> expression = jmespath.compile(expr);
JsonNode input = om.readTree(har.toFile());
JsonNode result = expression.search(input);

if (result.size() == 0) {
	KeywordUtil.markFailedAndStop(
		"${harRelative} contains no node that matches ${expr}")
} else if (result.size() == 1) {
	KeywordUtil.logInfo("${harRelative} is fine")
} else {
	KeywordUtil.markFailedAndStop(
		"${harRelative} contains 2 or more nodes that match ${expr}")
}
