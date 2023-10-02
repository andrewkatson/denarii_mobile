//
//  ObservableInt.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

class ObservableInt:  ObservableObject {
    @Published var value: Int = 0
    
    func setValue(_ newVal: Int) {
        self.value = newVal
    }
    
    func getValue() -> Int {
        return self.value
    }
}
