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
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user = ObservableUser()
    
    init() {}
    
    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        HStack(alignment: .center) {
            Spacer()
            VStack(alignment: .center) {
                Text("Login").font(.largeTitle)
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password).autocorrectionDisabled().textContentType(.newPassword)
                SecureField("Confirm Password", text: $confirmPassword).autocorrectionDisabled().textContentType(.newPassword)
                Button("Submit") {
                    isSubmitted = attemptSubmit()
                    showingPopover = true
                }.popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }.accessibilityIdentifier(Constants.POPOVER)
                }
                Spacer()
                HStack(alignment: .center) {
                    NavigationLink(destination: RequestResetView(user.getValue())) {
                        Text("Forgot Password")
                    }
                }
                Spacer()
                NavigationLink(destination: WalletDecisionView(user.getValue())) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if (password != confirmPassword) {
            successOrFailure.setValue("Failed to login becasue password is not the same as confirm password")
            return false
        }
        if Constants.DEBUG {
            successOrFailure.setValue("Logged In in DEBUG mode")
            return true
        } else {
            let api = Config.api
            let denariiResponses = api.getUserId(username, email, password)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one denarii response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to login becasue of a server side error")
                return false
            }
            
            let userIdentifierOptional = Int(response.userIdentifier)
            
            if userIdentifierOptional == nil {
                successOrFailure.setValue("Failed to register due to a client side error")
                return false
            }
            
            user.getValue().userID = String(userIdentifierOptional!)
            successOrFailure.setValue("Logged In")
            return true
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
