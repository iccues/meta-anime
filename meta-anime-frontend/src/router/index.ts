import { createRouter, createWebHistory } from 'vue-router'
import MainPage from "../pages/MainPage.vue";
import AnimeListPage from "../pages/AnimeListPage.vue";
import AdminHomePage from '../pages/admin/AdminHomePage.vue';
import AdminListPage from '../pages/admin/AdminListPage.vue';
import Callback from '../pages/auth/Callback.vue';
import Login from '../pages/auth/Login.vue';
import { authGuard } from '../auth/authGuard';
import AuthHome from '../pages/auth/AuthHome.vue';

const routes = [
    { path: '/', component: MainPage },
    { path: '/anime/list', component: AnimeListPage },
    {
        path: '/admin', component: AdminHomePage,
        meta: { requiresAuth: true }
    },
    {
        path: '/admin/list', component: AdminListPage,
        meta: { requiresAuth: true }
    },
    { path: '/admin/auth/callback', component: Callback },
    { path: '/admin/auth/login', component: Login },
    {
        path: '/admin/auth', component: AuthHome,
        meta: { requiresAuth: true },
    },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(authGuard);

export default router
