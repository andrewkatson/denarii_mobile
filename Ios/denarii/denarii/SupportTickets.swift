//
//  SupportTickets.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SupportTickets: View {
    
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
        VStack (alignment: .center) {
            Text("Support Tickets").font(.largeTitle)
            Spacer()
            NavigationLink(destination: CreateSupportTicket(user.getValue())) {
                Text("Create Support Ticket")
            }
            Spacer()
            VStack {
                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                 */
                ForEach(self.supportTickets.getValue(), id: \.self) { supportTicket in
                    
                    NavigationLink(destination: UserSettings(user.getValue())) {
                        Text(supportTicket.title)
                    }
                    
                }.refreshable {
                    getSupportTickets()
                }
            }
            Spacer()
        }
    }
}

#Preview {
    SupportTickets()
}
