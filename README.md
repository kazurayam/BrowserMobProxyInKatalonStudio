# Sample usage of BrowserMob Proxy in Katalon Studio

## Background

In the [Katalon User Forum](https://forum.katalon.com/), there is a Frequently Asked Questions.

> I want to do a Web UI test using browsers, and at the same time I want to get the payload of requests and responses exchanged between browser and server. I want to save it into a file. How can I do it?

Katalon Studio does not support recording the payload of HTTP messages exchanged between a browser and HTTP servers. You need to employ additional technology. The following post by matteo.lauria at Feb '21 suggested [BrowserMob Proxy](https://bmp.lightbody.net/) for this purpose.

- https://forum.katalon.com/t/how-to-generate-har-file-for-web-test-suite/24384/5

BrowserMob Proxy will enabled us to make a [HAR](<https://en.wikipedia.org/wiki/HAR_(file_format)>) file which contains all HTTP requests and responses recorded during tests in Katalon Studio. The HTTP messages will be formated in JSON.

The post suggested the way how to make use of BrowserMob Proxy in Katalon Studio. Unfortunately the sample code by matteo.lauria were incomplete complete (e.g, the `import` statements were trimmed off), so it was difficult to reuse in my project.

## What's this

Here I would show you a runnable Katalon Studio project empowered by BrowserMob Proxy to make a HAR file that contain HTTP messages exchanged by browser and HTTP servers.

![sequence](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/diagrams/out/sequence/sequence.png)

Additionally, I would show you a set of sample codes that post-processes the HAR file. For example, this project provides a demo that tests if the HAR contains a request to a URL ending with a string `jquery.min.js`.

You can download the zip of the project from the [Releases](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/releases/) page. You want to download the zip; unzip it; open the project with your local Katalon Studio; run the "Test Suite/TS1_WebUI.openBrowser"; when it finished you will find `sample.har` file is created in the project folder.

## BrowserMob Proxy is bundled in Katalon Studio

Every Katalon Studio project has `.classpath` file which list all the built-in jar files available to your test case scripts. It contains the BrowserMob Proxy as this on my Mac:

```
...
	<classpathentry kind="lib" path="/Applications/Katalon Studio.app/Contents/Eclipse/configuration/resources/lib/browsermob-core-2.1.5.jar"/>
...
```

## Demo Test Suite

Try running the `Test Suites/TS1_WebUI.openBrowser`, which comprises with 3 Test Case scripts.

1. [Test Cases/startProxy_WebUI.openBrowser](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/startProxy_WebUI.openBrowser/Script1721103602049.groovy)
2. [Test Cases/TestStuff1]()
3. [Test Cases/closeBrowser_stopProxy]()

### `startProxy_WebUI.openbBrowser`

1. starts a process of BrowserMob Proxy Server.
2. opens a web browser using `WebUI.openBrowser()` keyword. Only Chrome and Chrome (headless) is supported.
3. configures the browser so that it communicate with the target URL via proxy (= BrowserMob Proxy Server)

### `TestStuff1`

An usual Katalon Test Case. It navigates to a URL `http://demoauto.katalon.com` and operates on it.

### `closeBrowser_stopProxy`

1. closes browser window
2. ask the BrowserMob Proxy Server for the HAR content which includes all of HTTP requests and responses in JSON format. Save the HAR into a local file.
3. stops the BrowserMob Proxy Server.


When `TS1_WebUI.openBrowser` finished, you will find a file `<projectDir>/sample.har`.

## How to view the HAR file

The `sample.jar` file will be a very large JSON text file. It is difficult to see it and grasp overall in a text editor. You would need a tailored viewer for HAR file. I would recommend you to use [Visual Studio Code](https://code.visualstudio.com/) with [HAR Viewer](https://marketplace.visualstudio.com/items?itemName=unclebeast.har-viewer) plugin installed. The following image shows an example:

![VSCode HAR Viewer](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/VSCode_HARViewer.png)

## How to post-process the HAR file as JSON using Jayway JsonPath

Additionally, I want to automate verifying the content of HAR file. I expect that the `sample.har` file contains a record of a HTTP request to a url that ends with `jquery.min.js`.

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
        }
      }
      ...
    ]
  }
}
```

How can I do it?

There could be many ways to meet this requirement.

I tried to solve this problem using [Jaywa JsonPath](https://github.com/json-path/JsonPath) library.

You can find my solution in the code of [Test Cases/process_har_with_jsonpath](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/tree/main/Scripts/process_har_with_jsonpath/Script1676083293661.groovy).

The following screenshot shows how the demo `TS1` runs with verifying HAR file.

![verify HAR](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/TS1_with_verify_HAR.png)

The Test Case `process_har_with_jsonpath` verifies if the `sample.har` file contains a URL that ends with `jquery.min.js`. Of course you can modify the condition as you want. Please study the code and the Jayway JsonPath documentation.

### How to import required jar files.

Katalon Studio does not bundle the jar files required to use the Jayway JsonPath. You need to import external jar files from Maven Central repository into the `<projectDir>/Drivers` folder. The following document explains how to do it:

- [Katalon Gradle Plugin](https://github.com/katalon-studio/katalon-gradle-plugin)

To do this, you need to install Java and Gradle into your machine. The following post has an instruction how to:

- [setting up gradle build tool](https://forum.katalon.com/t/automated-visual-inspection/81966#setting-up-gradle-build-tool-22)

Once you installed Java and Gradle, you want to write [`<projectDir>/build.gradle`](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/main/build.gradle) as follows:

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
  implementation group: 'org.slf4j', name: 'slf4j-api', version: '2.0.13'
  implementation group: 'org.slf4j', name: 'slf4j-simple', version: '2.0.13'
}

```

