//
//  HistoryView.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 29.11.22.
//

import SwiftUI

struct HistoryView: View {
    @ObservedObject private var historyViewModel: HistoryViewModel
    
    init() {
        self.historyViewModel = HistoryViewModel()
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                ForEach(Array(historyViewModel.questions.enumerated()), id: \.offset) { i, question in
                    NavigationLink {
                        DeferView{
                            GameView(questionViewModel: QuestionsViewModel(savedQuestion: question))
                            // TODO - Implement replay mode in GameView
                        }
                    } label: {
                        CardView() {
                            HStack {
                                VStack(alignment: .leading) {
                                    Text(question.category)
                                        .foregroundColor(Color.black)
                                        .bold()
                                        .padding(.vertical, 5)
                                        .lineLimit(1)
                                    Text(question.question)
                                        .foregroundColor(Color.black)
                                        .lineLimit(1)
                                }.padding(10)
                                Spacer()
                                if historyViewModel.correct[i] {
                                    Image(systemName: "checkmark.square.fill").foregroundColor(Color.green)
                                } else {
                                    Image(systemName: "xmark.square.fill").foregroundColor(Color.red)
                                }
                            }
                            .padding(.horizontal, 10)
                        }
                    }
                }
            }.navigationTitle("History").onAppear(perform: historyViewModel.fetchSaved)
        }
    }
}
