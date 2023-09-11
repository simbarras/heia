//
//  QuestionRest.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 22.11.22.
//

import Foundation

struct QuestionRest : Decodable {
    var questions: [Question]
    
    enum CodingKeys: String, CodingKey {
        case questions = "results"
    }
}
