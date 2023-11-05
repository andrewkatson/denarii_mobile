//
//  SupportTicketDetails.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SupportTicketDetails: View {
    
    
    
    @State private var comment: String = ""
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var title: ObservableString = ObservableString()
    @ObservedObject private var description: ObservableString = ObservableString()
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var comments: ObservableArray<SupportTicketComment> = ObservableArray()
    @ObservedObject private var supportTicketID = ObservableString()
    
    init() {
        getSupportTicketInfo("")
        getComments()
    }

    init(_ user: UserDetails, _ supportTicketID: String) {
        self.user.setValue(user)
        self.supportTicketID.setValue(supportTicketID)
        getSupportTicketInfo(supportTicketID)
        getComments()
    }
    
    func getSupportTicketInfo(_ supportTicketID: String) {
        if !supportTicketID.isEmpty && !self.user.getValue().userID.isEmpty {
            let api = Config.api
            
            let responses = api.getSupportTicket(Int(self.user.getValue().userID)!, supportTicketID)
            
            if !responses.isEmpty {
                let response = responses.first!
                
                title.setValue(response.title)
                description.setValue(response.description)
            }
        }
    }
    
    func getComments() {
        if !self.user.getValue().userID.isEmpty {
            let api = Config.api
            
            let responses = api.getCommentsOnTicket(Int(self.user.getValue().userID)!, self.supportTicketID.getValue())
            
            if !responses.isEmpty {
                var newComments: Array<SupportTicketComment> = Array()
                for response in responses {
                    let newComment = SupportTicketComment()
                    newComment.author = response.author
                    newComment.content = response.content
                    
                    newComments.append(newComment)
                }
                
                self.comments.setValue(newComments)
            }
        }
    }
    
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Support Ticket Details").font(.largeTitle)
            Spacer()
            Text("\(title.getValue())")
            Text("\(description.getValue())")
            VStack(alignment: .center) {
                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                 */
                ForEach(self.comments.getValue(), id: \.self) { comment in
                    HStack {
                        Text(comment.author)
                        Text(comment.content)
                    }
                }.refreshable {
                    getComments()
                }
            }
            TextField("New Comment", text: $comment, axis: Axis.vertical)
            Button("Submit") {
                isSubmitted = attemptSubmit()
                showingPopover = true
            }.popover(isPresented: $showingPopover) {
                Text(successOrFailure.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopover = false
                    }.accessibilityIdentifier("Popover")
            }
            Spacer()
        }
    }
    
    func attemptSubmit() -> Bool {
        if Constants.DEBUG {
            successOrFailure.setValue("Successfully added a new comment in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                self.successOrFailure.setValue("Failed to create a new comment")
                return false
            }
            
            let responses = api.updateSupportTicket(Int(self.user.getValue().userID)!, self.supportTicketID.getValue(), comment)
            
            if responses.isEmpty {
                successOrFailure.setValue("Failed to create a new comment server error")
                return false
            } else {
                let response = responses.first!
                
                if response.responseCode != 200 {
                    successOrFailure.setValue("Failed to create a new comment server error")
                    return false
                }
                
                successOrFailure.setValue("Successfully created a new comment")
                return true
            }
        }
    }
}

#Preview {
    SupportTicketDetails()
}
