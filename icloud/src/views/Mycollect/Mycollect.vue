<template>
    <div>
      <div class="header">
        <span class="title">我的收藏</span>
      </div>
      <div class="middle">
        <el-button class="canclebtn" type="primary" icon="el-icon-delete"
          @click="cancelLike">取消收藏</el-button
        >
        <el-button class="downloadbtn" type="primary" icon="el-icon-download"
          @click="downFile">下载</el-button
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
          <el-table-column type="selection" width="180"> </el-table-column>
          <el-table-column prop="fileName" label="文件名" width="250">
          </el-table-column>
          <el-table-column prop="filePath" label="文件目录" width="250">
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小(B)" width="250">
          </el-table-column>
          <el-table-column prop="saveTime" label="收藏日期" width="250">
          </el-table-column>
          <el-table-column prop="shareUser" label="文件上传者"> </el-table-column>
        </el-table>
      </div>
    </div>
  </template>
    
    <script>
  export default {
    data() {
      return {
        tableData: this.$global_msg.save
      };
    },
    methods: {
      cancelLike() {
        let currentSelect = this.$refs.accountTable.selection;
        
        var url = 'http://localhost:8081/runyun/cancelLikes/'+this.$global_msg.userId;
        this.axios.post(url, currentSelect).then((resp)=>{
              let data = resp.data;
              if (data.code == 200) {
                this.$global_msg.save = data.data;
                this.tableData = data.data;
              }
          })
      },

      downFile() {
        let currentSelect = this.$refs.accountTable.selection;

        if (currentSelect.length != 1) {
            alert("请选择一个文件进行下载");
        } else {
          var url = 'http://localhost:8081/runyun/download/' + currentSelect[0].shareUser;
          var filename = currentSelect[0].fileName.substring(currentSelect[0].fileName.lastIndexOf('\\')+1);
          this.axios.get( url ,
          {
              params:{
                  path: currentSelect[0].fileName
              },
              responseType: 'blob'
          }
                ).then((res)=>{
                    console.log('文件下载成功');
                    const blob = new Blob([res.data]);
                    const fileName = filename;

                    //对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
                    //IE10以上支持blob，但是依然不支持download
                    if ('download' in document.createElement('a')) {
                        //支持a标签download的浏览器
                        const link = document.createElement('a');//创建a标签
                        link.download = fileName;//a标签添加属性
                        link.style.display = 'none';
                        link.href = URL.createObjectURL(blob);
                        document.body.appendChild(link);
                        link.click();//执行下载
                        URL.revokeObjectURL(link.href); //释放url
                        document.body.removeChild(link);//释放标签
                    } else {
                        navigator.msSaveBlob(blob, fileName);
                    }
                }).catch((res)=>{
                    console.log('文件下载失败');
                });

        }
        
            
      },



    },
  };
  </script>
    
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
  .canclebtn{
    display: flex;
    //margin-left: 170px;
    height: 80%;
    margin-top: 5px;
    margin-left: 10px;
  }
.downloadbtn {
    display: flex;
    margin-left: 10px;
    height: 80%;
    margin-top: 5px;
  }
  .line {
    height: 0;
    width: 100%;
    border: 1px solid #dcdfe6;
  }
  .body {
     // margin-left: 170px;
      margin: 20px 10px;
  }
  </style>