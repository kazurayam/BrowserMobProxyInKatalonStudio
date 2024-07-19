package com.kazurayam.ks;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)

public class JSONPrettyPrinterTest {

	private String ugly = '''{"store": {
			"book": [ {"author": "Nigel Rees","title": "Sayings of the Century","price": 8.95
				}, {"author": "J. R. R. Tolkien","title": "The Lord of the Rings","isbn": "0-395-19395-8","price": 22.99
				}
			],"bicycle": {"color": "red","price": 399}}}'''

	@Test
	public void test_smoke() {
		StringReader sr = new StringReader(ugly);
		StringWriter sw = new StringWriter();
		JSONPrettyPrinter.prettyPrintJSON(sr,  sw);
		assertTrue(sw.toString().length() > 0);
		print sw.toString();
	}
	
}