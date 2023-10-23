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
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()

    
    var body: some View {
        VStack (alignment: .center) {
            Text("Support Tickets").font(.largeTitle)
            Spacer()
            Button("Create Support Ticket") {
                isCreated = attemptCreateTicket()
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
    
    func attemptCreateTicket() -> Bool {
        return false
    }
}

#Preview {
    SupportTickets()
}
