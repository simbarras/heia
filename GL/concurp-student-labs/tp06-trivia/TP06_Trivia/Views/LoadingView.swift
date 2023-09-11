//
//  LoadingView.swift
//  TP06_Trivia
//
//  Created by Rafic Galli on 15.07.20.
//  Copyright © 2020 Rafic Galli. All rights reserved.
//

import SwiftUI

struct LoadingView: UIViewRepresentable {
    func makeUIView(context: Context) -> UIActivityIndicatorView {
        return UIActivityIndicatorView()
    }
    
    func updateUIView(_ uiView: UIActivityIndicatorView, context: Context) {
        uiView.startAnimating()
    }
}

struct LoadingView_Previews: PreviewProvider {
    static var previews: some View {
        LoadingView()
    }
}
