<template>
    <div>
      <div class="header">
        <span class="title">收到分享</span>
      </div>
      <div class="middle">
        <el-button class="receivebtn" type="primary" icon="el-icon-star-off"
                   @click="QRDecode()">
          扫码接收
        </el-button>
        <el-button class="receivebtn" type="primary" icon="el-icon-star-off"
                   @click="downloadFile()">
          下载文件
        </el-button>
        <input
            id="file-selector"
            ref="uploadInput"
            type="file"
            @change="uploadFile"
            style="display: none"/>
      </div>
      <div class="line"></div>
      <div class="body">
        <el-table
          :data="fileInfo"
          height="550"
          stripe
          border
          style="width: 100%"
          ref="accountTable">
          <el-table-column type="selection" width="150" align="center"> </el-table-column>
          <el-table-column prop="fileName" label="分享文件名" width="300" align="center">
          </el-table-column>
          <el-table-column prop="fileType" label="文件类型" width="150" align="center">
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小" width="150" align="center">
          </el-table-column>
          <el-table-column prop="ownerName" label="分享人" width="150" align="center">
          </el-table-column>
          <el-table-column prop="downloadTimes" label="下载次数" align="center"> </el-table-column>
        </el-table>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        tableData: "",
        fileList:'',
        fileInfo:[]
      };
    },
    methods: {
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePreview(file) {
        console.log(file);
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },
      saveShare() {

      },
      QRDecode(){//扫码接收
        console.log("扫码接收被点击了");
        this.$refs.uploadInput.click();
      },
      uploadFile(e){
        const file = e.target.files[0];
        console.log("执行了uploadFile方法");
        //获取文件
        let formData = new FormData();
        formData.append('file', file);
        console.log("获取到的文件" + formData);
        //发起请求
        var url = "http://localhost:8989/fileShare/readQRCode";
        let config = {
          headers:{
            'Content-Type':'multipart/form-data',
            'satoken':sessionStorage.getItem("saToken")
          }
        }
        console.log(config);
        this.$http.post(url,formData,config).then(res=>{
          console.log(res);
          let data = res.data;
          //加判断语句
          if(res.data.code!==10000){//若失败，直接提示错误原因
            this.$message.error(res.data.message);
          }else{//成功
            this.$message.success("提取文件" + res.data.message);
            console.log(this.fileInfo);//可以通过this获取到data中的内容
            console.log(this.fileInfo.length);
            this.fileInfo.push(res.data.data.fileInfo);
            console.log(this.fileInfo);
            console.log(this.fileInfo.length);
          }
        })
      },
      //文件下载
      downloadFile(){
        console.log("下载文件按钮点击");
        var that = this;
        for (var i=0;i<this.fileInfo.length;i++){
          this.$axios.get(that.fileInfo[i].downloadUrl)
        }
      }
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
  .receivebtn {
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
    margin: 20px 10px;
  }
  </style>