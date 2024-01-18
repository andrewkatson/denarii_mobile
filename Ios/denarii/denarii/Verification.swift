//
//  Verification.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct Verification: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @State private var showingSidebar = false
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
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                GeometryReader { geometry in
                    List {
                        VStack(alignment: .center) {
                            Text("Verification").font(.largeTitle)
                            Text("\(status)")
                            VStack {
                                TextField("First Name", text: $firstName).padding(.top)
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
                                Spacer()
                                Button("Submit") {
                                    isSubmitted = attemptSubmit()
                                    showingPopover = true
                                }.popover(isPresented: $showingPopover) {
                                    Text(successOrFailure.getValue())
                                        .font(.headline)
                                        .padding().onTapGesture {
                                            showingPopover = false
                                        }.accessibilityIdentifier(Constants.POPOVER)
                                }.buttonStyle(BorderlessButtonStyle())
                            }.opacity(!status.contains("Status: Verified") ? 1 : 0)
                            Spacer()
                        }.frame(
                            minWidth: geometry.size.width,
                            maxWidth: .infinity,
                            minHeight: geometry.size.height,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                    }.refreshable {
                        refreshStatus()
                    }.listStyle(PlainListStyle())
                        .frame(
                            minWidth: 0,
                            maxWidth: .infinity,
                            minHeight: 0,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                }
                Sidebar(isSidebarVisible: $showingSidebar, userDetails: self.$user.value)
            }
        }
         else if horizontalSizeClass == .regular && verticalSizeClass == .compact {
             
             Text("iPhone Landscape")
         }
         else if horizontalSizeClass == .regular && verticalSizeClass == .regular {
             
             Text("iPad Portrait/Landscape")
         } else if horizontalSizeClass == .compact && verticalSizeClass == .compact {
             ZStack {
                 GeometryReader { geometry in
                     List {
                         VStack(alignment: .center) {
                             Text("Verification").font(.headline)
                             Spacer()
                             Text("\(status)").font(.subheadline)
                             VStack {
                                 HStack {
                                     TextField("First Name", text: $firstName)
                                     TextField("Middle Initial", text: $middleName)
                                     TextField("Last Name", text: $lastName)
                                 }
                                 HStack {
                                     TextField("Email", text: $email)
                                     TextField("Date of Birth", text: $dob)
                                     TextField("Social Security Number", text: $ssn)
                                 }
                                 HStack {
                                     TextField("Zipcode", text: $zipcode)
                                     TextField("Phone Number", text: $phone)
                                 }
                                 HStack {
                                     TextField("Work City", text: $workCity)
                                     TextField("Work State", text: $workState)
                                     TextField("Work Country", text: $workCountry)
                                 }
                                 Spacer()
                                 Button("Submit") {
                                     isSubmitted = attemptSubmit()
                                     showingPopover = true
                                 }.popover(isPresented: $showingPopover) {
                                     Text(successOrFailure.getValue())
                                         .font(.headline)
                                         .padding().onTapGesture {
                                             showingPopover = false
                                         }.accessibilityIdentifier(Constants.POPOVER)
                                 }.buttonStyle(BorderlessButtonStyle())
                             }.opacity(!status.contains("Status: Verified") ? 1 : 0)
                             Spacer()
                         }.frame(
                            minWidth: geometry.size.width,
                            maxWidth: .infinity,
                            minHeight: geometry.size.height,
                            maxHeight: .infinity,
                            alignment: .topLeading
                         )
                     }.refreshable {
                         refreshStatus()
                     }.listStyle(PlainListStyle())
                         .frame(
                            minWidth: 0,
                            maxWidth: .infinity,
                            minHeight: 0,
                            maxHeight: .infinity,
                            alignment: .topLeading
                         )
                 }
                 Sidebar(isSidebarVisible: $showingSidebar, userDetails: self.$user.value)
             }
         } else {
           Text("Who knows")
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
            
            var invalid_fields: Array<String> = Array()
            
            if !is_valid_input(firstName, Constants.Patterns.name) {
                invalid_fields.append(Constants.Params.firstName)
            }
            
            if !is_valid_input(middleName, Constants.Patterns.singleLetter) {
                invalid_fields.append(Constants.Params.middleName)
            }
            
            if !is_valid_input(lastName, Constants.Patterns.name) {
                invalid_fields.append(Constants.Params.lastName)
            }
            
            if !is_valid_input(email, Constants.Patterns.email) {
                invalid_fields.append(Constants.Params.email)
            }
            
            if !is_valid_input(dob, Constants.Patterns.slashDate) {
                invalid_fields.append(Constants.Params.dob)
            }
            
            if !is_valid_input(ssn, Constants.Patterns.digitsAndDashes) {
                invalid_fields.append(Constants.Params.ssn)
            }
            
            if !is_valid_input(zipcode, Constants.Patterns.digitsAndDashes) {
                invalid_fields.append(Constants.Params.zipcode)
            }
            
            if !is_valid_input(phone, Constants.Patterns.phoneNumber) {
                invalid_fields.append(Constants.Params.phone)
            }
            
            if !is_valid_input(formatWorkLocations(workCity, workState, workCountry), Constants.Patterns.jsonDictOfUpperAndLowerCaseChars) {
                invalid_fields.append(Constants.Params.workLocations)
            }
            
            if !invalid_fields.isEmpty {
                successOrFailure.setValue("Invalid fields: \(invalid_fields)")
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
