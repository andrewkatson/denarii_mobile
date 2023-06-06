//
//  API.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

protocol API {
    // Returns a Wallet instance with a user identifier
    func getUserId(_ username: String, _ email: String, _ password: String) -> Wallet
    
    // Returns a Wallet instance with nothing in it
    func requestPasswordReset(_ usernameOrEmail: String) -> Wallet
    
    // Returns a Wallet instance with nothing in it
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Wallet
    
    // Returns a Wallet instance with nothing in it
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Wallet
    
    // Returns a Wallet instance with seed and address
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet
    
    // Returns a Wallet instance with address
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Wallet
    
    // Returns a Wallet instance with seed and address
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet
    
    // Returns a Wallet instance with balance
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Wallet
    
    // Returns a Wallet instance with nothing in it
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Wallet
}

class RealAPI: API {
    
    private var urlBase: String = "https://denariimobilebackend.com"
    
    func makeApiCall(_ urlStr: String) -> Wallet {
        
        let url = URL(string: urlStr)
        
        var wallet = Wallet()
        
        var errorString = ""
        var responseCode = 200

        let task = URLSession.shared.dataTask(with: url!, completionHandler: { (data, response, error) in
            if let err = error {
                errorString = String(error!.localizedDescription)
                responseCode = 400
                return
            }
            
            guard let httpResponse = response as? HTTPURLResponse,
                      (200...299).contains(httpResponse.statusCode) else {
              errorString = "Problem with the http response"
              responseCode = 400
              return
            }
            
            if let data = data,
                    let walletData = try? JSONDecoder().decode(Wallet.self, from: data) {
                    wallet = walletData
                  }
        })

        task.resume()
        
        if responseCode != 200 {
            wallet.responseCode = responseCode
            wallet.responseCodeText = errorString
        }
        
        return wallet
    }

    func getUserId(_ username: String, _ email: String, _ password: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(username)/\(email)/\(password)"
        return makeApiCall(urlString)
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(usernameOrEmail)/request_reset"
        return makeApiCall(urlString)
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Wallet {
        var urlString = "\(urlBase)/users/\(usernameOrEmail)/\(resetId)/verify_reset"
        return makeApiCall(urlString)
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(username)/\(email)/\(password)/reset_password"
        return makeApiCall(urlString)
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(userIdentifier)/\(walletName)/\(password)/create"
        return makeApiCall(urlString)
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(userIdentifier)/\(walletName)/\(password)/\(seed)/restore"
        return makeApiCall(urlString)
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(userIdentifier)/\(walletName)/\(password)/open"
        return makeApiCall(urlString)
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Wallet {
        var urlString = "\(urlBase)/users/\(userIdentifier)/\(walletName)/balance"
        return makeApiCall(urlString)
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Wallet {
        var urlString = "\(urlBase)/users/\(userIdentifier)/\(walletName)/\(adddressToSendTo)/\(amountToSend)/send"
        return makeApiCall(urlString)
    }
}

class StubbedAPI: API {
    func getUserId(_ username: String, _ email: String, _ password: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        
        var walletDetails = WalletDetails()
        walletDetails.userIdentifier = 1
        
        wallet.response = walletDetails
        return wallet
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        return wallet
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        return wallet
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        return wallet
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        
        var walletDetails = WalletDetails()
        walletDetails.walletAddress = "123"
        walletDetails.seed = "some seed here"
        
        wallet.response = walletDetails
        return wallet
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        
        var walletDetails = WalletDetails()
        walletDetails.walletAddress = "123"
        
        wallet.response = walletDetails
        return wallet
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        
        var walletDetails = WalletDetails()
        walletDetails.walletAddress = "123"
        walletDetails.seed = "some seed here"
        
        wallet.response = walletDetails
        return wallet
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        
        var walletDetails = WalletDetails()
        walletDetails.balance = 3.0
        
        wallet.response = walletDetails
        return wallet
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Wallet {
        var wallet = Wallet()
        wallet.responseCode = 200
        return wallet
    }
}
