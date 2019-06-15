//
//  User.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

struct User {
    var uid: String?
    var email: String?
    var firstName: String?
    var lastName: String?
}

extension User {
    func toDictionary() -> [String : Any] {
        return ["email" : email ?? "", "firstName" : firstName ?? "", "lastName" : lastName ?? ""]
    }
}
