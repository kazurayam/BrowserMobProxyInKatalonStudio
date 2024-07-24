import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.exception.SendRequestException
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import java.util.Timer
import internal.GlobalVariable as GlobalVariable

def timer = new Timer()
if (GlobalVariable.BrowserMobProxyServer != null) {
	def task = timer.runAfter(5000) {
		GlobalVariable.BrowserMobProxyServer.abort()
		GlobalVariable.BrowserMobProxyServer = null
		println "Actually executed at ${new Date()}."
	}
	println "Current date is ${new Date()}. Task is executed at ${new Date(task.scheduledExecutionTime())}."
}

String url = "http://127.0.0.1:3000/dump"

try {
	status = getResponseStatus(url)
} catch(Exception ex) {
	println('in catch')
}

String getResponseStatus(String url) {
	String status = '???'
	println("requesting on URL: " + url)
	if (url != null && url.startsWith('http')) {
		RequestObject req = new RequestObject('check if this url is reachable')
		req.setServiceType('REST')
		req.setRestUrl(url)
		req.setRestRequestMethod('GET')
		// Send the request and get the response
		try {
			ResponseObject res = WS.sendRequest(req)
			status = res.getStatusCode()  // '200' OK, '302' redirected, '403' etc
			println("status=" + status)
		} catch (SendRequestException sre) {
			println("send reques exception occured")
		} catch (SocketException se) {
			println("Socket Exception: Not able to send request to " + url)
		} catch(Exception ex) {
			println('Exception occured')
		}
	}
	return status
}