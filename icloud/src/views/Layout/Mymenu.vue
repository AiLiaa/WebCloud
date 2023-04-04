<template>
<div>
    <el-row class="tac">
      <el-col :span="24">
        <el-menu
           default-active="/add-home"
           class="el-menu-vertical-demo"
           @open="handleOpen"
           @close="handleClose"
           router
           :collapse="isCollapse">
<!--           <el-submenu index="1">-->
<!--              <template slot="title">-->
<!--                <i class="el-icon-s-home"></i>-->
<!--                <span>首页</span>-->
<!--              </template>-->
<!--              <el-menu-item-group>-->
<!--                <el-menu-item index="/add-home">欢迎页面</el-menu-item>-->
<!--              </el-menu-item-group>-->
<!--           </el-submenu>-->
          <el-menu-item  @click="home">
            <i class="el-icon-s-home"></i>
            <span slot="title">欢迎页面</span>
          </el-menu-item>
          <el-menu-item  @click="myDisk">
                    <i class="el-icon-upload"></i>
                    <span slot="title">我的网盘</span>
          </el-menu-item>
          <el-menu-item  @click="myFriends">
                  <i class="el-icon-s-custom"></i>
                  <span slot="title">关注用户</span>
          </el-menu-item>
          <el-menu-item  @click="myShare">
                  <i class="el-icon-document-remove"></i>
                  <span slot="title">我的分享</span>
          </el-menu-item>
          <el-menu-item  @click="shared">
                  <i class="el-icon-document-add"></i>
                  <span slot="title">收到分享</span>
          </el-menu-item>
          <el-menu-item  @click="mySave">
                  <i class="el-icon-star-on"></i>
                  <span slot="title">我的收藏</span>
          </el-menu-item>
          <el-menu-item  @click="myBin">
                  <i class="el-icon-delete"></i>
                  <span slot="title">回收站</span>
          </el-menu-item>
    </el-menu>
  </el-col>
</el-row>

    </div>
</template>

<script>
    export default {
        props:['isCollapse'],
        data() {
            return {
               isCollapse: false
            }
        },

        methods: {
          home(){
            this.$router.push({path:'/add-home'});
          },
          myDisk() {
            this.axios({
              method:'GET',
              url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/getFolderSize?path=/',
            })

            var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesAndFolders?path=/';
            this.axios({
              method:'GET',
              url: url,
            }).then((resp)=>{
              let data = resp.data;
              console.log(data)
              if (data.success) {
                this.$global_msg.disk = data.data.fileList;
                this.$router.push({path:'/Mydisk'})
              }
            })
          },

          myShare(){
            this.$router.push({path:'/Myshare'})
          },
          shared() {
            this.$router.push({path:'/Receiveshare'})
          },
          mySave() {
            this.$router.push({path:'/Mycollect'})
          },
          myBin () {
            var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesByBinPath?path=/';
            this.axios({
              method:'GET',
              url: url,
            }).then((resp)=>{
              let data = resp.data;
              console.log(data)
              if (data.success) {
                this.$global_msg.binData = data.data.fileList;
                this.$router.push({path:'/Bin'})
              }
            })
          },
          myFriends(){
            this.$router.push({path:'/Usercare'})
          },
      },
    }
</script>

<style lang="less" scoped>
  .el-menu-vertical-demo:not(.el-menu--collapse) {
    width: 100%;
    min-height: 400px;
    text-align: center;
  }
</style>