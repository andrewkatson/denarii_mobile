//
//  Verification.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct Verification: View {
    @Environment(\.horizontalSizeClass) var sizeClass
    
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
    @State private var status: String = "Status: Not Verified Yet"
    
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
                
                let responses = api.isAVerifiedPerson(Int(self.user.getValue().userID)!)
                
                if responses.isEmpty {
                    status = formatStatus("unknown")
                } else {
                    let response = responses.first!
                    
                    if response.responseCode != 200 {
                        status = formatStatus("unknown")
                    } else {
                        status = formatStatus(response.verificationStatus)
                    }
                }
            }
        }
    }
    
    func formatStatus(_ status: String) -> String {
        if status == "is_verified" {
            return "Status: Verified"
        }
        else if status == "failed_verification" {
            return "Status: Failed Verification"
        }
        else if status == "verification_pending" {
            return "Status: Verification Pending"
        }
        else if status == "is_not_verified" {
            return "Status: Not Verified Yet"
        }
        else {
            return "Status: Unknown"
        }
    }

    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical, showsIndicators: true) {
                VStack(alignment: .center) {
                    Text("Verification").font(.largeTitle)
                    Spacer()
                    Text("\(status)")
                    if !status.contains("Status: Verfiied") {
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
                                }.accessibilityIdentifier(Constants.POPOVER)
                        }
                    }
                    if self.sizeClass == .compact {
                        Spacer()
                    }
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
                }.frame(width: proxy.size.width, height: proxy.size.height)
            }
        }
    }
    
    func attemptSubmit() -> Bool {
        if Constants.DEBUG {
            self.successOrFailure.setValue("Successfully verified in DEBUG mode")
            return true
        } else {
            
            if self.user.getValue().userID.isEmpty {
                successOrFailure.setValue("Failed to verify.")
                return false
            }
            
            let api = Config.api
            
            let responses = api.verifyIdentity(Int(self.user.getValue().userID)!, firstName, middleName, lastName, email, dob, ssn, zipcode, phone, formatWorkLocations(workCity, workState, workCountry))
            
            if responses.isEmpty {
                successOrFailure.setValue("Could not verify identity server error")
                return false
            }
                       
            successOrFailure.setValue("Successfully applied for verification. See status for details.")
            return true
        }
    }
    
    func formatWorkLocations(_ city: String, _ state: String, _ country: String) -> String {
        
        return "[{'country': \(country), 'state': \(state), 'city': \(city)}]"
    }
}

#Preview {
    Verification()
}
