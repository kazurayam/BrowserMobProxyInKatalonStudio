package com.kazurayam.ks

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.util.KeywordUtil

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
		RunConfiguration.metaClass.'static'.toJson = {
			->
			ObjectMapper om = new ObjectMapper()
			om.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
			om.configure(SerializationFeature.INDENT_OUTPUT, true)
			String json = om.writeValueAsString(RunConfiguration.localExecutionSettingMapStorage)
			return json
		}

		/**
		 * {
		 *   "execution" : {
		 *     ...
		 *     "general" : {
		 *       ...
		 *       "proxy" : "{\"proxyOption\":\"MANUAL_CONFIG\",\"proxyServerType\":\"HTTP\",\"username\":\"\",\"password\":\"\",\"proxyServerAddress\":\"127.0.0.1\",\"proxyServerPort\":9999,\"exceptionList\":\"\",\"applyToDesiredCapabilities\":true}",
		 *       ...
		 */
		RunConfiguration.metaClass.'static'.setHttpProxyInformation = { String address, Integer port ->
			Map<String, Object> general = RunConfiguration.getExecutionGeneralProperties()
			assert general != null
			if (!general.containsKey("proxy")) {
				KeywordUtil.markWarning("no property named proxy found in the RunConfiguration")
				return
			}
			String value = """{"proxyOption": "MANUAL_CONFIG", "proxyServerType": "HTTP", "username": "", "password": "", "proxyServerAddress": "${address}", "proxyServerPort": ${port}, "exceptionList": "", "applyToDesiredCapabilities": true }"""
			general.put('proxy', value)
		}

		/**
		 *   {
		 *     "execution" : {
		 *       ...
		 *       "drivers" : {
		 *         "preferences" : {
		 *           "WebUI" : {
		 *             "acceptInsecureCerts" : true
		 *           }
		 *         },
		 *     ...
		 */
		RunConfiguration.metaClass.'static'.setAcceptInsecureCerts = { Boolean v ->
			RunConfiguration.setWebDriverPreferencesProperty('acceptInsecureCerts', v)
		}
	}
}