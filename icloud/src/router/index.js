import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Layout from '../views/Layout/index.vue'

// 异步
const Mydisk = ()=>import('../views/Mydisk/Mydisk.vue')
const Usercare = ()=>import('../views/Usercare/Usercare.vue')
const Myshare = ()=>import('../views/Myshare/Myshare.vue')
const Receiveshare = ()=>import('../views/Receiveshare/Receiveshare.vue')
const Mycollect = ()=>import('../views/Mycollect/Mycollect.vue')
const Bin = ()=>import('../views/Bin/Bin.vue')

// 子级路由
const AddHome = ()=>import('../views/Home/AddHome.vue')
const Persondata = ()=>import('../views/Persondata/Persondata.vue')

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/Register',
    name: 'Register',
    component: Register
  },
  {
    path:'/Layout',
    component:Layout,
    children:[
      {
        path:'/add-home',
        name:'AddHome',
        component:AddHome,
        meta:{title:'首页'},
        children:[
          {
            path:'/add-home',
            name:'AddHome',
            component:AddHome,
            meta:{title:'欢迎页面'}, 
          },
        ]
      },
      {
        path:'/Mydisk',
        name:'Mydisk',
        component:Mydisk,
        // meta:{title:'我的网盘'},
      },{
        path:'/Usercare',
        name:'Usercare',
        component:Usercare
      },{
        path:'/Myshare',
        name:'Myshare',
        component:Myshare
      },{
        path:'/Receiveshare',
        name:'Receiveshare',
        component:Receiveshare
      },{
        path:'/Mycollect',
        name:'Mycollect',
        component:Mycollect
      },{
        path:'/Bin',
        name:'Bin',
        component:Bin
      },{
        path:'/Persondata',
        name:'Persondata',
        component:Persondata
      }
    ]
  },
  {
    path: '/home',
    name: 'home',
    component: HomeView
  },
  {
    path: '/about',
    name: 'about',
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  }
]

const router = new VueRouter({
  routes
})

export default router
