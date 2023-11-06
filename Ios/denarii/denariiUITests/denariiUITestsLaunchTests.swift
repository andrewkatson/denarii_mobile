//
//  denariiUITestsLaunchTests.swift
//  denariiUITests
//
//  Created by Andrew Katson on 5/14/23.
//

import XCTest
@testable import denarii

/*Sends a tap event to a hittable/unhittable element.*/
extension XCUIElement {
    func forceTapElement() {
        if self.isHittable {
            self.tap()
        }
        else {
            let coordinate: XCUICoordinate = self.coordinate(withNormalizedOffset: CGVectorMake(0.0, 0.0))
            coordinate.tap()
        }
    }
}

final class denariiUITestsLaunchTests: XCTestCase {
    
    var currentTestName = ""
    
    func dismissKeyboardIfPresent(_ app: XCUIApplication) {
        if app.keyboards.buttons["Return"].exists {
            app.keyboards.buttons["Return"].tap()
        }
    }
    
    func tapAway(_ app: XCUIApplication) {
        app.tap()
        
        // Have to do redundant tapping becuase the popover in landscape is harder to tap
        if app.staticTexts[Constants.POPOVER].exists {
            app.staticTexts[Constants.POPOVER].tap()
            if app.staticTexts[Constants.POPOVER].exists {
                app.staticTexts[Constants.POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.POPOVER].exists, "\(Constants.POPOVER) still exists")
        }
        if app.staticTexts[Constants.REFRESH_BALANCE_POPOVER].exists {
            app.staticTexts[Constants.REFRESH_BALANCE_POPOVER].tap()
            if app.staticTexts[Constants.REFRESH_BALANCE_POPOVER].exists {
                app.staticTexts[Constants.REFRESH_BALANCE_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.REFRESH_BALANCE_POPOVER].exists, "\(Constants.REFRESH_BALANCE_POPOVER) still exists")
        }
        if app.staticTexts[Constants.SEND_DENARII_POPOVER].exists {
            app.staticTexts[Constants.SEND_DENARII_POPOVER].tap()
            if app.staticTexts[Constants.SEND_DENARII_POPOVER].exists {
                app.staticTexts[Constants.SEND_DENARII_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.SEND_DENARII_POPOVER].exists, "\(Constants.SEND_DENARII_POPOVER) still exists")
        }
        if app.staticTexts[Constants.BUY_DENARII_POPOVER].exists {
            app.staticTexts[Constants.BUY_DENARII_POPOVER].tap()
            if app.staticTexts[Constants.BUY_DENARII_POPOVER].exists {
                app.staticTexts[Constants.BUY_DENARII_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.BUY_DENARII_POPOVER].exists, "\(Constants.BUY_DENARII_POPOVER) still exists")
        }
        if app.staticTexts[Constants.CANCEL_BUY_DENARII_POPOVER].exists {
            app.staticTexts[Constants.CANCEL_BUY_DENARII_POPOVER].tap()
            if app.staticTexts[Constants.CANCEL_BUY_DENARII_POPOVER].exists {
                app.staticTexts[Constants.CANCEL_BUY_DENARII_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.CANCEL_BUY_DENARII_POPOVER].exists, "\(Constants.CANCEL_BUY_DENARII_POPOVER) still exists")
        }
        if app.staticTexts[Constants.SELL_DENARII_POPOVER].exists {
            app.staticTexts[Constants.SELL_DENARII_POPOVER].tap()
            if app.staticTexts[Constants.SELL_DENARII_POPOVER].exists {
                app.staticTexts[Constants.SELL_DENARII_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.SELL_DENARII_POPOVER].exists, "\(Constants.SELL_DENARII_POPOVER) still exists")
        }
        if app.staticTexts[Constants.CANCEL_SELL_DENARII_POPOVER].exists {
            app.staticTexts[Constants.CANCEL_SELL_DENARII_POPOVER].tap()
            if app.staticTexts[Constants.CANCEL_SELL_DENARII_POPOVER].exists {
                app.staticTexts[Constants.CANCEL_SELL_DENARII_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.CANCEL_SELL_DENARII_POPOVER].exists, "\(Constants.CANCEL_SELL_DENARII_POPOVER) still exists")
        }
        if app.staticTexts[Constants.SET_CREDIT_CARD_INFO_POPOVER].exists {
            app.staticTexts[Constants.SET_CREDIT_CARD_INFO_POPOVER].tap()
            if app.staticTexts[Constants.SET_CREDIT_CARD_INFO_POPOVER].exists {
                app.staticTexts[Constants.SET_CREDIT_CARD_INFO_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.SET_CREDIT_CARD_INFO_POPOVER].exists, "\(Constants.SET_CREDIT_CARD_INFO_POPOVER) still exists")
        }
        if app.staticTexts[Constants.CLEAR_CREDIT_CARD_INFO_POPOVER].exists {
            app.staticTexts[Constants.CLEAR_CREDIT_CARD_INFO_POPOVER].tap()
            if app.staticTexts[Constants.CLEAR_CREDIT_CARD_INFO_POPOVER].exists {
                app.staticTexts[Constants.CLEAR_CREDIT_CARD_INFO_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.CLEAR_CREDIT_CARD_INFO_POPOVER].exists, "\(Constants.CLEAR_CREDIT_CARD_INFO_POPOVER) still exists")
        }
        if app.staticTexts[Constants.CREATE_NEW_COMMENT_POPOVER].exists {
            app.staticTexts[Constants.CREATE_NEW_COMMENT_POPOVER].tap()
            if app.staticTexts[Constants.CREATE_NEW_COMMENT_POPOVER].exists {
                app.staticTexts[Constants.CREATE_NEW_COMMENT_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.CREATE_NEW_COMMENT_POPOVER].exists, "\(Constants.CREATE_NEW_COMMENT_POPOVER) still exists")
        }
        if app.staticTexts[Constants.RESOLVE_TICKET_POPOVER].exists {
            app.staticTexts[Constants.RESOLVE_TICKET_POPOVER].tap()
            if app.staticTexts[Constants.RESOLVE_TICKET_POPOVER].exists {
                app.staticTexts[Constants.RESOLVE_TICKET_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.RESOLVE_TICKET_POPOVER].exists, "\(Constants.RESOLVE_TICKET_POPOVER) still exists")
        }
        if app.staticTexts[Constants.DELETE_TICKET_POPOVER].exists {
            app.staticTexts[Constants.DELETE_TICKET_POPOVER].tap()
            if app.staticTexts[Constants.DELETE_TICKET_POPOVER].exists {
                app.staticTexts[Constants.DELETE_TICKET_POPOVER].swipeDown()
            }
            XCTAssert(!app.staticTexts[Constants.DELETE_TICKET_POPOVER].exists, "\(Constants.DELETE_TICKET_POPOVER) still exists")
        }
    }
    
