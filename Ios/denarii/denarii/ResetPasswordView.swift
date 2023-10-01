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
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    init() {}
    
    init(_ userIdentifier: Int) {
        self.userIdentifier.setValue(userIdentifier)
    }
    
    
    var body: some View {
        HStack {
            Spacer(minLength: 100)
            VStack {
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password)
                SecureField("Confirm Password", text: $confirmPassword)
                HStack {
                    Button("Reset Password") {
                        isReset = attemptReset()
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
                NavigationLink(destination: LoginView(userIdentifier.getValue())) {
                    if (isReset) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                Spacer()
            }
        }
    }
    
    func attemptReset() -> Bool {
        
        if (password != confirmPassword) {
            successOrFailure.setValue("Failed to reset password because password and confirm password did not match")
            return false
        }
        
        if Constants.DEBUG {
            successOrFailure.setValue("Reset password in DEBUG mode")
            return true
        } else {
            let api = Config().api
            let denariiResponses = api.resetPassword(username, email, password)
            // We only expect one response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to reset password due to a server side error")
                return false
            }
            successOrFailure.setValue("Reset password")
            return true
        }
    }
}

struct ResetPasswordView_Previews: PreviewProvider {
    static var previews: some View {
        ResetPasswordView()
    }
}
