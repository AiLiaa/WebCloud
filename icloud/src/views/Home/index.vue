<template>
    <div class="index">
        <div class="header">
        <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item class="box2">首页</el-breadcrumb-item>
            <el-breadcrumb-item class="box2" :to="{ path: '/Mydisk' }">欢迎页面</el-breadcrumb-item>
        </el-breadcrumb>
        </div>
        <div class="main">
            <h2>欢迎进入云盘系统</h2>
        </div>
    </div>
</template>

<script>
    export default {
        
    }
</script>

<style lang="less" scoped>
.index{
    margin-left: 160px;
    margin-top: 10px;
}
.header{
    font-size: 100px;
}
.header .box2{
    font-size: 20px;
}
.main{
    float: left;
    color: blue;
    margin-top: 20px;
}
</style>