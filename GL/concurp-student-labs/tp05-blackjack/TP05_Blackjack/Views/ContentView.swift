//
//  ContentView.swift
//  TP05_Blackjack
//
//  Created by Claudio Herren on 08.11.22.
//

import SwiftUI

struct ContentView: View {
    @ObservedObject private var viewModel = ViewModel()
    
    var body: some View {
        ZStack {
            gameView.opacity(viewModel.isGameOver ? 0.1 : 1)

            if viewModel.isGameOver {
                gameOver
            }
        }
    }
    
    var gameView: some View {
        VStack {
            scores
            Spacer()
            HStack {
                Spacer()
                CardView(cards: viewModel.userCards)
                Spacer()
                CardView(cards: viewModel.dealerCards)
                Spacer()
            }
            Spacer()
            commands
        }
    }
    
    var gameOver: some View {
        VStack {
            Spacer()

            Text("\(viewModel.gameOverText)")
                .font(.largeTitle)
                .foregroundColor(Color.red)

            Spacer()

            Button(action: { self.viewModel.replay() }) {
                Text("Replay").font(.title)
            }
            .frame(height: 50).frame(maxWidth: .infinity)
            .padding()
        }
    }

    var scores: some View {
        HStack {
            VStack {
                Text("Your Score").foregroundColor(viewModel.isUserTurn ? Color.black : Color.gray)

                HStack {
                    // 1
                    ForEach(viewModel.userScores, id: \.self) { score in
                        Text("\(score)").font(.title).foregroundColor(viewModel.isUserTurn ? Color.black : Color.gray)
                    }
                }
            }
            .frame(maxWidth: .infinity)

            VStack {
                Text("Dealer Score").foregroundColor(viewModel.isUserTurn ? Color.gray : Color.black)

                HStack {
                    // 1
                    ForEach(viewModel.dealerScores, id: \.self) { score in
                        Text("\(score)").font(.title).foregroundColor(viewModel.isUserTurn ? Color.gray : Color.black)
                    }
                }
            }
            .frame(maxWidth: .infinity)
        }
        .padding()
    }

    var commands: some View {
        HStack {
            Button(action: { self.viewModel.newCard() }) {
                Text("Card").font(.title)
            }
            .frame(height: 50).frame(maxWidth: .infinity)
            .disabled(viewModel.isGameOver)

            Button(action: { self.viewModel.stay() }) {
                Text("Stay").font(.title)
            }
            .frame(height: 50).frame(maxWidth: .infinity)
            .disabled(viewModel.isGameOver)
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
