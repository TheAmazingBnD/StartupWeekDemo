//
//  User.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

struct User: Equatable {
    var uid: String?
    var email: String?
    var firstName: String?
    var lastName: String?
}

extension User {
    func toDictionary() -> [String : Any] {
        return ["email" : email ?? "",
                "firstName" : firstName ?? "",
                "lastName" : lastName ?? ""]
    }
    
    func saveToDefaults() {
        UserDefaults.standard.set(self.uid, forKey: "me")
    }
}

extension DataSnapshot {
    func toUser(with uid: String) -> User? {
        guard let dict = self.value as? [String : Any] else {
            return nil
        }
        
        let email = dict["email"] as? String
        let firstName = dict["firstName"] as? String
        let lastName = dict["lastName"] as? String
        
        return User(uid: uid,
                    email: email,
                    firstName: firstName,
                    lastName: lastName)
    }
}
