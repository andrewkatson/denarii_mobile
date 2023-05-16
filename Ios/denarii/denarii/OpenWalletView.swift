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
    
    var body: some View {
        HStack {
            Spacer(minLength: 140)
            VStack {
                Spacer(minLength: 320)
                TextField("Wallet Name", text: $walletName)
                SecureField("Wallet Password", text: $walletPassword)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 25)
                    Spacer()
                }
                Spacer(minLength: 150)
                NavigationLink(destination: OpenedWalletView()) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                    .padding(.top, 125)
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        return true
    }
}

struct OpenWalletView_Previews: PreviewProvider {
    static var previews: some View {
        OpenWalletView()
    }
}
