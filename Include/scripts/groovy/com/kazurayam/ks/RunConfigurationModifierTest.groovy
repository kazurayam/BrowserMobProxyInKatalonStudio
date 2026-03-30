package com.kazurayam.ks

import com.kms.katalon.core.configuration.RunConfiguration

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class RunConfigurationModifierTest {

	@Test
	public void test_toJson() {
		RunConfigurationModifier.apply()
		String json = RunConfiguration.toJson()
		println json
	}
}
