//
//  ChangingTextView.swift
//  denarii
//
//  Created by Andrew Katson on 5/31/23.
//

import Foundation
import SwiftUI

struct ChangingTextView : View {
    @Binding var value : String

    var body: some View {
        return Text(value)
    }
}
