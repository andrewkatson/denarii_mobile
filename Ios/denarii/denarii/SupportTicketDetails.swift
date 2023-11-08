//
//  SupportTicketDetails.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SupportTicketDetails: View {
    
    
    
    @State private var comment: String = ""
    @State private var isCreated: Bool = false
    @State private var isResolved: Bool = false
    @State private var isDeleted: Bool = false
    @State private var showingPopoverForCreateNewComment = false
    @State private var showingPopoverForResolveTicket = false
    @State private var showingPopoverForDeleteTicket = false
    
    @ObservedObject private var title: ObservableString = ObservableString()
    @ObservedObject private var description: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForCreateNewComment: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForResolveTicket: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForDeleteTicket: ObservableString = ObservableString()
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
            GeometryReader { proxy in
                ScrollView(.vertical, showsIndicators: true) {
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
                    }.frame(width: proxy.size.width, height: proxy.size.height)
                }
            }
            TextField("New Comment", text: $comment, axis: Axis.vertical)
            Button("Submit") {
                isCreated = attemptCreateNewComment()
                showingPopoverForCreateNewComment = true
            }.popover(isPresented: $showingPopoverForCreateNewComment) {
                Text(successOrFailureForCreateNewComment.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForCreateNewComment = false
                    }.accessibilityIdentifier(Constants.CREATE_NEW_COMMENT_POPOVER)
            }
            Spacer()
            Button("Resolve") {
                isResolved = attemptResolveTicket()
                showingPopoverForResolveTicket = true
            }.popover(isPresented: $showingPopoverForResolveTicket) {
                Text(successOrFailureForResolveTicket.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForResolveTicket = false
                    }.accessibilityIdentifier(Constants.RESOLVE_TICKET_POPOVER)
            }
            Button("Delete") {
                isDeleted = attemptDeleteTicket()
                showingPopoverForDeleteTicket = true
            }.popover(isPresented: $showingPopoverForDeleteTicket) {
                Text(successOrFailureForDeleteTicket.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForDeleteTicket = false
                    }.accessibilityIdentifier(Constants.DELETE_TICKET_POPOVER)
            }
            Spacer()
        }
    }
    
    func attemptCreateNewComment() -> Bool {
        if Constants.DEBUG {
            successOrFailureForCreateNewComment.setValue("Successfully added a new comment in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                self.successOrFailureForCreateNewComment.setValue("Failed to create a new comment")
                return false
            }
            
            let responses = api.updateSupportTicket(Int(self.user.getValue().userID)!, self.supportTicketID.getValue(), comment)
            
            if responses.isEmpty {
                successOrFailureForCreateNewComment.setValue("Failed to create a new comment server error")
                return false
            } else {
                let response = responses.first!
                
                if response.responseCode != 200 {
                    successOrFailureForCreateNewComment.setValue("Failed to create a new comment server error")
                    return false
                }
                
                successOrFailureForCreateNewComment.setValue("Successfully created a new comment")
                return true
            }
        }
    }
    
    func attemptResolveTicket() -> Bool {
        if Constants.DEBUG {
            successOrFailureForResolveTicket.setValue("Successfully resolved ticket in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                self.successOrFailureForResolveTicket.setValue("Failed to resolve ticekt")
                return false
            }
            
            let responses = api.resolveSupportTicket(Int(self.user.getValue().userID)!, self.supportTicketID.getValue())
            
            if responses.isEmpty {
                successOrFailureForResolveTicket.setValue("Failed to resolve ticket server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                successOrFailureForResolveTicket.setValue("Failed to resolve ticket server error")
                return true
            } else {
                successOrFailureForResolveTicket.setValue("Successfully resolved support ticket")
                return true
            }
        }
    }
    
    func attemptDeleteTicket() -> Bool {
        if Constants.DEBUG {
            successOrFailureForDeleteTicket.setValue("Successfully deleted ticket in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                self.successOrFailureForDeleteTicket.setValue("Failed to delete ticekt")
                return false
            }
            
            let responses = api.deleteSupportTicket(Int(self.user.getValue().userID)!, self.supportTicketID.getValue())
            
            if responses.isEmpty {
                successOrFailureForDeleteTicket.setValue("Failed to delete ticket server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                successOrFailureForDeleteTicket.setValue("Failed to delete ticket server error")
                return true
            } else {
                successOrFailureForDeleteTicket.setValue("Successfully deleted support ticket")
                return true
            }
        }
    }
}

#Preview {
    SupportTicketDetails()
}
