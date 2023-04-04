<template>
    <div>
        <div class="header">
            <span class="title">云盘系统</span>
            <i v-if="!isCollapse"  @click="changeMenu" class="el-icon-s-fold" id="left1"></i>
            <i v-else @click="changeMenu" class="el-icon-s-unfold" id="right1"></i>
            <div class="header-right">
                <el-dropdown>
                <span class="el-dropdown-link" style="color:#fff;">
                多语言<i class="el-icon-arrow-down el-icon--right"></i>
                </span>
                   <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item>中文</el-dropdown-item>
                        <el-dropdown-item>English</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <div class="user"> 欢迎{{this.$global_msg.username}}:
                    <svg class="icon" aria-hidden="true" @click="userInformation()">
                        <use xlink:href="#icon-icon-test1"></use>
                    </svg>
                </div>
                <i class="el-icon-refresh-left" @click="loginout()" id="return"></i>
            </div>
        </div>
        <!-- 内容区域--路由出口 -->
        <!-- <div class="content">
            <breadcrumb/>
           <router-view/>
        </div> -->
    </div>
</template>

<script>
    import Breadcrumb from '@/components/Breadcrumb'
    export default {
        props:['isCollapse'],
        components:{
            Breadcrumb
        },
        methods:{
            changeMenu(){
                // 点击切换按钮的时候,修改父组件的数据 isCollapse
                this.$emit('changeCollapse')
            },
             loginout(){
                 this.$router.push({path:'/'})
             },
             userInformation(){
                var url = 'http://localhost:8081/runyun/getUser/'+this.$global_msg.userId;
                this.axios.get(url).then((resp)=>{
                    let data = resp.data;
                    if (data.code == 200) {
                        this.$global_msg.userInfo = data;
                        this.$router.push({path:'/Persondata'})
                    }
                })
             }
        }
    }
</script>
<style lang="less" scoped>
.header{
    // display: flex;
    height: 60px;
    background-color:#4682B4;
    // width: 100%;
    line-height: 60px;
    color: #fff;
    display: flex;
}
.title{
        font-size: 35px;
        font-family: 楷体;
        float: left;
    }

#left1,#right1{
        display: flex;
        height: 60px;
        width: 60px;
        line-height: 60px;
        flex: 1;
        font-size: 30px;
}
.header-right{
    float: right;
    // padding-right: 20px;
    display: flex;
    font-size: 20px;
    .user{
        margin-left: 20px;
        // font-size: 20px;
    };
#return{
        display: flex;
        height: 60px;
        width: 60px;
        line-height: 60px;
        font-size: 30px;
    }

.icon {
  width: 1.5rem;
  height: 1.5em;
  line-height: 1.5rem;
  vertical-align: -0.15em;
  fill: currentColor;
  overflow: hidden;
}
}

</style>