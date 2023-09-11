import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import vuetify from './plugins/vuetify'
import { loadFonts } from './plugins/webfontloader'
import { createPinia } from 'pinia'

import VueDatePicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css'

const pinia = createPinia()

loadFonts()

createApp(App)
    .use(router)
    .use(vuetify)
    .use(pinia)
    .component('VueDatePicker', VueDatePicker)
    .mount('#app')
