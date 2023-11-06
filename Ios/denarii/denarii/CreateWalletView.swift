//
//  CreateWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct CreateWalletView: View {
    @State private var walletName: String = ""
    @State private var walletPassword: String = ""
    @State private var confirmWalletPassword: String = ""
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
                Text("Create Wallet").font(.largeTitle)
                Spacer()
                TextField("Wallet Name", text: $walletName)
                SecureField("Wallet Password", text: $walletPassword).textContentType(.newPassword)
                SecureField("Confirm Wallet Password", text: $confirmWalletPassword)
                    .textContentType(.newPassword)
                HStack(alignment: .center) {
                    Text("Seed: ")
                    Spacer()
                    ChangingTextView(value: $seed.value)
                }
                HStack(alignment: .center) {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }
                    .popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopover = false
                                if !seed.getValue().isEmpty {
                                    
                                }
                            }
                            .accessibilityIdentifier(Constants.POPOVER)
                    }
                    Spacer()
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
        
        if (walletPassword != confirmWalletPassword) {
            successOrFailure.setValue("Failed to create wallet because passwords do not match.")
            return false
        }
        
        if Constants.DEBUG {
            successOrFailure.setValue("Created wallet in DEBUG mode")
            return true
        } else {
            let api = Config.api
            var userId = -1
            
            if !user.getValue().userID.isEmpty {
                userId = Int(user.getValue().userID)!
            }
            
            let denariiResponses = api.createWallet(userId,walletName, walletPassword)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            
            // We only expect one response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to create wallet due to a server side error")
                return false
            }
            successOrFailure.setValue("Created wallet")
            seed.setValue(response.seed)
            return true
        }
    }
}

struct CreateWalletView_Previews: PreviewProvider {
    static var previews: some View {
        CreateWalletView()
    }
}
