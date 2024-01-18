//
//  VerifyResetView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct VerifyResetView: View {
    
    @State private var resetId: String = ""
    @State private var isVerified: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var usernameOrEmail: ObservableString = ObservableString()
    
    init() {}

    init(_ user: UserDetails, _ usernameOrEmail: String) {
        self.user.setValue(user)
        self.usernameOrEmail.setValue(usernameOrEmail)
    }
    
    var body: some View {
            VStack(alignment: .center) {
                Text("Verify Request").font(.largeTitle)
                Spacer()
                TextField("Reset id", text: $resetId)
                Button("Verify Reset") {
                    isVerified = attemptVerifyReset()
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
                NavigationLink(destination: ResetPasswordView(user.getValue())) {
                    if (isVerified) {
                        Text("Next")
                    }
                }
        }
    }
    
    func attemptVerifyReset() -> Bool {
        
        if resetId == "" {
            successOrFailure.setValue("Failed to verify password reset because reset id was not set")
            return false
        }
        
        if Constants.DEBUG {
            successOrFailure.setValue("Verified password reset in DEBUG mode")
            return true
        } else {
            var invalid_fields: Array<String> = Array()
            
            if !is_valid_input(resetId, Constants.Patterns.resetId) {
                invalid_fields.append(Constants.Params.resetId)
            }
            
            if !invalid_fields.isEmpty {
                successOrFailure.setValue("Invalid fields: \(invalid_fields)")
                return false
            }
            
            let api = Config.api
            let denariiResponses = api.verifyReset(usernameOrEmail.getValue(), Int(resetId)!)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to verify password reset due to a server side error")
                return false
            }
            successOrFailure.setValue("Verified password reset")
            return true
        }
    }
}

struct VerifyResetView_Previews: PreviewProvider {
    static var previews: some View {
        VerifyResetView()
    }
}
