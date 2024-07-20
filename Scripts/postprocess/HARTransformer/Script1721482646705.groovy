// BrowserMobProxyInKatalonStudio/Test Cases/prostprocess/HARTransformer

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.kazurayam.jsonflyweight.JsonFlyweightPrettyPrinter
import com.kms.katalon.core.util.KeywordUtil

import groovy.json.JsonOutput
import groovy.lang.Closure


// check the parameters given by the caller Test Case
assert inputHar != null
assert inputHar instanceof Path

assert templates != null
assert templates instanceof Closure

assert outputJson != null    // Path
assert outputJson instanceof Path


// apply the transformer over the input HAR to get the result
List<Map<String, Object>> result = templates.call(inputHar)

// Re-construct a Map in the format of HAR
def reconstructed = ["log":[
	// "version": null,
	// "creator": null,
	// "pages": null,

	"entries": result,

	//"comment": ""
]]

// jsonify the reconstructed object,
String reconstructedJson = JsonOutput.toJson(reconstructed)

// Reader
Reader rdr = new StringReader(reconstructedJson)

// Writer
Writer wrt = 
	new OutputStreamWriter(
		new FileOutputStream(outputJson.toFile()),
		StandardCharsets.UTF_8)

// pretty-print it and
// save the result into the destination JSON file
JsonFlyweightPrettyPrinter.prettyPrint(rdr, wrt)


// diagnose the size of input/output files
int harLength = inputHar.toFile().length()
int outLength = outputJson.toFile().length()
int perCent = Math.floor(outLength * 100 / harLength)

KeywordUtil.logInfo String.format("input HAR   = %,10d bytes", harLength)
KeywordUtil.logInfo String.format("output JSON = %,10d bytes (%d%%)", outLength, perCent)

assert perCent < 10 :
	"the new JSON is expected to be far smaller than the source HAR (less than 10%) but is not"
