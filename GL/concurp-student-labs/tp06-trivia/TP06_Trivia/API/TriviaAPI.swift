//
//  TriviaAPI.swift
//  TP06_Trivia
//
//  Created by Rafic Galli on 09.07.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import Foundation
import Combine

enum TriviaAPI {
    private static let base = URL(string: "https://opentdb.com/")!
    private static let questionsPath = "/api.php"
    private static let categoriesPath = "/api_category.php"
    private static let agent = Agent()
    
    private static let scheme = "https"
    private static let host = "opentdb.com"
    
    private static func createRequest(path: String, queryItems: [URLQueryItem] = []) -> URLComponents {
        var request = URLComponents()
        request.scheme = scheme
        request.host = host
        request.path = path
        request.queryItems = queryItems
        return request
    }
    
    private static let amountQuery = "amount"
    private static let categoryQuery = "category"
    private static let difficultyQuery = "difficulty"
    private static let typeQuery = "type"
    
    private static let defaultAmount = 10
    
    static func categories () -> AnyPublisher<CategoryRest, Error> {
        return agent.run(createRequest(path: categoriesPath).request!)
    }
    
    static func questions (amount: Int = defaultAmount, category: Category? = nil, difficulty: Difficulty = .any, type: QuestionType = .multipleChoice) -> AnyPublisher<QuestionRest, Error> {
        var queryItems = [URLQueryItem(name: amountQuery, value: "\(amount)")]
        
        if category != nil {
            queryItems.append(URLQueryItem(name: categoryQuery, value: "\(category!.id)"))
        }
        
        if difficulty != .any {
            queryItems.append(URLQueryItem(name: difficultyQuery, value: difficulty.rawValue))
        }
        
        if type != .any {
            queryItems.append(URLQueryItem(name: typeQuery, value: type.rawValue))
        }
        
        return agent.run(createRequest(path: questionsPath, queryItems: queryItems).request!)
    }
}

extension TriviaAPI {
    enum Difficulty: String {
        case any = ""
        case easy = "easy"
        case medium = "medium"
        case hard = "hard"
    }
    
    enum QuestionType: String {
        case any = ""
        case multipleChoice = "multiple"
        case trueFalse = "boolean"
    }
}

private extension URLComponents {
    var request: URLRequest? {
        url.map { URLRequest.init(url: $0) }
    }
}
