//
//  SupportTickets.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SupportTickets: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @State private var userDetails = UserDetails()
    @State private var showingSidebar = false
    @State private var goToTicket: Int? = 0
    @State private var isCreated: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var supportTickets: ObservableArray<SupportTicket> = ObservableArray()
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @State private var moveToSupporTicketDetails = false
    
    init() {
        getSupportTickets()
    }

    init(_ user: UserDetails) {
        self.user.setValue(user)
        self.userDetails = self.user.getValue()
        getSupportTickets()
    }
    
    func getSupportTickets() {
        if !self.user.getValue().userID.isEmpty {
                let api = Config.api
                
                // TODO allow user to see resolved tickets
                let responses = api.getSupportTickets(Int(self.user.getValue().userID)!, false)
                
            if !responses.isEmpty {
                
                var newSupportTickets: Array<SupportTicket> = Array()
                
                for response in responses {
                    let newSupportTicket = SupportTicket()
                    
                    newSupportTicket.supportID = response.supportTicketID
                    newSupportTicket.resolved = response.isResolved
                    newSupportTicket.title
                    = response.title
                    newSupportTicket.description = response.description
                    
                    newSupportTickets.append(newSupportTicket)
                }
                supportTickets.setValue(newSupportTickets)
            }
        }
    }
    
    var body: some View {
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                GeometryReader { geometry in
                    List {
                        VStack (alignment: .center) {
                            Text("Support Tickets").font(.largeTitle)
                            Spacer()
                            NavigationLink(destination: CreateSupportTicket(user.getValue())) {
                                Text("Create Support Ticket").padding(.top).padding(.top)
                            }
                            Spacer()
                            GeometryReader { proxy in
                                ScrollView(.vertical, showsIndicators: true) {
                                    VStack {
                                        /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                         */
                                        ForEach(self.supportTickets.getValue(), id: \.self) { supportTicket in
                                            
                                            NavigationLink(destination: SupportTicketDetails(user.getValue(), supportTicket.supportID)) {
                                                Text(supportTicket.title)
                                            }
                                            
                                        }
                                    }.frame(width: proxy.size.width, height: proxy.size.height)
                                }
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
                        getSupportTickets()
                    }.listStyle(PlainListStyle())
                        .frame(
                            minWidth: 0,
                            maxWidth: .infinity,
                            minHeight: 0,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
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
              GeometryReader { geometry in
                  List {
                      VStack (alignment: .center) {
                          Text("Support Tickets").font(.headline)
                          Spacer()
                          NavigationLink(destination: CreateSupportTicket(user.getValue())) {
                              Text("Create Support Ticket")
                          }
                          Spacer()
                          GeometryReader { proxy in
                              ScrollView(.vertical, showsIndicators: true) {
                                  VStack {
                                      /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                       */
                                      ForEach(self.supportTickets.getValue(), id: \.self) { supportTicket in
                                          
                                          NavigationLink(destination: SupportTicketDetails(user.getValue(), supportTicket.supportID)) {
                                              Text(supportTicket.title).font(.subheadline)
                                          }
                                          
                                      }.refreshable {
                                          getSupportTickets()
                                      }
                                  }.frame(width: proxy.size.width, height: proxy.size.height)
                              }
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
                      getSupportTickets()
                  }.listStyle(PlainListStyle())
                      .frame(
                        minWidth: 0,
                        maxWidth: .infinity,
                        minHeight: 0,
                        maxHeight: .infinity,
                        alignment: .topLeading
                      )
              }
              Sidebar(isSidebarVisible: $showingSidebar, userDetails: $userDetails)
          }
      } else {
        Text("Who knows")
     }
    }
}

#Preview {
    SupportTickets()
}
