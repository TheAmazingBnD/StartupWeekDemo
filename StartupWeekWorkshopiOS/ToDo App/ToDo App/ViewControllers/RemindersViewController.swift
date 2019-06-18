//
//  RemindersViewController.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class RemindersViewController: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    var reminders = [Reminder]()
    var selectedReminder: Reminder?
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if isViewLoaded {
            fetchReminders()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let uid = UserDefaults.standard.value(forKey: "me") as? String {
            AuthenticationManager.shared.fetchCurrentUser(with: uid) { [weak self] user, error in
                if let _ = error {
                    self?.performSegue(withIdentifier: "AuthenticateUser", sender: nil)
                    return
                }
                

                self?.fetchReminders()
            }
        }
        else {
            performSegue(withIdentifier: "AuthenticateUser", sender: nil)
        }
    }
}

// UITableView

extension RemindersViewController: UITableViewDelegate, UITableViewDataSource {
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return reminders.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as? ReminderTableViewCell else {
            return UITableViewCell()
        }
        
        let reminder = reminders[indexPath.row]
        cell.reminder = reminder
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.cellForRow(at: indexPath)?.isSelected = false
        selectedReminder = reminders[indexPath.row]
        performSegue(withIdentifier: "EditReminder", sender: nil)
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let removedReminder = reminders.remove(at: indexPath.row)
            delete(reminder: removedReminder)
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "EditReminder",
            let destination = segue.destination as? ReminderDetailViewController {
            destination.reminder = selectedReminder
        }
    }
}

// Database Manager

extension RemindersViewController {
    private func fetchReminders() {
        DatabaseManager.shared.fetchCurrentUserReminders(completion: { [weak self] reminders, error in
            if let err = error {
                self?.presentAlert(for: err, title: "Unable To Fetch Posts")
                return
            }
            
            
            guard let reminders = reminders else {
                return
            }
            
            self?.reminders = reminders
            
            DispatchQueue.main.async { [weak self] in
                self?.tableView.reloadData()
            }
        })
    }
    
    private func delete(reminder: Reminder?) {
        guard let reminder = reminder else {
            return
        }
        
        DatabaseManager.shared.deleteReminder(id: reminder.id) { [weak self] error in
            if let err = error {
                self?.presentAlert(for: err, title: "Unable To Delete Reminder")
                return
            }
            
            DispatchQueue.main.async { [weak self] in
                self?.tableView.reloadData()
            }
        }
    }
}
