//
//  Constants.swift
//  denarii
//
//  Created by Andrew Katson on 5/24/23.
//

import Foundation

struct Constants {
    // Used in Unit Testing. See Config for how to stub an API in UI Testing
    static var DEBUG = false
    
    // Popovers are a view that appears on top of the main view and the user taps away
    static var POPOVER = "Popover"
    static var REFRESH_BALANCE_POPOVER = "Refresh Balance Popover"
    static var SEND_DENARII_POPOVER = "Send Denarii Popover"
    static var BUY_DENARII_POPOVER = "Buy Denarii Popover"
    static var CANCEL_BUY_DENARII_POPOVER = "Cancel Buy Denarii Popover"
    static var SELL_DENARII_POPOVER = "Sell Denarii Popover"
    static var CANCEL_SELL_DENARII_POPOVER = "Cancel Sell Denarii Popover"
    static var SET_CREDIT_CARD_INFO_POPOVER = "Set Credit Card Info Popover"
    static var CLEAR_CREDIT_CARD_INFO_POPOVER = "Clear Credit Card Info Popover"
    static var CREATE_NEW_COMMENT_POPOVER = "Create New Comment Popover"
    static var RESOLVE_TICKET_POPOVER = "Resolve Ticket Popover"
    static var DELETE_TICKET_POPOVER = "Delete Ticket Popover"
}
