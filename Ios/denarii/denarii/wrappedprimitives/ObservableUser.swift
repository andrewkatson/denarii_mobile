//
//  ObservableUser.swift
//  denarii
//
//  Created by Andrew Katson on 10/25/23.
//

import Foundation

class ObservableUser : ObservableObject {
    @Published var value: UserDetails = UserDetails()
    
    func setValue(_ newVal: UserDetails) {
        self.value = newVal
    }
    
    func getValue() -> UserDetails {
        return self.value
    }
}
