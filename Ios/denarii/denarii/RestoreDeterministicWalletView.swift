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
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    init() {}

    init(_ userIdentifier: Int) {
        self.userIdentifier.setValue(userIdentifier)
    }
    
    var body: some View {
        HStack {
            Spacer(minLength: 140)
            VStack {
                Spacer()
                TextField("Wallet Name", text: $walletName)
                SecureField("Wallet Password", text: $walletPassword).textContentType(.password)
                TextField("Wallet Seed", text: $walletSeed)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 25).popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopover = false
                            }
                            .accessibilityIdentifier("Popover")
                    }
                    Spacer()
                }
                Spacer()
                NavigationLink(destination: OpenedWalletView(userIdentifier.getValue(), walletName, walletSeed)) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }.padding(.leading, 100).padding(.top, 60)
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if Constants.DEBUG {
            successOrFailure.setValue("Restored wallet in DEBUG mode")
            return true
        } else {
            let api = Config().api
            let denariiResponses = api.restoreWallet(userIdentifier.getValue(),walletName, walletPassword, walletSeed)
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
