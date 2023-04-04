<template>
    <div>
        <el-tabs type="border-card">
        <el-tab-pane label="基本信息">
          <div class="footer1">
            <div class="header1">
                <h2>基本信息</h2>
            </div>
            <div class="main1">
                <p>
                    昵称<input type="text" v-model="username">
                </p>
                <p>
                    生日<input type="date" v-model="birthday">
                </p>
                <p>
                    性别
                    <input type="radio" name="sex" value="男" v-model="sex">男
                    <input type="radio" name="sex" value="女" v-model="sex">女
                </p>
                <p>
                    备注
                    <textarea cols = "20" rows = "2" wrap = "hard" id = "story" name = "story" 
                        v-model="other"></textarea>
                </p>
            </div>
            <div class="header2">
                <h2>联系方式</h2>
            </div>
            <div class="main2">
                <p>
                    邮箱<input type="email" name="myemail" v-model="email">
                </p>
                <p>
                    个人地址<input type="url" placeholder="完整地址" v-model="myPath">
                </p>
                <p>
                    电话<input type="tel" v-model="phone">
                </p>
            </div>
            <div class="footer">
                <button type="submit" class="mybutton1" @click="save">保存</button>
                <button type="reset" class="mybutton2" @click="_return">撤销</button>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="密码">
                <p>
                    新密码<input type="text" v-model="password">
                </p>
                <p>
                    确认密码<input type="text" v-model="password2">
                </p>
                <div class="footer1">
                <button class="mybutton1" @click="savePd">保存</button>
                <button class="mybutton2" @click="_returnPd">撤销</button>
            </div>
        </el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
    export default {
        
        data() {
            return {
                username: this.$global_msg.userInfo.username,
                birthday: this.$global_msg.userInfo.birthday,
                sex: this.$global_msg.userInfo.sex,
                other: this.$global_msg.userInfo.other,
                email: this.$global_msg.userInfo.email,
                myPath: this.$global_msg.userInfo.myPath,
                phone: this.$global_msg.userInfo.phone
            };
        },
        methods: {
            save() {
                var user_= {};
                if (this.username == "" || this.email == "" || this.username == null || this.email == null) {
                    this.$message({
                      message: '用户名或邮件不能为空',
                      type: 'error'
                    });
                    return;
                }
                user_.username = this.username;
                user_.birthday = this.birthday;
                user_.sex = this.sex;
                user_.other = this.other;
                user_.email = this.email;
                user_.myPath = this.myPath;
                user_.phone = this.phone;
                // console.log('user_')
                // console.log(user_)
                var url = 'http://localhost:8081/runyun/updateUserInfo/'+this.$global_msg.userId;
                this.axios.post(url, user_).then((resp)=>{
                    let data = resp.data;
                    if (data.code == 200) {
                        this.$global_msg.userInfo = data;
                        this.$message({
                            message: data.msg,
                            type: 'success'
                        });
                        return;
                    }
                })
            },

            _return() {
                var url = 'http://localhost:8081/runyun/getUser/'+this.$global_msg.userId;
                this.axios.get(url).then((resp)=>{
                    let data = resp.data;
                    if (data.code == 200) {
                        this.$global_msg.userInfo = data;
                        this.$router.push({path:'/Persondata'})
                    }
                })
            },
            savePd() {
                var pd = this.password;
                var reptPd = this.password2;
                if (pd != reptPd) {
                    alert("密码不一致！");
                } else {
                    var d = {};
                    d.password = pd;
                    var url = 'http://localhost:8081/runyun/updatePd/'+this.$global_msg.userId;
                    this.axios.post(url, d).then((resp)=>{
                        let data = resp.data;
                        if (data.code == 200) {
                            this.$message({
                                message: data.msg,
                                type: 'success'
                            });
                            this.password = '';
                            this.password2 = '';
                        }
                    })
                }
            },
            _returnPd() {
                this.$set(this,'password','');
                this.$set(this,'password2','');
            },


        },


    }
</script>

<style lang="less" scoped>
.header1{
    display: flex;
    color: blue;
}
.main1{
    margin: 10px 10px;
    border-top: 1px solid #CCC;
}
.main1,p{
    margin-top: 20px;
}
.header2{
    display: flex;
    color: blue;
}
.main2{
    margin: 10px 10px;
    border-top: 1px solid #CCC;
}
.main2,p{
    margin-top: 20px;
}
.footer{
    background: white;
}
.mybutton1{
    margin-right: 50px;
    width: 50px;
    height: 30px;
    background: blue;
    color: white;
}
.mybutton2{
    margin-left: 50px;
    width: 50px;
    height: 30px;
    color: white;
    background: grey;
}
.footer1,p{
    margin-top: 20px;
}
</style>
