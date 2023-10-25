//
//  WalletDecisionView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct WalletDecisionView: View {
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {}
    
    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        HStack (alignment: .center) {
            Spacer()
            VStack(alignment: .center) {
                Text("Wallet Decision").font(.largeTitle)
                Spacer()
                NavigationLink(destination: CreateWalletView(user.getValue())) {
                    Text("Create Wallet")
                }
                Spacer()
                NavigationLink(destination: RestoreDeterministicWalletView(user.getValue())) {
                    Text("Restore Deterministic Wallet")
                }
                Spacer()
                NavigationLink(destination: OpenWalletView(user.getValue())) {
                    Text("Open Wallet")
                }
                Spacer()
            }
            Spacer()
        }
    }
}

struct WalletDecisionView_Previews: PreviewProvider {
    static var previews: some View {
        WalletDecisionView()
    }
}
