//
//  BuyDenarii.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct BuyDenarii: View {
    
    @State private var amount: String = ""
    @State private var price: String = ""
    @State private var failIfFullAmountIsntMet = true
    @State private var buyRegardlessOfPrice = false
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    
    private let failIfFullAmountIsntMetUIAction: UIAction = UIAction(title: "Fail if fullAmountIsntMet") { action in
        print("Hello")
    }

    var body: some View {
        VStack(alignment: .center) {
            Text("Buy Denarii").font(.largeTitle)
            Spacer()
            TextField("Amount", text: $amount)
            TextField("Price", text: $price)
            VStack {
                Toggle("Buy regardless of price", isOn: $buyRegardlessOfPrice)
            }.toggleStyle(.switch)
            VStack {
                Toggle("Fail if full amount isnt met", isOn: $failIfFullAmountIsntMet)
            }.toggleStyle(.switch)
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
            Text("Asks").font(.title)
            Grid {
                GridRow {
                    Text("Amount")
                    Text("Price")
                }
            }
            Spacer()
            Text("Queued Buys").font(.title)
            Grid {
                GridRow {
                    Text("Amount")
                    Text("Price")
                    Text("Amount Bought")
                    Text("Cancel Buy")
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
    BuyDenarii()
}
