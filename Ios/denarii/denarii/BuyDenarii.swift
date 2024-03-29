//
//  BuyDenarii.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import SwiftUI

struct BuyDenarii: View {
    
    @Environment(\.verticalSizeClass) var verticalSizeClass: UserInterfaceSizeClass?
    @Environment(\.horizontalSizeClass) var horizontalSizeClass: UserInterfaceSizeClass?
    
    let lock: NSLock = NSLock()
    
    @State private var showingSidebar = false
    @State private var amount: String = ""
    @State private var price: String = ""
    @State private var failIfFullAmountIsntMet = true
    @State private var buyRegardlessOfPrice = false
    @State private var isBought: Bool = false
    @State private var isCancelled: Bool = false
    @State private var showingPopoverForBuyDenarii = false
    @State private var showingPopoverForCancelBuyDenarii = false
    
    @ObservedObject private var successOrFailureForBuyDenarii: ObservableString = ObservableString()
    @ObservedObject private var successOrFailureForCancelBuyDenarii: ObservableString = ObservableString()
    @ObservedObject private var user: ObservableUser = ObservableUser()
    @ObservedObject private var queuedBuys: ObservableArray<DenariiAsk> = ObservableArray()
    @ObservedObject private var currentAsks: ObservableArray<DenariiAsk> = ObservableArray()
    
    init() {
        getNewAsks()
        refreshSettledTransactions()
    }

