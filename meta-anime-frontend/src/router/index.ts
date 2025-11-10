import { createRouter, createWebHistory } from 'vue-router'
import MainPage from "../pages/MainPage.vue";
import AdminListPage from '../pages/admin/AdminListPage.vue';

const routes = [
    { path: '/', component: MainPage },
    { path: '/admin/list', component: AdminListPage }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
