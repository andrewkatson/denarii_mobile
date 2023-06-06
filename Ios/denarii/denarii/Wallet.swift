//
//  Wallet.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

struct Wallet : Codable {
    var responseCode: Int = 0
    var responseCodeText: String = ""
    var response: WalletDetails = WalletDetails()
}
