feedback-tp2.md

- Vue3: syntaxe adéquate, API réactivité (mais pas dans GraphicView).

- v-router: router-view, fichier router: pas d'utilisation de guarde de navigation et autres aspects expliqués dans le codelab mais ok.

- microlayout: grid ok mais pas responsive.

- macrolayout: navigation ok, pas suffisament inspiré du codelab (pas de navitationd drawer). remarque mineur: ça serait mieux d'avoir une option explicite "Sensors", même si le logo ramène à cette home page.

- structuration en composants: ok dans l'ensemble, structuration du code en composants enfants avec passage de props. Il y a dans plusieurs composants, des parties de code qui se répètent qu'on aurait pu les déplacer dans un composant enfant en leur passant les valeurs en changement en props, ainsi quand on modifie le code, il est modifie à un endroit, et ça facilite la maintenance (MiscForm, ProfileView, peut-être certaines parties du grid v-select etc). 
profils view

- backend api (optionel): juste pour le login

- store (optional): très bien, utilisation d'une autre syntaxe que celle proposée dans les slides, mais tout à fait correcte. côté composants, utilisation adéquate de storeToRef pour maintenir la réactivité des différentes variables du store, et update dans le template avec l'API réactivité (computer).

- identité visuelle/charte de couleurs: identité visuelle claire, joli travail. configuration avec Vuetify ok.


