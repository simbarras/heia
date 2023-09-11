//
//  CardView.swift
//  TP06_Trivia
//
//  Created by Rafic Galli on 10.07.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import SwiftUI

struct CardView<Content>: View where Content: View {
    let content: () -> Content
    let color: Color
    
    var body: some View {
        ZStack {
            Rectangle()
                .fill(color)
                .shadow(radius: 3)
            self.content()
        }
    }
    
    init (@ViewBuilder content: @escaping () -> Content, color: Color = Color.white) {
        self.content = content
        self.color = color
    }
}

struct CardView_Previews: PreviewProvider {
    static var previews: some View {
        CardView() {
            VStack {
                Text("Title")
                    .font(.largeTitle)
                
                Text("Description")
            }
        }
    }
}
