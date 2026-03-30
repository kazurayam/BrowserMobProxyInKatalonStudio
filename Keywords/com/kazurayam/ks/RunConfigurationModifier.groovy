package com.kazurayam.ks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.kms.katalon.core.configuration.RunConfiguration

/**
 * This class modifies the RunConfiguration class using Groovy's Metaprogramming techique.
 */
public class RunConfigurationModifier {
	
	public static void apply() {
		
		/*
		 * The `RunConfiguration.toJson()` returns a JSON string which serialize all of the settings information
		 * of a project passed from Katalon Studio to user's test scripts. In the output JSON, the keys are sorted
		 * in alphabetical ascending order.
		 */
		RunConfiguration.metaClass.'static'.toJson = { ->
			ObjectMapper om = new ObjectMapper()
			om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
			om.configure(SerializationFeature.INDENT_OUTPUT, true)
			String json = om.writeValueAsString(RunConfiguration.localExecutionSettingMapStorage)
			return json
		}
	}

}