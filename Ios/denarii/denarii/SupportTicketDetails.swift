//
//  SupportTicketDetails.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SupportTicketDetails: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @State private var showingSidebar = false
    @State private var resolve: Bool = false
    @State private var delete: Bool = false
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
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                GeometryReader { geometry in
                    List {
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
                                        }
                                    }.frame(width: proxy.size.width, height: proxy.size.height)
                                }
                            }
                            TextField("New Comment", text: $comment)
                            Button("Submit") {
                                isCreated = attemptCreateNewComment()
                                showingPopoverForCreateNewComment = true
                            }.popover(isPresented: $showingPopoverForCreateNewComment) {
                                Text(successOrFailureForCreateNewComment.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForCreateNewComment = false
                                    }.accessibilityIdentifier(Constants.CREATE_NEW_COMMENT_POPOVER)
                            }.buttonStyle(BorderlessButtonStyle())
                            Spacer()
                            Button("Resolve") {
                                isResolved = attemptResolveTicket()
                                showingPopoverForResolveTicket = true
                            }.popover(isPresented: $showingPopoverForResolveTicket) {
                                Text(successOrFailureForResolveTicket.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForResolveTicket = false
                                        if isResolved {
                                            resolve = true
                                        }
                                    }.accessibilityIdentifier(Constants.RESOLVE_TICKET_POPOVER)
                            }.background {
                                if resolve && showingPopoverForResolveTicket == false {
                                    NavigationLink("Resolve") {
                                        EmptyView()
                                    }.navigationDestination(isPresented: $resolve) {
                                        SupportTickets(self.user.getValue())
                                    }
                                }
                            }.buttonStyle(BorderlessButtonStyle())
                            Spacer()
                            Button("Delete") {
                                isDeleted = attemptDeleteTicket()
                                showingPopoverForDeleteTicket = true
                            }.popover(isPresented: $showingPopoverForDeleteTicket) {
                                Text(successOrFailureForDeleteTicket.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForDeleteTicket = false
                                        if isDeleted {
                                            delete = true
                                        }
                                    }.accessibilityIdentifier(Constants.DELETE_TICKET_POPOVER)
                            }.background {
                                if delete && showingPopoverForDeleteTicket == false {
                                    NavigationLink("Delete") {
                                        EmptyView()
                                    }.navigationDestination(isPresented: $delete) {
                                        SupportTickets(self.user.getValue())
                                    }
                                }
                            }.buttonStyle(BorderlessButtonStyle())
                            Spacer()
                        }.frame(
                            minWidth: geometry.size.width,
                            maxWidth: .infinity,
                            minHeight: geometry.size.height,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                    }.refreshable {
                        getComments()
                    }.listStyle(PlainListStyle())
                        .frame(
                            minWidth: 0,
                            maxWidth: .infinity,
                            minHeight: 0,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                }
                Sidebar(isSidebarVisible: $showingSidebar, userDetails: self.$user.value)
            }
      }
      else if horizontalSizeClass == .regular && verticalSizeClass == .compact {
          
          Text("iPhone Landscape")
      }
      else if horizontalSizeClass == .regular && verticalSizeClass == .regular {
          
          Text("iPad Portrait/Landscape")
      } else if horizontalSizeClass == .compact && verticalSizeClass == .compact {
          ZStack {
              GeometryReader { geometry in
                  List {
                      VStack(alignment: .center) {
                          Text("Support Ticket Details").font(.headline)
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
                                              Text(comment.author).font(.subheadline)
                                              Text(comment.content).font(.subheadline)
                                          }
                                      }
                                  }.frame(width: proxy.size.width, height: proxy.size.height)
                              }
                          }
                          HStack {
                              TextField("New Comment", text: $comment)
                              Spacer()
                              Button("Submit") {
                                  isCreated = attemptCreateNewComment()
                                  showingPopoverForCreateNewComment = true
                              }.popover(isPresented: $showingPopoverForCreateNewComment) {
                                  Text(successOrFailureForCreateNewComment.getValue())
                                      .font(.headline)
                                      .padding().onTapGesture {
                                          showingPopoverForCreateNewComment = false
                                      }.accessibilityIdentifier(Constants.CREATE_NEW_COMMENT_POPOVER)
                              }.buttonStyle(BorderlessButtonStyle())
                          }
                          Spacer()
                          HStack {
                              Spacer()
                              Button("Resolve") {
                                  isResolved = attemptResolveTicket()
                                  showingPopoverForResolveTicket = true
                              }.popover(isPresented: $showingPopoverForResolveTicket) {
                                  Text(successOrFailureForResolveTicket.getValue())
                                      .font(.headline)
                                      .padding().onTapGesture {
                                          showingPopoverForResolveTicket = false
                                          if isResolved {
                                              resolve = true
                                          }
                                      }.accessibilityIdentifier(Constants.RESOLVE_TICKET_POPOVER)
                              }.background {
                                  if resolve && showingPopoverForResolveTicket == false {
                                      NavigationLink("Resolve") {
                                          EmptyView()
                                      }.navigationDestination(isPresented: $resolve) {
                                          SupportTickets(self.user.getValue())
                                      }
                                  }
                              }.buttonStyle(BorderlessButtonStyle())
                              Spacer()
                              Button("Delete") {
                                  isDeleted = attemptDeleteTicket()
                                  showingPopoverForDeleteTicket = true
                              }.popover(isPresented: $showingPopoverForDeleteTicket) {
                                  Text(successOrFailureForDeleteTicket.getValue())
                                      .font(.headline)
                                      .padding().onTapGesture {
                                          showingPopoverForDeleteTicket = false
                                          if isDeleted {
                                              delete = true
                                          }
                                      }.accessibilityIdentifier(Constants.DELETE_TICKET_POPOVER)
                              }.background {
                                  if delete && showingPopoverForDeleteTicket == false {
                                      NavigationLink("Delete") {
                                          EmptyView()
                                      }.navigationDestination(isPresented: $delete) {
                                          SupportTickets(self.user.getValue())
                                      }
                                  }
                              }.buttonStyle(BorderlessButtonStyle())
                              Spacer()
                          }
                          Spacer()
                      }.frame(
                        minWidth: geometry.size.width,
                        maxWidth: .infinity,
                        minHeight: geometry.size.height,
                        maxHeight: .infinity,
                        alignment: .topLeading
                      )
                  }.refreshable {
                      getComments()
                  }.listStyle(PlainListStyle())
                      .frame(
                        minWidth: 0,
                        maxWidth: .infinity,
                        minHeight: 0,
                        maxHeight: .infinity,
                        alignment: .topLeading
                      )
              }
              Sidebar(isSidebarVisible: $showingSidebar, userDetails: self.$user.value)
          }
      } else {
        Text("Who knows")
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
