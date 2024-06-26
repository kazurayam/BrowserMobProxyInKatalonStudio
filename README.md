# Sample usage of BrowserMob Proxy in Katalon Studio

## Background

In the [Katalon User Forum](https://forum.katalon.com/), there is a Frequently Asked Questions.

>I want to do a Web UI test using browsers and at the same time I want to save the payload of HTTP requests and responses into a file. How can I do it?

Katalon Studio v8.5.x does not support recording the payload of HTTP messages exchanged between a browser and HTTP servers. You need to employ other technologies. The following post by matteo.lauria at Feb '21 suggested [BrowserMob Proxy](https://bmp.lightbody.net/) for this purpose.

- https://forum.katalon.com/t/how-to-generate-har-file-for-web-test-suite/24384/5

BrowserMob Proxy will enabled us to make a [HAR](https://en.wikipedia.org/wiki/HAR_(file_format)) file which contains all HTTP requests and responses recorded during tests in Katalon Studio.  The HTTP messages will be formated in JSON.

The post suggested the way how to make use of BrowserMob Proxy in Katalon Studio. Unfortunately the presented sample codes were not complete (e.g, the `import` statements were trimmed off), so it was a bit difficult to reuse on my machine.

## What's this

Here I would show you a runnable Katalon Studio project empowered by BrowserMob Proxy to make a HAR file that contain HTTP messages exchanged by browser and HTTP servers.

Additionally, I would show you a sample code that parses the HAR file as JSON and verify if it meets some condition. For example, the demo tests if the HAR contains a request to a URL that ends with a string `jquery.min.js`. You can modify the test condition as a Regular Expression as you want.

![sequence](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/diagrams/out/sequence/sequence.png)

You can download the zip of the project from the [Releases](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/releases/) page. You want to download the zip; unzip it; open the project with your local Katalon Studio; run  the "Test Suite/TS1"; when it  finished you will find `sample.har` file is created in the project folder.

## BrowserMob Proxy is bundled in Katalon Studio

Every Katalon Studio project has `.classpath` file which list all the built-in jar files available to your test case scripts. It contains the BrowserMob Proxy as this on my Mac:

```
...
	<classpathentry kind="lib" path="/Applications/Katalon Studio.app/Contents/Eclipse/configuration/resources/lib/browsermob-core-2.1.5.jar"/>
...
```

## Demo Test Suite

Try running the `Test Suites/TS0`, which comprises with 3 Test Case scripts.

1. [Test Cases/startProxy_openBrowser](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/startProxy_openBrowser/Script1673684243630.groovy)
2. [Test Cases/TestStuff](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/TestStuff/Script1673678647580.groovy)
3. [Test Cases/closeBrowser_stopProxy](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/blob/develop/Scripts/closeBrowser_stopProxy/Script1673684270460.groovy)

Each scrits do what their names mean.

When finished, the `TS0` will create a new file `<projectDir>/sample.har`.


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

## Conclusion

Hope this helps.
