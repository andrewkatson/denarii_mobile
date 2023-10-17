//
//  Config.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

struct Config {
    
    static let _api : API = UITesting() ? StubbedAPI() : RealAPI()
    
    static var api: API {
        get {
            return _api
        }
    }
}

private func UITesting() -> Bool {
    return ProcessInfo.processInfo.arguments.contains("UI-TESTING")
}
