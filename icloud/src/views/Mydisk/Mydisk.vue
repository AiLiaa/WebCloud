<template>
    <div class="mydisk">
        <el-dialog
          title="分享详情"
          :visible.sync="dialogVisible"
          width="30%">
          <strong>您的分享二维码：</strong>
<!--          <image width='390' height='200' :src="imgUrl"/>-->
<!--          <image src="F://wallhaven/bg.jpg"></image>-->
          <img :src="imgUrl" class="dialogImg">

          <strong><br>您的文件下载地址：</strong>
          <textarea id="textarea" cols="25" rows="1" v-model="fileUrl"></textarea>
          <span slot="footer" class="dialog-footer">
            <el-button @click="copy">复制</el-button>
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
          </span>
        </el-dialog>
        <!-- 按钮区域 -->
<!--        <el-image :src="imgUrl"></el-image>-->
        <div class="header">
            <!-- <el-button type="danger" >上传文件</el-button> -->
            <form style="heigth: 40px">
                <input type="file" class="input1_" @change="getFile($event)">
                <button class="btn1_" @click="submitForm($event)">提交</button>
            </form>

            <el-button type="primary" @click="createDir" style="margin-left: 30px">创建文件夹</el-button>
<!--            <el-button type="info" @click="deleteFile">删除</el-button>-->
<!--            <el-button type="primary" @click="shareFiles">分享</el-button>-->
            <el-button type="primary" @click="returnToLastDir()">返回上级</el-button>
<!--            <el-button type="primary"></el-button>-->
        </div>
<!--        展现面包屑-->
        <div class="bread">
          当前路径：{{pathString}}
        </div>
      <!-- 表格区域展示视图数据 -->
        <div class="wrapper">
            <el-table :data="tableData" border ref="accountTable" style="width:100%">
<!--                <el-table-column type="" width="100"> </el-table-column>-->
                <el-table-column v-if="show" prop="id" label="id" width="200" style="padding: 0"></el-table-column>
              <el-table-column prop="fileImg" label="" width="50"></el-table-column>
                <el-table-column prop="fileName" label="文件名" width="200"></el-table-column>
                <el-table-column prop="fileSize" label="大小(KB)" width="200"></el-table-column>
                <el-table-column prop="uploadTime" label="上传时间" width="200"> </el-table-column>
                <el-table-column label="操作" width="500">
                  <template slot-scope="scope">
                    <el-button size="mini" v-if="scope.row.isfolder===1" @click="enterNextDir(scope.row)">进入</el-button>
                    <el-button size="mini" v-if="scope.row.isfolder===0" @click="download(scope.row)">下载</el-button>
                    <el-button size="mini" @click="shareFile(scope.row)">分享</el-button>
                    <el-button size="mini" @click="handleEdit_(scope.row)">重命名</el-button>
                    <el-button size="mini" @click="moveFile(scope.row)">移动</el-button>
                    <el-button size="mini" type="danger" @click="deleteFile_(scope.row, scope.$index)">删除</el-button>
                  </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- 分页 -->
    </div>
</template>

