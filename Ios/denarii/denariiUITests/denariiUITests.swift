//
//  denariiUITests.swift
//  denariiUITests
//
//  Created by Andrew Katson on 5/14/23.
//

import XCTest
@testable import denarii

final class denariiUITests: XCTestCase {
    
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
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func navigateToLogin(_ app: XCUIApplication) {
        let loginButton = app.buttons["Login"]
        loginButton.tap()
    }
    
    func navigateToRegister(_ app: XCUIApplication) {
        let registerButton = app.buttons["Register"]
        registerButton.tap()
    }
    
    func navigateToLoginFromLoginOrRegister(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)
        navigateToLogin(app)
    }
    
    func loginWithDenarii(_ app: XCUIApplication) {
        registerWithDenarii(app)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        navigateToLogin(app)
        
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
        dismissKeyboardIfPresent(app)
        
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
        dismissKeyboardIfPresent(app)
        
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
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordTextField = app.secureTextFields["Password"]
        passwordTextField.tap()
        passwordTextField.typeText("password")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordTextField.tap()
        confirmPasswordTextField.typeText("password")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
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
        walletNameTextField.typeText("wallet_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmWalletPasswordSecureTextField = app.secureTextFields["Confirm Wallet Password"]
        confirmWalletPasswordSecureTextField.tap()
        confirmWalletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func openWallet(_ app: XCUIApplication) {
        createWallet(app)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        let openWalletButton = app.buttons["Open Wallet"]
        openWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func restoreDeterministicWallet(_ app: XCUIApplication) {
        createWallet(app)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        let restoreWalletButton = app.buttons["Restore Deterministic Wallet"]
        restoreWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletSeedTextField = app.textFields["Wallet Seed"]
        walletSeedTextField.tap()
        walletSeedTextField.typeText("some seed here")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
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
        dismissKeyboardIfPresent(app)
        
        
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
        
        // get the name and remove the class name and what comes before the class name
        self.currentTestName = self.name.replacingOccurrences(of: "-[denariiUITests ", with: "")

        // And then you'll need to remove the closing square bracket at the end of the test name
        self.currentTestName = self.currentTestName.replacingOccurrences(of: "]", with: "")
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
    
    func testBuyDenarii() throws {
        
    }
    
    func testCancelBuyOfDenarii() throws {
        
    }
    
    func testSellDenarii() throws {
        
    }
    
    func testCancelSellDenarii() throws {
        
    }
    
    func testVerifyIdentity() throws {
        
    }
    
    func testSetCreditCardInfo() throws {
        
    }
    
    func testClearCreditCardInfo() throws {
        
    }
    
    func testSupportTickets() throws {
        
    }
    
    func testCreateSupportTicket() throws {
        
    }
    
    func testDeleteSupportTicket() throws {
        
    }
    
    func testResolveSupportTicket() throws {
        
    }
    
    func testDeleteAccount() throws {
        
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
    
    func testBuyDenariiPerformance() throws {
        
    }
    
    func testCancelBuyOfDenariiPerformance() throws {
        
    }
    
    func testSellDenariiPerformance() throws {
        
    }
    
    func testCancelSellDenariiPerformance() throws {
        
    }
    
    func testVerifyIdentityPerformance() throws {
        
    }
    
    func testSetCreditCardInfoPerformance() throws {
        
    }
    
    func testClearCreditCardInfoPerformance() throws {
        
    }
    
    func testSupportTicketsPerformance() throws {
        
    }
    
    func testCreateSupportTicketPerformance() throws {
        
    }
    
    func testDeleteSupportTicketPerformance() throws {
        
    }
    
    func testResolveSupportTicketPerformance() throws {
        
    }
    
    func testDeleteAccountPerformance() throws {
        
    }
}
