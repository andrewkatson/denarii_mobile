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
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    init() {}
    
    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    
    var body: some View {
            VStack(alignment: .center) {
                Text("Reset Password").font(.largeTitle)
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password).textContentType(.newPassword)
                SecureField("Confirm Password", text: $confirmPassword).textContentType(.newPassword)
                Button("Reset Password") {
                    isReset = attemptReset()
                    showingPopover = true
                }.popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }
                        .accessibilityIdentifier(Constants.POPOVER)
                }
                Spacer()
                NavigationLink(destination: LoginView(user.getValue())) {
                    if (isReset) {
                        Text("Next")
                    }
                }
                Spacer()
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
            let api = Config.api
            let denariiResponses = api.resetPassword(username, email, password)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
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
