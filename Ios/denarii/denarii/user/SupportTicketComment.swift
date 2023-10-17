//
//  SupportTicketComment.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

class SupportTicketComment {
    private(set) var author : String = ""
    
    private(set) var content : String = ""
    
    init(author: String, content: String) {
        self.author = author
        self.content = content
    }
}
