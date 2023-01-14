# Sample usage of BrowserMob Proxy in Katalon Studio

In the [Katalon User Forum](https://forum.katalon.com/), there is a Frequently Asked Question.

>I want to Web UI test while performing browser-automation and at the same time I want to save the payload of HTTP request and responses. How can I do it?

Katalon Studio does not support recording the payload of HTTP messages. You need to employ other technologies. The following post by matteo.lauria at Feb '21 suggests [BrowserMob Proxy](https://bmp.lightbody.net/) for this purpose.

https://forum.katalon.com/t/how-to-generate-har-file-for-web-test-suite/24384/5

The post suggested how to make use of BrowserMob Proxy in Katalon Studio to make a HAR file which records all HTTP requests and responses during tests in Katalon Studio. Unfortunately the presented sample codes were not very complete (e.g, the `import` statements were trimmed off). So here I would show you a runnable Katalon Studio project.

You can download the zip of the project from the [Releases]() page.
