//
//  OpenWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct OpenWalletView: View {
    @State private var walletName: String = ""
    @State private var walletPassword: String = ""
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var seed: ObservableString = ObservableString()
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        HStack(alignment: .center) {
            Spacer()
            VStack(alignment: .center) {
                Text("Open Wallet").font(.largeTitle)
                Spacer()
                TextField("Wallet Name", text: $walletName)
                SecureField("Wallet Password", text: $walletPassword).textContentType(.password)
                    Text("Seed: ")
                    Spacer()
                    ChangingTextView(value: $seed.value)
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }.popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .onTapGesture {
                                showingPopover = false
                            }
                            .accessibilityIdentifier(Constants.POPOVER)
                }
                Spacer()
                NavigationLink(destination: OpenedWalletView(user.getValue(), walletName, seed.getValue())) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if Constants.DEBUG {
            successOrFailure.setValue("Opened wallet in DEBUG mode")
            return true
        } else {
            var invalid_fields: Array<String> = Array()
            
            if !is_valid_input(walletName, Constants.Patterns.alphanumeric) {
                invalid_fields.append(Constants.Params.walletName)
            }
            
            if !is_valid_input(walletPassword, Constants.Patterns.password) {
                invalid_fields.append(Constants.Params.password)
            }
            
            if !invalid_fields.isEmpty {
                successOrFailure.setValue("Invalid fields: \(invalid_fields)")
                return false
            }
            
            let api = Config.api
            
            var userId = -1
            if !user.getValue().userID.isEmpty {
                userId = Int(user.getValue().userID)!
            }
            
            let denariiResponses = api.openWallet(userId,walletName, walletPassword)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            
            // We only expect one response
            let response = denariiResponses.first!
            
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to open wallet due a server side error")
                return false
            }
            successOrFailure.setValue("Opened wallet")
            seed.setValue(response.seed)
            return true
        }
    }
}

struct OpenWalletView_Previews: PreviewProvider {
    static var previews: some View {
        OpenWalletView()
    }
}
