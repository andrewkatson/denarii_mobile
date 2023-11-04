//
//  Verification.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct Verification: View {
    @State private var firstName: String = ""
    @State private var middleName: String = ""
    @State private var lastName: String = ""
    @State private var email: String = ""
    @State private var dob: String = ""
    @State private var ssn: String = ""
    @State private var zipcode: String = ""
    @State private var phone: String = ""
    @State private var workCity: String = ""
    @State private var workState: String = ""
    @State private var workCountry: String = ""
    @State private var status: String = ""
    
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var keepRefreshing: ObservableBool = ObservableBool()
    
    init() {
        refreshStatus()
    }

    init(_ user: UserDetails) {
        self.user.setValue(user)
        refreshStatus()
    }
    
    func refreshStatus() {
        if !self.user.getValue().userID.isEmpty {
            Timer.scheduledTimer(withTimeInterval: 5, repeats: true) { timer in

                if self.keepRefreshing.getValue()  {
                    timer.invalidate()
                }
                
                let api = Config.api
                
                
            }
        }
    }

    var body: some View {
        VStack(alignment: .center) {
            Text("Verification").font(.largeTitle)
            Spacer()
            Text("Status: \(status)")
            TextField("First Name", text: $firstName)
            TextField("Middle Initial", text: $middleName)
            TextField("Last Name", text: $lastName)
            TextField("Email", text: $email)
            TextField("Date of Birth", text: $dob)
            TextField("Social Security Number", text: $ssn)
            TextField("Zipcode", text: $zipcode)
            TextField("Phone Number", text: $phone)
            TextField("Work City", text: $workCity)
            TextField("Work State", text: $workState)
            TextField("Work Country", text: $workCountry)
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
            Spacer()
            HStack {
                NavigationLink(destination: OpenedWalletView(user.getValue())) {
                    Text("Wallet")
                }
                NavigationLink(destination: BuyDenarii(user.getValue())) {
                    Text("Buy Denarii")
                }
                NavigationLink(destination: SellDenarii(user.getValue())) {
                    Text("Sell Denarii")
                }
                NavigationLink(destination: CreditCardInfo(user.getValue())) {
                    Text("Credit Card")
                }
                NavigationLink(destination: UserSettings(user.getValue())) {
                    Text("Settings")
                }
            }
            Spacer()
        }
    }
    
    func attemptSubmit() -> Bool {
        return false
    }
}

#Preview {
    Verification()
}
