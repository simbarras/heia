//
//  TP06_TriviaApp.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 22.11.22.
//

import SwiftUI

@main
struct TP06_TriviaApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
