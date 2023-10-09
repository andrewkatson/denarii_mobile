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
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    @ObservedObject private var usernameOrEmail: ObservableString = ObservableString()
    
    init() {}

    init(_ userIdentifier: Int, _ usernameOrEmail: String) {
        self.userIdentifier.setValue(userIdentifier)
        self.usernameOrEmail.setValue(usernameOrEmail)
    }
    
    var body: some View {
        HStack {
            Spacer(minLength: 125)
            VStack {
                Spacer()
                TextField("Reset id", text: $resetId)
                    .padding(.leading, 40)
                Button("Verify Reset") {
                    isVerified = attemptVerifyReset()
                    showingPopover = true
                }.padding(.trailing, 120).popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }
                        .accessibilityIdentifier("Popover")
                }
                Spacer()
                NavigationLink(destination: ResetPasswordView(userIdentifier.getValue())) {
                    if (isVerified) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                    .padding(.bottom, 25)
                
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
            let api = Config().api
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
