import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration

import groovy.json.JsonOutput

/**
 * This Test Case post-processes the '<projectDir>/sample.har' JSON file
 * using JayWay JsonPath library.
 *
 * See https://www.baeldung.com/guide-to-jayway-jsonpath
 */
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("work/sample.har")
InputStream harInputStream = Files.newInputStream(har)

Filter filter = Filter.filter(
	Criteria.where("request.url")
	        .regex(Pattern.compile('.*jquery\\.min\\.js')));

List<Map<String, Object>> squeezed = 
	JsonPath.parse(harInputStream)
		.read("\$['log']['entries'][?]", filter)   
		                        // [?(...)] is a filter expression

def reconstructed = ["log":[
						"version": null,
						"creator": null,
						"pages": null, 
						"entries": squeezed
						]]

String juice = JsonOutput.toJson(reconstructed)
String pretty = JsonOutput.prettyPrint(juice)
Path result = projectDir.resolve("work/squeezed.json")
Files.writeString(result, pretty)

//
int hl = har.toFile().length()
String hls = String.format("%,11d", hl)
int ql = result.toFile().length()
String qls = String.format("%,11d", ql)
int percent = Math.floor(ql * 100 / hl)
println "HAR length      = ${hls}"
println "squeezed length = ${qls} (${percent}%)"

// we expect the result file is far smaller than the source HAR file; less than 5%
assert percent < 5
