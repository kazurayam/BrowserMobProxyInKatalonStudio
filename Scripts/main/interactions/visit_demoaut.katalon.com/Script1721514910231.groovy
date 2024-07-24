import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

WebUI.navigateToUrl('http://demoaut.katalon.com/')

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/a_Make Appointment'))

WebUI.setText(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/input_Username_username'), 'John Doe')

WebUI.setEncryptedText(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/input_Password_password'), 'g3/DOGG74jC3Flrr3yH+3D/yKbOqqUNM')

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/button_Login'))

WebUI.selectOptionByValue(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/select_Tokyo CURA Healthcare Center        _5b4107'), 
    'Hongkong CURA Healthcare Center', true)

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/input_Medicaid_programs'))

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/input_Visit Date (Required)_visit_date'))

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/td_31'))

WebUI.setText(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/textarea_Comment_comment'), 'This is a comment')

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/button_Book Appointment'))

WebUI.click(findTestObject('Object Repository/TC1/Page_CURA Healthcare Service/a_Go to Homepage'))

