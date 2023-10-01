//
//  UserDetails.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct UserDetails {
    
    private(set) var userName : String = ""
    private(set) var userEmail : String = ""
    private(set) var userPassword : String = ""
    private(set) var walletDetails : WalletDetails = WalletDetails()
    private(set) var creditCard : CreditCard = CreditCard()
    private(set) var supportTicketList : Array<SupportTicket> = Array()
    private(set) var denariiAskList : Array<DenariiAsk> = Array()
    private(set) var userID : String = ""
    
    init(userName: String, userEmail: String, userPassword: String, walletDetails: WalletDetails, creditCard: CreditCard, userID: String) {
        self.userName = userName
        self.userEmail = userEmail
        self.userPassword = userPassword
        self.walletDetails = walletDetails
        self.creditCard = creditCard
        self.userID = userID
    }
}
