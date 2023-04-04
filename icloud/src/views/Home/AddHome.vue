<template>
  <div>
    <el-carousel width="100%">
      <el-carousel-item v-for="item in imgArray" :key="item">
        <img :src="item" class="rightImg">
      </el-carousel-item>
    </el-carousel>
  </div>
  </template>
  <script>
  export default {
    name: "item",
    data() {
      return {
        imgArray: [
          require('../../assets/css/image/1.jpg'),
          require('../../assets//css/image/2.jpg'),
          require('../../assets//css/image/3.jpg'),
        ]
      }
    }
  }
  </script>
  <style>
  .rightImg {
    margin: 10px 10px;
  }
    .el-carousel__item h3 {
      color: #475669;
      font-size: 18px;
      opacity: 0.75;
      line-height: 300px;
      margin: 0;
    }
    
    .el-carousel__item:nth-child(2n) {
      background-color: #99a9bf;
    }
    
    .el-carousel__item:nth-child(2n+1) {
      background-color: #d3dce6;
    }
  </style>