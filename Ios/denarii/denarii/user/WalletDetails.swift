//
//  WalletDetails.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct WalletDetails {
    private(set) var walletName : String = ""

    private(set) var walletPassword : String = ""

    private(set) var seed : String = ""

    private(set) var balance : Double = 0.0

    private(set) var walletAddress : String = ""
    
    init(walletName: String, walletPassword: String, seed: String, balance: Double, walletAddress: String) {
        self.walletName = walletName
        self.walletPassword = walletPassword
        self.seed = seed
        self.balance = balance
        self.walletAddress = walletAddress
    }
    
    init() {
        self.walletName = ""
        self.walletPassword = ""
        self.seed = ""
        self.balance = 0.0
        self.walletAddress = ""
    }
}
