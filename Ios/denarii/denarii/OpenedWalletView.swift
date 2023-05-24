//
//  OpenedWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct OpenedWalletView: View {
    
    @ObservedObject private var ownAddress: ObservableString =  ObservableString()
    @ObservedObject private var balance: ObservableString =  ObservableString()
    @State  private var addressToSendTo: String = ""
    @State private var amountToSend: String = ""
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Address: " + ownAddress.getValue())
            Text("Balance: " + balance.getValue())
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
        if DEBUG {
            if self.balance.getValue().isEmpty {
                print("Setting it to 1")
                self.balance.setValue("1")
                print(balance.getValue())
            }
        } else {
            // TODO: Make API call
        }
    }
    
    func sendDenarii() {
        if DEBUG {
            var currBalance = "1"
            if self.balance.getValue().isEmpty == false {
                print("Setting balance with another balance")
                currBalance = self.balance.getValue()
                print(currBalance)
            }
            self.balance.setValue( String(Int(currBalance)! - 1))
            print(self.balance.getValue())
        } else {
            // TODO: Make API call
        }
    }
    
    func getBalance() -> String {
        return self.balance.getValue()
    }
    
    func setBalance(_ newBalance: String) {
        self.balance.setValue(newBalance)
    }
}

struct OpenedWalletView_Previews: PreviewProvider {
    static var previews: some View {
        OpenedWalletView()
    }
}
