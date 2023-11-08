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
    
    // Returns a DenariiResponse with ask_id, asking_price, amount, and amount_bought
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
    
    // Logs the user out
    func logout(_ userIdentifier: Int) -> Array<DenariiResponse>
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
    
    func logout(_ userIdentifier: Int) -> Array<DenariiResponse> {
        let urlString = "\(urlBase)/users/loggout/\(userIdentifier)"
        return makeApiCall(urlString)
    }
}

class StubbedAPI: API {
        
    var users : Array<UserDetails> = Array()
    
    var loggedInUser: UserDetails = UserDetails()
    
    var lastAskId = 1
    
    var lastUserId = 1
    
    func deleteAskById(_ askId: String) {
        for index in self.users.indices {
            let user = self.users[index]
            var finalAsks = Array<DenariiAsk>()
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
                if ask.askID != askId {
                    finalAsks.append(ask)
                }
            }
            user.denariiAskList = finalAsks
        }
    }
    
    func requireLogin(_ userIdentifier: Int) -> Bool {
        
        if self.loggedInUser.userID != String(userIdentifier) {
            return false
        }
        return true
    }
    
    func getUserId(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
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
            self.loggedInUser = aUser
        } else if !self.loggedInUser.userID.isEmpty {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        } else {
            denariiResponse.userIdentifier = String(lastUserId)
            
            let newUser = UserDetails(userName: username, userEmail: email, userPassword: password, walletDetails: WalletDetails(), creditCard: CreditCard(), userID: denariiResponse.userIdentifier, denariiUser: DenariiUser())
            users.append(newUser)
            
            lastUserId += 1
        }
        
        
        denariiResponses.append(denariiResponse)
        
        return denariiResponses
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userName == usernameOrEmail || user.userEmail == usernameOrEmail {
                // A static resetID to make testing easier
                user.denariiUser.resetID = 123
                break
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = -1
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userName == usernameOrEmail || user.userEmail == usernameOrEmail {
                if user.denariiUser.resetID == resetId {
                    denariiResponse.responseCode = 200
                    break
                }
            }
        }
 
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userName == username && user.userEmail == email {
                if user.userName == username && user.userEmail == email {
                    user.userPassword = password
                    break
                }
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200

        denariiResponse.walletAddress = String(Int.random(in: 1..<100))
        denariiResponse.seed = "some seed here \(Int.random(in: 1..<100))"
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: denariiResponse.seed, balance: 3.0, walletAddress: denariiResponse.walletAddress)
                break
            }
        }

        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = "123 \(Int.random(in: 1..<100))"
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: seed, balance: 10.0, walletAddress: denariiResponse.walletAddress)
                break
            }
        }
 
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = String(Int.random(in: 1..<100))
        denariiResponse.seed = "some seed here \(Int.random(in: 1..<100))"
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                user.walletDetails = WalletDetails(walletName: walletName, walletPassword: password, seed: denariiResponse.seed, balance: 0.0, walletAddress: denariiResponse.walletAddress)
                break
            }
        }

        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                denariiResponse.balance = user.walletDetails.balance
                break
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let sendingUser = self.users[index]
            if sendingUser.userID == String(userIdentifier) {
                if sendingUser.walletDetails.balance >= amountToSend {
                    sendingUser.walletDetails.balance -= amountToSend
                    
                    for otherIndex in self.users.indices {
                        let receivingUser = self.users[otherIndex]
                        if receivingUser.walletDetails.walletAddress == adddressToSendTo {
                            receivingUser.walletDetails.balance += amountToSend
                        }
                    }
                }
                break
            }
        }

        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getPrices(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }

        for user in self.users {
            if user.userID != String(userIdentifier) {
                for ask in user.denariiAskList {
                    var denariiResponse = DenariiResponse()
                    denariiResponse.responseCode = 200
                    denariiResponse.askID = ask.askID
                    denariiResponse.amount = ask.amount
                    denariiResponse.askingPrice = ask.askingPrice
                    
                    denariiResponses.append(denariiResponse)
                }
            }
        }
        
        return denariiResponses
    }
    
    func buyDenarii(_ userIdentifier: Int, _ amount: Double, _ bidPrice: Double, _ buyRegardlessOfPrice: Bool, _ failIfFullAmountIsntMet: Bool) -> Array<DenariiResponse> {

        var denariiResponses = Array<DenariiResponse>()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        
        var boughtAsks = Array<DenariiAsk>()

        var currentAmountBought = 0.0
        
        var allAsks = Array<DenariiAsk>()
        
        for userIndex in self.users.indices {
            let user = self.users[userIndex]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
                if !ask.inEscrow && !ask.isSettled {
                    allAsks.append(ask)
                }
            }
        }
        
        for askIndex in allAsks.indices {
            let ask = allAsks[askIndex]
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
            
            for askIndex in boughtAsks.indices {
                let ask = boughtAsks[askIndex]
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
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        
        for userIndex in self.users.indices {
            let user = self.users[userIndex]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
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
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.amount = amount
        denariiResponse.askingPrice = askingPrice
        denariiResponse.askID = String(lastAskId)
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                let newAsk = DenariiAsk(askID: denariiResponse.askID, amount: denariiResponse.amount, askingPrice: denariiResponse.askingPrice, inEscrow: false, amountBought: 0.0, isSettled: false, seenBySeller: false, buyerId: "")
                
                user.denariiAskList.append(newAsk)
                break
            }
        }

        lastAskId += 1
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }

    func pollForCompletedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }

        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for askIndex in user.denariiAskList.indices {
                    let ask = user.denariiAskList[askIndex]
                    if ask.isSettled && !ask.inEscrow {
                        var denariiResponse = DenariiResponse()
                        denariiResponse.responseCode = 200
                        denariiResponse.askID = ask.askID
                        denariiResponse.askingPrice = ask.askingPrice
                        denariiResponse.amount = ask.amount
                        denariiResponse.amountBought = ask.amountBought
                        
                        ask.seenBySeller = true
                        denariiResponses.append(denariiResponse)
                    }
                }
                break
            }
        }
        
        return denariiResponses
    }
    
    func cancelAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        denariiResponse.responseCode = -1
        denariiResponse.askID = askId
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                var doDeleteAsk = false
                for ask in user.denariiAskList {
                    if ask.askID == askId {
                        if !ask.inEscrow && !ask.isSettled {
                            doDeleteAsk = true
                        }
                    }
                }
                if doDeleteAsk {
                    var finalAsks = Array<DenariiAsk>()
                    for askIndex in user.denariiAskList.indices {
                        let ask = user.denariiAskList[askIndex]
                        if ask.askID != askId {
                            finalAsks.append(ask)
                        }
                    }
                    user.denariiAskList = finalAsks                   
                    denariiResponse.responseCode = 200
                }
                break
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func hasCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                if user.creditCard.customerId != "" {
                    denariiResponse.hasCreditCardInfo = true
                }
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func setCreditCardInfo(_ userIdentifier: Int, _ cardNumber: String, _ epirationDateMonth: String, _ expirationDateYear: String, _ securityCode: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                user.creditCard = CreditCard(customerId: String(Int.random(in: 1..<100)), sourceTokenId: String(Int.random(in: 1..<100)))
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func clearCreditCardInfo(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                user.creditCard = CreditCard()
            }
        }        
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getMoneyFromBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendMoneyToSeller(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func isTransactionSettled(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.askID = askId
        
        for index in self.users.indices {
            let user = self.users[index]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
                if ask.askID != askId {
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
                    break
                }
            }
        }

        return denariiResponses
    }
    
    func deleteUser(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        var finalUsers = Array<UserDetails>()
        for user in self.users {
            if user.userID != String(userIdentifier) {
                finalUsers.append(user)
            }
        }
        
        self.users = finalUsers
        self.loggedInUser = UserDetails()
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getAskWithIdentifier(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
                if ask.askID != askId {
                    denariiResponse.askID = ask.askID
                    denariiResponse.amount = ask.amount
                    denariiResponse.amountBought = ask.amountBought
                    denariiResponse.askingPrice = ask.askingPrice
                    
                    denariiResponses.append(denariiResponse)
                    break
                }
            }
        }

        return denariiResponses
    }
    
    func transferDenariiBackToSeller(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for otherIndex in self.users.indices {
                    let otherUser = self.users[otherIndex]
                    for askIndex in user.denariiAskList.indices {
                        let ask = user.denariiAskList[askIndex]
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
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendMoneyBackToBuyer(_ userIdentifier: Int, _ amount: Double, _ currency: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func cancelBuyOfAsk(_ userIdentifier: Int, _ askId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        
        denariiResponse.responseCode = -1
        
        for index in self.users.indices {
            let user = self.users[index]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
                if ask.askID != askId {
                    if ask.inEscrow {
                        if !ask.isSettled {
                            ask.inEscrow = false
                            ask.buyerId = ""
                            ask.amountBought = 0
                            denariiResponse.responseCode = 200
                            denariiResponses.append(denariiResponse)
                            break
                        }
                    }
                }
            }
        }
        
        return denariiResponses
    }
    
    func verifyIdentity(_ userIdentifier: Int, _ firstName: String, _ middleName: String, _ lastName: String, _ email: String, _ dob: String, _ ssn: String, _ zipCode: String, _ phone: String, _ workLocations: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.verificationStatus = "is_verified"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func isAVerifiedPerson(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.verificationStatus = "is_verified"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getAllAsks(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }

        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for askIndex in user.denariiAskList.indices {
                    let ask = user.denariiAskList[askIndex]
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
            }
        }

        return denariiResponses
    }
    
    func getAllBuys(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        
        var allBuys = Array<DenariiAsk>()
        
        for userIndex in self.users.indices {
            let user = self.users[userIndex]
            for askIndex in user.denariiAskList.indices {
                let ask = user.denariiAskList[askIndex]
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
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200

        let supportTicketId = String(Int.random(in: 0..<100))
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                let newTicket = SupportTicket(supportID: supportTicketId, description: description, title: title, resolved: false, supportTicketCommentList: Array())
                
                user.supportTicketList.append(newTicket)
                break
            }
        }

        denariiResponse.supportTicketID = supportTicketId
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func updateSupportTicket(_ userIdentifier: Int, _ supportTicketId: String, _ comment: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        denariiResponse.commentID = String(Int.random(in: 1..<100))
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for supportTicketIndex in user.supportTicketList.indices {
                    let supportTicket = user.supportTicketList[supportTicketIndex]
                    
                    if supportTicket.supportID == supportTicketId {
                        let newComment = SupportTicketComment(author: user.userName, content: comment, commentID: denariiResponse.commentID)
                        
                        supportTicket.supportTicketCommentList.append(newComment)
                    }
                }
            }
        }
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func deleteSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        denariiResponse.supportTicketID = supportTicketId
        
        for index in self.users.indices {
            let user = self.users[index]
            var finalSupportTickets = Array<SupportTicket>()
            for supportTicketIndex in user.supportTicketList.indices {
                let supportTicket = user.supportTicketList[supportTicketIndex]
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
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for supportTicketIndex in user.supportTicketList.indices {
                    let supportTicket = user.supportTicketList[supportTicketIndex]
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
                break
            }
        }

        return denariiResponses
    }

    func getSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for supportTicketIndex in user.supportTicketList.indices {
                    let supportTicket = user.supportTicketList[supportTicketIndex]
                    if supportTicket.supportID == supportTicketId{
                        denariiResponse.supportTicketID = supportTicket.supportID
                        denariiResponse.author = user.userName
                        denariiResponse.description = supportTicket.description
                        denariiResponse.updatedTimeBody = ""
                        denariiResponse.creationTimeBody = ""
                        denariiResponse.isResolved = supportTicket.resolved
                        
                        denariiResponses.append(denariiResponse)
                        break
                    }
                }
            }
        }

        return denariiResponses
    }

    func getCommentsOnTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        denariiResponse.responseCode = 200

        for index in self.users.indices {
            let user = self.users[index]
            for stIndex in user.supportTicketList.indices {
                let supportTicket = user.supportTicketList[stIndex]
                if supportTicket.supportID == supportTicketId {
                    for comment in supportTicket.supportTicketCommentList {
                        denariiResponse.author = comment.author
                        denariiResponse.content = comment.content
                        denariiResponse.updatedTimeBody = ""
                        denariiResponse.creationTimeBody = ""
                        
                        denariiResponses.append(denariiResponse)
                    }
                    break
                }
            }
        }
        return denariiResponses
    }
    
    func resolveSupportTicket(_ userIdentifier: Int, _ supportTicketId: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
                
        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for supportTicketIndex in user.supportTicketList.indices {
                    let supportTicket = user.supportTicketList[supportTicketIndex]
                    if supportTicket.supportID == supportTicketId{
                        supportTicket.resolved = true
                        denariiResponse.supportTicketID = supportTicketId
                        denariiResponse.updatedTimeBody = ""
                        denariiResponses.append(denariiResponse)
                        break
                    }
                }
            }
        }
        
        return denariiResponses
    }

    func pollForEscrowedTransaction(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            return denariiResponses
        }
        denariiResponse.responseCode = 200

        for index in self.users.indices {
            let user = self.users[index]
            if user.userID == String(userIdentifier) {
                for askIndex in user.denariiAskList.indices {
                    let ask = user.denariiAskList[askIndex]
                    if ask.inEscrow && !ask.isSettled {
                        denariiResponse.askID = ask.askID
                        denariiResponse.amount = ask.amount
                        denariiResponse.askingPrice = ask.askingPrice
                        denariiResponse.amountBought = ask.amountBought
                        
                        denariiResponses.append(denariiResponse)
                    }
                }
            }
        }

        return denariiResponses
    }
    
    func logout(_ userIdentifier: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        var denariiResponse = DenariiResponse()
        
        if !requireLogin(userIdentifier) {
            denariiResponse.responseCode = -1
            denariiResponses.append(denariiResponse)
            return denariiResponses
        }
        denariiResponse.responseCode = 200
        
        self.loggedInUser = UserDetails()
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
}
