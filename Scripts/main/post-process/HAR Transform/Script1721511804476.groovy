// BrowserMobProxyInKatalonStudio/Test Cases/prostprocess/HARTransformer

import java.nio.charset.StandardCharsets
import java.nio.file.Path

import com.kazurayam.jsonflyweight.FlyPrettyPrinter
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

if (shouldBeLessThan != null) {
	assert shouldBeLessThan instanceof Integer
} else {
	shouldBeLessThan = 10
}


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
// save the selection result into the destination JSON file
int numLines = FlyPrettyPrinter.prettyPrint(rdr, wrt)

KeywordUtil.logInfo String.format("#lines of transform result = %,8d lines", numLines)


// diagnose the size of input/output files
int harLength = inputHar.toFile().length()
int outLength = outputJson.toFile().length()
int perCent = Math.floor(outLength * 100 / harLength)

KeywordUtil.logInfo String.format("input HAR size   = %,10d bytes", harLength)
KeywordUtil.logInfo String.format("output JSON size = %,10d bytes (%d%%)", outLength, perCent)

assert perCent < shouldBeLessThan :
	"the new JSON is expected to be far smaller than the source HAR (less than ${shouldBeLessThan}%) but is not"
