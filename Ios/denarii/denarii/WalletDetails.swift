//
//  WalletDetails.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

struct WalletDetails : Codable {
    var balance: Double = 0.0
    var seed: String = ""
    var userIdentifier: Int = 0
    var walletName: String = ""
    var walletPassword: String = ""
    var walletAddress: String = ""
}
