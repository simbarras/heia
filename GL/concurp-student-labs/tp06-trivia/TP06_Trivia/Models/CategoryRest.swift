//
//  QuestionAPI.swift
//  TP06_Trivia
//
//  Created by Rafic Galli on 10.07.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import Foundation

struct CategoryRest : Decodable {
    var categories: [Category]
    
    enum CodingKeys: String, CodingKey {
        case categories = "trivia_categories"
    }
}
