import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.jsonflyweight.FlyPrettyPrinter
import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Timekeeper
import com.kms.katalon.core.configuration.RunConfiguration

import groovy.json.JsonOutput
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.fasterxml.jackson.databind.ObjectMapper


def targets = [
	"gson",
	"jackson",
	"jsonflyweight",
	//"groovy"
]

/**
 * This scripts measures various JSON pretty-printing methods.
 */
int WD = 32

Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path sourceHAR = projectDir.resolve("work/TS3_process_large_HAR.har")

println String.format("%-${WD}s : %11.2f Megabytes", "source HAR file size",
	sourceHAR.toFile().length() / 1000000)  // 11 Megabytes

Timekeeper tk = new Timekeeper()
Measurement m1 = new Measurement.Builder("Pretty printing a large JSON", ["Case"]).build()


if (targets.contains("gson")) {
	// Gson
	m1.before(["Case": "Gson"])
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String source = Files.readString(sourceHAR)
	String pp = gson.toJson(source)                  // this runs fine
	Path out = Files.createTempFile("gson", ".json")
	Files.writeString(out, pp)
	m1.after()
	//
	println String.format("%-${WD}s : %11.2f Megabytes", "Gson pretty JSON length",
		pp.length() / 1000000);   // 11 Megabytes
	println String.format("%-${WD}s : %,8d milliseconds", "Gson duration", 
		m1.getLastRecordDurationMillis())
}

if (targets.contains("jackson")) {
	// Jackson Databind
	m1.before(["Case": "Jackson Databind"])
	ObjectMapper mapper = new ObjectMapper()
	Object source = Files.readString(sourceHAR)
	String pp = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(source)
	Path output = Files.createTempFile("jackson", ".json");
	Files.writeString(output, pp)
	m1.after()
	//
	println String.format("%-${WD}s : %11.2f Megabytes", "Jackson pretty JSON length",
		pp.length() / 1000000);   // 11 Megabytes
	println String.format("%-${WD}s : %,8d milliseconds", "Jackson duration",
		m1.getLastRecordDurationMillis())
}

if (targets.contains("jsonflyweight")) {
	// kazurayam's FlyPrettyPrinter
	m1.before(["Case": "FlyPrettyPrinter"])
	InputStream is = Files.newInputStream(sourceHAR)
	Path output = Files.createTempFile("kazurayam", ".json")
	OutputStream os = Files.newOutputStream(output)
	int nuMegabytesLines = FlyPrettyPrinter.prettyPrint(is, os)          // this runs fine
	m1.after()
	//
	println String.format("%-${WD}s : %,8d milliseconds", "FlyPrettyPrinter duration",
		m1.getLastRecordDurationMillis())
	println String.format("%-${WD}s : %11.2f Megabytes", "FlyPrettyPrinter JSON length",
		output.toFile().length() / 1000000)
	println String.format("%-${WD}s : %11.2f Megalines", "FlyPrettyPrinter #lines",
		nuMegabytesLines / 1000000)
}

if (targets.contains("groovy")) {
	// Groovy's JsonOutput
	m1.before(["Case": "JsonOutput"])
	String s = Files.readString(sourceHAR)
	String t = JsonOutput.prettyPrint(s)         // here OutOfMemoryError is raised
	Path targetJson = Files.createTempFile("groovy", ".json")
	Files.writeString(targetJson, t)
	m1.after()
	//
	println String.format("%-${WD}s : %,8d milliseconds", "Groovy JsonOutput duration", 
		m1.getLastRecordDurationMillis())
}

