//
//  OpenedWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct OpenedWalletView: View {
    
    @State private var ownAddress: String = ""
    @State private var balance: String = ""
    @State private var addressToSendTo: String = ""
    @State private var amountToSend: String = ""
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Address: " + ownAddress)
            Text("Balance: " + balance)
            Button(action: refreshBalance) {
                Text("Refresh Balance")
            }.padding(.top)
            TextField("Send To", text: $addressToSendTo).padding(.leading, 160).padding(.top)
            TextField("Amount to Send", text: $amountToSend).padding(.leading, 130).padding(.top)
            Button(action: sendDenarii) {
                Text("Send").padding(.top)
            }
        }
    }
    
    func refreshBalance() {
    }
    
    func sendDenarii() {
        
    }
}

struct OpenedWalletView_Previews: PreviewProvider {
    static var previews: some View {
        OpenedWalletView()
    }
}
