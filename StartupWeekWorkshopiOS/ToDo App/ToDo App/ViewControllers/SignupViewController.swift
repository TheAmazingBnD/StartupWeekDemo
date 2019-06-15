//
//  SignupViewController.swift
//  ToDo App
//
//  Created by Erik Lasky on 6/12/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class SignupViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        AuthenticationManager.shared.signUp(with: "andrewfoghel@gmail.com", password: "tester123", firstName: "Drew", lastName: "Tester") { (user, error) in
            if let err = error {
                print("\(err.localizedDescription)")
                return
            }
            
            guard let user = user else {
                return
            }
            
            DatabaseManager.shared.putUser(user: user)
        }
    }
}
