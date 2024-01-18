//
//  InputPatternValidator.swift
//  denarii
//
//  Created by Andrew Katson on 1/18/24.
//

import Foundation


func is_valid_input(_ text: String, _ pattern: String) -> Bool {
    
    do {
        let regexObj = try Regex(pattern)
        
        return regexObj.contains(captureNamed: text)
    } catch {
        return false
    }
}
