//
//  WalletDecisionView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct WalletDecisionView: View {
    var body: some View {
        HStack {
            Spacer()
            VStack {
                NavigationLink(destination: CreateWalletView()) {
                    Text("Create Wallet")
                }.padding(.bottom, 10)
                NavigationLink(destination: RestoreDeterministicWalletView()) {
                    Text("Restore Deteministic Wallet")
                }.padding(.bottom, 10)
                NavigationLink(destination: OpenWalletView()) {
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
