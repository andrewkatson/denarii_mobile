//
//  ObservableArray.swift
//  denarii
//
//  Created by Andrew Katson on 10/30/23.
//

import Foundation

class ObservableArray<T> : ObservableObject {
    @Published var value: Array<T> = Array()
    
    func setValue(_ newVal: Array<T>) {
        self.value = newVal
    }
    
    func getValue() -> Array<T> {
        return self.value
    }
}
