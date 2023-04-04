<template>
<body id="poster">
    <el-form class="login-container" label-position="left" label-width="0px">
        <h1 class="login_title">
            云盘系统
        </h1>

        <el-form-item label="" class="cup">
            <i class="el-icon-coffee-cup">登录页面</i>
        </el-form-item>
        <el-form-item label="">
            <el-input type="text"  v-model="loginForm.account" autocomplete="off" placeholder="邮箱/手机号"></el-input>
        </el-form-item>
        <el-form-item label="">
            <el-input type="password" v-model="loginForm.password" autocomplete="off" placeholder="密码"></el-input>
        </el-form-item>


        <el-form-item>
            <el-checkbox v-model="checked" class="button1">记住我</el-checkbox>
            <el-button type="primary" style="background:blue;border:none" v-on:click="Login()" icon="el-icon-lollipop" class="button2">登录</el-button>
        </el-form-item>


        <el-form-item class="whole">
            <i class="el-icon-back button3">忘记密码</i>
            <el-button @click="toRegister()" icon="el-icon-right" class="button4">注册</el-button>
        </el-form-item>
    </el-form>
</body>
  </template>
  
  <script>
  
  export default {
    name: 'Login',
    data() {
      return {
        loginForm: {
          account: '1005772685@qq.com',
          password: '123456'
        },
      }
    },
    methods: {
      newLogin(){

      },
      Login() {
        var that = this;
        this.axios({
          method:'POST',
          url:'http://localhost:8989/login',
          data:{
            account: this.loginForm.account,
            password: this.loginForm.password
          }
        })
            .then(function (response) {
              //每次登录以前，都将isLogin设置为false
              console.log(sessionStorage.getItem("isLogin"));
              sessionStorage.setItem("isLogin","false");
              console.log(sessionStorage.getItem("isLogin"));
              console.log(response);
              console.log(response.data.data.satoken);//输出用户token值
              console.log(response.data.data.user);//输出用户信息
              console.log(response.data.success);
              if(!response.data.success){
                console.log("账号或密码错误");
                that.$message({
                  message: '登录失败，请重试',
                  type: 'error'
                });
                return;
              }
              var satoken = response.data.data.satoken;
              var userId = response.data.data.user.id;
              var userName = response.data.data.user.username;
              //将信息放入缓存中
              sessionStorage.setItem("isLogin","true");
              sessionStorage.setItem("saToken",satoken);
              sessionStorage.setItem("userId",userId);
              sessionStorage.setItem("userName",userName);
              //检测是否登录成功
              if(sessionStorage.getItem("isLogin")=="true"){
                that.$message({
                  message: '成功登录，欢迎您来到云盘系统，' + sessionStorage.getItem("userName"),
                  type: 'success'
                });
                that.$router.push({path:'/Layout'});
              }else{
                that.$message({
                  message: '登录失败，请重试',
                  type: 'error'
                });
              }
            })
            .catch(function (error) {
              that.$message({
                message: '服务器错误，请稍后重试！',
                type: 'error'
              });
            });

      },
      toRegister(){
        this.$router.push({path:'/Register'})
      }
    }
  }
  </script>

  <style>
    #poster{
        background-position: center;
        height: 100%;
        width: 100%;
        background-size: cover;
        position: fixed;
    }
    body{
        margin: 0px;
        padding: 0px;
    }
    .login-container{
        border-radius: 15px;
        background-clip: padding-box;
        margin: 90px auto;
        width: 350px;
        padding: 35px 35px 15px 35px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }
    .login_title{
        margin: 0px auto 40px auto;
        text-align: center;
        color: #505458;
    }

    .cup{
        float: left;
        color: green;
        width: 80px;
    }
    .button1{
        float: left;
    }
    .button2{
        float: right;
        text-align: center;
    }
    .whole{
        background-color: blue;
        color: yellow;
    }
    .button3{
        padding: 10px 10px;
        float: left;
    }
    .whole .button4{
        margin-left: auto;
        float: right;
        background-color: blue;
        border: none;
        color: white;
    }
  </style>
  