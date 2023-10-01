//
//  DenariiAsk.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct DenariiAsk {
    private(set) var askID : String = ""

    private(set) var amount : Double = 0.0

    private(set) var askingPrice : Double = 0.0

    private(set) var inEscrow : Bool = false

    private(set) var amountBought : Double = 0.0

    private(set) var isSettled : Bool = false

    private(set) var seenBySeller : Bool = false
    
    init(askID: String, amount: Double, askingPrice: Double, inEscrow: Bool, amountBought: Double, isSettled: Bool, seenBySeller: Bool) {
        self.askID = askID
        self.amount = amount
        self.askingPrice = askingPrice
        self.inEscrow = inEscrow
        self.amountBought = amountBought
        self.isSettled = isSettled
        self.seenBySeller = seenBySeller
    }
}
