//
//  ObservableString.swift
//  denarii
//
//  Created by Andrew Katson on 5/24/23.
//

import Foundation

class ObservableString : ObservableObject {
    private var value: String = ""
    
    func setValue(_ newVal: String) {
        self.value = newVal
    }
    
    func getValue() -> String {
        return self.value
    }
}
