# Sample usage of BrowserMob Proxy in Katalon Studio

## Background

In the [Katalon User Forum](https://forum.katalon.com/), there is a Frequently Asked Question.

>I want to do a Web UI test using browsers and at the same time I want to save the payload of HTTP requests and responses into a file. How can I do it?

Katalon Studio v8.5.x does not support recording the payload of HTTP messages. You need to employ other technologies. The following post by matteo.lauria at Feb '21 suggested [BrowserMob Proxy](https://bmp.lightbody.net/) for this purpose.

- https://forum.katalon.com/t/how-to-generate-har-file-for-web-test-suite/24384/5

BrowserMob Proxy will enabled us to make a [HAR](https://en.wikipedia.org/wiki/HAR_(file_format)) file which contains all HTTP requests and responses in JSON recorded during tests in Katalon Studio.

The post suggested the way how to make use of BrowserMob Proxy in Katalon Studio. Unfortunately the presented sample codes were not complete (e.g, the `import` statements were trimmed off), so it was a bit difficult to run it on your machine.

## What's this

Here I would show you a runnable Katalon Studio project empowered by BrowserMob Proxy to make a HAR file.

You can download the zip of the project from the [Releases](https://github.com/kazurayam/BrowserMobProxyInKatalonStudio/releases/) page. You want to download the zip; unzip it; open the project with your local Katalon Studio; run  the "Test Suite/TS1"; when it  finished you will find `sample.har` file is created in the project folder.

## How to view

The `sample.jar` file will be a very large JSON text file. It is difficult to see it and grasp overall. You would need a tailored viewer for HAR file. I would recommend you to use [Visual Studio Code](https://code.visualstudio.com/) with [HAR Viewer](https://marketplace.visualstudio.com/items?itemName=unclebeast.har-viewer) plugin installed. The following image shows an example:

![VSCode HAR Viewer](https://kazurayam.github.io/BrowserMobProxyInKatalonStudio/images/VSCode_HARViewer.png)

Hope this helps.
