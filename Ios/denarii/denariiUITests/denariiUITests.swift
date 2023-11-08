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
    
    func refreshScreen(_ app: XCUIApplication, _ element: XCUIElement) {
        let start = element.coordinate(withNormalizedOffset: CGVectorMake(0, 0))
        let finish = element.coordinate(withNormalizedOffset: CGVectorMake(0, 6))
        start.press(forDuration: 0, thenDragTo: finish)
    }
    
    func tapAway(_ app: XCUIApplication) {
        app.tap()
        
        tapAwayOnPopovers(app, [Constants.POPOVER, Constants.BUY_DENARII_POPOVER, Constants.CANCEL_BUY_DENARII_POPOVER, Constants.CANCEL_SELL_DENARII_POPOVER, Constants.CLEAR_CREDIT_CARD_INFO_POPOVER, Constants.CREATE_NEW_COMMENT_POPOVER, Constants.DELETE_ACCOUNT_POPOVER, Constants.DELETE_TICKET_POPOVER, Constants.LOGOUT_POPOVER,Constants.REFRESH_BALANCE_POPOVER, Constants.RESOLVE_TICKET_POPOVER, Constants.SELL_DENARII_POPOVER, Constants.SEND_DENARII_POPOVER, Constants.SET_CREDIT_CARD_INFO_POPOVER])
    }

    func tapAwayOnPopovers(_ app: XCUIApplication, _ popoverNames: Array<String>) {
        for popoverName in popoverNames{
            // Have to do redundant tapping becuase the popover in landscape is harder to tap
            if app.staticTexts[popoverName].exists {
                app.staticTexts[popoverName].tap()
                if app.staticTexts[popoverName].exists {
                    app.staticTexts[popoverName].swipeDown()
                }
                XCTAssert(!app.staticTexts[popoverName].exists, "\(popoverName) still exists")
            }
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
    
    func loginWithDenarii(_ app: XCUIApplication, _ suffix: String) {
        registerWithDenarii(app, suffix)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        navigateToLogin(app)
        
        let loginTextField = app.textFields["Name"]
        loginTextField.tap()
        loginTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordSecureTextField = app.secureTextFields["Password"]
        passwordSecureTextField.tap()
        passwordSecureTextField.typeText("Password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordSecureTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordSecureTextField.tap()
        confirmPasswordSecureTextField.typeText("Password_\(self.currentTestName)_\(suffix)")

        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func registerWithDenarii(_ app: XCUIApplication, _ suffix: String) {
        navigateToLoginOrRegister(app)

        navigateToRegister(app)
        
        let loginTextField = app.textFields["Name"]
        loginTextField.tap()
        loginTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordSecureTextField = app.secureTextFields["Password"]
        passwordSecureTextField.tap()
        passwordSecureTextField.typeText("Password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordSecureTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordSecureTextField.tap()
        confirmPasswordSecureTextField.typeText("Password_\(self.currentTestName)_\(suffix)")

        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func resetPassword(_ app: XCUIApplication, _ suffix: String) {
        registerWithDenarii(app, suffix)
        
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
        usernameOrEmailTextField.element.typeText("User_\(self.currentTestName)_\(suffix)")

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
        usernameTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("Email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordTextField = app.secureTextFields["Password"]
        passwordTextField.tap()
        passwordTextField.typeText("password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordTextField = app.secureTextFields["Confirm Password"]
        confirmPasswordTextField.tap()
        confirmPasswordTextField.typeText("password_\(self.currentTestName)_\(suffix)")
        
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
    
    func strictlyCreateWallet(_ app: XCUIApplication, _ suffix: String) {
        let createWalletButton = app.buttons["Create Wallet"]
        createWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmWalletPasswordSecureTextField = app.secureTextFields["Confirm Wallet Password"]
        confirmWalletPasswordSecureTextField.tap()
        confirmWalletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func createWallet(_ app: XCUIApplication, _ suffix: String) {
        loginWithDenarii(app, suffix)
        
        strictlyCreateWallet(app, suffix)
    }
    
    func openWallet(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        let openWalletButton = app.buttons["Open Wallet"]
        openWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func restoreDeterministicWallet(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        // Then navigate back
        let backButton = app.buttons["Back"]
        backButton.tap()
        
        let backButtonTwo = app.buttons["Back"]
        backButtonTwo.tap()
        
        let restoreWalletButton = app.buttons["Restore Deterministic Wallet"]
        restoreWalletButton.tap()
        
        let walletNameTextField = app.textFields["Wallet Name"]
        walletNameTextField.tap()
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        walletPasswordSecureTextField.tap()
        walletPasswordSecureTextField.typeText("wallet_password_\(self.currentTestName)_\(suffix)")
        
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
    
    func refreshBalance(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let refreshBalanceButton = app.buttons["Refresh Balance"]
        refreshBalanceButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func sendDenarii(_ app: XCUIApplication, _ suffix: String) {
        refreshBalance(app, suffix)
        
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
    
    func logout(_ app: XCUIApplication, _ suffix: String) {
        let userSettingsButton = app.buttons["Settings"]
        userSettingsButton.tap()
        
        let logoutButton = app.buttons["Logout"]
        logoutButton.tap()
        
        // We should be on the ContentView screen
        XCTAssert(app.buttons["Next"].exists)
    }
    
    func logoutFromWalletDecision(_ app: XCUIApplication, _ suffix: String) {
        strictlyCreateWallet(app, suffix)
        logout(app, suffix)
    }
    
    func logoutFromSupportTickets(_ app: XCUIApplication, _ suffix: String) {
        let backButton = app.buttons["Back"]
        backButton.tap()
        logout(app, suffix)
    }
    
    func logoutFromSupportTicketDetails(_ app: XCUIApplication, _ suffix: String) {
        let backToCreateSupportTicketButton = app.buttons["Back"]
        backToCreateSupportTicketButton.tap()
        
        let backToSupportTicketsButton = app.buttons["Back"]
        backToSupportTicketsButton.tap()
        logoutFromSupportTickets(app, suffix)
    }
    
    func buyDenarii(_ app: XCUIApplication, _ firstUserSuffix: String, _ secondUserSuffix: String) {
        sellDenarii(app, firstUserSuffix)
        
        logout(app, firstUserSuffix)
        
        createWallet(app, secondUserSuffix)
        
        let buyDenariiButton = app.buttons["Buy Denarii"]
        buyDenariiButton.tap()
        
        let amountTextField = app.textFields["Amount"]
        amountTextField.tap()
        // More than is sold
        amountTextField.typeText("10.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        // Willing to buy at a high price
        let priceTextField = app.textFields["Price"]
        priceTextField.tap()
        priceTextField.typeText("100.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let secondBuyDenariiButton = app.buttons["Buy Denarii"]
        secondBuyDenariiButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func cancelBuyDenarii(_ app: XCUIApplication, _ firstUserSuffix: String, _ secondUserSuffix: String) {
        buyDenarii(app, firstUserSuffix, secondUserSuffix)
        
        let queuedBuysGrid = app.grids[Constants.QUEUED_BUYS_GRID]
        refreshScreen(app, queuedBuysGrid)
        
        let cancelButton = queuedBuysGrid.buttons["Cancel"]
        cancelButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func sellDenarii(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let sellDenariiButton = app.buttons["Sell Denarii"]
        sellDenariiButton.tap()
        
        let amountTextField = app.textFields["Amount"]
        amountTextField.tap()
        // More than is sold
        amountTextField.typeText("10.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        // Willing to buy at a high price
        let priceTextField = app.textFields["Price"]
        priceTextField.tap()
        priceTextField.typeText("100.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let secondSellDenariiButto  = app.buttons["Sell Denarii"]
        secondSellDenariiButto.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func cancelSellDenarii(_ app: XCUIApplication, _ suffix: String) {
        sellDenarii(app, suffix)
        
        let ownAsksGrid = app.grids[Constants.OWNED_ASKS_GRID]
        refreshScreen(app, ownAsksGrid)
        
        let cancelButton = ownAsksGrid.buttons["Cancel Ask"]
        cancelButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func verifyIdentity(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let verificationButton = app.buttons["Verification"]
        verificationButton.tap()
        
        let firstNameTextField = app.textFields["First Name"]
        firstNameTextField.tap()
        firstNameTextField.typeText("first_name_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let middleNameTextField = app.textFields["Middle Name"]
        middleNameTextField.tap()
        middleNameTextField.typeText("middle_name_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let lastNameTextField = app.textFields["Last Name"]
        lastNameTextField.tap()
        lastNameTextField.typeText("last_name_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        emailTextField.tap()
        emailTextField.typeText("email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let dateOfBirthTextField = app.textFields["Date Of Birth"]
        dateOfBirthTextField.tap()
        dateOfBirthTextField.typeText("2020/02/10")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let socialSecurityNumberTextField = app.textFields["Social Security Number"]
        socialSecurityNumberTextField.tap()
        socialSecurityNumberTextField.typeText("101")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let zipcodeTextField = app.textFields["Zipcode"]
        zipcodeTextField.tap()
        zipcodeTextField.typeText("Zipcode")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let phoneNumberTextField = app.textFields["Phone Number"]
        phoneNumberTextField.tap()
        phoneNumberTextField.typeText("123")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workCityTextField = app.textFields["Work City"]
        workCityTextField.tap()
        workCityTextField.typeText("city_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workStateTexttField = app.textFields["Work State"]
        workStateTexttField.tap()
        workStateTexttField.typeText("state_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workCountryTextField = app.textFields["Work Country"]
        workCountryTextField.tap()
        workCountryTextField.typeText("country_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func setCreditCardInfo(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let creditCardInfoButton = app.buttons["Credit Card"]
        creditCardInfoButton.tap()
        
        let creditCardNumberTextField = app.textFields["Credit Card Number"]
        creditCardNumberTextField.tap()
        creditCardNumberTextField.typeText("234")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardExpirationDateMonthTextField = app.textFields["Expiration Date Month"]
        creditCardExpirationDateMonthTextField.tap()
        creditCardExpirationDateMonthTextField.typeText("02")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardExpirationDateYearTextField = app.textFields["Expiration Date Year"]
        creditCardExpirationDateYearTextField.tap()
        creditCardExpirationDateYearTextField.typeText("2020")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardSecurityCodeTextField = app.textFields["Security Code"]
        creditCardSecurityCodeTextField.tap()
        creditCardSecurityCodeTextField.typeText("001")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let setInfoButton = app.textFields["Set Info"]
        setInfoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func clearCreditCardInfo(_ app: XCUIApplication, _ suffix: String) {
        setCreditCardInfo(app, suffix)
        
        let clearCreditCardInfoButton = app.buttons["Clear Info"]
        clearCreditCardInfoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func navigateToSupportTickets(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let userSettingsButton = app.buttons["Settings"]
        userSettingsButton.tap()
        
        let supportTicketsButton = app.buttons["Support Tickets"]
        supportTicketsButton.tap()
    }
    
    func createSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        navigateToSupportTickets(app, suffix)
        
        let createTicketButton = app.buttons["Create Support Ticket"]
        createTicketButton.tap()
        
        let titleTextField = app.textFields["Title"]
        titleTextField.tap()
        titleTextField.typeText("title_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let descriptionTextField = app.textFields["Description"]
        descriptionTextField.tap()
        descriptionTextField.typeText("description_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let createTicketTwoButton = app.buttons["Create Support Ticket"]
        createTicketTwoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func createNewComment(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let newCommentTextField = app.textFields["New Comment"]
        newCommentTextField.tap()
        newCommentTextField.typeText("comment_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let createNewCommentButton = app.buttons["Submit"]
        createNewCommentButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func deleteSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let deleteTicketButton = app.buttons["Delete"]
        deleteTicketButton.tap()
    }
    
    func resolveSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let resolveButton = app.buttons["Resolve"]
        resolveButton.tap()
    }
    
    func deleteAccount(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        let userSettingsButton = app.buttons["Settings"]
        userSettingsButton.tap()
        
        let deleteAccountButton = app.buttons["Delete Account"]
        deleteAccountButton.tap()
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
    
    func checkForSupportTicketDetailsView(_ app: XCUIApplication) {
        XCTAssert(app.buttons["Submit"].exists)
        XCTAssert(app.buttons["Resolve"].exists)
        XCTAssert(app.buttons["Delete"].exists)
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
        
        loginWithDenarii(app, Constants.FIRST_USER)
        
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForWalletDecisionView(app)
        
        // Always log the user out
        logoutFromWalletDecision(app, Constants.FIRST_USER)
    }
    
    func testRegister() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        registerWithDenarii(app, Constants.FIRST_USER)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForWalletDecisionView(app)
        
        // Always log the user out
        logoutFromWalletDecision(app, Constants.FIRST_USER)
    }

    func testResetPassword() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
    
        resetPassword(app, Constants.FIRST_USER)
    
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForLoginView(app)
    }
    
    func testCreateWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        createWallet(app, Constants.FIRST_USER)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testOpenWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        openWallet(app, Constants.FIRST_USER)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testRestoreDeterministicWallet() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        restoreDeterministicWallet(app, Constants.FIRST_USER)
        
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForOpenedWalletView(app)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testRefreshBalance() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        refreshBalance(app, Constants.FIRST_USER);

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForUpdatedBalance(app, 3.0);
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testSendDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        sendDenarii(app, Constants.FIRST_USER)

        // Use XCTAssert and related functions to verify your tests produce the correct results.
        checkForUpdatedBalance(app, 1.0)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testBuyDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        buyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)
        
        XCTAssert(app.buttons["Cancel"].exists)
        
        // Always log the user out
        logout(app, Constants.SECOND_USER)
    }
    
    func testCancelBuyOfDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
     
        cancelBuyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)
        
        XCTAssert(!app.buttons["Cancel"].exists)
        
        // Always log the user out
        logout(app, Constants.SECOND_USER)
    }
    
    func testSellDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
     
        sellDenarii(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Cancel"].exists)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testCancelSellDenarii() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        cancelSellDenarii(app, Constants.FIRST_USER)
        
        XCTAssert(!app.buttons["Cancel"].exists)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testVerifyIdentity() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        verifyIdentity(app, Constants.FIRST_USER)
        
        XCTAssert(!app.buttons["Submit"].exists)
        
        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testSetCreditCardInfo() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        setCreditCardInfo(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Clear Info"].exists)

        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testClearCreditCardInfo() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
     
        clearCreditCardInfo(app, Constants.FIRST_USER)
        
        XCTAssert(!app.buttons["Clear Info"].exists)

        // Always log the user out
        logout(app, Constants.FIRST_USER)
    }
    
    func testSupportTickets() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
     
        navigateToSupportTickets(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Create Support Ticket"].exists)
        XCTAssert(!app.buttons["Submit"].exists)
        
        // Always log the user out
        logoutFromSupportTickets(app, Constants.FIRST_USER)
    }
    
    func testCreateSupportTicket() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        createSupportTicket(app, Constants.FIRST_USER)
        
        checkForSupportTicketDetailsView(app)
        
        // Always log the user out
        logoutFromSupportTicketDetails(app, Constants.FIRST_USER)
    }
    
    func testCreateNewComment() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        createNewComment(app, Constants.FIRST_USER)

        checkForSupportTicketDetailsView(app)
        
        // Always log the user out
        logoutFromSupportTicketDetails(app, Constants.FIRST_USER)
    }
    
    func testDeleteSupportTicket() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        deleteSupportTicket(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Create Support Ticket"].exists)

        // Always log the user out
        logoutFromSupportTickets(app, Constants.FIRST_USER)
    }
    
    func testResolveSupportTicket() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        resolveSupportTicket(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Create Support Ticket"].exists)
        
        // Always log the user out
        logoutFromSupportTickets(app, Constants.FIRST_USER)
    }
    
    func testDeleteAccount() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()
        
        deleteAccount(app, Constants.FIRST_USER)
        
        XCTAssert(app.buttons["Next"].exists)
    }
    
    
    func testLaunchLoginPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()
                
                loginWithDenarii(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromWalletDecision(app, Constants.FIRST_USER)
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
             
                registerWithDenarii(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromWalletDecision(app, Constants.FIRST_USER)
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
                
                resetPassword(app, Constants.FIRST_USER)
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

                createWallet(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
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

                openWallet(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
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

                restoreDeterministicWallet(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
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

                refreshBalance(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
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

                sendDenarii(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testBuyDenariiPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                buyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)
                
                // Always log the user out
                logout(app, Constants.SECOND_USER)
            }
        }
    }
    
    func testCancelBuyOfDenariiPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                cancelBuyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)
                
                // Always log the user out
                logout(app, Constants.SECOND_USER)
            }
        }
    }
    
    func testSellDenariiPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                sellDenarii(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testCancelSellDenariiPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                cancelSellDenarii(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testVerifyIdentityPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                verifyIdentity(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testSetCreditCardInfoPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                setCreditCardInfo(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testClearCreditCardInfoPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                clearCreditCardInfo(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testSupportTicketsPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                navigateToSupportTickets(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromSupportTickets(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testCreateSupportTicketPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                createSupportTicket(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromSupportTicketDetails(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testCreateNewCommentPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                createNewComment(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromSupportTicketDetails(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testDeleteSupportTicketPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                deleteSupportTicket(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromSupportTickets(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testResolveSupportTicketPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                resolveSupportTicket(app, Constants.FIRST_USER)
                
                // Always log the user out
                logoutFromSupportTickets(app, Constants.FIRST_USER)
            }
        }
    }
    
    func testDeleteAccountPerformance() throws {
        if #available(macOS 10.15, iOS 13.0, tvOS 13.0, watchOS 7.0, *) {
            // This measures how long it takes to launch your application.
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launchArguments = ["UI-TESTING"]
                app.launch()

                deleteAccount(app, Constants.FIRST_USER)
                
                // Always log the user out
                logout(app, Constants.FIRST_USER)
            }
        }
    }
}
