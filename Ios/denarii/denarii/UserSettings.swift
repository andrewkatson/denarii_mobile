//
//  UserSettings.swift
//  denarii
//
//  Created by Andrew Katson on 10/25/23.
//

import SwiftUI

struct UserSettings: View {
    @Environment(\.horizontalSizeClass) var sizeClass
    
    @State private var isDeleted = false
    @State private var isLoggedOut = false
    @State private var showingPopoverForDeleteAccount = false
    @State private var showingPopoverForLogout = false
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var successOrFailureForDeleteAccount: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForLogout: ObservableString = ObservableString()
    
    init() {}

    init(_ user: UserDetails) {
        self.user.setValue(user)
    }
    
    var body: some View {
        GeometryReader { proxy in
            ScrollView(.vertical, showsIndicators: true) {
                VStack(alignment: .center) {
                    Spacer()
                    Text("Settings").font(.largeTitle)
                    Spacer()
                    NavigationLink(destination: SupportTickets(user.getValue())) {
                        Text("Support Tickets")
                    }
                    Spacer()
                        Button("Logout") {
                            isLoggedOut = attemptLogout()
                            showingPopoverForLogout = true
                        }.popover(isPresented: $showingPopoverForLogout) {
                            Text(successOrFailureForLogout.getValue())
                                .font(.headline)
                                .padding().onTapGesture {
                                    showingPopoverForLogout = false
                                }.accessibilityIdentifier(Constants.LOGOUT_POPOVER)
                        }.navigationDestination(isPresented: $isLoggedOut) {
                            return ContentView()
                        }
                    Spacer()
                    
                    Button("Delete Account") {
                        isDeleted = attemptDeleteAccount()
                        showingPopoverForDeleteAccount = true
                    }.popover(isPresented: $showingPopoverForDeleteAccount) {
                        Text(successOrFailureForDeleteAccount.getValue())
                            .font(.headline)
                            .padding().onTapGesture {
                                showingPopoverForDeleteAccount = false
                            }.accessibilityIdentifier(Constants.DELETE_ACCOUNT_POPOVER)
                    }.navigationDestination(isPresented: $isDeleted) {
                        return ContentView()
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
                        NavigationLink(destination: Verification(user.getValue())) {
                            Text("Verification")
                        }
                        NavigationLink(destination: CreditCardInfo(user.getValue())) {
                            Text("Credit Card")
                        }
                    }
                    Spacer()
                }.frame(width: proxy.size.width, height: proxy.size.height)
            }
        }
    }
    
    func attemptLogout() -> Bool {
        if Constants.DEBUG {
            successOrFailureForDeleteAccount.setValue("Successfully logged out in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                successOrFailureForLogout.setValue("Failed to logout account")
                return false
            }
            
            let responses = api.logout(Int(self.user.getValue().userID)!)
            
            if responses.isEmpty {
                successOrFailureForLogout.setValue("Failed to logout account server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                successOrFailureForLogout.setValue("Failed to logout account server error")
                return false
            }
            
            successOrFailureForLogout.setValue("Successfully logout account")
            return true
        }
    }
    
    func attemptDeleteAccount() -> Bool {
        if Constants.DEBUG {
            successOrFailureForDeleteAccount.setValue("Successfully deleted account in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            if self.user.getValue().userID.isEmpty {
                successOrFailureForDeleteAccount.setValue("Failed to delete account")
                return false
            }
            
            let responses = api.deleteUser(Int(self.user.getValue().userID)!)
            
            if responses.isEmpty {
                successOrFailureForDeleteAccount.setValue("Failed to delete account server error")
                return false
            }
            
            let response = responses.first!
            
            if response.responseCode != 200 {
                successOrFailureForDeleteAccount.setValue("Failed to delete account server error")
                return false
            }
            
            successOrFailureForDeleteAccount.setValue("Successfully deleted account")
            return true
        }
    }
}

#Preview {
    UserSettings()
}
