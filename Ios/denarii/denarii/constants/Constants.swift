//
//  Constants.swift
//  denarii
//
//  Created by Andrew Katson on 5/24/23.
//

import Foundation

struct Constants {
    // Used in Unit Testing. See Config for how to stub an API in UI Testing
    static var DEBUG = false
    
    // Popovers are a view that appears on top of the main view and the user taps away
    static var POPOVER = "Popover"
    static var REFRESH_BALANCE_POPOVER = "Refresh Balance Popover"
    static var SEND_DENARII_POPOVER = "Send Denarii Popover"
    static var BUY_DENARII_POPOVER = "Buy Denarii Popover"
    static var CANCEL_BUY_DENARII_POPOVER = "Cancel Buy Denarii Popover"
    static var SELL_DENARII_POPOVER = "Sell Denarii Popover"
    static var CANCEL_SELL_DENARII_POPOVER = "Cancel Sell Denarii Popover"
    static var SET_CREDIT_CARD_INFO_POPOVER = "Set Credit Card Info Popover"
    static var CLEAR_CREDIT_CARD_INFO_POPOVER = "Clear Credit Card Info Popover"
    static var CREATE_NEW_COMMENT_POPOVER = "Create New Comment Popover"
    static var RESOLVE_TICKET_POPOVER = "Resolve Ticket Popover"
    static var DELETE_TICKET_POPOVER = "Delete Ticket Popover"
    static var DELETE_ACCOUNT_POPOVER = "Delete Account Popover"
    static var LOGOUT_POPOVER = "Logout Popover"
    
    // User suffixes used in testing
    static var FIRST_USER = "tom"
    static var SECOND_USER = "jerry"
    
    // Buttons
    static var SIDEBAR_BUTTON = "Sidebar Button"
    

    class Patterns {
        static var password = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\S+$).{8,}$/
        static var double = /^\d{1,100}[.,]{0,1}\d{0,100}$/
        static var paragraphOfChars = /^[\w \n]{5,3000}$/
        static var alphanumeric = /^\w{10,500}$/
        static var singleLetter = /^[a-zA-Z]{1}$/
        static var name = /^[a-zA-Z]{3,100}$/
        static var currency = /[a-zA-Z]{3,5}$/
        static var digitsOnly = /^\d{1,100}$/
        static var slashDate = /[\d+\/]{3,100}$/
        static var seed = /^[\w ]{10,1000}$/
        static var digitsAndDashes = /^[\d-]{3,100}$/
        static var phoneNumber = /^(\+\d{1,2}\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$/
        static var alphanumericWithSpaces = /^[\w ]{5,100}$/
        static var email = /^[^@]+@[^@]+\.[^@]+$/
        static var uuid4 = /^[0-9a-f]{12}4[0-9a-f]{3}[89ab][0-9a-f]{15}\Z$/
        static var boolean = /^(?i)(true|false)$/
        static var jsonDictOfUpperAndLowerCaseChars = /^[\]\[{}:\"\\, a-zA-Z]{2,5000}$/
        static var resetId = /^\d{6}$/
    }

    class Params {
        static var username = "USERNAME"
        static var email = "EMAIL"
        static var password = "PASSWORD"
        static var confirmPassword = "CONFIRM_PASSWORD"
        static var usernameOrEmail = "USERNAME_OR_EMAIL"
        static var walletName = "WALLET_NAME"
        static var userId = "USER_ID"
        static var address = "ADDRESS"
        static var amount = "AMOUNT"
        static var bidPrice = "BID_PRICE"
        static var buyRegardlessOfPrice = "BUY_REGARDLESS_OF_PRICE"
        static var failIfFullAmountIsntMet = "FAIL_IF_FULL_AMOUNT_ISNT_MET"
        static var askingPrice = "ASK_PRICE"
        static var cardNumber = "CARD_NUMBER"
        static var expirationDateMonth = "EXPIRATION_DATE_MONTH"
        static var expirationDateYear = "EXPIRATION_DATE_YEAR"
        static var securityCode = "SECURITY_CODE"
        static var currency = "CURRENCY"
        static var firstName = "FIRST_NAME"
        static var middleName = "MIDDLE_INITIAL"
        static var lastName = "LAST_NAME"
        static var dob = "DATE_OF_BIRTH"
        static var ssn = "SOCIAL_SECURITY_NUMBER"
        static var zipcode = "ZIPCODE"
        static var phone = "PHONE"
        static var workLocations = "WORK_LOCATIONS"
        static var title = "TITLE"
        static var description = "DESCRIPTION"
        static var comment = "COMMENT"
        static var seed = "SEED"
        static var resetId = "RESET_ID"
    }
}
