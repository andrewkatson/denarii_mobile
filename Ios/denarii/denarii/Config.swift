//
//  Config.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

struct Config {
    var api: API {
        get {
            return UITesting() ? StubbedAPI() : RealAPI()
        }
    }
}

private func UITesting() -> Bool {
    return ProcessInfo.processInfo.arguments.contains("UI-TESTING")
}
