<template>
    <div>
      <div class="header">
        <span class="title">我的分享</span>
      </div>
      <div class="middle">
        <el-button class="canclebtn" type="primary" icon="el-icon-close"
          @click="cancelShare">取消分享</el-button
        >
      </div>
      <div class="line"></div>
      <div class="body">
        <el-table
          :data="tableData"
          height="550"
          stripe
          border
          style="width: 100%"
          ref="accountTable"
        >
          <el-table-column type="selection" width="150"> </el-table-column>
          <el-table-column prop="fileName" label="分享文件" width="300">
          </el-table-column>
          <el-table-column prop="fileType" label="文件类型" width="150">
          </el-table-column>
          <el-table-column prop="shareDate" label="分享日期" width="150">
          </el-table-column>
          <el-table-column prop="sharedUser" label="分享给"> </el-table-column>
        </el-table>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        tableData: this.$global_msg.share
      };
    },
    
    methods: {
      cancelShare() {
        let currentSelect = this.$refs.accountTable.selection;
        
        var url = 'http://localhost:8081/runyun/cancelShares/'+this.$global_msg.userId;
        this.axios.post(url, currentSelect).then((resp)=>{
              let data = resp.data;
              if (data.code == 200) {
                this.$global_msg.share = data.data;
                this.tableData = data.data;
              }
          })
      }
    },
  };
  </script>
  <!--获取后端的数据 this.$global_msg.share -->
  <style lang="less" scoped>
  .header {
    background-color: #dcdfe6;
    height: 60px;
    line-height: 60px;
    display: flex;
  }
  .title {
    color: #409eff;
    float: left;
    font-size: 30px;
    margin-left: 20px;
  }
  .middle {
    height: 50px;
    line-height: 50px;
    display: flex;
  }
  .canclebtn {
    display: flex;
    height: 80%;
    margin-top: 5px;
    margin-left: 10px;
  }
  .line {
    height: 0;
    width: 100%;
    border: 1px solid #dcdfe6;
  }
  .body {
      margin: 20px 10px;
  }
  </style>