//
//  NavigationCoordinator.swift
//  denarii
//
//  Created by Andrew Katson on 11/10/23.
//

import Foundation
import SwiftUI

class NavigationCoordinator: ObservableObject {
    @Published var path = NavigationPath()

    func show<V>(_ viewType: V.Type) where V: View {
        path.append(String(describing: viewType.self))
    }

    func popToRoot() {
        path.removeLast(path.count)
    }
}
