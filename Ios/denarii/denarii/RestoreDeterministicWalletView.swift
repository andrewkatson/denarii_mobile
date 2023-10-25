//
//  RestoreDeterministicWalletView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct RestoreDeterministicWalletView: View {
    @State private var walletName: String = ""
    @State private var walletPassword: String = ""
    @State private var walletSeed: String = ""
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
            VStack(alignment: .center) {
                Text("Restore Wallet").font(.largeTitle)
                Spacer()
                TextField("Wallet Name", text: $walletName)
                SecureField("Wallet Password", text: $walletPassword).textContentType(.password)
                TextField("Wallet Seed", text: $walletSeed)
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }.popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .onTapGesture {
                                showingPopover = false
                            }
                            .accessibilityIdentifier("Popover")
                }
                Spacer()
                NavigationLink(destination: OpenedWalletView(user.getValue(), walletName, walletSeed)) {
                    if (isSubmitted) {
                        Text("Next")
                    }
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if Constants.DEBUG {
            successOrFailure.setValue("Restored wallet in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            var userId = -1
            
            if !user.getValue().userID.isEmpty {
                userId = Int(user.getValue().userID)!
            }
            
            let denariiResponses = api.restoreWallet(userId,walletName, walletPassword, walletSeed)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to restore wallet due to a server side error")
                return false
            }
            successOrFailure.setValue("Restored wallet")
            return true
        }
    }
}

struct RestoreDeterministicWalletView_Previews: PreviewProvider {
    static var previews: some View {
        RestoreDeterministicWalletView()
    }
}
