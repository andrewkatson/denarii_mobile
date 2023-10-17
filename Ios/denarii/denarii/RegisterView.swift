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
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    var body: some View {
        HStack {
            Spacer(minLength: 100)
            VStack {
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password).textContentType(.newPassword)
                SecureField("Confirm Password", text: $confirmPassword).textContentType(.newPassword)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 75)
                    .popover(isPresented: $showingPopover) {
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
                NavigationLink(destination: WalletDecisionView(userIdentifier.getValue())) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                Spacer()
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        
        if password != confirmPassword {
            successOrFailure.setValue("Failed to register because password and confirm password did not match")
            return false
        }
        
        if Constants.DEBUG {
            successOrFailure.setValue("Registered in DEBUG mode")
            return true
        } else {
            let api = Config.api
            let denariiResponses = api.getUserId(username, email, password)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one response
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to register due to a server side error")
                return false
            }
            
            let userIdentifierOptional = Int(response.userIdentifier)
            
            if userIdentifierOptional == nil {
                successOrFailure.setValue("Failed to register due to a client side error")
                return false
            }
            
            userIdentifier.setValue(userIdentifierOptional!)
            successOrFailure.setValue("Registered")
            return true
        }
    }
}

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}
