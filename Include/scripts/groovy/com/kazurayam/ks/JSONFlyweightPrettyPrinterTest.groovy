package com.kazurayam.ks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.kms.katalon.core.configuration.RunConfiguration;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

@RunWith(JUnit4.class)

public class JSONFlyweightPrettyPrinterTest {

	private String ugly = '''{"store": {
			"book": [ {"author": "Nigel Rees","title": "Sayings of the Century","price": 8.95
				}, {"author": "J. R. R. Tolkien","title": "The Lord of the Rings","isbn": "0-395-19395-8","price": 22.99
				}
			],"bicycle": {"color": "red","price": 399}}}'''

	@Test
	public void test_smoke() {
		StringReader sr = new StringReader(ugly);
		StringWriter sw = new StringWriter();
		JSONFlyweightPrettyPrinter.prettyPrint(sr,  sw);
		assertTrue(sw.toString().length() > 0);
		println sw.toString();
	}

	@Test
	public void test_pretty_print_large_JSON() {
		Path projectDir = Paths.get(RunConfiguration.getProjectDir());
		// setup the input
		Path har = projectDir.resolve("work/sample.har");
		assert Files.exists(har): "${har} is not there. Please run the Test Suites/TS1 to prepare it"
		// setup the output
		Path out = projectDir.resolve("work/pp.json");
		Files.createDirectories(out.getParent())
		if (Files.exists(out)) {
			Files.delete(out)
		}
		// do pretty-printing
		InputStream is = Files.newInputStream(har)
		OutputStream os = Files.newOutputStream(out)
		JSONFlyweightPrettyPrinter.prettyPrint(is,  os);
		// verify the output
		assertTrue(Files.exists(out));
		assertTrue(out.toFile().length() > 0);
	}

	@Test
	public void test_empty_array() {
		String json = '''{"array":[]}'''
		StringReader sr = new StringReader(json);
		StringWriter sw = new StringWriter();
		JSONFlyweightPrettyPrinter.prettyPrint(sr, sw)
		println sw.toString()
	}

	@Test
	public void test_empty_object() {
		String json = '''{"array":{}}'''
		StringReader sr = new StringReader(json);
		StringWriter sw = new StringWriter();
		JSONFlyweightPrettyPrinter.prettyPrint(sr, sw)
		println sw.toString()
	}
}