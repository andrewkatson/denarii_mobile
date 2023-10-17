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
        openedWalletView.refreshBalance()
        XCTAssert(openedWalletView.getBalance() == "1")
    }
    
    func testOpenedWalletSendDenarii() throws {
        let openedWalletView = OpenedWalletView()
        // Get some denarii into the wallet
        openedWalletView.setBalance("10")
        openedWalletView.sendDenarii()
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
    
    func testLoginPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(LoginView().attemptSubmit() == true)
        }
    }
    
    func testCreateWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(CreateWalletView().attemptSubmit() == true)
        }
    }
    
    func testOpenWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(OpenWalletView().attemptSubmit() == true)
        }
    }
    
    func testOpenedWalletRefreshBalancePerformance() throws {
        Constants.DEBUG = false
        self.measure {
            let openedWalletView = OpenedWalletView()
            // Get some denarii into the wallet text view
            openedWalletView.setBalance("1")
            openedWalletView.refreshBalance()
            // The refresh of balance with the stubbed api always sets the balance to 0.0'
            XCTAssert(openedWalletView.getBalance() == "0.0")
        }
    }
    
    func testOpenedWalletSendDenariiPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            let openedWalletView = OpenedWalletView()
            // Get some denarii into the wallet text view
            openedWalletView.setBalance("10")
            openedWalletView.sendDenarii()
            // Nothing should be sent because the amountToSend text view is empty
            XCTAssert(openedWalletView.getBalance() == "10")
        }
    }
    
    func testRestoreDeterministicWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RestoreDeterministicWalletView().attemptSubmit() == true)
        }
    }
    
    func testRequestResetPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RequestResetView().attemptRequest() == true)
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
            XCTAssert(ResetPasswordView().attemptReset() == true)
        }
    }
    
    func testRegisterPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RegisterView().attemptSubmit() == true)
        }
    }

}
