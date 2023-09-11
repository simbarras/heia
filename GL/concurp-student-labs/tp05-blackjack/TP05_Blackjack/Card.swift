//
//  Card.swift
//  TD00_BlackJack
//
//  Created by Rafic Galli on 11.08.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import Foundation

struct Card : Hashable {
    var symbol: CardSymbol
    var value: Int
    var image: String
    
    static let hiddenCard = Card(cardType: .back)
    
    init(cardType: CardType) {
        self = CardUtils.generateCard(cardType: cardType)
    }
    
    init(symbol: CardSymbol, value: Int, image: String) {
        self.symbol = symbol
        self.value = value
        self.image = image
    }
}

enum CardSymbol {
    case hidden, club, diamond, heart, spade
}

enum CardType : CaseIterable {
    case back,
    clubAce, clubTwo, clubThree, clubFour, clubFive, clubSix, clubSeven, clubEight, clubNine, clubTen, clubJack, clubQueen, clubKing,
    diamondAce, diamondTwo, diamondThree, diamondFour, diamondFive, diamondSix, diamondSeven, diamondEight, diamondNine, diamondTen, diamondJack, diamondQueen, diamondKing,
    heartAce, heartTwo, heartThree, heartFour, heartFive, heartSix, heartSeven, heartEight, heartNine, heartTen, heartJack, hearthQueen, heartKing,
    spadeAce, spadeTwo, spadeThree, spadeFour, spadeFive, spadeSix, spadeSeven, spadeEight, spadeNine, spadeTen, spadeJack, spadeQueen, spadeKing
}
