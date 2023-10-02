//
//  ObservableBool.swift
//  denarii
//
//  Created by Andrew Katson on 5/28/23.
//

import Foundation

class ObservableBool : ObservableObject {
    @Published var value: Bool = false
    
    func setValue(_ newVal: Bool) {
        self.value = newVal
    }
    
    func getValue() -> Bool {
        return self.value
    }
}
