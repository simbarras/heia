import pandas as pd
from sklearn.feature_selection import SelectKBest, f_regression
import numpy as np
import matplotlib.pyplot as plt

# Charger le dataset à partir du fichier CSV
dataset = pd.read_csv('./dataset/training.csv')

dataset.drop(columns=['index', 'Date'], inplace=True)
dataset.dropna(subset=['solar_production'], inplace=True)

# Convert string to int with label encoding
dataset[['conditions']] = dataset[['conditions']].fillna('UNKNOWN')  # Fill nan value with unknown

dataset.fillna(0, inplace=True)
cats = pd.Categorical(dataset.conditions)
dataset['conditions'] = cats.codes

# Sélectionner toutes les colonnes, à l'exception de la cible
X = dataset.drop(columns=['solar_production'])
y = dataset['solar_production']

# Utiliser la régression linéaire pour sélectionner les k caractéristiques les plus pertinentes
selector = SelectKBest(score_func=f_regression, k=13)
selector.fit(X, y)

# Afficher les scores de chaque caractéristique
scores = selector.scores_
print(scores)

# Afficher les noms des caractéristiques sélectionnées
selected_columns = X.columns[selector.get_support()]
print(selected_columns)

# Plot the scores

# Sort the scores and the names of the features in descending order
sorted_scores = np.sort(scores)[::-1]
sorted_names = [selected_columns[i] for i in np.argsort(scores)[::-1]]

plt.bar([i for i in range(len(sorted_scores))], sorted_scores)
plt.xticks([i for i in range(len(sorted_names))], sorted_names, rotation='vertical')
plt.subplots_adjust(bottom=0.3)
plt.show()

# Keep the 6 first features and find every combination of minimum 2 features to maximum 6 features
from itertools import combinations

best_features = sorted_names[:6]
print(best_features)

# Find all combinations of features
c = []
for i in range(2, 7):
    c += list(combinations(best_features, i))

print(f'Number of combinations: {len(c)}')

# Save the current dataset to a new file
dataset.to_csv('./dataset/training_cleaned.csv', index=False)
