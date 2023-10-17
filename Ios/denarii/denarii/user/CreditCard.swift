//
//  CreditCard.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

class CreditCard {

    var customerId : String = ""
    var sourceTokenId : String = ""
    
    init(customerId: String, sourceTokenId: String) {
        self.customerId = customerId
        self.sourceTokenId = sourceTokenId
    }
    
    init() {
        self.customerId = ""
        self.sourceTokenId = ""
    }
}