<script>
    export default {
        data() {
            return{
              show:false,
              tableData: this.$global_msg.disk,
              pathArr:[],
              //点击分享按钮之后，值会变成blob的url地址，可以作为图片src进行返回
              imgUrl: "", 
              dialogVisible: false,
              fileUrl:''
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

      _methods: {
        copy(){
          var content = document.getElementById('textarea').innerHTML;

          navigator.clipboard.writeText(content)
          .then(() => {
            this.$alert("复制成功！")
          })
          .catch(err => {
          console.log('Something went wrong', err);
          })
        },

        //返回上级目录
        returnToLastDir(){
          this.updateFolderSize()
          if(this.pathArr.length===0){
            this.$message.info("已经到达根目录")
            return;
          }
          this.pathArr.pop();
          //todo:更新内容
          var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesAndFolders?path=' + this.pathString;
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
          this.updateFolderSize()
          console.log(row);
          var tempStr = row.fileName;
          this.pathArr.push(tempStr);
          //todo：更新内容
          var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesAndFolders?path=' + this.pathString;
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

        //文件分享
        shareFile(row){

          if(row.isfolder===1){
            this.$message.warning("暂时不支持分享文件夹内容")
            return;
          }
          var that = this;
          //请求token，由于异步的关系，在弹窗弹出来之前要改完数据，所有这段放前面，收图片肯定比收字符串慢

          // this.$loading("loading");//显示正在加载
          //接下来的逻辑为，发起请求，成功加载/加载失败
          console.log(sessionStorage.getItem("saToken"));
          var url = "http://localhost:8989/fileShare/createQRCode1?fileId=" + row.id;
          this.$axios({
            url:url,
            method:"GET",
            headers:{
              'satoken':sessionStorage.getItem("saToken")
            },
            responseType:"blob"
          }).then(function (res){
            console.log("请求发起成功");
            console.log(res);
            var typeArray = res.data;
            var blob = new Blob([typeArray],{type:res.headers['Content-Type']});
            var url = window.URL.createObjectURL(blob);
            while(1){
              console.log("还未获得url");
              if(url!=''){
                console.log("获得url了");
                console.log(url);
                that.imgUrl = url;
                that.$axios({
                  url:"http://localhost:8989/getShareCode?fileId="+row.id,
                  method:"GET",
                  headers:{
                    'satoken':sessionStorage.getItem("saToken")
                  },
                }).then(res=>{
                  console.log(res)
                  var token=res.data.data["access_token"]
                  that.fileUrl="http://loaclhost:8989/publicDownload?access_token="+token
                  that.dialogVisible=true
                })
                break;
              }
            }
            // var imgHtmlString = "<image src='" + url + "'>";
            //弹窗
            // that.$alert('<strong>您的分享二维码：</strong>' +
            //     "<image width='390' height='200' src='" + url + "'>" +
            //     '<strong>您的文件下载地址：</strong>', '分享详情', {
            //   dangerouslyUseHTMLString: true
            // });

          })
          
        },

        // 文件夹或文件重命名
        handleEdit_(row) {
          this.$prompt('修改文件名', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
          }).then(({ value }) => {
            if(value == null){
              this.$message({
                type: 'info',
                message: "输入不能为空"
              });
              return;
            }
            this.axios({
              method:'PUT',
              url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/rename',
              data:{
                folderId: row.id,
                newFolderName: value
              },
            }).then((resp) => {
              let data=resp.data;
              if(data.success) {
                this.getFileAndFolder()
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
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '取消输入'
            });
          });
        },


        //移动文件夹或文件
        moveFile(row) {
            console.log(row);
            this.$prompt('输入要移动的路径，格式: /**/**/', '移动到', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
            }).then(({ value }) => {
              if(value == null){
                this.$message({
                  type: 'info',
                  message: "输入不能为空"
                });
                return;
              }
              if(this.pathString == value){
                this.$message({
                  type: 'info',
                  message: "已在当前文件夹"
                });
                return;
              }
              this.axios({
                method:'PUT',
                url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/move',
                data:{
                  folderId: row.id,
                  newPath: value
                },
              }).then((resp) => {
                let data=resp.data;
                if(data.success) {
                  this.getFileAndFolder()
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
            }).catch(() => {
              this.$message({
                type: 'info',
                message: '取消输入'
              });
            });
        },

        updateFolderSize() {
          this.axios({
            method:'GET',
            url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/getFolderSize?path='+this.pathString,
          })
        },

        // 获取路径下文件和文件夹
        getFileAndFolder() {
          var url = 'http://localhost:8989/'+ sessionStorage.getItem("userId") + '/getFilesAndFolders?path=' + this.pathString;
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

        // 创建文件夹
        createDir() {
          this.$prompt('输入文件夹目录', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
          }).then(({ value }) => {
            if(value == null){
              this.$message({
                type: 'info',
                message: "输入不能为空"
              });
              return;
            }
            this.axios({
              method:'POST',
              url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/createFolder',
              data:{
                path: this.pathString,
                folderName: value
              }
            }).then((resp) => {
              let data=resp.data;
              if(data.success) {
                this.getFileAndFolder()
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
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '取消输入'
            });
          });
        },

        // 删除文件夹或文件
        deleteFile_(row, index) {
          if(confirm("是否删除！")) {
            this.axios({
              method:'DELETE',
              url:'http://localhost:8989/'+ sessionStorage.getItem("userId") +'/delete',
              data:{
                fileId: row.id
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


        getFile(event) {
          this.file=event.target.files[0];
          console.log(this.file);
        },

        submitForm(event) {
          event.preventDefault();
          let formData=new FormData();
          formData.append('ifile', this.file);
          formData.append("path", this.pathString)

          let config={
            headers:{
              'Content-Type': 'multipart/form-data',
              "satoken": sessionStorage.getItem("saToken")
            }
          }
          console.log(config)
          var url="http://localhost:8989/upload"
          this.$http.post(url, formData, config).then(res=>{
            console.log(res)
            let data=res.data
            if(data.code!==10000){
              this.$message.error(data.message)
            } else {
              this.getFileAndFolder()
              this.$message({
                message: "上传成功",
                type: "success"
              })
            }
          })
        },

        download(row) {
          let that=this
          console.log(row)
          let headers={
            'Content-Type': 'multipart/form-data',
            "satoken": sessionStorage.getItem("saToken")
          }
          var fileId=row.id
          var filename=row.fileName
          // let url="http://localhost:8989/download?fileId="+fileId
          // window.open(url, "_blank")
          this.axios({
            url: "http://localhost:8989/download",
            method: "GET",
            headers: headers,
            params: {
              fileId: fileId
            },
          }).then(res=>{
            console.log(res)
            that.$message({
              message: "请求成功",
              type: "success"
            })
            let blob = new Blob([res.data]);
            let downloadElement = document.createElement("a");
            let href = window.URL.createObjectURL(blob); //创建下载的链接
            downloadElement.href = href;
            downloadElement.download = filename; //下载后文件名
            document.body.appendChild(downloadElement);
            downloadElement.click(); //点击下载
            document.body.removeChild(downloadElement); //下载完成移除元素
            window.URL.revokeObjectURL(href); //释放掉blob对象
          }).catch(function(error){
            if (error.response.status==400){
              that.$message({
                message: "文件不存在",
                type: "error"
              })
            }
            else if (error.response.status==500){
              that.$message({
                message: "抱歉，文件已丢失",
                type: "error"
              })
            }
          })

        }
      },
      
      get methods() {
        return this._methods;
      },
      set methods(value) {
        this._methods=value;
      },
    }
</script>

<style lang="less" scoped>
.mydisk{
    margin-left: 0px;
}
.header{
    display: flex;
    margin-top: 10px;
    margin-left: 20px;
}

.bread{
  display: flex;
  margin-left: 20px;
  margin-top: 20px;
}

.wrapper{
    margin: 10px 20px;
}

.input1_ {
    width: 150px;
}
.btn1_ {
    background-color: #409EFF;
    border: 0px;
    height: 30px;
    border-radius: 8px;
    color: white;
    width: 50px;
}

.dialogImg{
  width: 80%;
  //margin-left: 5%;
  height: 60%;
  margin-bottom: 5%;
}
</style>