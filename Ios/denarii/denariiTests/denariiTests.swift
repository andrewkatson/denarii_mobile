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
        openedWalletView.refreshBalance()
        print(openedWalletView.getBalance())
        XCTAssert(openedWalletView.getBalance() == "1")
    }
    
    func testOpenedWalletSendDenarii() throws {
        let openedWalletView = OpenedWalletView()
        // Get some denarii into the wallet
        openedWalletView.setBalance("1")
        openedWalletView.sendDenarii()
        print(openedWalletView.getBalance())
        XCTAssert(openedWalletView.getBalance() == "0")
    }
    
    func testRestoreDeterministicWallet() throws {
        XCTAssert(RestoreDeterministicWalletView().attemptSubmit() == true)
    }
    
    func testRequestReset() throws {
        XCTAssert(RequestResetView().attemptRequest() == true)
    }
    
    func testVerifyReset() throws {
        XCTAssert(VerifyResetView().attemptVerifyReset() == true)
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
            XCTAssert(LoginView().attemptSubmit() == false)
        }
    }
    
    func testCreateWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(CreateWalletView().attemptSubmit() == false)
        }
    }
    
    func testOpenWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(OpenWalletView().attemptSubmit() == false)
        }
    }
    
    func testOpenedWalletRefreshBalancePerformance() throws {
        Constants.DEBUG = false
        self.measure {
            OpenedWalletView().refreshBalance()
        }
    }
    
    func testOpenedWalletSendDenariiPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            OpenedWalletView().sendDenarii()
        }
    }
    
    func testRestoreDeterministicWalletPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RestoreDeterministicWalletView().attemptSubmit() == false)
        }
    }
    
    func testRequestResetPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RequestResetView().attemptRequest() == false)
        }
    }
    
    func testVerifyResetPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(VerifyResetView().attemptVerifyReset() == false)
        }
    }
    
    func testResetPasswordPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(ResetPasswordView().attemptReset() == false)
        }
    }
    
    func testRegisterPerformance() throws {
        Constants.DEBUG = false
        self.measure {
            XCTAssert(RegisterView().attemptSubmit() == false)
        }
    }

}
