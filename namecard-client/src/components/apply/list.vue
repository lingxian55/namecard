<template>
  <el-card style="margin: 0 15% 0 15%;padding: 0 5% 0 5%">

    <el-form label-width="80px">
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item required label="地址:" prop="address">
            <el-input  v-model="formdata.address"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <div style="padding: 0px 0 20px 80%">
            <el-button type="primary" @click="acceptApply">授权</el-button>
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item required label="信息:" prop="message">
            <el-input v-model="message"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <div style="padding: 0px 0 20px 80%">
            <el-button type="primary" @click="applyfor">申请</el-button>
          </div>
        </el-col>
      </el-row>
    </el-form>

    <el-table
        :data="tableData"
        border
        style="width: 100%">
      <el-table-column
          prop="address"
          label="申请者地址"
          >
      </el-table-column>
      <el-table-column
          prop="message"
          label="申请信息"
          >
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script>
export default {
  name: "list",
  data(){
    return {
      formdata: {
        name: "",
        email: "",
        code:"",
        publicKey:"",
        address:"",
        encryptData:"",
      },
      message:"",
      contract:{},
      apply:[],
      contractAddress: "0xC4ACDFcd4731ca5D1A1992A5f27a63C5018b3b50",
      account:"",
      tableData:[]
    }
  },
  methods:{
    initContract() {
      let abi = this.contracts.namecard.contractABI
      this.contract = new this.web3.eth.Contract(abi, this.contracts.namecard.contractAddress)
    },
    getAccounts() {
      window.ethereum.request({method: 'eth_requestAccounts'}).then(data => {
        this.account = data[0];
        this.eventInit()
      }).catch(error => {
        console.log(error)
      })
    },
    acceptApply(){
      this.contract.methods.acceptApply(this.formdata.address).send(
          {from:this.account}
      ).then(data=>{
        console.log(data)
        this.$message.success("成功")
      }).catch(err=>{
        console.log(err)
        this.$message.error("失败")
      })
    },
    applyfor(){
      this.contract.methods.applyForNameCard(this.formdata.address,this.message).send(
          {from:this.account}
      ).then(data=>{
        console.log(data)
        this.$message.success("成功")
      }).catch(err=>{
        console.log(err)
        this.$message.error("失败")
      })
    },
    eventInit(){
      let that=this
      this.contract.getPastEvents("Apply",{
        filter:{receiver:[that.account]},
        fromBlock:0,
        toBlock:"latest"
      },function (error,event){
        console.log(event)
      }).then(function (event){
        console.log(event)
        event.forEach(item=>{
          let i={}
          i.address=item.returnValues.sender
          i.message=item.returnValues.message
          console.log(item.returnValues.received);
          console.log(that.account);
          if(item.returnValues.received.toLowerCase()==that.account.toLowerCase()){
            that.tableData.push(i)
          }
        })
      })
    }
  },
  mounted() {
    this.initContract();
    this.getAccounts();
  }
}
</script>

<style scoped>

</style>