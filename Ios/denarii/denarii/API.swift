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
    func getUserId(_ username: String, _ email: String, _ password: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func requestPasswordReset(_ usernameOrEmail: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func verifyReset(_ usernameOrEmail: String, _ resetId: Int) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func resetPassword(_ username: String, _ email: String, _ password: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func createWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func restoreWallet(_ userIdentifier: Int, _ walletName: String, _ password: String, _ seed: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func openWallet(_ userIdentifier: Int, _ walletName: String, _ password: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func getBalance(_ userIdentifier: Int, _ walletName: String) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
    }
    
    func sendDenarii(_ userIdentifier: Int, _ walletName: String, _ adddressToSendTo: String, _ amountToSend: Double) -> Wallet {
        // TODO call a real API
        var wallet = Wallet()
        wallet.responseCode = 400
        return wallet
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
