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
          <el-col :span="12">姓名:{{name}}</el-col>
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
        encryptData:""
      },
      show:false,
      name:"",
      email:"",
      contract:{},
      account:""
    }
  },
  methods:{
    getDecryptData(){
      this.Loading.open();
      this.contract.methods.getUserNameCard(this.formdata.address).call({
        from: this.account,
        to: this.contractAddress
      }).then(data=>{
        console.log(data)
        this.formdata.encryptData=data
        this.axios.post("/nameCard/getDeCyptData.json",this.formdata).then(data=>{
          if(data.data&&data.data.code==1){
            this.name=data.data.data.name;
            this.email=data.data.data.email;
            this.show=true;
          }else {
            this.$message.error(data.data.msg)
          }
          this.Loading.close();
        }).catch(err=>{
          console.log(err)
          this.Loading.close();
        })
      }).catch(error => {
        console.log(error)
        this.$message.error(error.toString())
        this.Loading.close();
      })

    },
    initContract() {
      let abi = this.contracts.namecard.contractABI
      this.contract = new this.web3.eth.Contract(abi, this.contracts.namecard.contractAddress)
    },
    getAccounts() {
      window.ethereum.request({method: 'eth_requestAccounts'}).then(data => {
        this.account = data[0];
      }).catch(error => {
        console.log(error)
      })
    },
  },
  mounted() {
    this.initContract();
    this.getAccounts();
  }
}

</script>

<style scoped>

</style>
