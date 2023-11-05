//
//  UserSettings.swift
//  denarii
//
//  Created by Andrew Katson on 10/25/23.
//

import SwiftUI

struct UserSettings: View {
    
    @State private var isDeleted = false
    @State private var showingPopover = false
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Settings").font(.largeTitle)
            Spacer()
            NavigationStack {
                Button("Delete Account") {
                    isDeleted = attemptDeleteAccount()
                    showingPopover = true
                }.popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }.accessibilityIdentifier("Popover")
                }
            }.navigationDestination(isPresented: $isDeleted) {
                return ContentView()
            }
            Spacer()
            NavigationLink(destination: SupportTickets(user.getValue())) {
                Text("Support Tickets")
            }
            Spacer()
            HStack {
                NavigationLink(destination: OpenedWalletView(user.getValue())) {
                    Text("Wallet")
                }
                NavigationLink(destination: BuyDenarii(user.getValue())) {
                    Text("Buy Denarii")
                }
                NavigationLink(destination: SellDenarii(user.getValue())) {
                    Text("Sell Denarii")
                }
                NavigationLink(destination: Verification(user.getValue())) {
                    Text("Verification")
                }
                NavigationLink(destination: CreditCardInfo(user.getValue())) {
                    Text("Credit Card")
                }
            }
            Spacer()
        }
    }
    
    func attemptDeleteAccount() -> Bool {
        return false
    }
}

#Preview {
    UserSettings()
}
