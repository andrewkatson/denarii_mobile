//
//  RadioButton.swift
//  denarii
//
//  Created by Andrew Katson on 10/1/23.
//

import Foundation
import UIKit

class RadioButton: UIButton {
    var alternateButton:Array<RadioButton>?
    
    override func awakeFromNib() {
        self.layer.cornerRadius = 5
        self.layer.borderWidth = 2.0
        self.layer.masksToBounds = true
    }
    
    func unselectAlternateButtons() {
        if alternateButton != nil {
            self.isSelected = true
            
            for aButton:RadioButton in alternateButton! {
                aButton.isSelected = false
            }
        } else {
            toggleButton()
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        unselectAlternateButtons()
        super.touchesBegan(touches, with: event)
    }
    
    func toggleButton() {
        self.isSelected = !isSelected
    }
    
    override var isSelected: Bool {
        didSet {
            if isSelected {
                // Blue
                self.layer.borderColor = CGColor.init(red: CGFloat(0.0), green: CGFloat(202.0), blue: CGFloat(255.0), alpha: CGFloat(100.0))
            } else {
                // Gray
                self.layer.borderColor = CGColor.init(red: CGFloat(63.0), green: CGFloat(63.0), blue: CGFloat(63.0), alpha: CGFloat(100.0))
            }
        }
    }
}
