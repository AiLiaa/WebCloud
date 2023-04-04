// 1.导入 MySQL 模块
const mysql = require('mysql')
// 2.建立与 MySQL 数据车的连接
const db = mysql.createPool({
	host:'localhost',	// 数据库的IP地址
	user:'root',			// 登录数据库的账号
	password:'1234',		// 登录数据的密码
	database:'runyun'		// 指定要操作哪个数据库
    // port:'3306'			   默认为3306，所以可以不写
})


// 封装数据库操作语句 sql语句 参数数组arr callback成功函数结果
function sqlFun(sql, arr,callback){
    db.query(sql,arr, function(error, result){
        if(error){
            console.log('数据库语句错误');
            return;
        }
        callback(result)
    })
}


module.exports=sqlFun