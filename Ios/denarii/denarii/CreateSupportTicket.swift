//
//  CreateSupportTicket.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct CreateSupportTicket: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @State private var userDetails = UserDetails()
    @State private var showingSidebar = false
    @State private var createTicket: Bool = false
    @State private var title: String = ""
    @State private var description: String = ""

    @State private var isCreated: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var newSupportTicketID: ObservableString = ObservableString()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
        self.userDetails = self.user.getValue()
    }
    
    var body: some View {
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                VStack(alignment: .center) {
                    Text("Create Support Ticket").font(.largeTitle)
                    Spacer()
                    TextField("Title", text: $title)
                    TextField("Description", text: $description, axis: Axis.vertical)
                    Spacer()
                    Button("Create Support Ticket") {
                        isCreated = attemptCreateTicket()
                        showingPopover = true
                    }.popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopover = false
                                
                                if isCreated {
                                    createTicket = true
                                }
                            }.accessibilityIdentifier(Constants.POPOVER)
                    }.background {
                        if isCreated && createTicket && showingPopover == false {
                            NavigationLink("Create Support Ticket")
                            {
                                EmptyView()
                            }.navigationDestination(isPresented: $createTicket) {
                                SupportTicketDetails(self.user.getValue(), self.newSupportTicketID.getValue())
                            }
                        }
                    }
                    Spacer()
                }
                Sidebar(isSidebarVisible: $showingSidebar, userDetails: $userDetails)
            }
      }
      else if horizontalSizeClass == .regular && verticalSizeClass == .compact {
          
          Text("iPhone Landscape")
      }
      else if horizontalSizeClass == .regular && verticalSizeClass == .regular {
          
          Text("iPad Portrait/Landscape")
      } else if horizontalSizeClass == .compact && verticalSizeClass == .compact {
          ZStack {
              VStack(alignment: .center) {
                  Text("Create Support Ticket").font(.headline)
                  Spacer()
                  TextField("Title", text: $title)
                  TextField("Description", text: $description, axis: Axis.vertical)
                  Spacer()
                  Button("Create Support Ticket") {
                      isCreated = attemptCreateTicket()
                      showingPopover = true
                  }.popover(isPresented: $showingPopover) {
                      Text(successOrFailure.getValue())
                          .font(.headline)
                          .padding().onTapGesture {
                              showingPopover = false
                              
                              if isCreated {
                                  createTicket = true
                              }
                          }.accessibilityIdentifier(Constants.POPOVER)
                  }.background {
                      if isCreated && createTicket && showingPopover == false {
                          NavigationLink("Create Support Ticket")
                          {
                              EmptyView()
                          }.navigationDestination(isPresented: $createTicket) {
                              SupportTicketDetails(self.user.getValue(), self.newSupportTicketID.getValue())
                          }
                      }
                  }
                  Spacer()
              }
              Sidebar(isSidebarVisible: $showingSidebar, userDetails: $userDetails)
          }
      } else {
        Text("Who knows")
     }
    }
    
    func attemptCreateTicket() -> Bool {
        
        if Constants.DEBUG {
            successOrFailure.setValue("Successfully created new support ticket in DEBUG mode")
            return true
        } else {
            if self.user.getValue().userID.isEmpty {
                successOrFailure.setValue("Failed to create support ticket")
                return false
            }
            let api = Config.api
                
            let responses = api.createSupportTicket(Int(self.user.getValue().userID)!, title, description)
            
            if responses.isEmpty {
                successOrFailure.setValue("Failed to create support ticket server error")
                return false
            } else {
    
                let response = responses.first!
                
                if response.responseCode != 200 {
                    successOrFailure.setValue("Failed to create support ticket server error")
                    return false
                }
                
                successOrFailure.setValue("Successfully created a new support ticket")
                
                self.newSupportTicketID.setValue(response.supportTicketID)
                return true
            }
        }
    }
}

#Preview {
    CreateSupportTicket()
}
