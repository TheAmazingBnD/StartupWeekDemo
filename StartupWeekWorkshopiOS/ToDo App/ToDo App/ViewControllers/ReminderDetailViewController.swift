//
//  ReminderDetailViewController.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class ReminderDetailViewController: UIViewController {
    
    var reminder: Reminder?
    
    @IBOutlet weak var titleTextField: UITextField!
    @IBOutlet weak var descriptionTextView: UITextView!
    @IBOutlet weak var saveButton: UIButton!
    
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
        descriptionTextView.layer.borderColor = UIColor.lightGray.cgColor
        descriptionTextView.layer.borderWidth = 1
        
        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard)))
    }
    
    @objc private func dismissKeyboard() {
        view.endEditing(true)
    }
}
