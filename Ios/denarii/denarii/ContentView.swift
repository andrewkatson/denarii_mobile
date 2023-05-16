//
//  ContentView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct ContentView: View {
    var body: some View {
        NavigationView {
            VStack {
                Spacer(minLength: 300)
                Text("Welcome to Denarii!").font(.largeTitle)
                HStack {
                    Spacer()
                    VStack {
                        Spacer()
                        NavigationLink(destination: LoginOrRegisterView()) {
                            Text("Next")
                        }.padding(.trailing, 50)
                    }
                }
            }
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
