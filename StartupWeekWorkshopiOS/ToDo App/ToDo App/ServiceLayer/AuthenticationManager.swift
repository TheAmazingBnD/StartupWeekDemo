//
//  AuthenticationManager.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

class AuthenticationManager {
    
    static var shared = AuthenticationManager()
    
    private(set) var user: User?
    
    func signUp(with email: String?, password: String?, firstName: String, lastName: String, completion: @escaping (User?, Error?) -> ()) {
        /// unwrap email and password
        guard let email = email,
            let password = password else {
                return
        }
        
        /// Create a new Firebase user using email and password
        Auth.auth().createUser(withEmail: email, password: password) { [weak self] result, error in
            if let err = error {
                print("Error Signing In: \(err.localizedDescription)")
                completion(nil, err)
                return
            }
            
            guard let result = result else {
                print("Error Signing In No User Data")
                completion(nil, nil)
                return
            }
            
            let user = User(uid: result.user.uid, email: email, firstName: firstName, lastName: lastName)
            
            /// save reference of user to UserDefaults
            user.saveToDefaults()
            
            /// set shared instance user to newly created user
            self?.user = user
            completion(user, nil)
        }
    }
    
    func login(with email: String?, password: String?, completion: @escaping (User?, Error?) -> ()) {
        /// unwrap email and password
        guard let email = email,
            let password = password else {
                return
        }
        
        /// Sign in with an existing Firebase user using email and password
        Auth.auth().signIn(withEmail: email, password: password) { [weak self] result, error in
            if let err = error {
                print("Error Signing In: \(err.localizedDescription)")
                completion(nil, err)
                return
            }
            
            guard let result = result else {
                print("Error Signing In No User Data")
                completion(nil, nil)
                return
            }
            
            /// Fetch the user data that is save in the database
            DatabaseManager.shared.fetchUser(with: result.user.uid, completion: { [weak self] user, error in
                if let err = error {
                    completion(nil, err)
                    return
                }
                
                /// save reference of user to UserDefaults
                user?.saveToDefaults()
                
                /// set shared instance user to newly created user
                self?.user = user
                completion(user, nil)
            })
        }
    }
    
    func fetchCurrentUser(with uid: String?, completion: @escaping (User?, Error?) -> ()) {
        guard let uid = uid else {
            return
        }
        
        DatabaseManager.shared.fetchUser(with: uid) { [weak self] user, error in
            self?.user = user
            completion(user, error)
        }
    }
}
