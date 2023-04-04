const express = require('express')
const sqlFun = require('./mysql')
const router = express.Router()
// 导入数据库 sqlFun('sql',[],res=>{})
const sqlFn = require('./mysql')

// 路由接口
router.get('/',(req,res)=>{
    res.send('hello')
})


module.exports = router