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
        let maxAttempts = 5
        var attempt = 0
        while (true && attempt < maxAttempts) {
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            if app.keyboards.buttons["Return"].exists {
                app.keyboards.buttons["Return"].tap()
                break
            }
            attempt += 1
        }
    }
    
    func refreshScreen(_ app: XCUIApplication, _ element: XCUIElement, _ amountToSwipe: CGFloat) {
        let start = element.coordinate(withNormalizedOffset: CGVectorMake(0, 0))
        let finish = element.coordinate(withNormalizedOffset: CGVectorMake(0, amountToSwipe))
        start.press(forDuration: 0, thenDragTo: finish)
    }
    
    func refreshScreen(_ app: XCUIApplication, _ element: XCUIElement) {
        refreshScreen(app, element, 6)
    }
    
    func refreshScreenUntilButtonAppears(_ app: XCUIApplication, _ element: XCUIElement, _ buttonName: String) {
        let maxAttempts = 3
        var attempt = 0
        while !app.buttons[buttonName].exists && attempt < maxAttempts {
            refreshScreen(app, element)
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            attempt += 1
        }
    }
    
    func tapAway(_ app: XCUIApplication) {
        tapAwayOnPopovers(app, [Constants.POPOVER, Constants.BUY_DENARII_POPOVER, Constants.CANCEL_BUY_DENARII_POPOVER, Constants.CANCEL_SELL_DENARII_POPOVER, Constants.CLEAR_CREDIT_CARD_INFO_POPOVER, Constants.CREATE_NEW_COMMENT_POPOVER, Constants.DELETE_ACCOUNT_POPOVER, Constants.DELETE_TICKET_POPOVER, Constants.LOGOUT_POPOVER,Constants.REFRESH_BALANCE_POPOVER, Constants.RESOLVE_TICKET_POPOVER, Constants.SELL_DENARII_POPOVER, Constants.SEND_DENARII_POPOVER, Constants.SET_CREDIT_CARD_INFO_POPOVER])
    }

    func tapAwayOnPopovers(_ app: XCUIApplication, _ popoverNames: Array<String>) {
        for popoverName in popoverNames{
            // Have to do redundant tapping becuase the popover in landscape is harder to tap
            while app.staticTexts[popoverName].exists {
                app.staticTexts[popoverName].tap()
                RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            }
        }
    }
    
    func tapElementAndWaitForPopoverToAppear(_ app: XCUIApplication, _ element: XCUIElement, _ popoverName: String) {
        while (!app.staticTexts[popoverName].exists) {
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            element.tap()
        }
    }
    
    func tapElementAndWaitForKeyboardToAppear(_ element: XCUIElement) {
        let keyboard = XCUIApplication().keyboards.element
        while (true) {
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            if element.exists {
                element.tap()
            }
            if keyboard.exists {
                break;
            }
        }
    }
    
    func openSidebar(_ app: XCUIApplication) {
        if app.buttons[Constants.SIDEBAR_BUTTON].exists {
            let sidebarButton = app.buttons[Constants.SIDEBAR_BUTTON]
            sidebarButton.tap()
        }
    }
    
    func closeSidebar(_ app: XCUIApplication) {
        if app.buttons[Constants.SIDEBAR_BUTTON].exists {
            let sidebarButton = app.buttons[Constants.SIDEBAR_BUTTON]
            sidebarButton.tap()
        }
    }
    
    func tapButtonAndWaitForTextFieldToAppear(_ app: XCUIApplication, _ button: XCUIElement, _ textFieldName: String) {
        while (true) {
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            let textField = app.textFields[textFieldName]
            if textField.exists {
                break;
            }
            button.tap()
        }
    }
    
    func tapButtonAndWaitForButtonToAppear(_ app: XCUIApplication, _ button: XCUIElement, _ buttonName: String) {
        while (true) {
            RunLoop.current.run(until: NSDate(timeIntervalSinceNow: 1.0) as Date)
            let otherButton = app.buttons[buttonName]
            if otherButton.exists {
                break;
            }
            button.tap()
        }
    }
    
    func navigateToLoginOrRegister(_ app: XCUIApplication) {
        let nextButton = app.buttons["Next"]
        tapButtonAndWaitForButtonToAppear(app, nextButton, "Login")
    }
    
    func navigateToLogin(_ app: XCUIApplication) {
        let loginButton = app.buttons["Login"]
        tapButtonAndWaitForTextFieldToAppear(app, loginButton, "Name")
    }
    
    func navigateToRegister(_ app: XCUIApplication) {
        let registerButton = app.buttons["Register"]
        tapButtonAndWaitForTextFieldToAppear(app, registerButton, "Name")
    }
    
    func navigateToLoginFromLoginOrRegister(_ app: XCUIApplication) {
        navigateToLoginOrRegister(app)
        navigateToLogin(app)
    }
    
    func loginWithDenarii(_ app: XCUIApplication, _ suffix: String) {
        registerWithDenarii(app, suffix)
        
        let loginTextField = app.textFields["Name or Email"]
        tapElementAndWaitForKeyboardToAppear(loginTextField)
        loginTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordSecureTextField = app.secureTextFields["Password"]
        tapElementAndWaitForKeyboardToAppear(passwordSecureTextField)
        passwordSecureTextField.typeText("Password1#A_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        tapElementAndWaitForPopoverToAppear(app, submitButton, Constants.POPOVER)

        // A popover appears and we need to tap it away
        tapAway(app)
        
        let nextButton = app.buttons["Next"]
        nextButton.tap()
    }
    
    func registerWithDenarii(_ app: XCUIApplication, _ suffix: String) {
        navigateToLoginOrRegister(app)

        navigateToRegister(app)
        
        let loginTextField = app.textFields["Name"]
        tapElementAndWaitForKeyboardToAppear(loginTextField)
        loginTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        tapElementAndWaitForKeyboardToAppear(emailTextField)
        emailTextField.typeText("Email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordSecureTextField = app.secureTextFields["Password"]
        tapElementAndWaitForKeyboardToAppear(passwordSecureTextField)
        passwordSecureTextField.typeText("Password1#A_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordSecureTextField = app.secureTextFields["Confirm Password"]
        tapElementAndWaitForKeyboardToAppear(confirmPasswordSecureTextField)
        confirmPasswordSecureTextField.typeText("Password1#A_\(self.currentTestName)_\(suffix)")

        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        tapElementAndWaitForPopoverToAppear(app, submitButton, Constants.POPOVER)
        
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
        tapElementAndWaitForKeyboardToAppear(usernameOrEmailTextField.element)
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
        tapElementAndWaitForKeyboardToAppear(resetIdTextField)
        resetIdTextField.typeText("123456")
        
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
        tapElementAndWaitForKeyboardToAppear(usernameTextField)
        usernameTextField.typeText("User_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        tapElementAndWaitForKeyboardToAppear(emailTextField)
        emailTextField.typeText("Email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let passwordTextField = app.secureTextFields["Password"]
        tapElementAndWaitForKeyboardToAppear(passwordTextField)
        passwordTextField.typeText("password1#A_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmPasswordTextField = app.secureTextFields["Confirm Password"]
        tapElementAndWaitForKeyboardToAppear(confirmPasswordTextField)
        confirmPasswordTextField.typeText("password1#A_\(self.currentTestName)_\(suffix)")
        
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
        tapElementAndWaitForKeyboardToAppear(walletNameTextField)
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        tapElementAndWaitForKeyboardToAppear(walletPasswordSecureTextField)
        walletPasswordSecureTextField.typeText("wallet_password1#A_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let confirmWalletPasswordSecureTextField = app.secureTextFields["Confirm Wallet Password"]
        tapElementAndWaitForKeyboardToAppear(confirmWalletPasswordSecureTextField)
        confirmWalletPasswordSecureTextField.typeText("wallet_password1#A_\(self.currentTestName)_\(suffix)")
        
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
        tapElementAndWaitForKeyboardToAppear(walletNameTextField)
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        tapElementAndWaitForKeyboardToAppear(walletPasswordSecureTextField)
        walletPasswordSecureTextField.typeText("wallet_password1#A_\(self.currentTestName)_\(suffix)")
        
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
        tapElementAndWaitForKeyboardToAppear(walletNameTextField)
        walletNameTextField.typeText("wallet_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletPasswordSecureTextField = app.secureTextFields["Wallet Password"]
        tapElementAndWaitForKeyboardToAppear(walletPasswordSecureTextField)
        walletPasswordSecureTextField.typeText("wallet_password1#A_\(self.currentTestName)_\(suffix)")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let walletSeedTextField = app.textFields["Wallet Seed"]
        tapElementAndWaitForKeyboardToAppear(walletSeedTextField)
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
        tapElementAndWaitForKeyboardToAppear(amountToSendTextField)
        amountToSendTextField.typeText("2")
        
        let sendToTextField = app.textFields["Send To"]
        tapElementAndWaitForKeyboardToAppear(sendToTextField)
        sendToTextField.typeText("3457891202")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let sendDenariiButton = app.buttons["Send"]
        sendDenariiButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func logout(_ app: XCUIApplication, _ suffix: String) {
        openSidebar(app)
        
        let userSettingsButton = app.buttons["Settings"]
        tapButtonAndWaitForButtonToAppear(app, userSettingsButton, "Logout")
        
        let logoutButton = app.buttons["Logout"]
        logoutButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        // We should be on the ContentView screen
        XCTAssert(app.buttons["Next"].exists)
    }
    
    func logoutFromWalletDecision(_ app: XCUIApplication, _ suffix: String) {
        strictlyCreateWallet(app, suffix)
        logout(app, suffix)
    }
    
    func logoutFromSupportTickets(_ app: XCUIApplication, _ suffix: String) {
        logout(app, suffix)
    }
    
    func logoutFromSupportTicketDetails(_ app: XCUIApplication, _ suffix: String) {
        logoutFromSupportTickets(app, suffix)
    }
    
    func buyDenarii(_ app: XCUIApplication, _ firstUserSuffix: String, _ secondUserSuffix: String) {
        sellDenarii(app, firstUserSuffix)
        
        logout(app, firstUserSuffix)
        
        createWallet(app, secondUserSuffix)
        
        strictlyVerifyIdentity(app, secondUserSuffix)
        
        strictlySetCreditCardInfo(app, secondUserSuffix)
        
        openSidebar(app)
        
        let buyDenariiButton = app.buttons["Buy Denarii"]
        tapButtonAndWaitForTextFieldToAppear(app, buyDenariiButton, "Amount")
        
        let amountTextField = app.textFields["Amount"]
        tapElementAndWaitForKeyboardToAppear(amountTextField)
        // Less than is sold
        amountTextField.typeText("10.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        // Willing to buy at a high price
        let priceTextField = app.textFields["Price"]
        tapElementAndWaitForKeyboardToAppear(priceTextField)
        priceTextField.typeText("100.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let secondBuyDenariiButton = app.buttons["Buy"]
        secondBuyDenariiButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func cancelBuyDenarii(_ app: XCUIApplication, _ firstUserSuffix: String, _ secondUserSuffix: String) {
        buyDenarii(app, firstUserSuffix, secondUserSuffix)
        
        let buyDenariiText = app.staticTexts["Buy Denarii"]
        refreshScreen(app, buyDenariiText)
        
        let cancelButton = app.buttons["Cancel"]
        cancelButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func sellDenarii(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        strictlyVerifyIdentity(app, suffix)
        
        strictlySetCreditCardInfo(app, suffix)
        
        openSidebar(app)
        
        let sellDenariiButton = app.buttons["Sell Denarii"]
        tapButtonAndWaitForTextFieldToAppear(app, sellDenariiButton, "Amount")
        
        let amountTextField = app.textFields["Amount"]
        tapElementAndWaitForKeyboardToAppear(amountTextField)
        amountTextField.typeText("12.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let priceTextField = app.textFields["Price"]
        tapElementAndWaitForKeyboardToAppear(priceTextField)
        priceTextField.typeText("50.0")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let secondSellDenariiButton  = app.buttons["Sell"]
        secondSellDenariiButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func cancelSellDenarii(_ app: XCUIApplication, _ suffix: String) {
        sellDenarii(app, suffix)
        
        let sellDenariiText = app.staticTexts["Sell Denarii"]
        refreshScreen(app, sellDenariiText, 8)
        
        let cancelButton = app.buttons["Cancel"]
        cancelButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func strictlyVerifyIdentity(_ app: XCUIApplication, _ suffix: String) {
        openSidebar(app)
        
        let verificationButton = app.buttons["Verification"]
        tapButtonAndWaitForTextFieldToAppear(app, verificationButton, "First Name")
        
        let firstNameTextField = app.textFields["First Name"]
        tapElementAndWaitForKeyboardToAppear(firstNameTextField)
        firstNameTextField.typeText("first")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let middleNameTextField = app.textFields["Middle Initial"]
        tapElementAndWaitForKeyboardToAppear(middleNameTextField)
        middleNameTextField.typeText("I")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let lastNameTextField = app.textFields["Last Name"]
        tapElementAndWaitForKeyboardToAppear(lastNameTextField)
        lastNameTextField.typeText("last")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let emailTextField = app.textFields["Email"]
        tapElementAndWaitForKeyboardToAppear(emailTextField)
        emailTextField.typeText("email_\(self.currentTestName)_\(suffix)@email.com")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let dateOfBirthTextField = app.textFields["Date of Birth"]
        tapElementAndWaitForKeyboardToAppear(dateOfBirthTextField)
        dateOfBirthTextField.typeText("2020/02/10")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let socialSecurityNumberTextField = app.textFields["Social Security Number"]
        tapElementAndWaitForKeyboardToAppear(socialSecurityNumberTextField)
        socialSecurityNumberTextField.typeText("101")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let zipcodeTextField = app.textFields["Zipcode"]
        tapElementAndWaitForKeyboardToAppear(zipcodeTextField)
        zipcodeTextField.typeText("123-456")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let phoneNumberTextField = app.textFields["Phone Number"]
        tapElementAndWaitForKeyboardToAppear(phoneNumberTextField)
        phoneNumberTextField.typeText("1234567890")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workCityTextField = app.textFields["Work City"]
        tapElementAndWaitForKeyboardToAppear(workCityTextField)
        workCityTextField.typeText("San Jose")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workStateTexttField = app.textFields["Work State"]
        tapElementAndWaitForKeyboardToAppear(workStateTexttField)
        workStateTexttField.typeText("California")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let workCountryTextField = app.textFields["Work Country"]
        tapElementAndWaitForKeyboardToAppear(workCountryTextField)
        workCountryTextField.typeText("United States")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let submitButton = app.buttons["Submit"]
        submitButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
    }
    
    func verifyIdentity(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        strictlyVerifyIdentity(app, suffix)
    }
    
    func strictlySetCreditCardInfo(_ app: XCUIApplication, _ suffix: String) {
        openSidebar(app)
        
        let creditCardInfoButton = app.buttons["Credit Card"]
        tapButtonAndWaitForTextFieldToAppear(app, creditCardInfoButton, "Credit Card Number")
        
        let creditCardNumberTextField = app.textFields["Credit Card Number"]
        tapElementAndWaitForKeyboardToAppear(creditCardNumberTextField)
        creditCardNumberTextField.typeText("234")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardExpirationDateMonthTextField = app.textFields["Expiration Date Month"]
        tapElementAndWaitForKeyboardToAppear(creditCardExpirationDateMonthTextField)
        creditCardExpirationDateMonthTextField.typeText("02")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardExpirationDateYearTextField = app.textFields["Expiration Date Year"]
        tapElementAndWaitForKeyboardToAppear(creditCardExpirationDateYearTextField)
        creditCardExpirationDateYearTextField.typeText("2020")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let creditCardSecurityCodeTextField = app.textFields["Security Code"]
        tapElementAndWaitForKeyboardToAppear(creditCardSecurityCodeTextField)
        creditCardSecurityCodeTextField.typeText("001")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let setInfoButton = app.buttons["Set Info"]
        setInfoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func setCreditCardInfo(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        strictlySetCreditCardInfo(app, suffix)
    }
    
    func clearCreditCardInfo(_ app: XCUIApplication, _ suffix: String) {
        setCreditCardInfo(app, suffix)
        
        let creditCardText = app.staticTexts["Credit Card Info"]
        refreshScreen(app, creditCardText, 8)
        
        let clearCreditCardInfoButton = app.buttons["Clear Info"]
        clearCreditCardInfoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func navigateToSupportTickets(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        openSidebar(app)
        
        let userSettingsButton = app.buttons["Settings"]
        tapButtonAndWaitForButtonToAppear(app, userSettingsButton, "Support Tickets")
        
        let supportTicketsButton = app.buttons["Support Tickets"]
        supportTicketsButton.tap()
    }
    
    func createSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        navigateToSupportTickets(app, suffix)
        
        let createTicketButton = app.buttons["Create Support Ticket"]
        createTicketButton.tap()
        
        let titleTextField = app.textFields["Title"]
        tapElementAndWaitForKeyboardToAppear(titleTextField)
        titleTextField.typeText("Title is this")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let descriptionTextField = app.textFields["Description"]
        tapElementAndWaitForKeyboardToAppear(descriptionTextField)
        descriptionTextField.typeText("Description is that")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let createTicketTwoButton = app.buttons["Create"]
        createTicketTwoButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func createNewComment(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let newCommentTextField = app.textFields["New Comment"]
        tapElementAndWaitForKeyboardToAppear(newCommentTextField)
        newCommentTextField.typeText("Commenting is fun")
        
        // After we type things in we need to dismiss the keyboard
        dismissKeyboardIfPresent(app)
        
        let createNewCommentButton = app.buttons["Submit"]
        createNewCommentButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
        
        let supportTicketDetailsText = app.staticTexts["Support Ticket Details"]
        refreshScreen(app, supportTicketDetailsText)
    }
    
    func deleteSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let deleteTicketButton = app.buttons["Delete"]
        deleteTicketButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func resolveSupportTicket(_ app: XCUIApplication, _ suffix: String) {
        createSupportTicket(app, suffix)
        
        let resolveButton = app.buttons["Resolve"]
        resolveButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }
    
    func deleteAccount(_ app: XCUIApplication, _ suffix: String) {
        createWallet(app, suffix)
        
        openSidebar(app)
        
        let userSettingsButton = app.buttons["Settings"]
        tapButtonAndWaitForButtonToAppear(app, userSettingsButton, "Delete Account")
        
        let deleteAccountButton = app.buttons["Delete Account"]
        deleteAccountButton.tap()
        
        // A popover appears and we need to tap it away
        tapAway(app)
    }

    override class var runsForEachTargetApplicationUIConfiguration: Bool {
        true
    }

    override func setUpWithError() throws {
        continueAfterFailure = false
        
        // get the name and remove the class name and what comes before the class name
        self.currentTestName = self.name.replacingOccurrences(of: "-[denariiUITestsLaunchTests ", with: "")

        // And then you'll need to remove the closing square bracket at the end of the test name
        self.currentTestName = self.currentTestName.replacingOccurrences(of: "]", with: "")
        
        self.currentTestName = self.currentTestName.replacingOccurrences(of: " ", with: "_")
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
        refreshBalance(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Refresh Balance Screen"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testSendDenariiScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        sendDenarii(app, Constants.FIRST_USER)
        
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Send Denarii Screen"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testResetPasswordScreen() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        resetPassword(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Reset Password Process (Login Screen)"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
    
    func testCancelBuyDenarii() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        cancelBuyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Cancel Buy Denarii"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.SECOND_USER)
    }
    
    func testCancelSellDenarii() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        cancelSellDenarii(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Cancel Sell Denarii"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testSellDenariiAndBuyDenarii() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        buyDenarii(app, Constants.FIRST_USER, Constants.SECOND_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Sell And Buy Denarii"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.SECOND_USER)
    }
    
    func testVerifyIdentity() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        verifyIdentity(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Verify Identity"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }

    func testSetCreditCardInfo() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        setCreditCardInfo(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Set Credit Card Info"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testResolveSupportTicket() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        resolveSupportTicket(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Resolve Support Ticket"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testDeleteSupportTicket() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        deleteSupportTicket(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Delete Support Ticket"
        attachment.lifetime = .keepAlways
        add(attachment)
        
        logout(app, Constants.FIRST_USER)
    }
    
    func testDeleteAccount() throws {
        let app = XCUIApplication()
        app.launchArguments = ["UI-TESTING"]
        app.launch()

        // Insert steps here to perform after app launch but before taking a screenshot,
        // such as logging into a test account or navigating somewhere in the app
        deleteAccount(app, Constants.FIRST_USER)

        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = "Delete Account"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
