//
//  Reminder.swift
//  ToDo App
//
//  Created by Andrew Foghel on 6/14/19.
//  Copyright © 2019 Erik Lasky. All rights reserved.
//

import Foundation
import Firebase

struct Reminder: Equatable {
    var id: String
    var title: String?
    var description: String?
    var timestamp: TimeInterval
    var isComplete: Bool
}

extension Reminder {
    func toDictionary() -> [String : Any] {
        return ["title" : title ?? "",
                "description" : description ?? "",
                "timestamp" : timestamp,
                "isComplete" : isComplete]
    }
}

extension DataSnapshot {
    func toReminder(with id: String) -> Reminder? {
        guard let dict = self.value as? [String : Any] else {
            return nil
        }
        
        let title = dict["title"] as? String
        let description = dict["description"] as? String
        let timestamp = dict["timestamp"] as? TimeInterval ?? Date().timeIntervalSince1970
        let isComplete = dict["isComplete"] as? Bool ?? false
        
        return Reminder(id: id,
                        title: title,
                        description: description,
                        timestamp: timestamp,
                        isComplete: isComplete)
    }
}
