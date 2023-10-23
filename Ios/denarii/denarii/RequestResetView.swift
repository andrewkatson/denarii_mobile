//
//  RequestResetView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct RequestResetView: View {
    
    @State private var usernameOrEmail: String = ""
    @State private var isRequested: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var userIdentifier: ObservableInt = ObservableInt()
    
    init() {}
    
    init(_ userIdentifier: Int) {
        self.userIdentifier.setValue(userIdentifier)
    }
    
    var body: some View {
            VStack(alignment: .center) {
                Text("Request Reset").font(.largeTitle)
                Spacer()
                TextField("Username or Email", text: $usernameOrEmail)
                Button("Request Reset") {
                    isRequested = attemptRequest()
                    showingPopover = true
                }.popover(isPresented: $showingPopover) {
                    Text(successOrFailure.getValue())
                        .font(.headline)
                        .padding().onTapGesture {
                            showingPopover = false
                        }
                        .accessibilityIdentifier("Popover")
                }
                Spacer()
                NavigationLink(destination: VerifyResetView(userIdentifier.getValue(), usernameOrEmail)) {
                    if (isRequested) {
                        Text("Next")
                    }
                }
            }
    }
    
    func attemptRequest() -> Bool {
        if Constants.DEBUG {
            successOrFailure.setValue("Requested password reset in DEBUG mode")
            return true
        } else {
            let api = Config.api
            let denariiResponses = api.requestPasswordReset(usernameOrEmail)
            if denariiResponses.isEmpty {
                successOrFailure.setValue("Failed to login there were no responses from server")
                return false
            }
            // We only expect one
            let response = denariiResponses.first!
            if response.responseCode != 200 {
                successOrFailure.setValue("Failed to request password reset due to a server side error")
                return false
            }
            successOrFailure.setValue("Requested password reset")
            return true
        }
    }
}

struct RequestResetView_Previews: PreviewProvider {
    static var previews: some View {
        RequestResetView()
    }
}
