//
//  CardUtils.swift
//  TD00_BlackJack
//
//  Created by Rafic Galli on 11.08.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import Foundation

struct CardUtils {
    static func generateCards() -> [Card] {
        var cards: [Card] = []
        
        for cardType in CardType.allCases {
            if cardType != .back {
                cards.append(Card(cardType: cardType))
            }
        }
        
        return cards
    }
    
    static func getScores(cards: [Card]) -> [Int] {
        var minScore = 0
        var aceCount = 0
        
        for card in cards {
            if card.value == 1 {
                aceCount += 1
            }
            
            minScore += card.value
        }
        
        var scores = [ minScore ]
        
        for i in 0 ..< aceCount {
            scores.append(scores[i] + 10)
        }
        
        return scores
    }
    
    static func generateCard(cardType: CardType) -> Card {
        switch cardType {
        case .back:
            return Card(symbol: .hidden, value: 0, image: "back")
        case .clubAce:
            return Card(symbol: .club, value: 1, image: "club_1")
        case .clubTwo:
            return Card(symbol: .club, value: 2, image: "club_2")
        case .clubThree:
            return Card(symbol: .club, value: 3, image: "club_3")
        case .clubFour:
            return Card(symbol: .club, value: 4, image: "club_4")
        case .clubFive:
            return Card(symbol: .club, value: 5, image: "club_5")
        case .clubSix:
            return Card(symbol: .club, value: 6, image: "club_6")
        case .clubSeven:
            return Card(symbol: .club, value: 7, image: "club_7")
        case .clubEight:
            return Card(symbol: .club, value: 8, image: "club_8")
        case .clubNine:
            return Card(symbol: .club, value: 9, image: "club_9")
        case .clubTen:
            return Card(symbol: .club, value: 10, image: "club_10")
        case .clubJack:
            return Card(symbol: .club, value: 10, image: "club_jack")
        case .clubQueen:
            return Card(symbol: .club, value: 10, image: "club_queen")
        case .clubKing:
            return Card(symbol: .club, value: 10, image: "club_king")
        case .diamondAce:
            return Card(symbol: .diamond, value: 1, image: "diamond_1")
        case .diamondTwo:
            return Card(symbol: .diamond, value: 2, image: "diamond_2")
        case .diamondThree:
            return Card(symbol: .diamond, value: 3, image: "diamond_3")
        case .diamondFour:
            return Card(symbol: .diamond, value: 4, image: "diamond_4")
        case .diamondFive:
            return Card(symbol: .diamond, value: 5, image: "diamond_5")
        case .diamondSix:
            return Card(symbol: .diamond, value: 6, image: "diamond_6")
        case .diamondSeven:
            return Card(symbol: .diamond, value: 7, image: "diamond_7")
        case .diamondEight:
            return Card(symbol: .diamond, value: 8, image: "diamond_8")
        case .diamondNine:
            return Card(symbol: .diamond, value: 9, image: "diamond_9")
        case .diamondTen:
            return Card(symbol: .diamond, value: 10, image: "diamond_10")
        case .diamondJack:
            return Card(symbol: .diamond, value: 10, image: "diamond_jack")
        case .diamondQueen:
            return Card(symbol: .diamond, value: 10, image: "diamond_queen")
        case .diamondKing:
            return Card(symbol: .diamond, value: 10, image: "diamond_king")
        case .heartAce:
            return Card(symbol: .heart, value: 1, image: "heart_1")
        case .heartTwo:
            return Card(symbol: .heart, value: 2, image: "heart_2")
        case .heartThree:
            return Card(symbol: .heart, value: 3, image: "heart_3")
        case .heartFour:
            return Card(symbol: .heart, value: 4, image: "heart_4")
        case .heartFive:
            return Card(symbol: .heart, value: 5, image: "heart_5")
        case .heartSix:
            return Card(symbol: .heart, value: 6, image: "heart_6")
        case .heartSeven:
            return Card(symbol: .heart, value: 7, image: "heart_7")
        case .heartEight:
            return Card(symbol: .heart, value: 8, image: "heart_8")
        case .heartNine:
            return Card(symbol: .heart, value: 9, image: "heart_9")
        case .heartTen:
            return Card(symbol: .heart, value: 10, image: "heart_10")
        case .heartJack:
            return Card(symbol: .heart, value: 10, image: "heart_jack")
        case .hearthQueen:
            return Card(symbol: .heart, value: 10, image: "heart_queen")
        case .heartKing:
            return Card(symbol: .heart, value: 10, image: "heart_king")
        case .spadeAce:
            return Card(symbol: .spade, value: 1, image: "spade_1")
        case .spadeTwo:
            return Card(symbol: .spade, value: 2, image: "spade_2")
        case .spadeThree:
            return Card(symbol: .spade, value: 3, image: "spade_3")
        case .spadeFour:
            return Card(symbol: .spade, value: 4, image: "spade_4")
        case .spadeFive:
            return Card(symbol: .spade, value: 5, image: "spade_5")
        case .spadeSix:
            return Card(symbol: .spade, value: 6, image: "spade_6")
        case .spadeSeven:
            return Card(symbol: .spade, value: 7, image: "spade_7")
        case .spadeEight:
            return Card(symbol: .spade, value: 8, image: "spade_8")
        case .spadeNine:
            return Card(symbol: .spade, value: 9, image: "spade_9")
        case .spadeTen:
            return Card(symbol: .spade, value: 10, image: "spade_10")
        case .spadeJack:
            return Card(symbol: .spade, value: 10, image: "spade_jack")
        case .spadeQueen:
            return Card(symbol: .spade, value: 10, image: "spade_queen")
        case .spadeKing:
            return Card(symbol: .spade, value: 10, image: "spade_king")
        }
    }
    
}
