//
//  denariiTests.swift
//  denariiTests
//
//  Created by Andrew Katson on 5/14/23.
//

import XCTest
@testable import denarii

final class denariiTests: XCTestCase {
    
    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
        Constants.DEBUG = true
    }
    
    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    
    func testLogin() throws {
        XCTAssert(LoginView().attemptSubmit() == true)
    }
    
    func testCreateWallet() throws {
        XCTAssert(CreateWalletView().attemptSubmit() == true)
    }
    
    func testOpenWallet() throws {
        XCTAssert(OpenWalletView().attemptSubmit() == true)
    }
    
    func testOpenedWalletRefreshBalance() throws {
        let openedWalletView = OpenedWalletView()
        // Get some denarii into the wallet
        openedWalletView.setBalance("1")
        let _ = openedWalletView.refreshBalance()
        XCTAssert(openedWalletView.getBalance() == "1")
    }
    
    func testOpenedWalletSendDenarii() throws {
        let openedWalletView = OpenedWalletView()
        // Get some denarii into the wallet
        openedWalletView.setBalance("10")
        let _ = openedWalletView.sendDenarii()
        // Nothing should be sent because the amountToSend text view is empty
        XCTAssert(openedWalletView.getBalance() == "10")
    }
    
    func testRestoreDeterministicWallet() throws {
        XCTAssert(RestoreDeterministicWalletView().attemptSubmit() == true)
    }
    
    func testRequestReset() throws {
        XCTAssert(RequestResetView().attemptRequest() == true)
    }
    
    func testVerifyReset() throws {
        // Will fail because it will look for a reset id (and one is never set)
        XCTAssert(VerifyResetView().attemptVerifyReset() == false)
    }
    
    func testResetPassword() throws {
        XCTAssert(ResetPasswordView().attemptReset() == true)
    }
    
    func testRegister() throws {
        XCTAssert(RegisterView().attemptSubmit() == true)
    }
    
    func testBuyDenarii() throws {
        XCTAssert(BuyDenarii().attemptBuy() == true)
    }
    
    func testSellDenarii() throws {
        XCTAssert(SellDenarii().attemptToSellDenarii() == true)
    }
    
    func testVerifyIdentity() throws {
        XCTAssert(Verification().attemptSubmit() == true)
    }
    
    func testSetCreditCardInfo() throws {
        XCTAssert(CreditCardInfo().attemptToSetCreditCardInfo() == true)
    }

    func testCreateSupportTicket() throws {
        XCTAssert(CreateSupportTicket().attemptCreateTicket() == true)
    }
    
    func testCreateNewComment() throws {
        XCTAssert(SupportTicketDetails().attemptCreateNewComment() == true)
    }
    
    func testDeleteSupportTicket() throws {
        XCTAssert(SupportTicketDetails().attemptDeleteTicket() == true)
    }
    
    func testResolveSupportTicket() throws {
        XCTAssert(SupportTicketDetails().attemptResolveTicket() == true)
    }
    
    func testDeleteAccount() throws {
        XCTAssert(UserSettings().attemptDeleteAccount() == true)
    }
    
    func testLoginPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(LoginView().attemptSubmit() == false)
        }
    }
    
    func testCreateWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(CreateWalletView().attemptSubmit() == false)
        }
    }
    
    func testOpenWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(OpenWalletView().attemptSubmit() == false)
        }
    }
    
    func testOpenedWalletRefreshBalancePerformance() throws {
        Constants.DEBUG = false
        self.measure {
            let openedWalletView = OpenedWalletView()
            // Get some denarii into the wallet text view
            openedWalletView.setBalance("1")
            let _ = openedWalletView.refreshBalance()
            // The refresh of balance with the real api never changes the balance because the call fails
            XCTAssert(openedWalletView.getBalance() == "1")
        }
    }
    
    func testOpenedWalletSendDenariiPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            let openedWalletView = OpenedWalletView()
            // Get some denarii into the wallet text view
            openedWalletView.setBalance("10")
            let _ = openedWalletView.sendDenarii()
            // Nothing should be sent because the amountToSend text view is empty
            XCTAssert(openedWalletView.getBalance() == "10")
        }
    }
    
    func testRestoreDeterministicWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(RestoreDeterministicWalletView().attemptSubmit() == false)
        }
    }
    
    func testRequestResetPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(RequestResetView().attemptRequest() == false)
        }
    }
    
    func testVerifyResetPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Will fail because it will look for a reset id (which isnt set) and will
            // see that it isnt and then return false
            XCTAssert(VerifyResetView().attemptVerifyReset() == false)
        }
    }
    
    func testResetPasswordPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(ResetPasswordView().attemptReset() == false)
        }
    }
    
    func testRegisterPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(RegisterView().attemptSubmit() == false)
        }
    }
    
    func testBuyDenariiPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(BuyDenarii().attemptBuy() == false)
        }
    }
    
    func testSellDenariiPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(SellDenarii().attemptToSellDenarii() == false)
        }
    }
    
    func testVerifyIdentityPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(Verification().attemptSubmit() == false)
        }
    }
    
    func testSetCreditCardInfoPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(CreditCardInfo().attemptToSetCreditCardInfo() == false)
        }
    }
    
    func testCreateSupportTicketPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(CreateSupportTicket().attemptCreateTicket() == false)
        }
    }
    
    func testCreateNewCommentPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(SupportTicketDetails().attemptCreateNewComment() == false)
        }
    }
    
    func testDeleteSupportTicketPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(SupportTicketDetails().attemptDeleteTicket() == false)
        }
    }
    
    func testResolveSupportTicketPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(SupportTicketDetails().attemptResolveTicket() == false)
        }
    }
    
    func testDeleteAccountPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            // Should be false since the real API doesnt work in local mode
            XCTAssert(UserSettings().attemptDeleteAccount() == false)
        }
    }

}
