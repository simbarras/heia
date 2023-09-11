//
//  ProfileView.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 29.11.22.
//

import SwiftUI

struct ProfileView: View {
    @Environment(\.managedObjectContext) private var viewContext
    @ObservedObject public var profileViewModel : ProfileViewModel
    
    init() {
        self.profileViewModel = ProfileViewModel()
    }
    
    var body: some View {
        NavigationStack {
            VStack {
                medalStack
                Spacer().frame(height: 50)
                statistics
            }.navigationTitle("Profile")
        }.onAppear(perform: profileViewModel.fetchProfileData)
    }
    
    var medalStack: some View {
        HStack {
            VStack {
                Image("MedalGold").resizable().aspectRatio(contentMode: .fit)
                Text(String(profileViewModel.nbGolds))
            }
            VStack {
                Image("MedalSilver").resizable().aspectRatio(contentMode: .fit)
                Text(String(profileViewModel.nbSilver))
            }
            VStack {
                Image("MedalBronze").resizable().aspectRatio(contentMode: .fit)
                Text(String(profileViewModel.nbBronze))
            }
        }
    }
    
    var statistics: some View {
        VStack(alignment: .leading) {
            Text("Player rating").font(.title)
            Text(String(profileViewModel.rating)).padding(.bottom, 10)
            Text("Trivia completed").font(.title)
            Text(String(profileViewModel.triviaCompleted)).padding(.bottom, 10)
            Text("Questions answered").font(.title)
            Text(String(profileViewModel.answeredQuestions)).padding(.bottom, 10)
            Text("Correct answers").font(.title)
            Text(String(profileViewModel.correctAnswers))
        }
    }
}
