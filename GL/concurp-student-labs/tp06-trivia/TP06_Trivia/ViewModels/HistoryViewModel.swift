//
//  HistoryViewModel.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 27.12.22.
//

import Foundation
import CoreData

class HistoryViewModel : ObservableObject {
    private let context: NSManagedObjectContext;
    @Published private (set) var questions = [Question]();
    @Published private (set) var correct = [Bool]();
    
    init() {
        self.context = PersistenceController.shared.container.viewContext
    }
    
    func fetchSaved() {
        let req: NSFetchRequest<QuestionDB> = QuestionDB.fetchRequest()
        self.questions = []
        self.correct = []
        do {
            let questionsDB = try context.fetch(req)
            for questionDB in questionsDB {
                let question = QuestionDB.getQuestion(in: context, question: questionDB)
                self.questions.append(question)
                self.correct.append(question.givenAnswer == question.correctIndex)
            }
        } catch {
            print("Error fetching questions from DB")
        }
    }
    
}
