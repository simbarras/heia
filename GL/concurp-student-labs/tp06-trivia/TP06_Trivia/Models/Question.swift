//
//  Question.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 22.11.22.
//

import Foundation

struct Question : Decodable, Identifiable {
    private (set) var id = UUID()
    
    private (set) var category          : String
    private (set) var type              : String
    private (set) var difficulty        : String
    private (set) var question          : String
    
    private (set) var answers: [String]
    private (set) var correctIndex: Int
    private (set) var givenAnswer: Int?
    
    init(category: String, type: String, difficulty: String, question: String, correctIndex: Int, givenAnswer: Int, answers: [String]) {
            self.category = category
            self.type = type
            self.difficulty = difficulty
            self.question = question
            
            self.answers = answers
            self.correctIndex = correctIndex
            self.givenAnswer = givenAnswer
        }
    
    init(question: Question, givenAnswer: Int) {
        self.init(category: question.category, type: question.type, difficulty: question.difficulty, question: question.question, correctIndex: question.correctIndex, givenAnswer: question.givenAnswer!, answers: question.answers)
    }
    
    enum CodingKeys: String, CodingKey {
        case category
        case type
        case difficulty
        case question
        case correct_answer
        case incorrect_answers
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.category = try container.decode(String.self, forKey: .category)
        self.type = try container.decode(String.self, forKey: .type)
        self.difficulty = try container.decode(String.self, forKey: .difficulty)
        self.question = try container.decode(String.self, forKey: .question).htmlToString()
        let correctAnswer = try container.decode(String.self, forKey: .correct_answer)
        let incorrectAnswers = try container.decode([String].self, forKey: .incorrect_answers)
            
        answers = incorrectAnswers;
        answers.append(correctAnswer);
        answers.shuffle();
        correctIndex = answers.firstIndex(of: correctAnswer)!
            
        for i in 0...answers.count-1 {
            answers[i] = answers[i].htmlToString();
        }
    }
    
    mutating func setGivenAnswer(index: Int) {
        self.givenAnswer = index
    }
}

extension StringProtocol{
    func htmlToString() -> String {
        return try! NSAttributedString(data: self.data(using: .utf8)!, options: [.documentType: NSAttributedString.DocumentType.html],
            documentAttributes: nil).string;
    }
}
