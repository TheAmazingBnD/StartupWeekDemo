//
//  Coordinate.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation

// TODO: may want to make these doubles (+ for N, - for S)
struct Coordinate: Codable, Equatable {
    var lat: String?
    var lon: String?
}
