<template>
    <div id="poster">
        <el-form :model="ruleForm"  label-position="left" status-icon :rules="rules" ref="ruleForm" label-width="0px" class="register-container">
            <h3 class="register_title" style="color:green">
            <i class="el-icon-service"></i>
            注册用户
            </h3>

        <el-form-item label="" prop="loginName">
           <el-input type="text" v-model="ruleForm.email" placeholder="邮箱（将收到验证码）" prefix-icon="el-icon-message"></el-input>
        </el-form-item>
        <el-form-item label="" prop="name">
           <el-input type="text" v-model="ruleForm.username" prefix-icon="el-icon-user-solid" placeholder="用户名"></el-input>
        </el-form-item>
        <el-form-item label="" prop="password">
           <el-input type="password" v-model="ruleForm.password" autocomplete="off" placeholder="密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item label="" prop="checkPass">
           <el-input type="password" v-model="ruleForm.checkPass" autocomplete="off" placeholder="重复密码"></el-input>
        </el-form-item>
          <el-form-item label="" prop="emailCode">
            <el-input type="text" v-model="ruleForm.emailCode" placeholder="邮箱验证码" prefix-icon="el-icon-message"></el-input>
          </el-form-item>
          <el-form-item label="" prop="phoneNumber">
            <el-input type="text" v-model="ruleForm.phoneNumber" placeholder="手机号" prefix-icon="el-icon-message"></el-input>
          </el-form-item>

        <el-form-item>
        <el-button  style="background:yellowgreen;border:none" @click="sendEmail()" icon="el-icon-refresh" class="button1">发送验证码</el-button>
        <el-button type="primary" style="background:green;border:none;color:white" @click="submitForm('ruleForm')" icon="el-icon-right" class="button2">注册</el-button>
        </el-form-item>
        <el-form-item style="width:100%;">
            <el-button type="primary" style="width:100%;background:green;border:none;color:yellow" v-on:click="toLogin()" icon="el-icon-back">回到登录</el-button>
        </el-form-item>
    </el-form>
    </div>
  </template>
  
  <script>
  
  export default {
    name: 'Register',
    data() {
      var validatePass = (rule, value, callback) => {//检查密码是否合法
        if (value === '') {
          callback(new Error('请输入密码'));
        } else {
          if (this.ruleForm.checkPass !== '') {
            this.$refs.ruleForm.validateField('checkPass');
          }
          callback();
        }
      };
      var validatePass2 = (rule, value, callback) => {//检查重复密码是否合法
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value !== this.ruleForm.password) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };
      return {
        ruleForm: {
          email: '',
          password: '',
          username: '',
          checkPass:"",
          emailCode:"",//邮箱验证码
          phoneNumber:''
        },
        rules: {
          password: [
            { validator: validatePass, trigger: 'blur' }
          ],
          checkPass: [
            { validator: validatePass2, trigger: 'blur' }
          ],
        }
      };
    },
    methods: {
      //发送邮件
      sendEmail(){
        //判断用户是否输入邮箱
        if(this.ruleForm.email==""){
          this.$message({
            message: '未输入邮箱',
            type: 'error'
          });
          return;
        }
        //调用发送邮件的接口
        var that = this;
        this.$axios({
          method:'GET',
          url:"http://localhost:8989/regist/sendEmail",
          params:{
            email:this.ruleForm.email
          }
        })
            .then(function (response){
              console.log(response);
              console.log(response.data);
              console.log(response.data.data);
              if(response.data.message==="邮箱格式错误或已被注册"){
                that.$message({
                  message: '邮箱已经被注册',
                  type: 'error'
                });
                return;
              }
              if(response.data.message==="发送成功！"){
                that.$message({
                  message: '发送成功！',
                  type: 'success'
                });
              }
            })
            .catch(function (error){
              console.log(error)
            })
      },
      //注册按钮
      submitForm(ruleForm) {
        //检查用户名，密码等是否已经输入完成
        if(this.ruleForm.email===""||this.ruleForm.username===""
            ||this.ruleForm.password===""||this.ruleForm.checkPass===""
            ||this.ruleForm.phoneNumber===""||this.ruleForm.emailCode===""){
          this.$message({
            message: '信息填写不完整',
            type: 'error'
          });
          return;
        }
        //此时表单内容已经输入完全
        var that = this;
        this.$axios({
          method:'POST',
          url:'http://localhost:8989/regist/byemail',
          data:{
            email:this.ruleForm.email,
            code:this.ruleForm.emailCode,
            username:this.ruleForm.username,
            password:this.ruleForm.password,
            phone:this.ruleForm.phoneNumber
          }
        }).then(function (response){
          console.log(response);
          console.log(response.data);
          if(response.data.message==="手机号已被注册"){
            that.$message({
              message: '手机号已被注册',
              type: 'error'
            });
            return;
          }
          if(response.data.message==="验证码错误或已失效"){
            that.$message({
              message: '验证码错误或已失效',
              type: 'error'
            });
            return;
          }
          if(response.data.message==="注册成功！"){
            that.$message({
              message: '注册成功！',
              type: 'success'
            });
            this.$router.push({path:'/'})
          }
        })
            .catch(function (error){
              // console.log(error);
              that.$message({
                message: '服务器错误，请稍后重试！',
                type: 'error'
              });
            })
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      },

      toLogin(){
        this.$router.push({path:'/'})
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
        margin: 0px;
        padding: 0px;
    }

    .register-container{
        border-radius: 15px;
        background-clip: padding-box;
        margin: 90px auto;
        width: 350px;
        padding: 35px 35px 15px 35px;
        background: #fff;
        border: 1px solid #eaeaea;
        box-shadow: 0 0 25px #cac6c6;
    }

    .register_title{
        margin: 0px auto 40px auto;
        text-align: center;
        color: #505458;
    }

    .button1{
        float:left ;
    }
    .button2{
        float: right;
        width: 200px;
    }
  </style>
  