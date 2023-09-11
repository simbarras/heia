import {createRouter, createWebHistory} from 'vue-router'
import HomeView from '../views/HomeView.vue'
import {getRole} from "@/stores/login";

const defaultPath = {
    "": "login",
    "participant": "home",
    "admin": "admin"
}

const myRouter = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: {
                acceptedRole: ["", "participant"]
            }
        },
        {
            path: '/account',
            name: 'account',
            component: () => import('../views/AccountView.vue'),
            meta: {
                acceptedRole: ["admin", "participant"]
            }
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/LoginView.vue'),
            meta: {
                acceptedRole: [""]
            }
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/AdminView.vue'),
            meta: {
                acceptedRole: ["admin"]
            }
        },
        {
            path: '/invoice',
            name: 'invoice',
            component: () => import('../views/InvoiceView.vue'),
            meta: {
                acceptedRole: ["admin"]
            }
        }
    ]
})

myRouter.beforeEach((to, from, next) => {
        // @ts-ignore
        if (to.meta.acceptedRole.includes(getRole())) {
            next()
        } else {
            // @ts-ignore
            next({name: defaultPath[getRole()]})
        }
    }
)

export default myRouter
