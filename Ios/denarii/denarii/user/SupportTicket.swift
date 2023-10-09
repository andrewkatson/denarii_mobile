//
//  SupportTicket.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

struct SupportTicket {
    private(set) var supportID : String = ""

    private(set) var description : String = ""

    private(set) var title : String = ""

    var resolved : Bool = false

    var supportTicketCommentList : Array<SupportTicketComment> = Array()
    
    init(supportID: String, description: String, title: String, resolved: Bool, supportTicketCommentList: Array<SupportTicketComment>) {
        self.supportID = supportID
        self.description = description
        self.title = title
        self.resolved = resolved
        self.supportTicketCommentList = supportTicketCommentList
    }
    
    init() {
        self.supportID = ""
        self.description = ""
        self.title = ""
        self.resolved = false
        self.supportTicketCommentList = Array()
    }
}