And you want to execute in the command line:

First, you want to change the current directory to the home directory of your Katalon project. For example:

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
-rw-r--r--   1 kazuakiurayama  staff   62531  2 11 11:21 katalon_generated_slf4j-api-2.0.13.jar
-rw-r--r--   1 kazuakiurayama  staff   15239  2 11 11:21 katalon_generated_slf4j-simple-2.0.13.jar
```

Now you are ready to run the `Test Suites/TS1` which indirectly execute the whole set of test cases.

## process HAR using JMESPath

An alternative way to process JSON could be JMESPath.

- https://jmespath.org/

JMESPath Python implementation is used by AWS Commandline Interface which I am used to.

JMESPath Java implementation is available at

- https://github.com/burtcorp/jmespath-java

I tried to implement [Test Cases/process_har_with_JMESPath](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/tree/main/Scripts/process_har_with_JMESPath/Script1676251084800.groovy) and succeeded.

I added a few lines in the build.gradle

```
  implementation group: 'com.amazonaws', name: 'jmespath-java', version: '1.12.738'
  implementation group: 'io.burt', name: 'jmespath', version: '0.6.0', ext: 'pom'
  implementation group: 'io.burt', name: 'jmespath-jackson', version: '0.6.0'
```

and did

```
$ gradle katalonCopyDependencies
```

then I got a few jar files added in the `Drivers` folder.

I wrote the `Test Cases/process_har_with_JMESPath`, which worked just the same as `Test Cases/process_har_with_jsonpath`.

## Process HAR using Jackson Streaming API

How large the sample.har is?

```
$ wc sample.har
    4152   10745 1017938 sample.har
```

The sample.har files has 4152 lines, 10745 words, 1017938 characters. Well, it's large. Now I want to pick up all URL string contained in the sample.har file. How can I do it? [Jackson Streaming API](https://www.baeldung.com/jackson-streaming-api) is an ideal solution for this problem.

I made a [`Test Cases/process_har_with_streaming_api`](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/process_har_with_streaming_api/Script1720675301367.groovy)

```
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
Path har = projectDir.resolve("sample.har")

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
```

As you see, this code uses the [`Jackson Core`](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.14.2) library. Katalon Studio bundles the jar of `Jackson Core`; so that you do not have to add any external jar.

When I ran this Test Case, I got the following output in the console:

```
2024-07-11 15:06:58.351 INFO  c.k.katalon.core.main.TestCaseExecutor   - --------------------
2024-07-11 15:06:58.354 INFO  c.k.katalon.core.main.TestCaseExecutor   - START Test Cases/process_har_with_streaming_api
http://demoaut.katalon.com/
https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/css/bootstrap-datepicker.min.css
https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datepicker/1.6.4/js/bootstrap-datepicker.min.js
https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css
https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/fonts/fontawesome-webfont.woff2?v=4.7.0
https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.3/jquery.min.js
https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/css/bootstrap.min.css
https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/fonts/glyphicons-halflings-regular.woff2
https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.7/js/bootstrap.min.js
https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,700,300italic,400italic,700italic
https://fonts.gstatic.com/s/sourcesanspro/v22/6xK3dSBYKcSV-LCoeQqfX1RYOo3qOK7lujVj9w.woff2
https://fonts.gstatic.com/s/sourcesanspro/v22/6xKydSBYKcSV-LCoeQqfX1RYOo3ig4vwlxdu3cOWxw.woff2
https://fonts.gstatic.com/s/sourcesanspro/v22/6xKydSBYKcSV-LCoeQqfX1RYOo3ik4zwlxdu3cOWxw.woff2
https://katalon-demo-cura.herokuapp.com/
https://katalon-demo-cura.herokuapp.com//css/theme.css
https://katalon-demo-cura.herokuapp.com//img/header.jpg
https://katalon-demo-cura.herokuapp.com//js/theme.js
https://katalon-demo-cura.herokuapp.com/appointment.php
https://katalon-demo-cura.herokuapp.com/authenticate.php
https://katalon-demo-cura.herokuapp.com/profile.php
2024-07-11 15:07:23.338 INFO  c.k.katalon.core.main.TestCaseExecutor   - END Test Cases/process_har_with_streaming_api
```

This run took 25 seconds to finish. Well, reasonable speed, I think. Not too slow, not too fast.

## Conclusion

Hope this helps.
