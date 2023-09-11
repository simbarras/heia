//
//  ContentView.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 22.11.22.
//

import SwiftUI
import CoreData

struct ContentView: View {
    @ObservedObject private var categoryViewModel = CategoryViewModel()

    var body: some View {
        NavigationStack {
            ScrollView {
                ForEach(categoryViewModel.categories) { category in
                    NavigationLink {
                        DeferView {
                            GameView(questionViewModel: QuestionsViewModel(category: category))
                        }
                    } label: {
                        CardView {
                            VStack {
                                Text(category.name)
                                    .padding(20)
                                    .foregroundColor(Color.black)
                            }
                        }
                    }
                    
                }
                .navigationTitle("Trivia")
                .toolbar {
                    ToolbarItem {
                        NavigationLink {
                            HistoryView()
                        } label: {
                            Image(systemName: "clock.arrow.circlepath")
                        }
                    }
                    ToolbarItem {
                        NavigationLink {
                            ProfileView()
                        } label: {
                            Image(systemName: "person.crop.circle.fill")
                        }
                    }
                
                }
            }
        }.onAppear(perform: categoryViewModel.fetchCategories)
    }
}
