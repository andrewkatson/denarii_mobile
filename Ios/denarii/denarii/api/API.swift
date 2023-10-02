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
}

class RealAPI: API {
    
    private var urlBase: String = "https://denariimobilebackend.com"
    
    func makeApiCall(_ urlStr: String) -> Array<DenariiResponse> {
        
        let url = URL(string: urlStr)
        
        var denariiResponses = Array<DenariiResponse>()
        
        var responseCode = 200

        let task = URLSession.shared.dataTask(with: url!, completionHandler: { (data, response, error) in
            if error != nil {
                // TODO log here and pick a better error code
                responseCode = -1
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse,
                      (200...299).contains(httpResponse.statusCode) else {
                // TODO log here and pick a better error code
                responseCode = -1
              return
            }
            
            if let data = data,
                    var singleDenariiResponse = try? JSONDecoder().decode(DenariiResponse.self, from: data) {
                singleDenariiResponse.responseCode = responseCode
                denariiResponses.append(singleDenariiResponse)
            }
        })

        task.resume()
        
        if denariiResponses.isEmpty {
            var emptyResponse = DenariiResponse()
            emptyResponse.responseCode = responseCode
            denariiResponses.append(emptyResponse)
        }
        
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
}

class StubbedAPI: API {
    func getUserId(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.userIdentifier = "1"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200

        denariiResponse.walletAddress = "123"
        denariiResponse.seed = "some seed here"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = "123"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.walletAddress = "123"
        denariiResponse.seed = "some seed here"
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()
        
        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        denariiResponse.balance = 3.0
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Array<DenariiResponse> {
        var denariiResponses = Array<DenariiResponse>()

        var denariiResponse = DenariiResponse()
        denariiResponse.responseCode = 200
        
        denariiResponses.append(denariiResponse)
        return denariiResponses
    }
}
