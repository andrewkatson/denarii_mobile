//
//  OpenedWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct OpenedWalletView: View {
    
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var balance: ObservableString =  ObservableString("0")
    @ObservedObject private var successOrFailureForRefreshBalance: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForSendDenarii: ObservableString = ObservableString()
    
    @State private var showingPopoverForRefreshBalance = false
    @State private var showingPopoverForSendDenarii = false
    @State private var addressToSendTo: String = ""
    @State private var amountToSend: String = ""

    init() {}
    
    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    init(_ user: UserDetails, _ walletName: String, _ seed: String) {
        
        user.walletDetails.walletName = walletName
        user.walletDetails.seed = seed
        
        self.user.setValue(user)
    }
    
    var body: some View {
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            VStack(alignment: .center) {
                Text("Wallet").font(.largeTitle)
                Spacer()
                Text("Wallet Name").accessibilityLabel("Wallet Name")
                Text("Seed: " + user.getValue().walletDetails.seed).accessibilityIdentifier("Seed")
                Text("Address: " + user.getValue().walletDetails.walletAddress).accessibilityIdentifier("Address")
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
                        .accessibilityIdentifier(Constants.REFRESH_BALANCE_POPOVER)
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
                        .accessibilityIdentifier(Constants.SEND_DENARII_POPOVER)
                }
                Spacer()
                HStack {
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
                    NavigationLink(destination: UserSettings(user.getValue())) {
                        Text("Settings")
                    }
                }
                Spacer()
            }
        }
        else if horizontalSizeClass == .regular && verticalSizeClass == .compact {
            
            Text("iPhone Landscape")
        }
        else if horizontalSizeClass == .regular && verticalSizeClass == .regular {
            
            Text("iPad Portrait/Landscape")
        } else if horizontalSizeClass == .compact && verticalSizeClass == .compact {
            VStack(alignment: .center) {
                Text("Wallet").font(.headline)
                Spacer()
                HStack {
                    Spacer()
                    Text("Wallet Name").accessibilityLabel("Wallet Name")
                    Spacer()
                    Text("Seed: " + user.getValue().walletDetails.seed).accessibilityIdentifier("Seed")
                    Spacer()
                    Text("Address: " + user.getValue().walletDetails.walletAddress).accessibilityIdentifier("Address")
                    Spacer()
                }
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
                        .accessibilityIdentifier(Constants.REFRESH_BALANCE_POPOVER)
                }
                HStack {
                    Spacer()
                    TextField("Send To", text: $addressToSendTo).padding(.leading, 160).padding(.top)
                    Spacer()
                    TextField("Amount to Send", text: $amountToSend).padding(.leading, 130).padding(.top)
                    Spacer()
                }
                Button("Send") {
                    let _ = sendDenarii()
                    showingPopoverForSendDenarii = true
                }.padding(.top)        .popover(isPresented: $showingPopoverForSendDenarii) {
                    Text(successOrFailureForSendDenarii.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopoverForSendDenarii = false
                        }
                        .accessibilityIdentifier(Constants.SEND_DENARII_POPOVER)
                }
                Spacer()
                HStack {
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
                    NavigationLink(destination: UserSettings(user.getValue())) {
                        Text("Settings")
                    }
                }
                Spacer()
            }
        } else {
          Text("Who knows")
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
            let api = Config.api
            
            var userId = -1
            if !user.getValue().userID.isEmpty {
                userId = Int(user.getValue().userID)!
            }
            
            let denariiResponses = api.getBalance(userId, user.getValue().walletDetails.walletName)
            if denariiResponses.isEmpty {
                successOrFailureForRefreshBalance.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailureForRefreshBalance.setValue("Failed to refresh balance becasue of a server error")
                return false
            }
            
            self.balance.setValue(String(response.balance))
            successOrFailureForRefreshBalance.setValue("Refreshed balance")
            return true
        }
    }
    
    func sendDenarii() -> Bool {
        let balanceVal = self.balance.getValue()
        if balanceVal == "" {
            successOrFailureForSendDenarii.setValue("Failed to send denarii because there is no balance set")
            return false
        }
        else if amountToSend == "" {
            successOrFailureForSendDenarii.setValue("Failed to send denarii because no amount to send is set")
            return false
        }
        else if Double(balanceVal)! < Double(amountToSend)! {
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
            let api = Config.api
            
            var userId = -1
            if !user.getValue().userID.isEmpty {
                userId = Int(user.getValue().userID)!
            }
            
            let denariiResponses = api.sendDenarii(userId, user.getValue().walletDetails.walletName, addressToSendTo, Double(amountToSend)!)
            if denariiResponses.isEmpty {
                successOrFailureForSendDenarii.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
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
