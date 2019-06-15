//
//  FirebaseManager.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

class DatabaseManager {
    
    // shared instance of this class should only use this and not create any other instances of this
    static var shared = DatabaseManager()
    
    // the list of reminders that will be shown on the main screen
    var reminders = [Reminder]()
    
    // we make this private so that no other class can create an instance of this class
    private init() {}
    
    
}
