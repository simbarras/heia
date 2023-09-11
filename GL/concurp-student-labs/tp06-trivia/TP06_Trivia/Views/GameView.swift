//
//  GameView.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 29.11.22.
//

import SwiftUI

struct GameView: View {
    @ObservedObject private var questionViewModel : QuestionsViewModel
    
    init(questionViewModel: QuestionsViewModel) {
        self.questionViewModel = questionViewModel
    }
        
    var body: some View {
        if questionViewModel.state == .Idle {
            LoadingView().onAppear(perform: questionViewModel.fetchQuestions)
        } else if questionViewModel.state == .Downloaded || questionViewModel.state == .Replay {
            if questionViewModel.goToResult {
                ResultView(questionViewModel: questionViewModel)
            } else {
                question
            }
        } else {
            LoadingView()
        }
    }
    
    var question: some View {
        NavigationStack {
            ScrollView {
                VStack {
                    Text(questionViewModel.categoryTitle)
                        .padding(.top, 20)
                        .padding(10)
                        .bold()
                        .font(.system(size: 20))
                    Text(questionViewModel.difficulty)
                        .padding(10)
                        .foregroundColor(questionViewModel.difficultyColor)
                    Text(questionViewModel.question).padding(20)
                    ForEach(0..<4) { i in
                        Button {
                            questionViewModel.checkAnswer(index: i)
                        }
                    label: {
                        CardView(color: questionViewModel.backgroundColor[i]) {
                            VStack {
                                Text(questionViewModel.answers[i])
                                    .padding(20)
                                    .foregroundColor(Color.black)
                            }
                        }
                        .padding(.bottom, 5)
                    }
                }
                    
                }
            }.navigationTitle(questionViewModel.pageTitle)
        }
    }
}
