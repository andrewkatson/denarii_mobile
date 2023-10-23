//
//  SellDenarii.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SellDenarii: View {
    
    @State private var amount: String = ""
    @State private var price: String = ""
    @State private var isSubmitted: Bool = false
    @State private var showingPopover = false
    
    @ObservedObject private var goingPrice: ObservableString = ObservableString("0.00")
    @ObservedObject private var successOrFailure: ObservableString = ObservableString()
    
    
    var body: some View {
        VStack(alignment: .center) {
            Text("Sell Denarii").font(.largeTitle)
            Spacer()
            Text("Going Price: \(goingPrice.getValue())")
            Text("Asks").font(.title)
            Grid {
                GridRow {
                    Text("Amount")
                    Text("Price")
                }
            }
            TextField("Amount", text: $amount)
            TextField("Price", text: $price)
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
            Text("Own Asks").font(.title)
            Grid {
                GridRow {
                    Text("Amount")
                    Text("Price")
                    Text("Cancel Ask")
                }
            }
            Text("Bought Asks").font(.title)
            Grid {
                GridRow {
                    Text("Amount")
                    Text("Price")
                    Text("Amount Bought")
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
    SellDenarii()
}
