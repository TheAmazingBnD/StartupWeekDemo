//
//  ReminderTableViewCell.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class ReminderTableViewCell: UITableViewCell {
    
    var reminder: Reminder? {
        didSet {
            update()
        }
    }
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var statusLabel: UILabel!
    
    private func update() {
        titleLabel.text = reminder?.title
        descriptionLabel.text = reminder?.description
        
        if reminder?.isComplete == true {
            statusLabel.text = "Complete"
            statusLabel.textColor = #colorLiteral(red: 0.3084011078, green: 0.5618229508, blue: 0, alpha: 1)
        }
        else {
            statusLabel.text = "Incomplete"
            statusLabel.textColor = .red
        }
    }
}
