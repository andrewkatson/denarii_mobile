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
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    init() {}
    
    init(_ userIdentifier: Int) {
        self.userIdentifier.setValue(userIdentifier)
    }
    
    var body: some View {
        HStack {
            Spacer()
            VStack {
                Spacer()
                TextField("Name", text: $username)
                TextField("Email", text: $email)
                SecureField("Password", text: $password).textContentType(.password)
                SecureField("Confirm Password", text: $confirmPassword).textContentType(.password)
                HStack {
                    Button("Submit") {
                        isSubmitted = attemptSubmit()
                        showingPopover = true
                    }.popover(isPresented: $showingPopover) {
                        Text(successOrFailure.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopover = false
                            }.accessibilityIdentifier("Popover")
                    }
                    .padding(.top, 15)
                    .padding(.bottom, 15)
                    .padding(.leading, 75)
                    Spacer()
                }
                HStack {
                    NavigationLink(destination: RequestResetView(userIdentifier.getValue())) {
                        Text("Forgot Password")
                    }.padding(.trailing, 70)
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
        if (password != confirmPassword) {
            successOrFailure.setValue("Failed to login becasue password is not the same as confirm password")
            return false
        }
        if Constants.DEBUG {
            successOrFailure.setValue("Logged In in DEBUG mode")
            return true
        } else {
            let api = Config().api
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
            userIdentifier.setValue(Int(response.userIdentifier)!)
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
