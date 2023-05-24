//
//  RegisterView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct RegisterView: View {
    @State private var username: String = ""
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var confirmPassword: String = ""
    @State private var isSubmitted: Bool = false
    
    var body: some View {
        HStack {
            Spacer(minLength: 100)
            VStack {
                Spacer(minLength: 300)
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password)
                SecureField("Confirm Password", text: $confirmPassword)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 75)
                    Spacer()
                }
                Spacer(minLength: 150)
                NavigationLink(destination: WalletDecisionView()) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if DEBUG {
            return true
        } else {
            // TODO: Make API call
            return false
        }
    }
}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
