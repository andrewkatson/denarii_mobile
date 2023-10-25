//
//  UserSettings.swift
//  denarii
//
//  Created by Andrew Katson on 10/25/23.
//

import SwiftUI

struct UserSettings: View {
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Settings").font(.largeTitle)
            Spacer()
            NavigationLink("Delete Account") {
                ContentView()
            }
            Spacer()
            NavigationLink(destination: SupportTickets(user.getValue())) {
                Text("Suppor Tickets")
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
}

#Preview {
    UserSettings()
}
