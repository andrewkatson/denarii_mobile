//
//  CreateSupportTicket.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct CreateSupportTicket: View {
    
    @State private var title: String = ""
    @State private var description: String = ""

    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Create Support Ticket").font(.largeTitle)
            Spacer()
            TextField("Title", text: $title)
            TextField("Description", text: $description, axis: Axis.vertical)
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
    CreateSupportTicket()
}
