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
    @IBOutlet weak var completeButton: UIButton!
    
    @IBAction func saveButtonTapped(_ sender: Any) {
        save(reminder: reminder)
    }
    
    @IBAction func completeButtonTapped(_ sender: Any) {
        guard let reminder = reminder else {
            return
        }
        
        self.reminder?.isComplete = !reminder.isComplete
        toggleButtonState()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        titleTextField.text = reminder?.title
        descriptionTextView.text = reminder?.description
        
        title = reminder != nil ? "Edit Reminder" : "Create Reminder"
        toggleButtonState()
    }
    
    @objc private func dismissKeyboard() {
        view.endEditing(true)
    }
    
    private func save(reminder: Reminder?) {
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
    
    private func toggleButtonState() {
        if reminder?.isComplete == true {
            completeButton.setTitle("Mark As Incomplete", for: .normal)
            completeButton.backgroundColor = #colorLiteral(red: 0.5807225108, green: 0.066734083, blue: 0, alpha: 1)
        }
        else {
            completeButton.setTitle("Mark As Done", for: .normal)
            completeButton.backgroundColor = #colorLiteral(red: 0.3084011078, green: 0.5618229508, blue: 0, alpha: 1)
        }
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return reminder == nil ? 2 : 3
    }
}
