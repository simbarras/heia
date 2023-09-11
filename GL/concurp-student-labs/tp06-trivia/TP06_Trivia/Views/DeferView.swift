//
//  DeferView.swift
//  TP06_Trivia
//
//  Created by HEIA-FR INFO on 16.11.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import SwiftUI

// https://stackoverflow.com/questions/61238773/how-can-i-initialize-view-again-in-swiftui
struct DeferView<Content: View>: View {
    let content: () -> Content

    init(@ViewBuilder _ content: @escaping () -> Content) {
        self.content = content
    }
    
    var body: some View {
        content()
    }
}
