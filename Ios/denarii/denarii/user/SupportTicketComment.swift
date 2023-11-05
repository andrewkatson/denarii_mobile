//
//  SupportTicketComment.swift
//  denarii
//
//  Created by Andrew Katson on 9/30/23.
//

import Foundation

class SupportTicketComment: Equatable, Hashable {
    var author : String = ""
    
    var content : String = ""
    
    var commentID: String = ""
    
    init() {
        self.author = ""
        self.content = ""
        self.commentID = ""
    }
    
    init(author: String, content: String, commentID: String) {
        self.author = author
        self.content = content
        self.commentID = commentID
    }
    
    
    static func ==(lhs: SupportTicketComment, rhs: SupportTicketComment) -> Bool {
        return lhs.author == rhs.author && lhs.commentID == rhs.commentID
    }
    func hash(into hasher: inout Hasher) {
      hasher.combine(author)
     hasher.combine(commentID)
    }
}
