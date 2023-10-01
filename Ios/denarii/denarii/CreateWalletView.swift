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
                SecureField("Wallet Password", text: $walletPassword).textContentType(.newPassword)
                SecureField("Confirm Wallet Password", text: $confirmWalletPassword)
                    .textContentType(.newPassword)
                HStack {
                    Text("Seed: ")
                    Spacer()
                    ChangingTextView(value: $seed.value)
                }.padding(.trailing, 200)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 25)
                    .popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopover = false
                                if !seed.getValue().isEmpty {
                                    
                                }
                            }
                            .accessibilityIdentifier("Popover")
                    }
                    Spacer()
                }
                Spacer()
                NavigationLink(destination: OpenedWalletView(userIdentifier.getValue(), walletName, seed.getValue())) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }.padding(.leading, 100).padding(.top, 40)
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
            let api = Config().api
            let denariiResponses = api.createWallet(userIdentifier.getValue(),walletName, walletPassword)
            
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
