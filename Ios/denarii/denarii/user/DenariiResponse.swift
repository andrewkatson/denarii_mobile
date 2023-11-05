//
//  DenariiResponse.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct DenariiResponse : Codable {
    var hasCreditCardInfo : Bool = false

    var balance : Double = 0.0

    var seed : String = ""

    var userIdentifier : String = ""

    var walletAddress : String = ""

    var askID : String = ""

    var amount : Double = 0.0

    var askingPrice : Double = 0.0

    var amountBought : Double = 0.0

    var transactionWasSettled : Bool = false

    var verificationStatus : String = ""

    var author : String = ""

    var content : String = ""

    var description : String = ""

    var title : String = ""

    var supportTicketID : String = ""

    var isResolved : Bool = false

    var creationTimeBody : String = ""

    var updatedTimeBody : String = ""
    
    var responseCode : Int = -1
    
    var commentID: String = ""
}
