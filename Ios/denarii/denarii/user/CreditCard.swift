//
//  CreditCard.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct CreditCard {

    private(set) var customerId : String = ""
    private(set) var sourceTokenId : String = ""
    
    init(customerId: String, sourceTokenId: String) {
        self.customerId = customerId
        self.sourceTokenId = sourceTokenId
    }
    
    init() {
        self.customerId = ""
        self.sourceTokenId = ""
    }
}
