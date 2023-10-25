//
//  UserDetails.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

class UserDetails {
    
    private(set) var userName : String = ""
    private(set) var userEmail : String = ""
    var userPassword : String = ""
    var walletDetails : WalletDetails = WalletDetails()
    var creditCard : CreditCard = CreditCard()
    var supportTicketList : Array<SupportTicket> = Array()
    var denariiAskList : Array<DenariiAsk> = Array()
    var userID : String = ""
    var denariiUser: DenariiUser = DenariiUser()
    
    init(userName: String, userEmail: String, userPassword: String, walletDetails: WalletDetails, creditCard: CreditCard, userID: String, denariiUser: DenariiUser) {
        self.userName = userName
        self.userEmail = userEmail
        self.userPassword = userPassword
        self.walletDetails = walletDetails
        self.creditCard = creditCard
        self.userID = userID
        self.denariiUser = denariiUser
    }
    
    init () {
        self.userName = ""
        self.userEmail = ""
        self.userPassword = ""
        self.walletDetails = WalletDetails()
        self.creditCard = CreditCard()
        self.userID = "-1"
        self.denariiUser = DenariiUser()
    }
}
