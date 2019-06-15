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
        guard let email = email,
            let password = password else {
                return
        }
        
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
            
            let user = self?.createUser(with: result.user.uid,
                                        email: email,
                                        firstName: firstName,
                                        lastName: lastName)
            user?.saveToDefaults()
            self?.user = user
            completion(user, nil)
        }
    }
    
    func login(with email: String?, password: String?, completion: @escaping (User?, Error?) -> ()) {
        guard let email = email,
            let password = password else {
                return
        }
        
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
            
            DatabaseManager.shared.fetchUser(with: result.user.uid, completion: { [weak self] user, error in
                if let err = error {
                    completion(nil, err)
                    return
                }
                
                user?.saveToDefaults()
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
    
    private func createUser(with uid: String, email: String, firstName: String, lastName: String) -> User {
        let user = User(uid: uid, email: email, firstName: firstName, lastName: lastName)
        return user
    }
    
}
