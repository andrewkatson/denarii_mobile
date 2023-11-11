//
//  ContentView.swift
//  denarii
//
//  Created by Andrew Katson on 5/14/23.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var coordinator = NavigationCoordinator()
    
    var body: some View {
        NavigationStack(path: $coordinator.path) {
            VStack {
                Spacer()
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
        }.environmentObject(coordinator)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
