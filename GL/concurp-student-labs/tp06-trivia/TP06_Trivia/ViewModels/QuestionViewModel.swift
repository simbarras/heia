//
//  QuestionViewModel.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 06.12.22.
//

import Foundation
import Combine
import SwiftUI
import CoreData

class QuestionsViewModel : ObservableObject {
    var category: Category
    private var questions = [Question]();
    
    enum State {
        case Idle, Downloading, Downloaded, Replay
    }
    
    private let context: NSManagedObjectContext
    
    @Published private(set) var state = State.Idle
    @Published var pageTitle: String = "Replay" // Defaults to "Replay" when clicked question from history, gets overwritten if not called there
    
    @Published var categoryTitle: String = "Test"
    @Published var difficulty: String = "Test"
    @Published var question: String = "Test"
    @Published var answers: [String] = ["Test", "Test", "Test", "Test"]
    
    @Published var difficultyColor: Color = Color.black
    @Published var backgroundColor: [Color] = [Color.white, Color.white, Color.white, Color.white]
    @Published var goToResult: Bool = false
    
    var nbCorrectAnswers: Int = 0
        
    var givenAnswer: Int = 0
    
    var crtQuestionIndex: Int = 0
    
    private var cancellableSet = Set<AnyCancellable>()
    
    // Variables used for ResultView
    let staticScoreText = "/10"
    
    @Published var medal    =   "MedalFail"
    @Published var text     =   "Placeholder"
    @Published var score    =   "0/10"
    
    init(category: Category) {
        self.category = category
        self.context = PersistenceController.shared.container.viewContext
    }
    
    init(savedQuestion: Question) {
        self.context = PersistenceController.shared.container.viewContext
        self.category = Category(id: -1, name: savedQuestion.category)
        
        self.state = .Replay
        
        self.categoryTitle = savedQuestion.category
        self.difficulty = savedQuestion.difficulty
        setDifficultyColor(difficulty: self.difficulty)
        self.question = savedQuestion.question
        self.answers = savedQuestion.answers
        
        if savedQuestion.givenAnswer == savedQuestion.correctIndex {
            self.backgroundColor[savedQuestion.givenAnswer!] = Color.green
        } else {
            self.backgroundColor[savedQuestion.correctIndex] = Color.green
            self.backgroundColor[savedQuestion.givenAnswer!] = Color.red
        }
        
    }
    
    
    func fetchQuestions() {
        if state != .Replay {
            state = .Downloading
            TriviaAPI.questions(category: category)
                .sink(receiveCompletion: { _ in }, receiveValue: {
                    self.questions = $0.questions
                    // Set current values passed to View to first question
                    self.categoryTitle = self.questions[0].category

                    self.difficulty = self.questions[0].difficulty
                    self.setDifficultyColor(difficulty: self.difficulty)
                    self.question = self.questions[0].question
                    self.answers = self.questions[0].answers
                    self.givenAnswer = self.questions[0].givenAnswer ?? 0
                    //questionId = questions[0].id
                    self.setTitle()
                    self.state = .Downloaded
            })
                .store(in: &cancellableSet)
        }
    }
    
    func nextQuestion() {
        self.backgroundColor = [Color.white, Color.white, Color.white, Color.white]
        
        crtQuestionIndex += 1
        if crtQuestionIndex < questions.count {
            self.setTitle()
            
            categoryTitle = questions[crtQuestionIndex].category
            difficulty = questions[crtQuestionIndex].difficulty
            setDifficultyColor(difficulty: difficulty)
            question = questions[crtQuestionIndex].question
            answers = questions[crtQuestionIndex].answers
        } else {
            // No more questions
            self.setFinalValues()
            saveValuesforStats(key: "triviaCompleted", value: 1)
            self.goToResult = true
            
        }
    }
    
    func checkAnswer(index: Int) {
        if self.state == .Replay {
            return
        }
        
        var ok = index == questions[crtQuestionIndex].correctIndex
        questions[crtQuestionIndex].setGivenAnswer(index: index)
        
        saveValuesforStats(key: "answeredQuestions", value: 1)
        
        if ok {
            self.backgroundColor[questions[crtQuestionIndex].correctIndex] = Color.green
            self.nbCorrectAnswers += 1
            saveValuesforStats(key: "correctAnswers", value: 1)
        } else {
            self.backgroundColor[questions[crtQuestionIndex].correctIndex] = Color.green
            self.backgroundColor[questions[crtQuestionIndex].givenAnswer ?? 1] = Color.red
        }
        
        // Save question to DB
        saveQuestion(question: self.questions[crtQuestionIndex])
        
        // Wait 2 seconds before next question
        Timer.scheduledTimer(withTimeInterval: 2, repeats: false) {
            _ in self.nextQuestion()
        }
    }
    
    func setFinalValues() {
        let score = Double(nbCorrectAnswers) / Double(questions.count) * 100
        switch score {
        case 80...100:
            self.medal = "MedalGold"
            self.text = "Awesome!"
        case 50...79:
            self.medal = "MedalSilver"
            self.text = "Pretty good!"
        case 25...49:
            self.medal = "MedalBronze"
            self.text = "You could do better.."
        default:
            self.medal = "MedalFail"
            self.text = "You are pretty bad huh?"
        }
        self.score = String(nbCorrectAnswers) + staticScoreText
        
        saveValuesforStats(key: self.medal, value: 1)
    }
    
    func saveQuestion(question: Question) {
        _ = QuestionDB.saveQuestion(in: self.context, question: question)
        do {
            try context.save()
        } catch {
            print("Error while saving Question to DB")
        }
    }
    
    // increments field in UserDefaults with key=key by increment=value
    func saveValuesforStats(key: String, value: Int) {
        UserDefaults.standard.set(
            UserDefaults.standard.integer(forKey: key) + value, forKey: key
        )
    }
    
    func setDifficultyColor(difficulty: String) {
        switch difficulty {
        case "easy": self.difficultyColor = Color.green
        case "medium": self.difficultyColor = Color.yellow
        case "hard": self.difficultyColor = Color.red
        default: self.difficultyColor = Color.black
        }
    }
    
    func setTitle() {
        self.pageTitle = "Question " + String(crtQuestionIndex + 1) + "/" + String(self.questions.count)
    }
}
