//
//  CreditCardInfo.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct CreditCardInfo: View {
    
    @State private var number: String = ""
    @State private var expirationDateMonth: String = ""
    @State private var expirationDateYear: String = ""
    @State private var securityCode: String = ""

    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    @ObservedObject private var status: ObservableString = ObservableString("Not verified")
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Credit Card Info").font(.largeTitle)
            Spacer()
            Text("Status \(status.getValue())")
            TextField("Credit Card Number", text: $number)
            TextField("Expiration Date Month", text: $expirationDateMonth)
            TextField("Expiration Date Year", text: $expirationDateYear)
            TextField("Security Code", text: $securityCode)
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
        }
    }
    
    func attemptSubmit() -> Bool {
        return false
    }
}

#Preview {
    CreditCardInfo()
}
