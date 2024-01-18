//
//  ObservableDouble.swift
//  denarii
//
//  Created by Andrew Katson on 1/18/24.
//

import Foundation

class ObservableDouble : ObservableObject {
    @Published var value: Double = 0.0
    
    func setValue(_ newVal: Double) {
        self.value = newVal
    }
    
    func getValue() -> Double {
        return self.value
    }
}
