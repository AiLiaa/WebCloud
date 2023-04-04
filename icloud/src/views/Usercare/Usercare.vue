<template>
    <div class="box">
        <header>
            <div class="header">
                <div>
                    <i class="iconfont icon-dangqianhuihua"></i>
                    <span>会话</span>
                </div>
                <div>
                    <i class="iconfont icon-jurassic_user"></i>
                    <span>好友</span>
                </div>
                <div>
                    <i class="iconfont icon-shanchuhaoyou_huaban1"></i>
                    <span @click="deleteFriend">删除好友</span>
                </div>
                <div>
                    <i class="iconfont icon-tianjiahaoyou"></i>
                    <span @click="addFriend">添加好友</span>
                </div>
                <div>
                    <i class="iconfont icon-haoyoutixing"></i>
                    <span>好友提醒</span>
                </div>
            </div>
        </header>
        <div class="content">
            <el-table :data="tableData" border ref="accountTable">
                <el-table-column type="selection" width="100"> </el-table-column>
                <el-table-column prop="aUserName" label="用户名" width="300"></el-table-column>
                <el-table-column prop="email" label="邮箱" width="300"></el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
    export default {
        data() {
            return{
                tableData: this.$global_msg.friends
            };
            
        },
        methods: {
          addFriend() {
            this.$prompt('请输入邮箱', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
            inputErrorMessage: '邮箱格式不正确'
                }).then(({ value }) => {
                    var url = 'http://localhost:8081/runyun/addFriend/'+this.$global_msg.userId;
                    this.axios.post(url, value).then((resp)=>{
                        let data = resp.data;
                        if (data.code == 200) {
                            this.$global_msg.friends = data.data;
                            this.tableData = data.data;
                            this.$message({
                                type: 'success',
                                message: data.msg
                            });
                        }else if(data.code == 400) {
                            this.$message({
                                type: 'info',
                                message: data.msg
                            });
                        }
                    })
                }).catch(() => {
                    this.$message({
                    type: 'info',
                    message: '取消输入'
                    });      
                });
            },
            deleteFriend() {
                let currentSelect = this.$refs.accountTable.selection;
                // alert(currentSelect.length);
                var url = 'http://localhost:8081/runyun/deleteFriends/'+this.$global_msg.userId;
                this.axios.post(url, currentSelect).then((resp)=>{
                    let data = resp.data;
                    if (data.code == 200) {
                        this.$global_msg.friends = data.data;
                        this.tableData = data.data;
                        this.$message({
                            type: 'success',
                            message: data.msg
                        });
                    }
                })
            },
        }
    }
</script>

<style lang="less" scoped>
.box{
    margin: 0 auto;
    display: flex;
    flex-direction: column;
}
.header{
    height: 128px;
    display: flex;
}
.header div{
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    color: #acadef;
}
.header div:hover{
    color: #06a9ee;
}
.header div i{
    height: 30px;
    line-height: 30px;
    text-align: center;
    font-size: 58px;
}
.header div span{
    font-size: 20px;
    height: 30px;
    line-height: 40px;
    text-align: center;
}
.content{
    margin-left: 50px;
    width: 800px;
}

</style>