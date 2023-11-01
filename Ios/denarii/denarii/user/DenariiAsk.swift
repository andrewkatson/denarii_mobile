//
//  DenariiAsk.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

class DenariiAsk : Equatable, Hashable {
    var askID : String = ""

    var amount : Double = 0.0

    var askingPrice : Double = 0.0

    var inEscrow : Bool = false

    var amountBought : Double = 0.0

    var isSettled : Bool = false

    var seenBySeller : Bool = false
    
    var buyerId : String = ""
    
    init(askID: String, amount: Double, askingPrice: Double, inEscrow: Bool, amountBought: Double, isSettled: Bool, seenBySeller: Bool, buyerId: String) {
        self.askID = askID
        self.amount = amount
        self.askingPrice = askingPrice
        self.inEscrow = inEscrow
        self.amountBought = amountBought
        self.isSettled = isSettled
        self.seenBySeller = seenBySeller
        self.buyerId = buyerId
    }
    
    init() {
        self.askID = "-1"
        self.amount = 0.0
        self.askingPrice = 0.0
        self.isSettled = false
        self.amountBought = 0.0
        self.inEscrow = false
        self.seenBySeller = false
        self.buyerId = ""
    }
    
    static func ==(lhs: DenariiAsk, rhs: DenariiAsk) -> Bool {
        return lhs.askID == rhs.askID
    }
    func hash(into hasher: inout Hasher) {
      hasher.combine(askID)
    }
}
