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
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Support Ticket Details").font(.largeTitle)
            Spacer()
            Text("\(title.getValue())")
            Text("\(description.getValue())")
            VStack(alignment: .center) {
                // TODO put the comments here
            }
            TextField("Comment", text: $comment, axis: Axis.vertical)
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
        return false
    }
}

#Preview {
    SupportTicketDetails()
}
