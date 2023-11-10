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
    @ObservedObject private var user: ObservableUser = ObservableUser()
    
    var body: some View {
            VStack(alignment: .center) {
                Text("Register").font(.largeTitle)
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password).textContentType(.newPassword)
                SecureField("Confirm Password", text: $confirmPassword).textContentType(.newPassword)
                Button("Submit") {
                    isSubmitted = attemptSubmit()
                    showingPopover = true
                }
                .popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }
                        .accessibilityIdentifier(Constants.POPOVER)
                }
                Spacer()
                NavigationLink(destination: LoginView(user.getValue())) {
                    if (isSubmitted) {
                        Text("Next")
                    }
                }
                Spacer()
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
            
            let userDetails = UserDetails()
            userDetails.userID = String(userIdentifierOptional!)
            user.setValue(userDetails)
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
