//
//  ViewExtensions.swift
//  denarii
//
//  Created by Andrew Katson on 11/7/23.
//

import Foundation
import SwiftUI

extension View {
    @ViewBuilder
    func forceRotation(orientation: UIInterfaceOrientationMask) -> some View {
        if UIDevice.current.userInterfaceIdiom == .phone {
            self.onAppear() {
                AppDelegate.orientationLock = orientation
            }
            // Reset orientation to previous setting
            let currentOrientation = AppDelegate.orientationLock
            self.onDisappear() {
                AppDelegate.orientationLock = currentOrientation
            }
        } else {
            self
        }
    }
}
