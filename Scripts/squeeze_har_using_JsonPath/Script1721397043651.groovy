import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

import com.kazurayam.jsonflyweight.JsonFlyweightPrettyPrinter
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
// Input
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("work/sample.har")

// I am solely interested in a HTTP request of which URL contains a string ".jquery.min.js"
Filter filter = Filter.filter(
	Criteria.where("request.url")
	        .regex(Pattern.compile('.*jquery\\.min\\.js')));

// Now squeeze it!
List<Map<String, Object>> squeezed = 
	JsonPath.parse(Files.newInputStream(har))
		.read("\$['log']['entries'][?]", filter)   
		                        // [?(...)] is a filter expression

// Re-construct a Map in the format of HAR
def reconstructed = ["log":[
						// "version": null,
						// "creator": null,
						// "pages": null, 
						
						"entries": squeezed,
						
						//"comment": ""
						]]

// the destination file where we store the squeezed JSON content
Path squeezedFile = projectDir.resolve("work/squeezed.json")
Files.createDirectories(squeezedFile.getParent());

// jsonify the cobject, pretty-print it, save the result into the destination file
JsonFlyweightPrettyPrinter.prettyPrint(
	new StringReader(JsonOutput.toJson(reconstructed)),
	new OutputStreamWriter(new FileOutputStream(squeezedFile.toFile()), StandardCharsets.UTF_8)
	);

// diagnoze the size of 2 files
int hl = har.toFile().length()
String hls = String.format("%,11d", hl)
int ql = squeezedFile.toFile().length()
String qls = String.format("%,11d", ql)
int percent = Math.floor(ql * 100 / hl)

println "HAR length      = ${hls}"
println "squeezed length = ${qls} (${percent}%)"

// The result file will be far smaller than the source HAR file.
// Here we expect the squeezed file should be less than 5% of the source
assert percent < 5
