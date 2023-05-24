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
    
    
    var body: some View {
        HStack {
            Spacer(minLength: 125)
            VStack {
                Spacer()
                TextField("Username or Email", text: $usernameOrEmail)
                Button("Request Reset") {
                    isRequested = attemptRequest()
                }.padding(.trailing, 120)
                Spacer()
                NavigationLink(destination: VerifyResetView()) {
                    if (isRequested) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                 .padding(.bottom, 25)
            }
        }
    }
    
    func attemptRequest() -> Bool {
        if DEBUG {
            return true
        } else {
            // TODO: Make API call
            return false
        }
    }
}

struct RequestResetView_Previews: PreviewProvider {
    static var previews: some View {
        RequestResetView()
    }
}
