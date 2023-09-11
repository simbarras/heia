//
//  ViewModel.swift
//  TP05_Blackjack
//
//  Created by Claudio Herren on 08.11.22.
//

import Foundation

final class ViewModel: ObservableObject {
    // Data displayed by ContentView
    @Published private(set) var userScores: [Int] = [0]
    @Published private(set) var dealerScores: [Int] = [0]
    @Published private(set) var currentCard = Card.hiddenCard
    @Published private(set) var gameOverText = ""
    @Published private(set) var isGameOver = false
    @Published private(set) var isUserTurn = true
    
    @Published private(set) var textOnUI = ""
    @Published private(set) var counterOnUi = 0

    // Private variables used to manage a BlackJack game
    private var cards = CardUtils.generateCards()
    @Published private(set) var userCards: Array<Card> = [Card.hiddenCard]
    @Published private(set) var dealerCards: Array<Card> = [Card.hiddenCard]
    //@Published private(set) var dealerCards = [Card]()
    

    func newCard() {
        let randCard: Int = Int.random(in: 0 ..< cards.count)
        currentCard = cards[randCard]
        cards.remove(at: randCard)
        var doStay: Bool = true
        if isUserTurn {
            userCards.append(currentCard)
            if let index = userCards.firstIndex(of: Card.hiddenCard) {
                userCards.remove(at: index)
            }
            userScores = CardUtils.getScores(cards: userCards)
            for score in userScores {
                if score < 21 {
                    doStay = false
                }
                if score == 21 {
                    doStay = true
                    break
                }
            }
        } else {
            dealerCards.append(currentCard)
            if let index = dealerCards.firstIndex(of: Card.hiddenCard) {
                dealerCards.remove(at: index)
            }
            dealerScores = CardUtils.getScores(cards: dealerCards)
            for score in dealerScores {
                if score < 17 {
                    doStay = false
                }
                if score >= 17 && score <= 21 {
                    doStay = true
                    break
                }
            }
        }
        if doStay {
            stay()
        }
    }
    
    func stay() {
        if isUserTurn {
            isUserTurn = false
        } else {
            isGameOver = true
            var finalUserScore = -1
            var finalDealerScore = -1
            for score in userScores {
                if score <= 21 && score > finalUserScore {
                    finalUserScore = score
                }
            }
            for score in dealerScores {
                if score <= 21 && score > finalDealerScore {
                    finalDealerScore = score
                }
            }
            if finalUserScore > finalDealerScore {
                gameOverText = "You win!"
            } else if finalUserScore == finalDealerScore {
                gameOverText = "Draw"
            } else {
                gameOverText = "Dealer wins!"
            }
        }
    }
    func replay() {
        isUserTurn = true
        isGameOver = false
        cards = CardUtils.generateCards()
        userCards = [Card.hiddenCard]
        dealerCards = [Card.hiddenCard]
        userScores = CardUtils.getScores(cards: userCards)
        dealerScores = CardUtils.getScores(cards: dealerCards)
        currentCard = Card.hiddenCard
    }
}
