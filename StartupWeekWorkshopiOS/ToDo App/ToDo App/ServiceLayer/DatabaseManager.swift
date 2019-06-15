//
//  DatabaseManager.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

class DatabaseManager {
    
    enum Nodes: String {
        case users = "Users"
        case reminders = "Reminders"
    }
    
    // shared instance of this class should only use this and not create any other instances of this
    static var shared = DatabaseManager()
    
    // we make this private so that no other class can create an instance of this class
    private init() {}
    
    func putUser(user: User?) {
        guard let user = user,
            let uid = user.uid else {
            return
        }
        
        Database.database().reference().child(Nodes.users.rawValue).child(uid).setValue(user.toDictionary())
    }
    
    func fetchUser(with uid: String?, completion: @escaping (User?, Error?) -> ()) {
        guard let uid = uid else {
            completion(nil, nil)
            return
        }
        
        Database.database().reference().child(Nodes.users.rawValue).child(uid).observeSingleEvent(of: .value, with: { snapshot in
            completion(snapshot.toUser(with: uid), nil)
        }) { error in
            completion(nil, error)
        }
    }
    
    func putReminder(reminder: Reminder?, title: String, description: String?, completion: @escaping (Error?) -> ()) {
        guard let uid = AuthenticationManager.shared.user?.uid else {
            return
        }
        
        let uuid = reminder?.id ?? UUID().uuidString
        let timestamp = reminder?.timestamp ?? Date().timeIntervalSince1970
        let reminder = Reminder(id: uuid, title: title, description: description, timestamp: timestamp)
        Database.database().reference().child(Nodes.reminders.rawValue).child(uid).child(uuid).setValue(reminder.toDictionary()) { (error, reference) in
            completion(error)
        }
    }
    
    func deleteReminder(id: String?, completion: @escaping (Error?) -> ()) {
        guard let uid = AuthenticationManager.shared.user?.uid,
            let id = id else {
            return
        }
        
        Database.database().reference().child(Nodes.reminders.rawValue).child(uid).child(id).removeValue { error, _ in
            completion(error)
        }
    }
    
    func fetchCurrentUserReminders(completion: @escaping ([Reminder]?, Error?) -> ()) {

        guard let uid = AuthenticationManager.shared.user?.uid else {
            return
        }
        
        Database.database().reference().child(Nodes.reminders.rawValue).child(uid).observe(.value, with: { snapshot in
            guard let children = snapshot.children.allObjects as? [DataSnapshot] else {
                return
            }
            
            var reminders = [Reminder]()
            
            for child in children {
                guard let reminder = child.toReminder(with: child.key) else {
                    continue
                }
                reminders.append(reminder)
            }
            
            reminders = reminders.sorted(by: { lhs, rhs -> Bool in
                return lhs.timestamp > rhs.timestamp
            })
            
            completion(reminders, nil)
        }) { error in
            completion(nil, error)
        }
        
    }
}
