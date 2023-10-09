//
//  WalletDecisionView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct WalletDecisionView: View {
    
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    init() {}
    
    init(_ userIdentifier: Int) {
        self.userIdentifier.setValue(userIdentifier)
    }
    
    var body: some View {
        HStack {
            Spacer()
            VStack {
                NavigationLink(destination: CreateWalletView(userIdentifier.getValue())) {
                    Text("Create Wallet")
                }
                Spacer()
                NavigationLink(destination: RestoreDeterministicWalletView(userIdentifier.getValue())) {
                    Text("Restore Deterministic Wallet")
                }
                Spacer()
                NavigationLink(destination: OpenWalletView(userIdentifier.getValue())) {
                    Text("Open Wallet")
                }
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
