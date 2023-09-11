//
//  CategoryViewModel.swift
//  TP06_Trivia
//
//  Created by Claudio Herren on 06.12.22.
//

import Foundation
import Combine

class CategoryViewModel : ObservableObject {
    enum State {
        case Idle, Downloading, Downloaded
    }
    
    @Published var categories = [Category]();
    @Published private(set) var state = State.Idle;
    
    private var cancellableSet = Set<AnyCancellable>();
    func fetchCategories() {
//        --- Used to reset UserDefaults at the launch of the application if necessary ---
//        let domain = Bundle.main.bundleIdentifier!
//        UserDefaults.standard.removePersistentDomain(forName: domain)
//        UserDefaults.standard.synchronize()
        
        state = .Downloading;
        TriviaAPI.categories().sink(receiveCompletion: { _ in }, receiveValue: {
            self.categories = $0.categories;
            self.state = .Downloaded;
        })
        .store(in: &cancellableSet);
    }
}
