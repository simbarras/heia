//
//  CardView.swift
//  TD00_BlackJack
//
//  Created by Rafic Galli on 11.08.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import SwiftUI

struct CardView: View {
    let cards: Array<Card>
    
    var body: some View {
        ZStack(alignment: .leading) {
            ForEach(0..<cards.count, id: \.self) { card in
                Image(cards[card].image).offset(y: CGFloat(40*card))
            }
        }
    }
}

struct CardView_Previews: PreviewProvider {
    static var previews: some View {
        CardView(cards: [Card(cardType: CardType.heartAce),
                         Card(cardType: CardType.clubAce), Card(cardType: CardType.clubAce)])
    }
}
