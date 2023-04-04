<template>
    <div>
      <div class="header">
        <span class="title">回收站</span>
        <el-button style="margin-left: 20px; height: 40px;margin-top: 10px" type="primary" @click="returnToLastDir()">返回上级</el-button>
      </div>
<!--      <div class="middle">-->
<!--        <el-button class="canclebtn" type="primary" icon="el-icon-folder-checked"-->
<!--          @click="recoverFile">恢复</el-button-->
<!--        >-->
<!--      </div>-->
      <div class="line"></div>
      <!--        展现面包屑-->
      <div class="bread">
        当前路径：{{pathString}}
      </div>
      <div class="body">
        <el-table
          :data="tableData"
          height="550"
          stripe
          border
          style="width: 100%"
          ref="accountTable"
        >
<!--          <el-table-column type="selection" width="150"> </el-table-column>-->
          <el-table-column v-if="show" prop="id" label="id" width="200">
          </el-table-column>
          <el-table-column prop="fileImg" label="" width="50">
          </el-table-column>
          <el-table-column prop="fileName" label="文件名" width="200">
          </el-table-column>
          <el-table-column prop="virtualPath" label="文件目录" width="300">
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小(KB)" width="150">
          </el-table-column>
          <el-table-column prop="deleteTime" label="删除日期"> </el-table-column>
          <el-table-column label="操作" width="400">
            <template slot-scope="scope">
              <el-button size="mini" v-if="scope.row.isfolder===1" @click="enterNextDir(scope.row)">进入</el-button>
              <el-button size="mini" v-if="scope.row.binPath==='/'" @click="recoverFile_(scope.row, scope.$index)">恢复</el-button>
              <el-button size="mini" v-if="scope.row.binPath==='/'" type="danger" @click="deleteBinFile_(scope.row,scope.$index)">彻底删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </template>
      
      <script>
  export default {
    data() {
      return {
        tableData: this.$global_msg.binData,
        show: false,
        pathArr:[],
      };
    },
    computed:{
      //经过拼接的面包屑目录
      pathString:function (){
        var pstr = "/";
        if(this.pathArr.length===0){
          return pstr;
        }
        for(var i = 0;i<this.pathArr.length;i++){
          var tempStr = this.pathArr[i] + "/";
          pstr += tempStr;
        }
        return pstr;
      }
    },

    methods: {
      //返回上级目录
      returnToLastDir(){
        if(this.pathArr.length===0){
          this.$message.info("已经到达根目录")
          return;
        }
        this.pathArr.pop();
        var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesByBinPath?path=' + this.pathString;
        this.axios({
          method:'GET',
          url: url,
        }).then((resp)=>{
          let data = resp.data;
          console.log(data)
          if (data.success) {
            this.tableData = data.data.fileList;
          }
        })
      },

      //点入文件夹
      enterNextDir(row){
        console.log(row);
        var tempStr = row.fileName;
        this.pathArr.push(tempStr);
        var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesByBinPath?path=' + this.pathString;
        this.axios({
          method:'GET',
          url: url,
        }).then((resp)=>{
          let data = resp.data;
          console.log(data)
          if (data.success) {
            this.tableData = data.data.fileList;
          }
        })
      },

        recoverFile_(row, index) {
          if(confirm("是否恢复")) {
            this.axios({
              method:'PUT',
              url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/recoverFolder',
              data:{
                folderId: row.id
              }
            }).then((resp) => {
              let data=resp.data;
              if(data.success) {
                this.tableData.splice(index,1)
                this.$message({
                  type: 'success',
                  message: data.message
                });
              } else {
                this.$message({
                  type: 'info',
                  message: data.message
                });
              }
            });
          }else {
            this.$message({
              type: 'info',
              message: '取消操作'
            });
          }
        },

      // 删除文件夹或文件
      deleteBinFile_(row, index) {
        if(confirm("是否删除！")) {
          this.axios({
            method:'DELETE',
            url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/deleteBinFolder',
            data:{
              folderId: row.id
            }
          }).then((resp) => {
            let data=resp.data;
            if(data.success) {
              this.tableData.splice(index,1)
              this.$message({
                type: 'success',
                message: data.message
              });
            } else {
              this.$message({
                type: 'info',
                message: data.message
              });
            }
          });
        }else {
          this.$message({
            type: 'info',
            message: '取消删除'
          });
        }
      },

      // 获取路径下文件和文件夹
      getBinFileAndFolder() {
        var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesAndFolders?path=/';
        this.axios({
          method:'GET',
          url: url,
        }).then((resp)=>{
          let data = resp.data;
          console.log(data)
          if (data.success) {
            this.tableData = data.data.fileList;
          }
        })
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
   // margin-left: 170px;
    margin-left: 20px;
  }
  .middle {
    height: 50px;
    line-height: 50px;
    display: flex;
  }
  .canclebtn {
    display: flex;
    //margin-left: 170px;
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
   // margin-left: 170px;
   margin: 20px 10px;
  }
  .bread{
    display: flex;
    margin-left: 20px;
    margin-top: 20px;
  }
  </style>