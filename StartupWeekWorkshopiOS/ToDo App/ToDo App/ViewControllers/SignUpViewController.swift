//
//  SignUpDetailViewController.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/15/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import UIKit

// TODO: handle confirm password
// TODO: handle alerting errors

class SignUpDetailViewController: UIViewController {
    
    @IBOutlet weak var emailTextField: UITextField!
    @IBOutlet weak var firstNameTextField: UITextField!
    @IBOutlet weak var lastNameTextField: UITextField!
    @IBOutlet weak var passwordTextField: UITextField!
    @IBOutlet weak var confirmPasswordTextField: UITextField!
    
    @IBAction func signUpButtonTapped(_ sender: Any) {
        AuthenticationManager.shared.signUp(with: emailTextField.text,
                                            password: passwordTextField.text,
                                            firstName: firstNameTextField.text ?? "",
                                            lastName: lastNameTextField.text ?? "") { [weak self] user, error in
                                                if let err = error {
                                                    self?.presentAlert(for: err, title: "Unable To Sign In")
                                                    return
                                                }

                                                DatabaseManager.shared.putUser(user: user)
                                                self?.dismiss(animated: true, completion: nil)
        }
    }
}
