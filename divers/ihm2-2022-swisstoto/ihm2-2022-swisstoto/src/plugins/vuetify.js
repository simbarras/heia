// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import {createVuetify} from 'vuetify'

const myCustomLightTheme = {
    dark: false,
    colors: {
        primary: '#2d2a2f',
        secondary: '#5293c6',
        accent: '#8c9eff',
        error: '#b71c1c',
        'primary-dark': '#020005',
        'primary-light': '#565258',
        'secondary-dark': '#116595',
        'secondary-light': '#86c3f9',
        info: '#2196F3',
        success: '#4CAF50',
        warning: '#FB8C00',
    }
}

export default createVuetify({
    theme: {
        defaultTheme: 'myCustomLightTheme',
        themes: {
            myCustomLightTheme,
        }
    }
})
