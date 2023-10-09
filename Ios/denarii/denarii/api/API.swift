//
//  API.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

protocol API {
    // Returns a DenariiResponse instance with a user identifier
    func getUserId(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with nothing in it
    func requestPasswordReset(_ usernameOrEmail: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with nothing in it
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with nothing in it
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with seed and address
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with address
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with seed and address
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with balance
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse instance with nothing in it
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with ask_id, asking_price, and amount
    func getPrices(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with ask_id
    func buyDenarii(_ userIdentifier: Int, _ amount: Double, _ bidPrice: Double, _ buyRegardlessOfPrice: Bool, _ failIfFullAmountIsntMet: Bool) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id and amount_bought
    func transferDenarii(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id, asking_price, and amount
    func makeDenariiAsk(_ userIdentifier: Int, _ amount: Double, _ askingPrice: Double) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id, asking_price, and amount
    func pollForCompletedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id
    func cancelAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with has_credit_card_info
    func hasCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func setCreditCardInfo(_ userIdentifier: Int, _ cardNumber: String, _ epirationDateMonth: String, _ expirationDateYear: String, _ securityCode: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func clearCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func getMoneyFromBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func sendMoneyToSeller(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id, and transaction_was_settled
    func isTransactionSettled(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func deleteUser(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id, amount, and amount_bought
    func getAskWithIdentifier(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with ask_id
    func transferDenariiBackToSeller(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func sendMoneyBackToBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with nothing in it
    func cancelBuyOfAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with verification_status
    func verifyIdentity(_ userIdentifier: Int, _ firstName: String, _ middleName: String, _ lastName: String, _ email: String, _ dob: String, _ ssn: String, _ zipCode: String, _ phone: String, _ workLocations: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with verification_status
    func isAVerifiedPerson(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with ask_id, amount, asking_price, and amount_bought
    func getAllAsks(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with ask_id, amount, asking_price, and amount_bought
    func getAllBuys(_ userIdentifier: Int) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with support_ticket_id, and creation_time_body
    func createSupportTicket(_ userIdentifier: Int, _ title: String, _ description: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with support_ticket_id, and updated_time_body
    func updateSupportTicket(_ userIdentifier: Int, _ supportTicketId: String, _ comment: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with support_ticket_id
    func deleteSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with support_ticket_id, author, title, description,
    // updated_time_body, creation_time_body, resolved
    func getSupportTickets(_ userIdentifier: Int, _ canBeResolved: Bool) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with support_ticket_id, author, title, description,
    // updated_time_body, creation_time_body, resolved
    func getSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with author, content, updated_time_body,
    // creation_time_body
    func getCommentsOnTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse>
    
    // Returns a DenariiResponse with support_ticket_id, and updated_time_body
    func resolveSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse>
    
    // Returns a list of DenariiResponse with ask_id, amount, asking_price, amount_bought
    func pollForEscrowedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse>
}

class RealAPI: API {
    
    private var urlBase: String = "https://denariimobilebackend.com"
    
    func makeApiCall(_ urlStr: String) -> Array<DenariiResponse> {
        
        let url = URL(string: urlStr)
        
        var denariiResponses = Array<DenariiResponse>()
        
        var responseCode = -1

        let task = URLSession.shared.dataTask(with: url!, completionHandler: { (data, response, error) in
            if error != nil {
                // TODO log here and pick a better error code
                responseCode = -1000
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse,
                      (200...299).contains(httpResponse.statusCode) else {
                let responseParsed = response as? HTTPURLResponse
                responseCode = responseParsed!.statusCode
              return
            }
            
            if let data = data,
                    var singleDenariiResponse = try? JSONDecoder().decode(DenariiResponse.self, from: data) {
                let responseParsed = response as? HTTPURLResponse
                singleDenariiResponse.responseCode = responseParsed!.statusCode
                denariiResponses.append(singleDenariiResponse)
            }
        })

        task.resume()
        
        print(responseCode)
        
        return denariiResponses
    }

    func getUserId(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/\(username)/\(email)/\(password)"
        return makeApiCall(urlString)
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/request_reset/\(usernameOrEmail)"
        return makeApiCall(urlString)
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/verify_reset/\(usernameOrEmail)/\(resetId)"
        return makeApiCall(urlString)
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/reset_password/\(username)/\(email)/\(password)"
        return makeApiCall(urlString)
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/create/\(userIdentifier)/\(walletName)/\(password)"
        return makeApiCall(urlString)
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/restore/\(userIdentifier)/\(walletName)/\(password)/\(seed)"
        return makeApiCall(urlString)
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/open/\(userIdentifier)/\(walletName)/\(password)"
        return makeApiCall(urlString)
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/balance/\(userIdentifier)/\(walletName)"
        return makeApiCall(urlString)
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/send/\(userIdentifier)/\(walletName)/\(adddressToSendTo)/\(amountToSend)"
        return makeApiCall(urlString)
    }
    
    func getPrices(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_prices/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func buyDenarii(_ userIdentifier: Int, _ amount: Double, _ bidPrice: Double, _ buyRegardlessOfPrice: Bool, _ failIfFullAmountIsntMet: Bool) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/buy_denarii/\(userIdentifier)/\(amount)/\(bidPrice)/\(buyRegardlessOfPrice)/\(failIfFullAmountIsntMet)"
        return makeApiCall(urlString)
    }
    
    func transferDenarii(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/transfer_denarii/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func makeDenariiAsk(_ userIdentifier: Int, _ amount: Double, _ askingPrice: Double) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/make_denarii_ask/\(userIdentifier)/\(amount)/\(askingPrice)"
        return makeApiCall(urlString)
    }
    
    func pollForCompletedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/poll_for_completed_transaction/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func cancelAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/cancel_ask/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func hasCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/has_credit_card_info/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func setCreditCardInfo(_ userIdentifier: Int, _ cardNumber: String, _ expirationDateMonth: String, _ expirationDateYear: String, _ securityCode: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/set_credit_card_info/\(userIdentifier)/\(cardNumber)/\(expirationDateMonth)/\(expirationDateYear)/\(securityCode)"
        return makeApiCall(urlString)
    }
    
    func clearCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/clear_credit_card_info/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func getMoneyFromBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_money_from_buyer/\(userIdentifier)/\(amount)/\(currency)"
        return makeApiCall(urlString)
    }
    
    func sendMoneyToSeller(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/send_money_to_seller/\(userIdentifier)/\(amount)/\(currency)"
        return makeApiCall(urlString)
    }
    
    func isTransactionSettled(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/is_transaction_settled/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func deleteUser(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/delete_user/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func getAskWithIdentifier(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_ask_with_identifier/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func transferDenariiBackToSeller(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/transfer_denarii_back_to_seller/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func sendMoneyBackToBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/send_money_back_to_buyer/\(userIdentifier)/\(amount)/\(currency)"
        return makeApiCall(urlString)
    }
    
    func cancelBuyOfAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/cancel_buy_of_ask/\(userIdentifier)/\(askId)"
        return makeApiCall(urlString)
    }
    
    func verifyIdentity(_ userIdentifier: Int, _ firstName: String, _ middleName: String, _ lastName: String, _ email: String, _ dob: String, _ ssn: String, _ zipCode: String, _ phone: String, _ workLocations: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/verify_identity/\(userIdentifier)/\(firstName)/\(middleName)/\(lastName)/\(email)/\(dob)/\(ssn)/\(zipCode)/\(phone)/\(workLocations)"
        return makeApiCall(urlString)
    }
    
    func isAVerifiedPerson(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/is_a_verified_person/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func getAllAsks(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_all_asks/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func getAllBuys(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_all_buys/\(userIdentifier)"
        return makeApiCall(urlString)
    }
    
    func createSupportTicket(_ userIdentifier: Int, _ title: String, _ description: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/create_support_ticket/\(userIdentifier)/\(title)/\(description)"
        return makeApiCall(urlString)
    }
    
    func updateSupportTicket(_ userIdentifier: Int, _ supportTicketId: String, _ comment: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/update_support_ticket/\(userIdentifier)/\(supportTicketId)/\(comment))"
        return makeApiCall(urlString)
    }
    
    func deleteSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/delete_support_ticket/\(userIdentifier)/\(supportTicketId)"
        return makeApiCall(urlString)
    }

    func getSupportTickets(_ userIdentifier: Int, _ canBeResolved: Bool) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_support_tickets/\(userIdentifier)/\(canBeResolved)"
        return makeApiCall(urlString)
    }

    func getSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_support_ticket/\(userIdentifier)/\(supportTicketId)"
        return makeApiCall(urlString)
    }

    func getCommentsOnTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/get_comments_on_ticket/\(userIdentifier)/\(supportTicketId)"
        return makeApiCall(urlString)
    }
    
    func resolveSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/resolve_support_ticket/\(userIdentifier)/\(supportTicketId)"
        return makeApiCall(urlString)
    }
    
    func pollForEscrowedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/poll_for_escrowed_transaction/\(userIdentifier)"
        return makeApiCall(urlString)
    }
}

class StubbedAPI: API {
        
    var users : Array<UserDetails> = Array()
    
    var lastAskId = 4
    
    var lastUserId = 1
    
    func findSupportTicket(_ supportTicketId: String) -> SupportTicket {
        for user in self.users {
            for supportTicket in user.supportTicketList {
                return supportTicket
            }
        }
        return SupportTicket()
    }
    
    func findUser(_ usernameOrEmail: String) -> UserDetails {
        for user in self.users {
            if user.userName == usernameOrEmail || user.userEmail == usernameOrEmail {
                return user
            }
        }
        return UserDetails()
    }
    
    func findUserWithId(_ userIdentifier: String) -> UserDetails {
        for user in self.users {
            if user.userID == userIdentifier {
                return user
            }
        }
        return UserDetails()
    }
    
    func findUserWithWalletAddress(_ walletAddress: String) -> UserDetails {
        for user in self.users {
            if user.walletDetails.walletAddress == walletAddress {
                return user
            }
        }
        return UserDetails()
    }
    
    func deleteAsk(_ userIdentifier: String, _ askId: String) {
        for var user in self.users {
            if user.userID == userIdentifier {
                var finalAsks = Array<DenariiAsk>()
                for ask in user.denariiAskList {
                    if ask.askID != askId {
                        finalAsks.append(ask)
                    }
                }
                user.denariiAskList = finalAsks
            }
        }
    }
    
    func deleteAskById(_ askId: String) {
        for var user in self.users {
            var finalAsks = Array<DenariiAsk>()
            for ask in user.denariiAskList {
                if ask.askID != askId {
                    finalAsks.append(ask)
                }
            }
            user.denariiAskList = finalAsks
        }
    }
    
    func findAsk(_ askId: String) -> DenariiAsk {
        for user in self.users {
            for ask in user.denariiAskList {
                if ask.askID != askId {
                    return ask
                }
            }
        }
        return DenariiAsk()
    }
    
    func getUserId(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
 
        denariiResponses.append(denariiResponse)
        
        var foundUser = false
        var aUser = UserDetails()
        for user in self.users {
            if user.userName == username && user.userEmail == email && user.userPassword == password{
                foundUser = true
                aUser = user
                break
            }
        }
        
        if foundUser {
            denariiResponse.userIdentifier = aUser.userID
        } else {
            denariiResponse.userIdentifier = String(lastUserId)
            
            var newUser = UserDetails(userName: username, userEmail: email, userPassword: password, walletDetails: WalletDetails(), creditCard: CreditCard(), userID: denariiResponse.userIdentifier, denariiUser: DenariiUser())
            users.append(newUser)
            
            lastUserId += 1
        }
        
        return denariiResponses
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUser(usernameOrEmail)
        
        user.denariiUser.resetID = Int.random(in: 1..<100)
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = -1
        
        let user = findUser(usernameOrEmail)
        
        if user.denariiUser.resetID == resetId {
            denariiResponse.responseCode = 200
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUser(username)
        
        if user.userName == username && user.userEmail == email {
            user.userPassword = password
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200

        denariiResponse.walletAddress = String(Int.random(in: 1..<100))
        denariiResponse.seed = "some seed here \(Int.random(in: 1..<100))"
        
        var user = findUserWithId(String(userIdentifier))
        user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: denariiResponse.seed, balance: 0.0, walletAddress: denariiResponse.walletAddress)
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = "123 \(Int.random(in: 1..<100))"
        
        var user = findUserWithId(String(userIdentifier))
        user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: seed, balance: 10.0, walletAddress: denariiResponse.walletAddress)
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = String(Int.random(in: 1..<100))
        denariiResponse.seed = "some seed here \(Int.random(in: 1..<100))"
        
        var user = findUserWithId(String(userIdentifier))
        user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: denariiResponse.seed, balance: 0.0, walletAddress: denariiResponse.walletAddress)
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUserWithId(String(userIdentifier))
        
        denariiResponse.balance = user.walletDetails.balance
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var sendingUser = findUserWithId(String(userIdentifier))

        if sendingUser.walletDetails.balance >= amountToSend {
            sendingUser.walletDetails.balance -= amountToSend
            
            var receivingUser = findUserWithWalletAddress(adddressToSendTo)
            receivingUser.walletDetails.balance += amountToSend
            
        }
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getPrices(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        for user in self.users {
            for ask in user.denariiAskList {
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.askID = ask.askID
                denariiResponse.amount = ask.amount
                denariiResponse.askingPrice = ask.askingPrice
                
                denariiResponses.append(denariiResponse)
            }
        }
        
        return denariiResponses
    }
    
    func buyDenarii(_ userIdentifier: Int, _ amount: Double, _ bidPrice: Double, _ buyRegardlessOfPrice: Bool, _ failIfFullAmountIsntMet: Bool) -> Array<DenariiResponse> {

        var denariiResponses = Array<DenariiResponse>()
        var boughtAsks = Array<DenariiAsk>()

        var currentAmountBought = 0.0
        
        var allAsks = Array<DenariiAsk>()
        
        for user in self.users {
            for ask in user.denariiAskList {
                if !ask.inEscrow && !ask.isSettled {
                    allAsks.append(ask)
                }
            }
        }
        
        for var ask in allAsks {
            if currentAmountBought >= amount {
                break
            }
            
            if buyRegardlessOfPrice {
                
                currentAmountBought += ask.amount

                if currentAmountBought > amount {
                    ask.amountBought = ask.amount - (currentAmountBought - amount)
                }
                else {
                    ask.amountBought = ask.amount
                }
                
                ask.inEscrow = true
                ask.buyerId = String(userIdentifier)
            
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.askID = ask.askID
                
                boughtAsks.append(ask)
                denariiResponses.append(denariiResponse)
            } else {
                if ask.askingPrice <= bidPrice {
                    currentAmountBought += ask.amount

                    if currentAmountBought > amount {
                        ask.amountBought = ask.amount - (currentAmountBought - amount)
                    }
                    else {
                        ask.amountBought = ask.amount
                    }
                    
                    ask.inEscrow = true
                    ask.buyerId = String(userIdentifier)
                    
                    var denariiResponse = DenariiResponse()
                    denariiResponse.responseCode = 200
                    denariiResponse.askID = ask.askID
                    
                    boughtAsks.append(ask)
                    denariiResponses.append(denariiResponse)                }
            }
        }
        
        if failIfFullAmountIsntMet && currentAmountBought < amount {
            
            for var ask in boughtAsks {
                ask.inEscrow = false
                ask.buyerId = ""
                ask.amount += ask.amountBought
                ask.amountBought = 0
            }
            return Array()
        }
        
        return denariiResponses
    }
    
    func transferDenarii(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        for user in self.users {
            for var ask in user.denariiAskList {
                if ask.askID == askId {
                    var denariiResponse = DenariiResponse()
                    denariiResponse.responseCode = 200
                    denariiResponse.askID = ask.askID
                    denariiResponse.amountBought = ask.amountBought
                    
                    ask.amountBought = 0.0
                    ask.inEscrow = false
                    ask.isSettled = true
                    ask.buyerId = ""
                    
                    denariiResponses.append(denariiResponse)
                    break
                }
            }
        }
        
        return denariiResponses
    }
    
    func makeDenariiAsk(_ userIdentifier: Int, _ amount: Double, _ askingPrice: Double) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.amount = amount
        denariiResponse.askingPrice = askingPrice
        denariiResponse.askID = String(lastAskId)
        
        var user = findUserWithId(String(userIdentifier))
        
        var newAsk = DenariiAsk(askID: denariiResponse.askID, amount: denariiResponse.amount, askingPrice: denariiResponse.askingPrice, inEscrow: false, amountBought: 0.0, isSettled: false, seenBySeller: false, buyerId: "")
        
        user.denariiAskList.append(newAsk)
        
        lastAskId += 1
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }

    func pollForCompletedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var user = findUserWithId(String(userIdentifier))
        
        for var ask in user.denariiAskList {
            if ask.isSettled && !ask.inEscrow {
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.askID = ask.askID
                denariiResponse.askingPrice = ask.askingPrice
                denariiResponse.amount = ask.amount
                
                ask.seenBySeller = true
                denariiResponses.append(denariiResponse)
            }
        }
        
        return denariiResponses
    }
    
    func cancelAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = -1
        denariiResponse.askID = askId
        
        var user = findUserWithId(String(userIdentifier))
        
        var doDeleteAsk = false
        for ask in user.denariiAskList {
            if ask.askID == askId {
                if !ask.inEscrow && !ask.isSettled {
                    doDeleteAsk = true
                }
            }
        }
        
        if doDeleteAsk {
            deleteAsk(String(userIdentifier), askId)
            denariiResponse.responseCode = 200
        }
        
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func hasCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        let user = findUserWithId(String(userIdentifier))
        
        if user.creditCard.customerId != "" {
            denariiResponse.hasCreditCardInfo = true
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func setCreditCardInfo(_ userIdentifier: Int, _ cardNumber: String, _ epirationDateMonth: String, _ expirationDateYear: String, _ securityCode: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUserWithId(String(userIdentifier))
        
        user.creditCard = CreditCard(customerId: String(Int.random(in: 1..<100)), sourceTokenId: String(Int.random(in: 1..<100)))
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func clearCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUserWithId(String(userIdentifier))
        user.creditCard = CreditCard()
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getMoneyFromBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendMoneyToSeller(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func isTransactionSettled(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.askID = askId
        
        var ask = findAsk(askId)
        if !ask.isSettled || !ask.seenBySeller {
            denariiResponse.transactionWasSettled = false
        }
        else {
            denariiResponse.transactionWasSettled = true
            if ask.amount == 0 {
                deleteAskById(askId)
            }
            else {
                ask.isSettled = false
                ask.seenBySeller = false
            }
        }
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func deleteUser(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var finalUsers = Array<UserDetails>()
        for user in self.users {
            if user.userID != String(userIdentifier) {
                finalUsers.append(user)
            }
        }
        
        self.users = finalUsers
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getAskWithIdentifier(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        let ask = findAsk(askId)
        denariiResponse.askID = ask.askID
        denariiResponse.amount = ask.amount
        denariiResponse.amountBought = ask.amountBought
        denariiResponse.askingPrice = ask.askingPrice
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func transferDenariiBackToSeller(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        var user = findUserWithId(String(userIdentifier))
        
        for var otherUser in self.users {
            for var ask in user.denariiAskList {
                if ask.askID == askId {
                    var denariiResponse = DenariiResponse()
                    denariiResponse.responseCode = 200
                    denariiResponse.askID = ask.askID
                    denariiResponse.amountBought = ask.amountBought
                    
                    user.walletDetails.balance -= ask.amountBought
                    otherUser.walletDetails.balance += ask.amountBought
                    
                    ask.amount += ask.amountBought
                    ask.amountBought = 0.0
                    ask.inEscrow = false
                    ask.buyerId = ""
                    ask.isSettled = false
                    
                    denariiResponses.append(denariiResponse)
                    break
                }
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendMoneyBackToBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func cancelBuyOfAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = -1
        
        var ask = findAsk(askId)
        
        if ask.inEscrow {
            if !ask.isSettled {
                ask.inEscrow = false
                ask.buyerId = ""
                ask.amountBought = 0
                denariiResponse.responseCode = 200
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func verifyIdentity(_ userIdentifier: Int, _ firstName: String, _ middleName: String, _ lastName: String, _ email: String, _ dob: String, _ ssn: String, _ zipCode: String, _ phone: String, _ workLocations: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.verificationStatus = "is_verified"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func isAVerifiedPerson(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.verificationStatus = "is_verified"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getAllAsks(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        let user = findUserWithId(String(userIdentifier))
        for ask in user.denariiAskList {
            if !ask.inEscrow && !ask.isSettled {
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.askID = ask.askID
                denariiResponse.amount = ask.amount
                denariiResponse.amountBought = ask.amountBought
                denariiResponse.askingPrice = ask.askingPrice
                
                denariiResponses.append(denariiResponse)
            }
        }
        return denariiResponses
    }
    
    func getAllBuys(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var allBuys = Array<DenariiAsk>()
        
        for user in self.users {
            for ask in user.denariiAskList {
                if ask.buyerId == String(userIdentifier) {
                    allBuys.append(ask)
                }
            }
        }

        
        for buy in allBuys {
            var denariiResponse = DenariiResponse()
            denariiResponse.responseCode = 200
            denariiResponse.askID = buy.askID
            denariiResponse.amount = buy.amount
            denariiResponse.amountBought = buy.amountBought
            denariiResponse.askingPrice = buy.askingPrice
            
            denariiResponses.append(denariiResponse)
        }
        return denariiResponses
    }
    
    func createSupportTicket(_ userIdentifier: Int, _ title: String, _ description: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var user = findUserWithId(String(userIdentifier))
        
        let supportTicketId = String(Int.random(in: 0..<100))
        var newTicket = SupportTicket(supportID: supportTicketId, description: description, title: title, resolved: false, supportTicketCommentList: Array())
        
        user.supportTicketList.append(newTicket)
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func updateSupportTicket(_ userIdentifier: Int, _ supportTicketId: String, _ comment: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        
        var user = findUserWithId(String(userIdentifier))
        
        var supportTicket = findSupportTicket(supportTicketId)
        
        var newComment = SupportTicketComment(author: user.userName, content: comment)
        
        supportTicket.supportTicketCommentList.append(newComment)
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func deleteSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        
        for var user in self.users {
            var finalSupportTickets = Array<SupportTicket>()
            for supportTicket in user.supportTicketList {
                if supportTicket.supportID != supportTicketId {
                    finalSupportTickets.append(supportTicket)
                }
            }
            user.supportTicketList = finalSupportTickets
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }

    func getSupportTickets(_ userIdentifier: Int, _ canBeResolved: Bool) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        let user = findUserWithId(String(userIdentifier))
        
        for supportTicket in user.supportTicketList {
            if !supportTicket.resolved || canBeResolved {
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.supportTicketID = supportTicket.supportID
                denariiResponse.author = user.userName
                denariiResponse.description = supportTicket.description
                denariiResponse.updatedTimeBody = ""
                denariiResponse.creationTimeBody = ""
                denariiResponse.isResolved = supportTicket.resolved
                
                denariiResponses.append(denariiResponse)
            }
        }
        return denariiResponses
    }

    func getSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var user = findUserWithId(String(userIdentifier))
        for supportTicket in user.supportTicketList {
            if supportTicket.supportID == supportTicketId{
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.supportTicketID = supportTicket.supportID
                denariiResponse.author = user.userName
                denariiResponse.description = supportTicket.description
                denariiResponse.updatedTimeBody = ""
                denariiResponse.creationTimeBody = ""
                denariiResponse.isResolved = supportTicket.resolved
                
                denariiResponses.append(denariiResponse)
            }
        }

        return denariiResponses
    }

    func getCommentsOnTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var supportTicket = findSupportTicket(supportTicketId)
        for comment in supportTicket.supportTicketCommentList {
            var denariiResponse = DenariiResponse()
            denariiResponse.responseCode = 200
            denariiResponse.author = comment.author
            denariiResponse.content = comment.content
            denariiResponse.updatedTimeBody = ""
            denariiResponse.creationTimeBody = ""
            
            denariiResponses.append(denariiResponse)
        }
        return denariiResponses
    }
    
    func resolveSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var supportTicket = findSupportTicket(supportTicketId)
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        denariiResponse.updatedTimeBody = ""
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }

    func pollForEscrowedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        let user = findUserWithId(String(userIdentifier))
        
        for ask in user.denariiAskList {
            if ask.inEscrow && !ask.isSettled {
                var denariiResponse = DenariiResponse()
                denariiResponse.responseCode = 200
                denariiResponse.askID = ask.askID
                denariiResponse.amount = ask.amount
                denariiResponse.askingPrice = ask.askingPrice
                denariiResponse.amountBought = ask.amountBought
                
                denariiResponses.append(denariiResponse)
            }
        }

        return denariiResponses
    }
}
