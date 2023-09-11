//
//  ProfileViewModel.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 24.12.22.
//

import Foundation

class ProfileViewModel: ObservableObject {
    @Published var nbGolds  = 0
    @Published var nbSilver = 0
    @Published var nbBronze = 0
    
    @Published var rating   = "0"
    @Published var triviaCompleted      =   0
    @Published var answeredQuestions    =   0
    @Published var correctAnswers       =   0
    
    func fetchProfileData() {
        self.nbGolds = UserDefaults.standard.integer(forKey: "MedalGold")
        self.nbSilver = UserDefaults.standard.integer(forKey: "MedalSilver")
        self.nbBronze = UserDefaults.standard.integer(forKey: "MedalBronze")
        self.triviaCompleted = UserDefaults.standard.integer(forKey: "triviaCompleted")
        self.answeredQuestions = UserDefaults.standard.integer(forKey: "answeredQuestions")
        self.correctAnswers = UserDefaults.standard.integer(forKey: "correctAnswers")
        
        if self.answeredQuestions == 0 {
            self.rating = "0%"
        } else {
            self.rating = String(100 * self.correctAnswers / self.answeredQuestions) + "%"
        }

    }
}
