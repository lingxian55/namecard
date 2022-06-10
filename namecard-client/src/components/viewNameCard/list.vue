<template>
  <el-card style="margin: 0 15% 0 15%;padding: 0 5% 0 5%">
    <el-form label-width="80px">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item required label="地址:" prop="address">
            <el-input  v-model="formdata.address"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div style="padding: 20px 0 0 94%">
      <el-button type="primary" @click="getDecryptData">查看</el-button>
    </div>
    <div>
      <el-row :gutter="10" v-if="show" >
          <el-col :span="12">姓名:{{name}}}</el-col>
          <el-col :span="12">邮箱:{{email}}</el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "list",
  data(){
    return{
      formdata: {
        name: "",
        email: "",
        code:"",
        publicKey:"",
        address:"",
      },
      show:false,
      name:"",
      email:""
    }
  },
  methods:{
    getDecryptData(){

    }
  }
}

</script>

<style scoped>

</style>
