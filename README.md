# Sample usage of BrowserMob Proxy in Katalon Studio

## Background

In the [Katalon User Forum](https://forum.katalon.com/), there is a Frequently Asked Questions.

> I want to do a Web UI test using browsers, and at the same time I want to record the payload of HTTP Requests and Responses exchanged between a browser and servers. I want to save the information into a local file and later reuse it. How can I do it?

Katalon Studio does not support recording the payload of HTTP messages exchanged between a browser and HTTP servers. You need to employ additional technology. The following post by matteo.lauria at Feb '21 suggested [BrowserMob Proxy](https://bmp.lightbody.net/) for this purpose.

- https://forum.katalon.com/t/how-to-generate-har-file-for-web-test-suite/24384/5

BrowserMob Proxy will enabled us to make a [HAR](<https://en.wikipedia.org/wiki/HAR_(file_format)>) file which contains all HTTP requests and responses recorded during tests in Katalon Studio. The HTTP messages will be formated in JSON.

The post suggested the way how to make use of BrowserMob Proxy in Katalon Studio. Unfortunately the sample code by matteo.lauria were incomplete complete (e.g, the `import` statements were trimmed off), so it was a bit difficult to reproduce.

## What is this?

Here I would show you a runnable Katalon Studio project empowered by BrowserMob Proxy. With this project, you would see how to make a HAR file that contain HTTP messages exchanged by browser and HTTP servers.

![sequence](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/diagrams/out/sequence/sequence.png)

Additionally, this project would show you a number of sample codes that post-processes the HAR file.

You can download the zip of the project from the [Releases](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/releases/) page. You want to download the latest zip; unzip it; open the project with your local Katalon Studio; run the "Test Suite/TS1_WebUI.openBrowser"; when it finished you will find `sample.har` file is created in the project folder.

## BrowserMob Proxy is bundled in Katalon Studio

Every Katalon Studio project has `.classpath` file which list all the built-in jar files available to your test case scripts. It contains the BrowserMob Proxy as this on my Mac:

```
...
	<classpathentry kind="lib" path="/Applications/Katalon Studio.app/Contents/Eclipse/configuration/resources/lib/browsermob-core-2.1.5.jar"/>
...
```

## Demonstration

Here is the entry point: `Test Suites/TS1_demoaut.katalon.com`. Just try running it.

![TS1](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/TS1_demoaut.katalon.com.png)

This Test Suite comprises with 4 Test Case scripts.

### [Test Cases/main/common/startProxy_WebUI.openBrowser](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/main/common/startProxy_WebUI.openBrowser/Script1721103602049.groovy)

This script does the following:

1. starts a process of BrowserMob Proxy Server on the localhost.
2. opens a web browser using `WebUI.openBrowser()` keyword. The sample project supports only Chrome and Chrome (headless). To support other types of browsers, for example FireFox, you need to rewrite the line#26-35 of `startProxy_WebUI.openBrowser' script.
3. configures the browser so that it communicate with the target URL via proxy (= BrowserMob Proxy Server)

This step will take 5 seconds to finish.

### [Test Cases/main/interactions/visit_demoaut.katalon.com](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/main/interactions/visit_demoaut.katalon.com/Script1721514910231.groovy)

This is an usual Katalon Test Case. It navigates to a URL `http://demoauto.katalon.com` and operates on the web pages using `WebUI.*` keywords. It contains nothing special.

This step will take 10+α seconds to finish.

### [Test Cases/main/common/closeBrowser_stop](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/main/common/closeBrowser_stopProxy/Script1673684270460.groovy)

This script does the following:

1. Closes the browser window
2. Asks the BrowserMob Proxy Server for a HTTP Archive. The HAR will includes all HTTP requests and responses. The HAR is JSON-formatted.
3. Performs "pretty print" the HAR (insert newlines and indentations). Saves the JSON into a local file.
4. Stops the BrowserMob Proxy Server.

This step will take 4 seconds to finish.

When this step finished, you will find 2 files created

- `<projectDir>/work/TS1_demoaut.katalon.com-source.har`.
- `<projectDir>/work/TS1_demoaut.katalon.com-pretty.har`

```
cd :~/BrowserMobProxyInKatalonStudio (develop *)
$ ls -la work | grep TS1
-rw-r--r--   1 kazurayam  staff     959232  7 24 17:00 TS1_demoaut.katalon.com-pretty.har
-rw-r--r--   1 kazurayam  staff     905344  7 24 17:00 TS1_demoaut.katalon.com-source.har
```

The `*-source.har` file is the original HAR provided by BrowserMob Proxy, just as provided.

The script pretty-printed (insert newlines, insert indentations) and saved into the `-pretty.har` file. The original 900MB is broken down into 4000 lines by [com.kazurayam.jsonflyweight.JsonFlyweight](https://github.com/kazurayam/JsonFlyweight), a JSON pretty-printer library that I developped.

#### CAUTION!

Don't open the `*-source.har` file using text editor in Katalon Studio! If you double-click it, our poor Katalon Studio will hang. You will see a loading icon ![loading](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/loading-42-1.gif) rotating forever. You will have to kill Katalon Studio and restart it.

Why KS hangs up due to `*-source.har`? --- The HAR file provided by BrowserMob Proxy is a single line of 900 megabytes without any newline. A line of 900 megabyte surprises primitive text editors without any word-wrapping feature.

By the way, you can safely open the `-source.har` file using VSCode with word-wrapping. Excellence of VSCode!

The `TS1_demoaut.katalon.com-source.jar` file will be a very large JSON text file. Using a text editor, you can't understand the content of HAR. You would need a tailored viewer for HAR file. I would recommend you to use [Visual Studio Code](https://code.visualstudio.com/) with [HAR Viewer](https://marketplace.visualstudio.com/items?itemName=unclebeast.har-viewer) plugin installed. The following image shows an example:

![VSCode HAR Viewer](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/VSCode_HARViewer.png)

If you want to deal with very large JSON files like HTTP Archive in your Katalon project, you would certainly need to VSCode. Katalon Studio is not enough for working with HAR.

### [Test Cases/main/post-process/select_entries_for_jquery_or_fontawesome](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/main/post-process/select_entries_for_jquery_or_fontawesome/Script1721610779882.groovy)

The 4th script in the "Test Suites/TS1_demoaut.katalon.com" will transform the large HAR file into a smaller JSON file.

What did I do here? --- I want to select two types of HTTP Request-Response interactions out of the source HAR.

1. HTTP Requests to the URL that ends with "`jquery.min.js`"
2. HTTP Requests to the URL that contains "`fontawesome`"

I want to transform a large JSON into a smaller one. I want to select the portion that I am interested in, and I want to strip the rest off. Let me illustrate the transformation result:

```
{
  "log": {
    ...
    "entries": [
      ...
      {
        "request": {
          "method": "GET",
          "url": "https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.3/jquery.min.js",
          ...
        },
        "response": {
          ...
        }
      },
      ...
      {
        "request": {
          "method": "GET",
          "url": "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/fonts/fontawesome-webfont.woff2?v=4.7.0",
          ...
        },
        "response": {
          ...
        }
      },
      ...
    ]
  }
}
```

The script [Test Cases/main/post-process/select_entries_for_jquery_or_fontawesome](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/main/post-process/select_entries_for_jquery_or_fontawesome/Script1721610779882.groovy) does exactly what I wanted to do. I got a success.

When the script is executed, you will find a file with name `*-selection.har` will be created. Please find that the size of `*-selection.har` is much smaller than the `*-source.har` file.

```
$ ls -la work | grep TS1
-rw-r--r--   1 kazuakiurayama  staff     959232  7 24 17:00 TS1_demoaut.katalon.com-pretty.har
-rw-r--r--   1 kazuakiurayama  staff     905344  7 24 17:00 TS1_demoaut.katalon.com-source.har
-rw-r--r--   1 kazuakiurayama  staff     154419  7 24 17:00 TS1_demoaut.katalon.com-selection.har
```

How could I make it? --- Please read the source code and find out how to. The most essential part is this:

```
// specify how I use Jayway JsonPath to transform the input HAR
Closure cls = { Path har ->
	// I am interested in a HTTP request of which URL contains a string ".jquery.min.js"
	Filter filter1 = Filter.filter(
		Criteria.where("request.url")
				.regex(Pattern.compile('.*jquery\\.min\\.js')));

	// I am also interested in  "fontawesome-webfont.woff2"
	Filter filter2 = Filter.filter(
		 Criteria.where("request.url")
		 		.regex(Pattern.compile('.*fontawesome\\-webfont\\.woff2.*')));

	Filter filter = filter1.or(filter2)

	// Now select interesting entries out of the HAR to create a much smaller json file
	List<Map<String, Object>> result =
		JsonPath.parse(Files.newInputStream(har))
			.read("\$['log']['entries'][?]", filter)
									// [?(...)] is a filter expression
	return result
}

// apply the templates to transform the input into the output
WebUI.callTestCase(findTestCase("Test Cases/main/post-process/HAR Transform"),
					[
						"inputHar": inputHar,
						"templates": cls,
						"outputJson": output,
						"shouldBeLessThan": 20
					])
```

Do you think, this code fragment looks like a magic spell? --- Yes, very magical.

I employed [Jayway JsonPath](https://github.com/json-path/JsonPath) library. There are sevelral articles about JsonPath on the net.

- [Introduction to JsonPath, Baeldung](https://www.baeldung.com/guide-to-jayway-jsonpath) --- this article introduce me to JsonPath. It was enough for me.
- [Comprehensive Guide to JsonPath, CODDIPA](https://codippa.com/jayway-jsonpath-java/)

The 4th step takes just 3 seconds. Jayway JsonPath runs amazingly fast.

## How to import required jar files.

Katalon Studio does not bundle the jar files required to use the Jayway JsonPath. You need to download the required external jar files from Maven Central repository, locate them into the `<projectDir>/Drivers` folder. The following document explains how to do it:

- [Katalon Gradle Plugin](https://github.com/katalon-studio/katalon-gradle-plugin)

To do this, you need to install Java and Gradle into your machine. The following post has an instruction how to:

- [setting up gradle build tool](https://forum.katalon.com/t/automated-visual-inspection/81966#setting-up-gradle-build-tool-22)

Once you installed Java and Gradle, you want to write [`<projectDir>/build.gradle`](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/build.gradle) as follows:

```
plugins {
  id 'java'
  id "com.katalon.gradle-plugin" version "0.1.1"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation group: 'com.jayway.jsonpath', name: 'json-path', version: '2.9.0'
}

```

And you want to execute some commands in the command line. First, you want to change the current directory to the home directory of your Katalon project.

```
$ cd ~/BrowserMobProxyInKatalonStudio
```

then you want to do:

```
$ gradle katalonCopyDependencies
```

then you will see some jar files are imported into the Drivers dir:

```
$ ls -ls Drivers
total 1240
drwxr-xr-x   8 kazuakiurayama  staff     256  2 11 11:55 .
drwxr-xr-x  31 kazuakiurayama  staff     992  2 11 11:21 ..
-rw-r--r--   1 kazuakiurayama  staff   29489  2 11 11:21 katalon_generated_accessors-smart-2.5.0.jar
-rw-r--r--   1 kazuakiurayama  staff  121790  2 11 11:21 katalon_generated_asm-9.3.jar
-rw-r--r--   1 kazuakiurayama  staff  271159  2 11 11:21 katalon_generated_json-path-2.9.0.jar
-rw-r--r--   1 kazuakiurayama  staff  119227  2 11 11:21 katalon_generated_json-smart-2.5.0.jar
```

Now you are ready to run the `Test Suites/TS1_demoaut.katalon.com`.

## Conclusion

Hope this helps.
