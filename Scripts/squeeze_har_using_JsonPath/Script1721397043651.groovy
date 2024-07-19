import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

import com.jayway.jsonpath.Criteria
import com.jayway.jsonpath.Filter
import com.jayway.jsonpath.JsonPath
import com.kms.katalon.core.configuration.RunConfiguration

import groovy.json.JsonOutput

// Test Cases/squeeze_har_using_JsonPath
/**
 * This script reads a HAR file, filter the array entries that match my interest,
 * write the result into a new json file.
 * 
 * The resulting json will be far smaller in byte size than the original HAR file. 
 * e.g, less than 5%.
 * The resulting json is compact because unnecessary array entries are chomped off.
 * The resulting json is informative, good to share with others.
 * 
 * This script makes use of Jayway JsonPath library in depth.
 * For more information about JsonPath, see 
 * - https://www.baeldung.com/guide-to-jayway-jsonpath
 * 
 * @author kazurayam
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
						"entries": squeezed,
						"comment": ""
						]]

// write the squeezed JSON text into file
String juice = JsonOutput.toJson(reconstructed)
String pretty = JsonOutput.prettyPrint(juice)
Path result = projectDir.resolve("work/squeezed.json")
Files.writeString(result, pretty)

// show size of the files
int hl = har.toFile().length()
String hls = String.format("%,11d", hl)
int ql = result.toFile().length()
String qls = String.format("%,11d", ql)
int percent = Math.floor(ql * 100 / hl)

println "HAR length      = ${hls}"
println "squeezed length = ${qls} (${percent}%)"

// we expect the result file is far smaller than the source HAR file;
// the squeezed file should be less than 5% of the source
assert percent < 5
