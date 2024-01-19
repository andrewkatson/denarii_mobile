//
//  InputPatternValidator.swift
//  denarii
//
//  Created by Andrew Katson on 1/18/24.
//

import Foundation


func is_valid_input(_ text: String, _ pattern: Regex<Substring>) -> Bool {
    return text.contains(pattern)
}

func is_valid_input(_ text: String, _ pattern: Regex<(Substring, Substring?)>) -> Bool {
    return text.contains(pattern)
}
