import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

import internal.GlobalVariable

class TestListener {
	
	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext testSuiteContext) {
		GlobalVariable.TestSuiteShortName = 
			toTestSuiteShortName(testSuiteContext.getTestSuiteId())
	}
	
	@BeforeTestCase
	def beforeTestCase(TestCaseContext testCaseContext) {
		GlobalVariable.TestCaseShortName =
			toTestCaseShortName(testCaseContext.getTestCaseId())	
	}
	
	/**
	 * remove prepending "Test Suite/", and translate "/" to "_"
	 * 
	 * shortTestSuiteName("Test Suites/abc/def")
	 *  -> "abc_def"
	 *  
	 * @param testSuiteId
	 * @return
	 */
	private String toTestSuiteShortName(String testSuiteId) {
		return substringAfter(testSuiteId, "Test Suites/").replaceAll("/", "_")
	}
	
	private String toTestCaseShortName(String testCaseId) {
		return substringAfter(testCaseId, "Test Cases/").replaceAll("/", "_")
	}
	
	/**
	 * remove prepending "Test Suite/"
	 * 
	 * substringAfter("Test Suite/abc/def", "Test Suite/")
	 *  -> "abc/def"
	 */
	private String substringAfter(String source, String pattern) {
		int index = source.indexOf(pattern)
		return source.substring(index + pattern.length())	
	}
}