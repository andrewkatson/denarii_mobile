//
//  LoginView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct LoginView: View {
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
                HStack {
                    NavigationLink(destination: RequestResetView()) {
                        Text("Forgot Password")
                    }.padding(.trailing, 70)
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
        return true
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
