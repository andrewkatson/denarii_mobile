//
//  CreditCardInfo.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct CreditCardInfo: View {
    
    @State private var number: String = ""
    @State private var expirationDateMonth: String = ""
    @State private var expirationDateYear: String = ""
    @State private var securityCode: String = ""
    @State private var status: String = "Status: Unknown"

    @State private var isSet: Bool = false
    @State private var isCleared: Bool = false
    @State private var showingPopoverForSet = false
    @State private var showingPopoverForClear = false
    
    
    @ObservedObject private var successOrFailureForSet: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForClear: ObservableString = ObservableString()
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {
        checkCreditCardInfoState()
    }

    init(_ user: UserDetails) {
        self.user.setValue(user)
        checkCreditCardInfoState()
    }
    
    func checkCreditCardInfoState() {
        if self.user.getValue().userID.isEmpty {
            return
        }
        
        let api = Config.api
        
        let responses = api.hasCreditCardInfo(Int(self.user.getValue().userID)!)
        
        if responses.isEmpty {
            return
        }
        let response = responses.first!
        
        if response.hasCreditCardInfo {
            status = "Status: Set"
        } else {
            status = "Status: Not Set"
        }
    }
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Credit Card Info").font(.largeTitle)
            Spacer()
            Text("\(status)")
            TextField("Credit Card Number", text: $number)
            TextField("Expiration Date Month", text: $expirationDateMonth)
            TextField("Expiration Date Year", text: $expirationDateYear)
            TextField("Security Code", text: $securityCode)
            Button("Set Info") {
                isSet = attemptToSetCreditCardInfo()
                showingPopoverForSet = true
            }.popover(isPresented: $showingPopoverForSet) {
                Text(successOrFailureForSet.getValue())
                    .font(.headline)
                    .padding().onTapGesture {
                        showingPopoverForSet = false
                    }.accessibilityIdentifier(Constants.SET_CREDIT_CARD_INFO_POPOVER)
            }
            if status.contains("Status: Set") {
                Button("Clear Info") {
                    isCleared = attemptToClearCreditCardInfo()
                    showingPopoverForClear = true
                }.popover(isPresented: $showingPopoverForClear) {
                    Text(successOrFailureForClear.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopoverForClear = false
                        }.accessibilityIdentifier(Constants.CLEAR_CREDIT_CARD_INFO_POPOVER)
                }

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
                NavigationLink(destination: UserSettings(user.getValue())) {
                    Text("Settings")
                }
            }
            Spacer()

        }
    }
    
    func attemptToSetCreditCardInfo() -> Bool {
        if Constants.DEBUG {
            successOrFailureForSet.setValue("Successfully set credit card info in DEBUG mode")
            return true
        } else {
            
            if self.user.getValue().userID.isEmpty {
                successOrFailureForSet.setValue("Failed to set credit card info")
                return false
            }
            
            let api = Config.api
            
            let responses = api.setCreditCardInfo(Int(self.user.getValue().userID)!, number, expirationDateMonth, expirationDateYear, securityCode)
            
            if responses.isEmpty {
                self.successOrFailureForSet.setValue("Failed to set credit card info server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                self.successOrFailureForSet.setValue("Failed to set credit card info server error")
                return false
            } else {
                self.successOrFailureForSet.setValue("Successfully set credit card info")
                status = "Status: Set"
                return true
            }
        }
    }
    
    func attemptToClearCreditCardInfo() -> Bool {
        if Constants.DEBUG {
            successOrFailureForSet.setValue("Successfully cleared credit card info in DEBUG mode")
            return true
        } else {
            
            if self.user.getValue().userID.isEmpty {
                successOrFailureForClear.setValue("Failed to clear credit card info")
                return false
            }
            
            let api = Config.api
            
            let responses = api.clearCreditCardInfo(Int(self.user.getValue().userID)!)
            
            if responses.isEmpty {
                self.successOrFailureForClear.setValue("Faield to clear credit card info server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                self.successOrFailureForClear.setValue("Failed to clear credit card info server error")
                return false
            } else {
                self.successOrFailureForClear.setValue("Successfully cleared credit card info")
                status = "Status: Not Set"
                return true
            }
        }
    }

}

#Preview {
    CreditCardInfo()
}
