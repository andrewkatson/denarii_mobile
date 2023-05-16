//
//  LoginOrRegisterView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct LoginOrRegisterView: View {
    var body: some View {
        VStack {
            Spacer(minLength: 300)
            NavigationLink(destination: LoginView()) {
                Text("Login")
            }
            Spacer()
            NavigationLink(destination: RegisterView()) {
                Text("Register")
                
            }
            Spacer(minLength: 300)
        }
    }
}

struct LoginOrRegisterView_Previews: PreviewProvider {
    static var previews: some View {
        LoginOrRegisterView()
    }
}
