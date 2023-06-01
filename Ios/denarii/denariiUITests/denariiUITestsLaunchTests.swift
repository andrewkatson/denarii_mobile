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
            var coordinate: XCUICoordinate = self.coordinate(withNormalizedOffset: CGVectorMake(0.0, 0.0))
            coordinate.tap()
        }
    }
}

final class denariiUITestsLaunchTests: XCTestCase {
    
    func dismissKeyboardIfPresent(_ app: XCUIApplication) {
        if app.keyboards.buttons["Return"].exists {
            app.keyboards.buttons["Return"].tap()
        }
    }
    
    func tapAway(_ app: XCUIApplication) {
        app.tap()
        
        // Have to do redundant tapping becuase the popover in landscape is harder to tap
        if app.staticTexts["Popover"].exists {
            app.staticTexts["Popover"].tap()
        }
        if app.staticTexts["Refresh Balance Popover"].exists {
            app.staticTexts["Refresh Balance Popover"].tap()
        }
        if app.staticTexts["Send Denarii Popover"].exists {
            app.staticTexts["Send Denarii Popover"].tap()
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
    
    func navigateToLoginFromLoginOrRegister(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)
        navigateToLogin(app)
    }
    
    
    func loginWithDenarii(_ app: XCUIApplication) {
        navigateToLoginFromLoginOrRegister(app)
        
        let usernamePredicate = NSPredicate(format: "placeholderValue beginswith 'Name'")
        let emailPredicate = NSPredicate(format: "placeholderValue beginswith 'Email'")
        let passwordPredicate = NSPredicate(format: "placeholderValue beginswith 'Password'")
        let confirmPasswordPredicate = NSPredicate(format: "placeholderValue beginswith 'Confirm Password'")
        let submitButtonPredicate = NSPredicate(format: "label beginswith 'Submit'")
        
        let loginTextField = app.textFields.matching(usernamePredicate)
        loginTextField.element.tap()
        loginTextField.element.typeText("User")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let emailTextField = app.textFields.matching(emailPredicate)
        emailTextField.element.tap()
        emailTextField.element.typeText("Email@email.com")
        
        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let passwordSecureTextField = app.secureTextFields.matching(passwordPredicate)
        passwordSecureTextField.element.tap()
        passwordSecureTextField.element.typeText("Password")
        
        let confirmPasswordSecureTextField = app.secureTextFields.matching(confirmPasswordPredicate)
        confirmPasswordSecureTextField.element.tap()
        confirmPasswordSecureTextField.element.typeText("Password")

        let submitButton = app.buttons.matching(submitButtonPredicate)
        submitButton.element.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
   
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func resetPassword(_ app: XCUIApplication) {
        navigateToLoginFromLoginOrRegister(app)
        
        let forgotPasswordButton = app.buttons["Forgot Password"]
        forgotPasswordButton.tap()
        
        let userNameOrEmailPredicate = NSPredicate(format: "placeholderValue beginswith 'Username or Email'")
        
        let usernameOrEmailTextField = app.textFields.matching(userNameOrEmailPredicate)
        usernameOrEmailTextField.element.tap()
        usernameOrEmailTextField.element.typeText("User")

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
        usernameTextField.typeText("User")
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email@email.com")
        
        let passwordTextField = app.secureTextFields["Password"]
        passwordTextField.tap()
        passwordTextField.typeText("password")
        
        let confirmPasswordTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordTextField.tap()
        confirmPasswordTextField.typeText("password")
        
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
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("password")
        
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
}