    func navigateToLoginOrRegister(_ app: XCUIApplication) {
        let contentViewToLoginOrRegisterPredicate = NSPredicate(format: "label beginswith 'Next'")
        app.buttons.matching(contentViewToLoginOrRegisterPredicate).element.tap()
    }
    
    func navigateToLogin(_ app: XCUIApplication) {
        let loginPredicate = NSPredicate(format: "label beginswith 'Login'")
        app.buttons.matching(loginPredicate).element.tap()
    }
    
    func navigateToRegister(_ app: XCUIApplication) {
        let registerPredicate = NSPredicate(format: "label beginswith 'Register'")
        app.buttons.matching(registerPredicate).element.tap()
    }
    
    func navigateToLoginFromLoginOrRegister(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)
        navigateToLogin(app)
    }
    
    func registerWithDenarii(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)

        navigateToRegister(app)
        
        let loginTextField = app.textFields["Name"]
        loginTextField.tap()
        loginTextField.typeText("User_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email_\(self.currentTestName)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordSecureTextField = app.secureTextFields["Password"]
        passwordSecureTextField.tap()
        passwordSecureTextField.typeText("Password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordSecureTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordSecureTextField.tap()
        confirmPasswordSecureTextField.typeText("Password_\(self.currentTestName)")

        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func loginWithDenarii(_ app: XCUIApplication) {
        registerWithDenarii(app)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        navigateToLogin(app)
        
        let usernamePredicate = NSPredicate(format: "placeholderValue beginswith 'Name'")
        let emailPredicate = NSPredicate(format: "placeholderValue beginswith 'Email'")
        let passwordPredicate = NSPredicate(format: "placeholderValue beginswith 'Password'")
        let confirmPasswordPredicate = NSPredicate(format: "placeholderValue beginswith 'Confirm Password'")
        let submitButtonPredicate = NSPredicate(format: "label beginswith 'Submit'")
        
        let loginTextField = app.textFields.matching(usernamePredicate)
        loginTextField.element.tap()
        loginTextField.element.typeText("User_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let emailTextField = app.textFields.matching(emailPredicate)
        emailTextField.element.tap()
        emailTextField.element.typeText("Email_\(self.currentTestName)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let passwordSecureTextField = app.secureTextFields.matching(passwordPredicate)
        passwordSecureTextField.element.tap()
        passwordSecureTextField.element.typeText("Password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let confirmPasswordSecureTextField = app.secureTextFields.matching(confirmPasswordPredicate)
        confirmPasswordSecureTextField.element.tap()
        confirmPasswordSecureTextField.element.typeText("Password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")

        let submitButton = app.buttons.matching(submitButtonPredicate)
        submitButton.element.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
   
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func resetPassword(_ app: XCUIApplication) {
        registerWithDenarii(app)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        navigateToLogin(app)
        
        let forgotPasswordButton = app.buttons["Forgot Password"]
        forgotPasswordButton.tap()
        
        let userNameOrEmailPredicate = NSPredicate(format: "placeholderValue beginswith 'Username or Email'")
        
        let usernameOrEmailTextField = app.textFields.matching(userNameOrEmailPredicate)
        usernameOrEmailTextField.element.tap()
        usernameOrEmailTextField.element.typeText("User_\(self.currentTestName)")

        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let submitButton = app.buttons["Request Reset"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        var nextButton = app.buttons["Next"]
        nextButton.tap()
        
        let resetIdTextField = app.textFields["Reset id"]
        resetIdTextField.tap()
        resetIdTextField.typeText("123")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let verifyResetButton = app.buttons["Verify Reset"]
        verifyResetButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        // Refresh the next button link
        nextButton = app.buttons["Next"]
        nextButton.tap()
        
        let usernameTextField = app.textFields["Name"]
        usernameTextField.tap()
        usernameTextField.typeText("User_\(self.currentTestName)")
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email_\(self.currentTestName)@email.com")
        
        let passwordTextField = app.secureTextFields["Password"]
        passwordTextField.tap()
        passwordTextField.typeText("new_password_\(self.currentTestName)")
        
        let confirmPasswordTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordTextField.tap()
        confirmPasswordTextField.typeText("new_password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let resetPasswordButton = app.buttons["Reset Password"]
        resetPasswordButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        // Refresh the next button link
        nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    
    func createWallet(_ app: XCUIApplication) {
        loginWithDenarii(app)
        
        let createWalletButton = app.buttons["Create Wallet"]
        createWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("password")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let confirmWalletPasswordSecureTextField = app.secureTextFields["Confirm Wallet Password"]
        confirmWalletPasswordSecureTextField.tap()
        confirmWalletPasswordSecureTextField.typeText("password")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func refreshBalance(_ app: XCUIApplication) {
        createWallet(app)
        
        let refreshBalanceButton = app.buttons["Refresh Balance"]
        refreshBalanceButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func sendDenarii(_ app: XCUIApplication) {
        refreshBalance(app)
        
        let amountToSendTextField = app.textFields["Amount to Send"]
        amountToSendTextField.tap()
        amountToSendTextField.typeText("2")
        
        let sendToTextField = app.textFields["Send To"]
        sendToTextField.tap()
        sendToTextField.typeText("345")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        
        let sendDenariiButton = app.buttons["Send"]
        sendDenariiButton.tap()
    }
    

    override class var runsForEachTargetApplicationUIConfiguration: Bool {
        true
    }

    override func setUpWithError() throws {
        continueAfterFailure = false
        
        // get the name and remove the class name and what comes before the class name
        self.currentTestName = self.name.replacingOccurrences(of: "-[denariiUITests ", with: "")

        // And then you'll need to remove the closing square bracket at the end of the test name
        self.currentTestName = self.currentTestName.replacingOccurrences(of: "]", with: "")
    }

    func testLaunchScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Launch Screen"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
    
    func testRefreshBalanceScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
    
        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        refreshBalance(app)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Refresh Balance Screen"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
    
    func testSendDenariiScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        sendDenarii(app)
        
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Send Denarii Screen"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
    
    func testResetPasswordScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        resetPassword(app)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Reset Password Process (Login Screen)"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
    
    func testCancelBuyDenarii() throws {
        
    }
    
    func testCancelSellDenarii() throws {
        
    }
    
    func testSellDenariiAndBuyDenarii() throws {
        
    }
    
    func testVerifyIdentity() throws {
        
    }

    func testChangeCreditCardInfo() throws {
        
    }
    
    func testResolveSupportTicket() throws {
        
    }
    
    func testDeleteSupportTicket() throws {
        
    }
    
    func testDeleteAccount() throws {
        
    }
}
