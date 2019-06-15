//
//  ReminderDetailViewController.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class ReminderDetailViewController: UITableViewController {
    
    var reminder: Reminder?
    
    @IBOutlet weak var titleTextField: UITextField!
    @IBOutlet weak var descriptionTextView: UITextView!
    
    @IBAction func saveButtonTapped(_ sender: Any) {
        guard let title = titleTextField.text,
            let description = descriptionTextView.text else {
                return
        }
        
        DatabaseManager.shared.putReminder(reminder: reminder, title: title, description: description) { [weak self] error in
            if let err = error {
                self?.presentAlert(for: err, title: "Unable To Add Reminder")
                return
            }
            
            self?.navigationController?.popViewController(animated: true)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        titleTextField.text = reminder?.title
        descriptionTextView.text = reminder?.description
        
        title = reminder != nil ? "Edit Reminder" : "Create Reminder"
    }
    
    @objc private func dismissKeyboard() {
        view.endEditing(true)
    }
}
