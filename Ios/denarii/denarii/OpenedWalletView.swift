//
//  OpenedWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct OpenedWalletView: View {
    
    @ObservedObject private var seed: ObservableString = ObservableString()
    @ObservedObject private var ownAddress: ObservableString =  ObservableString()
    @ObservedObject private var balance: ObservableString =  ObservableString("0")
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    @ObservedObject private var walletName: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForRefreshBalance: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForSendDenarii: ObservableString = ObservableString()
    
    @State private var showingPopoverForRefreshBalance = false
    @State private var showingPopoverForSendDenarii = false
    @State private var addressToSendTo: String = ""
    @State private var amountToSend: String = ""

    init() {}
    
    init(_ userIdentifier: Int, _ walletName: String, _ seed: String) {
        self.userIdentifier.setValue(userIdentifier)
        self.walletName.setValue(walletName)
        self.seed.setValue(seed)
    }
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Wallet Name").accessibilityLabel("Wallet Name")
            Text("Seed: " + seed.getValue()).accessibilityIdentifier("Seed")
            Text("Address: " + ownAddress.getValue()).accessibilityIdentifier("Address")
            HStack {
                Spacer(minLength: 150)
                Text("Balance: ").accessibilityIdentifier("Balance")
                Spacer()
                ChangingTextView(value: $balance.value).accessibilityIdentifier("Balance Value")
                Spacer(minLength: 150)
            }
            Button("Refresh Balance") {
                let _ = refreshBalance()
                showingPopoverForRefreshBalance = true
            }.padding(.top)        .popover(isPresented: $showingPopoverForRefreshBalance) {
                Text(successOrFailureForRefreshBalance.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForRefreshBalance = false
                    }
                    .accessibilityIdentifier("Refresh Balance Popover")
            }
            TextField("Send To", text: $addressToSendTo).padding(.leading, 160).padding(.top)
            TextField("Amount to Send", text: $amountToSend).padding(.leading, 130).padding(.top)
            Button("Send") {
                let _ = sendDenarii()
               showingPopoverForSendDenarii = true
            }.padding(.top)        .popover(isPresented: $showingPopoverForSendDenarii) {
                Text(successOrFailureForSendDenarii.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForSendDenarii = false
                    }
                    .accessibilityIdentifier("Send Denarii Popover")
            }
        }
    }
    
    func refreshBalance() -> Bool {
        if Constants.DEBUG {
            if self.balance.getValue().isEmpty {
                self.balance.setValue("1")
            }
            successOrFailureForRefreshBalance.setValue("Refreshed balance in DEBUG mode")
            return true
        } else {
            let api = Config().api
            let wallet = api.getBalance(userIdentifier.getValue(), walletName.getValue())
            if wallet.responseCode != 200 {
                successOrFailureForRefreshBalance.setValue("Failed to refresh balance becasue of a server error")
                return false
            }
            
            self.balance.setValue(String(wallet.response.balance))
            successOrFailureForRefreshBalance.setValue("Refreshed balance")
            return true
        }
    }
    
    func sendDenarii() -> Bool {
        
        if Double(self.balance.getValue())! < Double(amountToSend)! {
            successOrFailureForSendDenarii.setValue("Failed to send denarii because you don't have enough to send")
            return false
        }
        
        if Constants.DEBUG {
            var currBalance = "1"
            if self.balance.getValue().isEmpty == false {
                currBalance = self.balance.getValue()
            }
            self.balance.setValue( String(Int(currBalance)! - 1))
            successOrFailureForSendDenarii.setValue("Sent denarii in DEBUG mode")
            return true
        } else {
            let api = Config().api
            let wallet = api.sendDenarii(userIdentifier.getValue(), walletName.getValue(), addressToSendTo, Double(amountToSend)!)
            
            if wallet.responseCode != 200 {
                successOrFailureForSendDenarii.setValue("Failed to send denarii because of a server error")
                return false
            }
            
            self.balance.setValue(String(Double(self.balance.getValue())! - Double(amountToSend)!))
            successOrFailureForSendDenarii.setValue("Sent denarii")
            return true
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
