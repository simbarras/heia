//
//  QuestionDB.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 27.12.22.
//

import Foundation
import CoreData

@objc(QuestionDB)
public class QuestionDB: NSManagedObject, Identifiable {
    @nonobjc public class func fetchRequest() -> NSFetchRequest<QuestionDB> {
        return NSFetchRequest<QuestionDB>(entityName: "QuestionDB")
    }
    
    @NSManaged public var id: UUID
    
    @NSManaged public var category: String
    @NSManaged public var type: String
    @NSManaged public var difficulty: String
    @NSManaged public var question: String
    
    @NSManaged public var answer1: String
    @NSManaged public var answer2: String
    @NSManaged public var answer3: String
    @NSManaged public var answer4: String
    
    @NSManaged public var correctAnswer: Int
    @NSManaged public var givenAnswer: Int
    
    static func saveQuestion(in context: NSManagedObjectContext, question: Question) -> QuestionDB {
        let toSave = QuestionDB(context: context)
        toSave.id = question.id
        
        toSave.category = question.category
        toSave.type = question.type
        toSave.difficulty = question.difficulty
        toSave.question = question.question
        
        toSave.answer1 = question.answers[0]
        toSave.answer2 = question.answers[1]
        toSave.answer3 = question.answers[2]
        toSave.answer4 = question.answers[3]
        
        toSave.correctAnswer = question.correctIndex
        toSave.givenAnswer = question.givenAnswer!
        
        return toSave
    }
    
    static func getQuestion(in context: NSManagedObjectContext, question: QuestionDB) -> Question {
        let answersArr = [question.answer1, question.answer2, question.answer3, question.answer4]
        let toReturn = Question(
            category: question.category,
            type: question.type,
            difficulty: question.difficulty,
            question: question.question,
            correctIndex: question.correctAnswer,
            givenAnswer: question.givenAnswer,
            answers: answersArr
        )
        
        return toReturn
    }
}
