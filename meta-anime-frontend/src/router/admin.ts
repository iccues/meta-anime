export default [
        {
        path: '/admin',
        component: () => import('@/pages/admin/AdminHomePage.vue'),
        meta: { requiresAuth: true },
    },
    {
        path: '/admin/list',
        component: () => import('@/pages/admin/AdminListPage.vue'),
        meta: { requiresAuth: true },
    },
    {
        path: '/admin/auth/callback',
        component: () => import('@/pages/auth/Callback.vue'),
    },
    {
        path: '/admin/auth/login',
        component: () => import('@/pages/auth/Login.vue'),
    },
    {
        path: '/admin/auth',
        component: () => import('@/pages/auth/AuthHome.vue'),
        meta: { requiresAuth: true },
    },
]
