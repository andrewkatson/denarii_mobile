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
    
    var body: some View {
        HStack {
            Spacer(minLength: 125)
            VStack {
                Spacer()
                TextField("Reset id", text: $resetId)
                    .padding(.leading, 40)
                Button("Verify Reset") {
                    let verified = attemptVerifyReset()
                    isVerified = verified
                }.padding(.trailing, 120)
                Spacer()
                NavigationLink(destination: ResetPasswordView()) {
                    if (true) {
                        Text("Next")
                    }
                }.padding(.leading, 100)
                    .padding(.bottom, 25)
                
            }
        }
    }
    
    func attemptVerifyReset() -> Bool {
        return true
    }
}

struct VerifyResetView_Previews: PreviewProvider {
    static var previews: some View {
        VerifyResetView()
    }
}
