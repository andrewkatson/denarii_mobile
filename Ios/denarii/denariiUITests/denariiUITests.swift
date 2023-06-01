//
//  denariiUITests.swift
//  denariiUITests
//
//  Created by Andrew Katson on 5/14/23.
//

import XCTest
@testable import denarii

final class denariiUITests: XCTestCase {
    
    func dismissKeyboardIfPresent(_ app: XCUIApplication) {
        if app.keyboards.buttons["Return"].exists {
            app.keyboards.buttons["Return"].tap()
        }
    }
    
    func tapAway(_ app: XCUIApplication) {
        app.tap()
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
    
    func loginWithDenarii(_ app: XCUIApplication) {
        navigateToLoginFromLoginOrRegister(app)
        
        let usernamePredicate = NSPredicate(format: "placeholderValue beginswith 'Name'")
        let emailPredicate = NSPredicate(format: "placeholderValue beginswith 'Email'")
        let passwordPredicate = NSPredicate(format: "placeholderValue beginswith 'Password'")
        let confirmPasswordPredicate = NSPredicate(format: "placeholderValue beginswith 'Confirm Password'")
        let submitButtonPredicate = NSPredicate(format: "label beginswith 'Submit'")
        let nextButtonPredicate = NSPredicate(format: "label beginswith 'Next'")
        
        let loginTextField = app.textFields.matching(usernamePredicate)
        loginTextField.element.tap()
        loginTextField.element.typeText("User")
        
        let emailTextField = app.textFields.matching(emailPredicate)
        emailTextField.element.tap()
        emailTextField.element.typeText("Email@email.com")
        
        let passwordSecureTextField = app.secureTextFields.matching(passwordPredicate)
        passwordSecureTextField.element.tap()
        passwordSecureTextField.element.typeText("Password")
        
        let confirmPasswordSecureTextField = app.secureTextFields.matching(confirmPasswordPredicate)
        confirmPasswordSecureTextField.element.tap()
        confirmPasswordSecureTextField.element.typeText("Password")

        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let submitButton = app.buttons.matching(submitButtonPredicate)
        submitButton.element.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons.matching(nextButtonPredicate)
        nextButton.element.tap()
    }
    
    func registerWithDenarii(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)

        navigateToRegister(app)
        
        let usernamePredicate = NSPredicate(format: "placeholderValue beginswith 'Name'")
        let emailPredicate = NSPredicate(format: "placeholderValue beginswith 'Email'")
        let passwordPredicate = NSPredicate(format: "placeholderValue beginswith 'Password'")
        let confirmPasswordPredicate = NSPredicate(format: "placeholderValue beginswith 'Confirm Password'")
        let submitButtonPredicate = NSPredicate(format: "label beginswith 'Submit'")
        let nextButtonPredicate = NSPredicate(format: "label beginswith 'Next'")
        
        let loginTextField = app.textFields.matching(usernamePredicate)
        loginTextField.element.tap()
        loginTextField.element.typeText("User")
        
        let emailTextField = app.textFields.matching(emailPredicate)
        emailTextField.element.tap()
        emailTextField.element.typeText("Email@email.com")
        
        let passwordSecureTextField = app.secureTextFields.matching(passwordPredicate)
        passwordSecureTextField.element.tap()
        passwordSecureTextField.element.typeText("Password")
        
        let confirmPasswordSecureTextField = app.secureTextFields.matching(confirmPasswordPredicate)
        confirmPasswordSecureTextField.element.tap()
        confirmPasswordSecureTextField.element.typeText("Password")

        // After we type things in we need to dismiss the keyboard
        XCTAssert(app.keyboards.count > 0, "The keyboard is not shown")
        dismissKeyboardIfPresent(app)
        XCTAssert(app.keyboards.count == 0, "The keyboard is shown")
        
        let submitButton = app.buttons.matching(submitButtonPredicate)
        submitButton.element.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons.matching(nextButtonPredicate)
        nextButton.element.tap()
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
    
    func openWallet(_ app: XCUIApplication) {
        loginWithDenarii(app)
        
        let openWalletButton = app.buttons["Open Wallet"]
        openWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet")
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("password")
        
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
    
    func restoreDeterministicWallet(_ app: XCUIApplication) {
        loginWithDenarii(app)
        
        let restoreWalletButton = app.buttons["Restore Deterministic Wallet"]
        restoreWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet")
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("password")
        
        let walletSeedTextField = app.textFields["Wallet Seed"]
        walletSeedTextField.tap()
        walletSeedTextField.typeText("some seed here")
        
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
    
    func checkForLoginView(_ app: XCUIApplication) {
        XCTAssert(app.buttons["Submit"].exists)
        XCTAssert(app.textFields["Name"].exists)
        XCTAssert(app.textFields["Email"].exists)
        XCTAssert(app.secureTextFields["Password"].exists)
        XCTAssert(app.secureTextFields["Confirm Password"].exists)
    }
    
    func checkForWalletDecisionView(_ app: XCUIApplication) {
        let createWalletButtonPredicate = NSPredicate(format: "label beginswith 'Create Wallet'")
        XCTAssert(app.buttons.matching(createWalletButtonPredicate).element.exists)
        
        let openWalletButtonPredicate = NSPredicate(format: "label beginswith 'Open Wallet'")
        XCTAssert(app.buttons.matching(openWalletButtonPredicate).element.exists)
        
        let restoreWalletButtonPredicate = NSPredicate(format: "label beginswith 'Restore Deterministic Wallet'")
        XCTAssert(app.buttons.matching(restoreWalletButtonPredicate).element.exists)
    }
    
    func checkForOpenedWalletView(_ app: XCUIApplication) {
        XCTAssert(app.staticTexts["Wallet Name"].exists)
        XCTAssert(app.staticTexts["Seed"].exists)
        XCTAssert(app.staticTexts["Balance"].exists)
        XCTAssert(app.staticTexts["Balance Value"].exists)
        XCTAssert(app.staticTexts["Address"].exists)
        XCTAssert(app.buttons["Refresh Balance"].exists)
        XCTAssert(app.buttons["Send"].exists)
        XCTAssert(app.textFields["Send To"].exists)
        XCTAssert(app.textFields["Amount to Send"].exists)
    }
    
    func checkForUpdatedBalance(_ app: XCUIApplication, _ balance: Double) {
        XCTAssert(app.staticTexts["Balance Value"].label == String(balance))
    }
    
    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false
        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testLogin() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        loginWithDenarii(app)
        
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForWalletDecisionView(app)
    }
    
    func testRegister() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        registerWithDenarii(app)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForWalletDecisionView(app)
    }

    func testResetPassword() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
    
        resetPassword(app)
    
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForLoginView(app)
    }
    
    func testCreateWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        createWallet(app)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
    }
    
    func testOpenWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        openWallet(app)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
    }
    
    func testRestoreDeterministicWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        restoreDeterministicWallet(app)
        
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
    }
    
    func testRefreshBalance() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        refreshBalance(app);

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForUpdatedBalance(app, 3.0);
    }
    
    func testSendDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        sendDenarii(app)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForUpdatedBalance(app, 1.0)
    }
    
    
    func testLaunchLoginPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()
                
                loginWithDenarii(app)
            }
        }
    }
    
    func testLaunchRegisterPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()
             
                registerWithDenarii(app)
            }
        }
    }
    
    func testLaunchResetPasswordPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()
                
                resetPassword(app)
            }
        }
    }
    
    func testLaunchCreateWalletPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                createWallet(app)
            }
        }
    }
    
    func testLaunchOpeneWalletPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                openWallet(app)
            }
        }
    }
    
    func testLaunchRestoreDeterministicWalletPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                restoreDeterministicWallet(app)
            }
        }
    }
    
    func testRefreshBalancePerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                refreshBalance(app)
            }
        }
    }
    
    func testSendDenariiPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                sendDenarii(app)
            }
        }
    }
}
