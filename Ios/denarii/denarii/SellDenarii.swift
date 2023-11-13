//
//  SellDenarii.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct SellDenarii: View {
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    let lock: NSLock = NSLock()
    
    @State private var userDetails = UserDetails()
    @State private var showingSidebar = false
    @State private var amount: String = ""
    @State private var price: String = ""
    @State private var isSold: Bool = false
    @State private var isCancelled: Bool = false
    @State private var showingPopoverForSellDenarii = false
    @State private var showingPopoverForCancelSellDenarii = false
    @State private var goingPrice: Double = 0.0
    
    
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var successOrFailureForSellDenarii: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForCancelSellDenarii: ObservableString = ObservableString()
    @ObservedObject private var currentAsks: ObservableArray<DenariiAsk> = ObservableArray()
    @ObservedObject private var ownAsks: ObservableArray<DenariiAsk>   = ObservableArray()
    @ObservedObject private var boughtAsks : ObservableArray<DenariiAsk>  = ObservableArray()
    
    init() {
        getNewAsks()
        refreshCompletedTransactions()
        getOwnAsks()
        refreshAsksInEscrow()
        refreshGoingPrice()
    }

    init(_ user: UserDetails) {
        self.user.setValue(user)
        self.userDetails = self.user.getValue()
        getNewAsks()
        refreshCompletedTransactions()
        getOwnAsks()
        refreshAsksInEscrow()
        refreshGoingPrice()
    }
    
    func getNewAsks() {
        if !self.user.getValue().userID.isEmpty {
                 // unlock at end of scope
                 defer {
                     lock.unlock()
                 }
                 lock.lock()
                
                var newAsks: Array<DenariiAsk> = Array()
                
                let api = Config.api
                
                let responses = api.getPrices(Int(self.user.getValue().userID)!)
                
                for response in responses {
                    let newAsk = DenariiAsk(askID: response.askID, amount: Double(response.amount), askingPrice: Double(response.askingPrice), inEscrow: false, amountBought: 0.0, isSettled: false, seenBySeller: false, buyerId: "-1")
                    newAsks.append(newAsk)
                }
                
                self.currentAsks.setValue(newAsks)
            }
    }
    func refreshCompletedTransactions() {
        if !self.user.getValue().userID.isEmpty {
                // unlock at end of scope
                defer {
                    lock.unlock()
                }
                lock.lock()

                let api = Config.api
                
                let responses = api.pollForCompletedTransaction(Int(self.user.getValue().userID)!)
                
                if !responses.isEmpty {
                    var newBoughtAsks: Array<DenariiAsk> = Array()
                    
                    for response in responses {
                        var foundAsk = false
                        for boughtAsk in self.boughtAsks.getValue() {
                            if boughtAsk.askID == response.askID {
                                foundAsk = true
                            }
                        }
                        
                        let ask = DenariiAsk()
                        ask.askID = response.askID
                        ask.amount = response.amount
                        ask.askingPrice = response.askingPrice
                        ask.amountBought = response.amountBought
                        
                        if foundAsk {
                            sendMoneyToSeller(api, self.user.getValue().userID, ask)
                        } else {
                            newBoughtAsks.append(ask)
                        }
                    }
                    
                    self.boughtAsks.setValue(newBoughtAsks)
                }
        }
    }
    func getOwnAsks() {
        if !self.user.getValue().userID.isEmpty {

                // unlock at end of scope
                defer {
                    lock.unlock()
                }
                lock.lock()
                
                let api = Config.api
                
                var ownAsks: Array<DenariiAsk> = Array()
                
                let responses = api.getAllAsks(Int(self.user.getValue().userID)!)
                
                for response in responses {
                    let newAsk = DenariiAsk()
                    newAsk.askID = response.askID
                    newAsk.amount = response.amount
                    newAsk.askingPrice = response.askingPrice
                    
                    ownAsks.append(newAsk)
                }
                
                self.ownAsks.setValue(ownAsks)
        }
    }
    func refreshAsksInEscrow() {
        if !self.user.getValue().userID.isEmpty {

                // unlock at end of scope
                defer {
                    lock.unlock()
                }
                lock.lock()
                
                let api = Config.api
                
                let responses = api.pollForEscrowedTransaction(Int(self.user.getValue().userID)!)
                
                var escrowedAsks: Array<DenariiAsk> = Array()
                
                for response in responses {
                    
                    let escrowed = DenariiAsk()
                    escrowed.askID = response.askID
                    escrowed.amount = response.amount
                    escrowed.askingPrice = response.askingPrice
                    
                    escrowedAsks.append(escrowed)
                }
                
                self.boughtAsks.setValue(escrowedAsks)
            }
    }
    
    func refreshGoingPrice() {
        if !self.user.getValue().userID.isEmpty {
            
            // unlock at end of scope
            defer {
                lock.unlock()
            }
            lock.lock()
            
            var goingPrice = Double.greatestFiniteMagnitude
            for ask in self.currentAsks.getValue() {
                if goingPrice > ask.askingPrice {
                    goingPrice = ask.askingPrice
                }
            }
        }
    }
    
    
    
    var body: some View {
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                GeometryReader { geometry in
                    List {
                        VStack(alignment: .center) {
                            Text("Sell Denarii").font(.largeTitle)
                            Spacer()
                            Text("Going Price: \(goingPrice)")
                            Text("Asks").font(.title)
                            ScrollView(.vertical, showsIndicators: true) {
                                VStack {
                                    Grid {
                                        GridRow {
                                            Text("Amount")
                                            Text("Price")
                                        }
                                        /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                         */
                                        ForEach(self.currentAsks.getValue(), id: \.self) {ask in
                                            GridRow {
                                                Text("\(ask.amount)")
                                                Text("\(ask.askingPrice)")
                                            }
                                        }
                                    }
                                }
                            }
                            TextField("Amount", text: $amount)
                            TextField("Price", text: $price)
                            Button("Sell") {
                                isSold = attemptToSellDenarii()
                                showingPopoverForSellDenarii = true
                            }.popover(isPresented: $showingPopoverForSellDenarii) {
                                Text(successOrFailureForSellDenarii.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForSellDenarii = false
                                    }.accessibilityIdentifier(Constants.SELL_DENARII_POPOVER)
                            }.buttonStyle(BorderlessButtonStyle())
                            Text("Own Asks").font(.title)
                            ScrollView(.vertical, showsIndicators: true) {
                                VStack {
                                    Grid {
                                        GridRow {
                                            Text("Amount")
                                            Text("Price")
                                            Text("Cancel Ask")
                                        }
                                        /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                         */
                                        ForEach(self.ownAsks.getValue(), id: \.self) {ask in
                                            GridRow {
                                                Text("\(ask.amount)")
                                                Text("\(ask.askingPrice)")
                                                Button("Cancel Ask") {
                                                    isCancelled = attemptCancelSellDenarii(ask)
                                                    if isCancelled {
                                                        self.successOrFailureForCancelSellDenarii.setValue("Successfully cancelled an ask to sell denarii")
                                                    }
                                                    showingPopoverForCancelSellDenarii = true
                                                }.popover(isPresented: $showingPopoverForCancelSellDenarii) {
                                                    Text(successOrFailureForCancelSellDenarii.getValue())
                                                        .font(.headline)
                                                        .padding().onTapGesture {
                                                            showingPopoverForCancelSellDenarii = false
                                                        }.accessibilityIdentifier(Constants.CANCEL_SELL_DENARII_POPOVER)
                                                }.buttonStyle(BorderlessButtonStyle())
                                            }
                                        }
                                    }
                                }
                            }
                            Text("Bought Asks").font(.title)
                            ScrollView(.vertical, showsIndicators: true) {
                                VStack {
                                    Grid {
                                        GridRow {
                                            Text("Amount")
                                            Text("Price")
                                            Text("Amount Bought")
                                        }
                                        /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                         */
                                        ForEach(self.boughtAsks.getValue(), id: \.self) {ask in
                                            GridRow {
                                                Text("\(ask.amount)")
                                                Text("\(ask.askingPrice)")
                                                Text("\(ask.amountBought)")
                                            }
                                        }
                                    }
                                }
                            }
                        }.frame(
                            minWidth: geometry.size.width,
                            maxWidth: .infinity,
                            minHeight: geometry.size.height,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                    }.refreshable {
                        getNewAsks()
                        refreshCompletedTransactions()
                        getOwnAsks()
                        refreshAsksInEscrow()
                        refreshGoingPrice()
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
                            Text("Sell Denarii").font(.headline)
                            Spacer()
                            Text("Going Price: \(goingPrice)").font(.subheadline)
                            TextField("Amount", text: $amount)
                            TextField("Price", text: $price)
                            Button("Sell") {
                                isSold = attemptToSellDenarii()
                                showingPopoverForSellDenarii = true
                            }.popover(isPresented: $showingPopoverForSellDenarii) {
                                Text(successOrFailureForSellDenarii.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForSellDenarii = false
                                    }.accessibilityIdentifier(Constants.SELL_DENARII_POPOVER)
                            }.buttonStyle(BorderlessButtonStyle())
                            HStack {
                                Spacer()
                                VStack {
                                    Text("Asks").font(.subheadline)
                                    ScrollView(.vertical, showsIndicators: true) {
                                        VStack {
                                            Grid {
                                                GridRow {
                                                    Text("Amount").font(.caption)
                                                    Text("Price").font(.caption)
                                                }
                                                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                                 */
                                                ForEach(self.currentAsks.getValue(), id: \.self) {ask in
                                                    GridRow {
                                                        Text("\(ask.amount)").font(.caption)
                                                        Text("\(ask.askingPrice)").font(.caption)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer()
                                VStack {
                                    Text("Own Asks").font(.subheadline)
                                    ScrollView(.vertical, showsIndicators: true) {
                                        VStack {
                                            Grid {
                                                GridRow {
                                                    Text("Amount").font(.caption)
                                                    Text("Price").font(.caption)
                                                    Text("Cancel Ask").font(.caption)
                                                }
                                                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                                 */
                                                ForEach(self.ownAsks.getValue(), id: \.self) {ask in
                                                    GridRow {
                                                        Text("\(ask.amount)").font(.caption)
                                                        Text("\(ask.askingPrice)").font(.caption)
                                                        Button("Cancel Ask") {
                                                            isCancelled = attemptCancelSellDenarii(ask)
                                                            if isCancelled {
                                                                self.successOrFailureForCancelSellDenarii.setValue("Successfully cancelled an ask to sell denarii")
                                                            }
                                                            showingPopoverForCancelSellDenarii = true
                                                        }.font(.caption).popover(isPresented: $showingPopoverForCancelSellDenarii) {
                                                            Text(successOrFailureForCancelSellDenarii.getValue())
                                                                .font(.headline)
                                                                .padding().onTapGesture {
                                                                    showingPopoverForCancelSellDenarii = false
                                                                }.accessibilityIdentifier(Constants.CANCEL_SELL_DENARII_POPOVER)
                                                        }.buttonStyle(BorderlessButtonStyle())
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer()
                                VStack {
                                    Text("Bought Asks").font(.subheadline)
                                    ScrollView(.vertical, showsIndicators: true) {
                                        VStack {
                                            Grid {
                                                GridRow {
                                                    Text("Amount").font(.caption)
                                                    Text("Price").font(.caption)
                                                    Text("Amount Bought").font(.caption)
                                                }
                                                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                                 */
                                                ForEach(self.boughtAsks.getValue(), id: \.self) {ask in
                                                    GridRow {
                                                        Text("\(ask.amount)").font(.caption)
                                                        Text("\(ask.askingPrice)").font(.caption)
                                                        Text("\(ask.amountBought)").font(.caption)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer()
                                }
                            }
                        }.frame(
                            minWidth: geometry.size.width,
                            maxWidth: .infinity,
                            minHeight: geometry.size.height,
                            maxHeight: .infinity,
                            alignment: .topLeading
                        )
                    }.refreshable {
                        getNewAsks()
                        refreshCompletedTransactions()
                        getOwnAsks()
                        refreshAsksInEscrow()
                        refreshGoingPrice()
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
    }
    
    func attemptToSellDenarii() -> Bool {
        
        if Constants.DEBUG {
            self.successOrFailureForSellDenarii.setValue("Succeeded in making denarii ask in DEBUG mode")
            return true
        } else {
            // unlock at end of scope
            defer {
                lock.unlock()
            }
            lock.lock()
            
            let api = Config.api
            
            let userId = self.user.getValue().userID
            
            if userId.isEmpty {
                self.successOrFailureForSellDenarii.setValue("Failed to make denarii ask")
                return false
            }
            
            if hasCreditCardInfo(api, userId) && isVerified(api, userId) {
                
                if amount.isEmpty {
                    self.successOrFailureForSellDenarii.setValue("Need to set an amount to sell")
                    return false
                }
                
                if price.isEmpty {
                    self.successOrFailureForSellDenarii.setValue("Need to set a price to sell at")
                    return false
                }
                
                let responses = api.makeDenariiAsk(Int(userId)!, Double(amount)!, Double(price)!)
                
                
                if responses.isEmpty {
                    self.successOrFailureForSellDenarii.setValue("Failed to make denarii ask server failure")
                    return false
                } else {
                    self.successOrFailureForSellDenarii.setValue("Successfully made a denarii ask")
                    return true
                }
            } else {
                self.successOrFailureForSellDenarii.setValue("Failed to sell denarii. No credit card info or not verified")
                return false
            }
        }
    }
    
    func hasCreditCardInfo(_ api: API, _ userId: String) -> Bool {
        
        let responses = api.hasCreditCardInfo(Int(userId)!)
        
        if !responses.isEmpty {
            let onlyResponse = responses.first!
            
            if onlyResponse.responseCode != 200 {
                return false
            }
            
            return onlyResponse.hasCreditCardInfo
        } else {
            return false
        }
    }
    
    func isVerified(_ api: API, _ userId: String) -> Bool {
        let responses = api.isAVerifiedPerson(Int(userId)!)
        
        if responses.isEmpty {
            return false
        }
        
        let onlyResponse = responses.first!
        
        if onlyResponse.responseCode != 200 {
            return false
        }
        
        return onlyResponse.verificationStatus == "is_verified"
    }
    
    func attemptCancelSellDenarii(_ ask: DenariiAsk) -> Bool {
        if Constants.DEBUG {
            self.successOrFailureForCancelSellDenarii.setValue("Succeeded in cancelling denarii ask in DEBUG mode")
            return true
        } else {
            // unlock at end of scope
            defer {
                lock.unlock()
            }
            lock.lock()
            
            let api = Config.api
            
            let userId = self.user.getValue().userID
            
            if userId.isEmpty {
                self.successOrFailureForCancelSellDenarii.setValue("Failed to cancel denarii ask")
                return false
            }
            
            let responses = api.cancelAsk(Int(userId)!, ask.askID)
            
            if responses.isEmpty {
                self.successOrFailureForCancelSellDenarii.setValue("Failed to cancel ask. Server side error")
                return false
            } else {
                self.successOrFailureForCancelSellDenarii.setValue("Successfully cancelled denarii ask")
                return true
            }
        }
    }
    
    func sendMoneyToSeller(_ api: API, _ userId: String, _ ask: DenariiAsk) {
        
        // TODO: other currencies
        let _ = api.sendMoneyToSeller(Int(userId)!, ask.amount, "usd")
        
        // TODO log whether the request succeeded.
    }
}

#Preview {
    SellDenarii()
}
