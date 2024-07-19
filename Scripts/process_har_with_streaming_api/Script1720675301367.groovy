import java.nio.file.Path
import java.nio.file.Paths

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.kms.katalon.core.configuration.RunConfiguration

/**
 * https://www.baeldung.com/jackson-streaming-api
 */
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path har = projectDir.resolve("work/sample.har")

JsonFactory jfactory = new JsonFactory()
JsonParser jParser = jfactory.createParser(har.toFile());

SortedSet<String> urls = new TreeSet<>();
while (jParser.nextToken() != null) {
	if ("url".equals(jParser.getCurrentName())) {
		jParser.nextToken()
		urls.add(jParser.getText());
	}
}
jParser.close()

urls.forEach { url ->
	println url
}
