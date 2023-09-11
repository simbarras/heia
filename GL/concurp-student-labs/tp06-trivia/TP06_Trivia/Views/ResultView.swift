//
//  ResultView.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 29.11.22.
//

import SwiftUI

struct ResultView: View {
    @ObservedObject public var questionViewModel : QuestionsViewModel
    
    var body: some View {
        VStack{
            Image(questionViewModel.medal).resizable().frame(width: 175, height: 175)
            Spacer().frame(height: 40)
            Text(questionViewModel.text)
            Spacer().frame(height: 75)
            Text("Score")
                 Text(questionViewModel.score)
        }.navigationTitle("Result")
    }
}
