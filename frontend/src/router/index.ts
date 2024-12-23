import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { getAppList, getComponents } from '../views/modules/_List'
import HomeView from '../views/HomeView.vue'
import { isLogin } from '@/modules/request'

export const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'home',
        components: {
            default: HomeView,
        },
        children: [
            {
                path: '/',
                name: 'Modules',
                components: getComponents()
            }],
        beforeEnter(to, from, next) {
            if (isLogin()) {
                next()
            } else {
                next({ name: 'login' })
            }
        }
    },
    {
        path: "/login",
        name: "login",
        component: () => import("../views/LoginView.vue")
    }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

export default router