    init(_ user: UserDetails) {
        self.user.setValue(user)

        getNewAsks()
        refreshSettledTransactions()
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
    
    func refreshSettledTransactions() {
        if !self.user.getValue().userID.isEmpty {

                 // unlock at end of scope
                 defer {
                     lock.unlock()
                 }
                 lock.lock()
                
                var remainingBuys: Array<DenariiAsk> = Array()
                 
                 var buysToReverse: Array<DenariiAsk> = Array()
                
                let api = Config.api
                 
                 for buy in self.queuedBuys.getValue() {
                     let responses = api.isTransactionSettled(Int(self.user.getValue().userID)!, buy.askID)
                     
                     if responses.isEmpty {
                         buysToReverse.append(buy)
                         continue
                     }
                     
                     let onlyResponse = responses.first!
                     
                     if onlyResponse.responseCode != 200 {
                         // TODO do we reverse if the server isnt responding, probably not?
                     } else {
                         // Explicitly keep any buys even that are not settled.
                         if onlyResponse.transactionWasSettled {
                             // Do not keep the buy if it was settled
                         } else {
                             remainingBuys.append(buy)
                         }
                     }
                 }
                 
                 for buy in buysToReverse {
                     let didReverse = completelyReverseTransaction(api, self.user.getValue().userID, buy)
                     
                     if !didReverse {
                         remainingBuys.append(buy)
                     }
                 }
                 
                 self.queuedBuys.setValue(remainingBuys)
            }
    }

    var body: some View {
        if horizontalSizeClass == .compact && verticalSizeClass == .regular {
            ZStack {
                GeometryReader { geometry in
                    List {
                        VStack(alignment: .center) {
                            Text("Buy Denarii").font(.largeTitle)
                            Spacer()
                            TextField("Amount", text: $amount).padding(.top).padding(.top)
                            TextField("Price", text: $price)
                            VStack {
                                Toggle("Buy regardless of price", isOn: $buyRegardlessOfPrice)
                            }.toggleStyle(.switch)
                            VStack {
                                Toggle("Fail if full amount isnt met", isOn: $failIfFullAmountIsntMet)
                            }.toggleStyle(.switch)
                            Button("Buy") {
                                isBought = attemptBuy()
                                if isBought {
                                    self.successOrFailureForBuyDenarii.setValue("Successfully bought some denarii")
                                }
                                showingPopoverForBuyDenarii = true
                            }.popover(isPresented: $showingPopoverForBuyDenarii) {
                                Text(successOrFailureForBuyDenarii.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForBuyDenarii = false
                                    }.accessibilityIdentifier(Constants.BUY_DENARII_POPOVER)
                            }.buttonStyle(BorderlessButtonStyle())
                            Spacer()
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
                            Spacer()
                            Text("Queued Buys").font(.title)
                            ScrollView(.vertical, showsIndicators: true) {
                                VStack {
                                    Grid {
                                        GridRow {
                                            Text("Amount")
                                            Text("Price")
                                            Text("Amount Bought")
                                            Text("Cancel Buy")
                                        }
                                        /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                         */
                                        ForEach(self.queuedBuys.getValue(), id: \.self) {buy in
                                            GridRow {
                                                Text("\(buy.amount)")
                                                Text("\(buy.askingPrice)")
                                                Text("\(buy.amountBought)")
                                                Button("Cancel") {
                                                    isCancelled = attemptCancel(buy)
                                                    if isCancelled {
                                                        self.successOrFailureForCancelBuyDenarii.setValue("Successfully cancelled an ask to buy denarii")
                                                    }
                                                    showingPopoverForCancelBuyDenarii = true
                                                }.popover(isPresented: $showingPopoverForCancelBuyDenarii) {
                                                    Text(successOrFailureForCancelBuyDenarii.getValue())
                                                        .font(.headline)
                                                        .padding().onTapGesture {
                                                            showingPopoverForCancelBuyDenarii = false
                                                        }.accessibilityIdentifier(Constants.CANCEL_BUY_DENARII_POPOVER)
                                                }.buttonStyle(BorderlessButtonStyle())
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
                        refreshSettledTransactions()
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
            Text("iPhone Landscape").font(.largeTitle)
        }
        else if horizontalSizeClass == .regular && verticalSizeClass == .regular {
            Text("iPad Portrait/Landscape").font(.largeTitle)
        } else if horizontalSizeClass == .compact && verticalSizeClass == .compact {
            ZStack {
                GeometryReader { geometry in
                    List {
                        VStack(alignment: .center) {
                            Spacer()
                            Text("Buy Denarii").font(.largeTitle)
                            Spacer()
                            HStack {
                                TextField("Amount", text: $amount)
                                TextField("Price", text: $price)
                                VStack {
                                    Toggle("Buy regardless of price", isOn: $buyRegardlessOfPrice).font(.caption)
                                }.toggleStyle(.switch)
                                VStack {
                                    Toggle("Fail if full amount isnt met", isOn: $failIfFullAmountIsntMet).font(.caption)
                                }.toggleStyle(.switch)
                            }
                            Spacer()
                            Button("Buy") {
                                isBought = attemptBuy()
                                if isBought {
                                    self.successOrFailureForBuyDenarii.setValue("Successfully bought some denarii")
                                }
                                showingPopoverForBuyDenarii = true
                            }.popover(isPresented: $showingPopoverForBuyDenarii) {
                                Text(successOrFailureForBuyDenarii.getValue())
                                    .font(.headline)
                                    .padding().onTapGesture {
                                        showingPopoverForBuyDenarii = false
                                    }.accessibilityIdentifier(Constants.BUY_DENARII_POPOVER)
                            }.buttonStyle(BorderlessButtonStyle())
                            Spacer()
                            HStack {
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
                                    Text("Queued Buys").font(.subheadline)
                                    ScrollView(.vertical, showsIndicators: true) {
                                        VStack {
                                            Grid {
                                                GridRow {
                                                    Text("Amount").font(.caption)
                                                    Text("Price").font(.caption)
                                                    Text("Amount Bought").font(.caption)
                                                    Text("Cancel Buy").font(.caption)
                                                }
                                                /* See https://stackoverflow.com/questions/67977092/swiftui-initialzier-requires-string-conform-to-identifiable
                                                 */
                                                ForEach(self.queuedBuys.getValue(), id: \.self) {buy in
                                                    GridRow {
                                                        Text("\(buy.amount)").font(.caption).font(.caption)
                                                        Text("\(buy.askingPrice)").font(.caption).font(.caption)
                                                        Text("\(buy.amountBought)").font(.caption).font(.caption)
                                                        Button("Cancel") {
                                                            isCancelled = attemptCancel(buy)
                                                            if isCancelled {
                                                                self.successOrFailureForCancelBuyDenarii.setValue("Successfully cancelled an ask to buy denarii")
                                                            }
                                                            showingPopoverForCancelBuyDenarii = true
                                                        }.font(.caption).popover(isPresented: $showingPopoverForCancelBuyDenarii) {
                                                            Text(successOrFailureForCancelBuyDenarii.getValue())
                                                                .font(.subheadline)
                                                                .padding().onTapGesture {
                                                                    showingPopoverForCancelBuyDenarii = false
                                                                }.accessibilityIdentifier(Constants.CANCEL_BUY_DENARII_POPOVER)
                                                        }.buttonStyle(BorderlessButtonStyle())
                                                    }
                                                }
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
                        refreshSettledTransactions()
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
            Text("Who knows").font(.largeTitle)
        }
    }

    func attemptCancel(_ buy: DenariiAsk) -> Bool {
        // unlock at end of scope
        defer {
            lock.unlock()
        }
        lock.lock()
        
        if Constants.DEBUG {
            self.successOrFailureForCancelBuyDenarii.setValue("Cancelled buy of denarii in DEBUG mode")
            return true
        } else {
            
            let api = Config.api
            
            let userId = self.user.getValue().userID
            if userId.isEmpty {
                self.successOrFailureForCancelBuyDenarii.setValue("Failed to cancel buy of denarii. Server side error")
                return false
            }
            
            var arrayOfSingleBuy: Array<DenariiAsk> = Array()
            arrayOfSingleBuy.append(buy)
            
            let arrayOfFailedSingleBuy = self.tryToCancelBuys(api, userId, arrayOfSingleBuy)
            
            if arrayOfFailedSingleBuy.isEmpty {
                self.successOrFailureForCancelBuyDenarii.setValue("Successfully canclled a buy of denarii")
                return true
            } else {
                self.successOrFailureForCancelBuyDenarii.setValue("Failed to cancel buy of denarii.  ")
                return false
            }
            
        }
    }
    
    func attemptBuy() -> Bool {
        
        // unlock at end of scope
        defer {
            lock.unlock()
        }
        lock.lock()
        
        if Constants.DEBUG {
            self.successOrFailureForBuyDenarii.setValue("Bought denarii in DEBUG mode")
            return true
        } else {
            let api = Config.api
            
            let userId = self.user.getValue().userID
            if userId.isEmpty {
                self.successOrFailureForBuyDenarii.setValue("Failed to buy denarii. Server side error")
                return false
            }
            
            if hasCreditCardInfo(api, userId) && isVerified(api, userId){
                
                var invalid_fields: Array<String> = Array()
                
                if !is_valid_input(amount, Constants.Patterns.double) {
                    invalid_fields.append(Constants.Params.amount)
                }
                
                if !is_valid_input(price, Constants.Patterns.double) {
                    invalid_fields.append(Constants.Params.bidPrice)
                }
                
                if !invalid_fields.isEmpty {
                    successOrFailureForBuyDenarii.setValue("Invalid fields: \(invalid_fields)")
                    return false
                }
                
                let asksBought = tryToBuyDenarii(api, userId)
                
                if !asksBought.isEmpty {
                    
                    if tryToGetMoneyFromBuyer(api, userId) {
                        
                        var anyAskFailed = false
                        var asksToReverse: Array<DenariiAsk> = Array()
                        
                        for ask in asksBought {
                            
                            let newAsk = tryToTransferDenarii(api, userId, ask)
                            if  !newAsk.askID.isEmpty {
                                
                                var updatedBuys = self.queuedBuys.getValue()
                                updatedBuys.append(newAsk)
                                
                                self.queuedBuys.setValue(updatedBuys)
                                
                                asksToReverse.append(newAsk)
                                return true
                                
                            } else {
                                anyAskFailed = true
                                break
                            }
                        }
                        
                        if anyAskFailed {
                            
                            let failedReversals = tryToReverseTransactions(api, userId, asksToReverse)
                            
                            for failed in failedReversals {
                                self.successOrFailureForBuyDenarii.setValue("Create a new support ticket with id \(failed.askID) because it failed to reverse")
                            }
                                
                            return false
                        }
                        
                    } else {
                        self.successOrFailureForBuyDenarii.setValue("Failed to get money from buyer.")
                       let failedCancels = tryToCancelBuys(api, userId, asksBought)
                        
                        for ask in failedCancels {
                            self.successOrFailureForBuyDenarii.setValue("Create a  new support ticket with id \(ask.askID) because it failed to cancel")
                        }
                        return false
                    }
                    
                } else {
                    self.successOrFailureForBuyDenarii.setValue("Failed to buy denarii from other user.")
                    return false
                }
                
            } else {
                self.successOrFailureForBuyDenarii.setValue("Failed to buy denarii. No credit card info or not verified")
                return false
            }
        }
        
        return false
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
    
    func tryToBuyDenarii(_ api: API, _ userId: String) ->  Array<DenariiAsk> {
        
        if amount.isEmpty || price.isEmpty {
            return Array()
        }
        
        let responses = api.buyDenarii(Int(userId)!, Double(amount)!, Double(price)!, buyRegardlessOfPrice, failIfFullAmountIsntMet)
        
        if !responses.isEmpty {
            var asks: Array<DenariiAsk> = Array()
            
            for response in responses {
                let ask: DenariiAsk = DenariiAsk()
                ask.askID = response.askID
                asks.append(ask)
            }
            
            return asks
        }
        
        return Array()
    }
    
    func tryToGetMoneyFromBuyer(_ api: API, _ userId: String) -> Bool {
        
        if amount.isEmpty {
            return false
        }
        
        // TODO do more currencies
        let responses = api.getMoneyFromBuyer(Int(userId)!, Double(amount)!, "usd")
        
        if responses.isEmpty {
            return false
        }
        
        let firstResponse = responses.first!
        
        return firstResponse.responseCode == 200
    }
    
    func tryToCancelBuys(_ api: API, _ userId: String, _ asksBought: Array<DenariiAsk>) -> Array<DenariiAsk> {
        
        var failedCancellations: Array<DenariiAsk> = Array()
        
        var successfulCancellations: Array<DenariiAsk> = Array()
        
        for ask in asksBought {
            let responses = api.cancelBuyOfAsk(Int(userId)!, ask.askID)
            
            if responses.isEmpty {
                failedCancellations.append(ask)
            } else {
                successfulCancellations.append(ask)
            }
        }
        
        var newQueuedBuys: Array<DenariiAsk> = Array()
        
        for ask in queuedBuys.getValue() {
            var successfullyCancelledAsk = false
            for successfulCancels in successfulCancellations {
                if ask.askID == successfulCancels.askID {
                    successfullyCancelledAsk = true
                    break
                }
            }
            
            // If the ask is not cancelled successfully then it sits in the queue
            if !successfullyCancelledAsk {
                newQueuedBuys.append(ask)
            }
        }
        
        self.queuedBuys.setValue(newQueuedBuys)
        
        return failedCancellations
    }
    
    func tryToReverseTransactions(_ api: API, _ userId: String, _ asksToReverse: Array<DenariiAsk>) ->  Array<DenariiAsk> {
        
        var failedReversals: Array<DenariiAsk> = Array()
        
        for ask in asksToReverse {
            // TODO do other currencies
            let responses = api.sendMoneyBackToBuyer(Int(userId)!, ask.amountBought, "usd")
            
            
            if responses.isEmpty {
                failedReversals.append(ask)
            } else {
                var singleAskArray: Array<DenariiAsk> =
                Array()
                singleAskArray.append(ask)
                let failedCancels = tryToCancelBuys(api, userId, singleAskArray)
                
                if !failedCancels.isEmpty {
                    failedReversals.append(ask)
                }
            }
        }

        return failedReversals
    }
    
    func completelyReverseTransaction(_ api: API, _ userId: String, _ buy: DenariiAsk) -> Bool {
        let responses = api.transferDenariiBackToSeller(Int(userId)!, buy.askID)
        
        if responses.isEmpty {
            self.successOrFailureForBuyDenarii.setValue("Failed to completely reverse transaction. File a support ticket with this id \(buy.askID)")
            self.showingPopoverForBuyDenarii = true
            return false
        }
        
        var asksToReverse: Array<DenariiAsk> = Array()
        asksToReverse.append(buy)
        
        let emptyAskIfFailed = self.tryToReverseTransactions(api, userId, asksToReverse)
        
        if emptyAskIfFailed.isEmpty {
            self.successOrFailureForBuyDenarii.setValue("Failed to completely reverse transaction. File a support ticket with this id \(buy.askID)")
            self.showingPopoverForBuyDenarii = true
            return false
        } else {
            return true
        }
    }
    
    func tryToTransferDenarii(_ api: API, _ userId: String, _ ask: DenariiAsk) -> DenariiAsk {
        
        
        let responses = api.transferDenarii(Int(userId)!, ask.askID)
        
        if responses.isEmpty {
            return DenariiAsk()
        }
        
        let onlyResponse = responses.first!
        
        if onlyResponse.responseCode != 200 {
            return DenariiAsk()
        }
        
        let askWithMoreDetails = tryToGetCurrentAsk(api, userId, ask)
        
        let newAsk: DenariiAsk = DenariiAsk()
        newAsk.askID = ask.askID
        newAsk.amountBought = onlyResponse.amountBought
        newAsk.amount = askWithMoreDetails.amount
        newAsk.askingPrice = askWithMoreDetails.askingPrice
        
        return newAsk
    }
    
    func tryToGetCurrentAsk(_ api: API, _ userId: String, _ ask: DenariiAsk) -> DenariiAsk {
        
        for otherAsk in self.currentAsks.getValue() {
            if otherAsk.askID == ask.askID {
                return otherAsk
            }
        }
        
        let responses = api.getAskWithIdentifier(Int(userId)!, ask.askID)
        
        if responses.isEmpty {
            return DenariiAsk()
        }
        
        let onlyResponse = responses.first!
        
        if onlyResponse.responseCode != 200 {
            return DenariiAsk()
        }
        
        let newAsk = DenariiAsk()
        
        newAsk.askID = onlyResponse.askID
        newAsk.amount = onlyResponse.amount
        newAsk.askingPrice = onlyResponse.askingPrice
        
        return newAsk
    }
}

#Preview {
    BuyDenarii()
}
