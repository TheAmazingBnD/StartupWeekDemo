//
//  LoginViewController.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    
    @IBAction func loginButtonTapped(_ sender: Any) {
        AuthenticationManager.shared.login(with: emailTextField.text, password: passwordTextField.text) { [weak self] user, error in
            if let err = error {
                self?.presentAlert(for: err, title: "Unable To Login")
                return
            }
            
            guard let _ = user else {
                return
            }
            
            self?.dismiss(animated: true, completion: nil)
        }
    }
}
