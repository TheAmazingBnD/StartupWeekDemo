//
//  Reminder.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation

struct Reminder: Codable, Equatable {
    var title: String?
    var description: String?
    var coordinate: Coordinate?
}
