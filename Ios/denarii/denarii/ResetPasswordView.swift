//
//  ResetPasswordView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct ResetPasswordView: View {
    
    @State private var username: String = ""
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var confirmPassword: String = ""
    @State private var isReset: Bool = false
    
    
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
                    Button("Reset Password") {
                        isReset = attemptReset()
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 25)
                    Spacer()
                }
                Spacer(minLength: 150)
                NavigationLink(destination: LoginView()) {
                    if (isReset) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                Spacer()
            }
        }
    }
    
    func attemptReset() -> Bool {
        return true
    }
}

struct ResetPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ResetPasswordView()
    }
}
