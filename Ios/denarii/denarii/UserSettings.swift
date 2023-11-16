//
//  UserSettings.swift
//  denarii
//
//  Created by Andrew Katson on 10/25/23.
//

import SwiftUI

struct UserSettings: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    @State private var showingSidebar = false
    @State private var logout: Bool = false
    @State private var deletedAccount: Bool = false
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
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                VStack(alignment: .center) {
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
                                
                                if isLoggedOut {
                                    logout = true
                                }
                            }.accessibilityIdentifier(Constants.LOGOUT_POPOVER)
                    }.background {
                        if logout && showingPopoverForLogout == false {
                            NavigationLink("Logout") {
                                EmptyView()
                            }.navigationDestination(isPresented: $logout) {
                                ContentView()
                            }
                        }
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
                                
                                if isDeleted {
                                    deletedAccount = true
                                }
                            }.accessibilityIdentifier(Constants.DELETE_ACCOUNT_POPOVER)
                    }.background {
                        if deletedAccount && showingPopoverForDeleteAccount == false {
                            NavigationLink("Delete Account") {
                                EmptyView()
                            }.navigationDestination(isPresented: $deletedAccount) {
                                ContentView()
                            }
                        }
                    }
                    Spacer()
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
                VStack(alignment: .center) {
                    Text("Settings").font(.headline)
                    Spacer()
                    HStack {
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
                                    
                                    if isLoggedOut {
                                        logout = true
                                    }
                                }.accessibilityIdentifier(Constants.LOGOUT_POPOVER)
                        }.background {
                            if logout && showingPopoverForLogout == false {
                                NavigationLink("Logout") {
                                    EmptyView()
                                }.navigationDestination(isPresented: $logout) {
                                    ContentView()
                                }
                            }
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
                                    
                                    if isDeleted {
                                        deletedAccount = true
                                    }
                                }.accessibilityIdentifier(Constants.DELETE_ACCOUNT_POPOVER)
                        }.background {
                            if deletedAccount && showingPopoverForDeleteAccount == false {
                                NavigationLink("Delete Account") {
                                    EmptyView()
                                }.navigationDestination(isPresented: $deletedAccount) {
                                    ContentView()
                                }
                            }
                        }
                        Spacer()
                    }
                    Spacer()
                }
                Sidebar(isSidebarVisible: $showingSidebar, userDetails: self.$user.value)
            }
        } else {
            Text("Who knows")
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
