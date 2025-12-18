export default [
    {
        path: '/',
        component: () => import('@/pages/public/MainPage.vue'),
    },
    {
        path: '/anime/list',
        component: () => import('@/pages/public/AnimeListPage.vue'),
    },
]
