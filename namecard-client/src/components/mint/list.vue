<template>
  <div>
    <el-card style="margin: 0 15% 0 15%;padding: 0 5% 0 5%">
      <el-form :model="formdata" :rules="ruleForm" label-width="80px">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="姓名:" prop="name">
              <el-input v-model="formdata.name"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱:" prop="email">
              <el-input v-model="formdata.email"></el-input>
              <button @click="getCode()" class="code-btn" :disabled="!show">
                <span v-show="show">发送验证码</span>
                <span v-show="!show" class="count">{{ count }} s</span>
              </button>
            </el-form-item>
          </el-col>
          <el-col :span="6" :offset="18">
            <el-form-item label="验证码:" >
              <el-input v-model="formdata.code" ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="showData">
            <div>加密数据: {{data}}</div>
          </el-col>
        </el-row>
      </el-form>
      <div style="padding: 20px 0 0 94%">
        <el-button type="primary" @click="mint">锻造</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "list",
  data() {
    return {
      formdata: {
        name: "",
        email: "",
        code:"",
        publicKey:"",
        address:"",
      },
      showData:false,
      show: true,
      count: '',
      timer: null,
      dialogVisible: false,
      ruleForm: {
        email: [{type: 'string', required: true}],
        name: [{type: 'string', required: true}],
        code: [{type: 'string', required: true}]
      },
      account: "",
      publicKey: "",
      data: "",
      contract: {},
    }
  },
  methods: {
    getAccounts() {
      window.ethereum.request({method: 'eth_requestAccounts'}).then(data => {
        this.account = data[0];
      }).catch(error => {
        console.log(error)
      })
    },
    initContract() {
      let abi = this.contracts.namecard.contractABI
      this.contract = new this.web3.eth.Contract(abi, this.contracts.namecard.contractAddress)
    },
    getPublicKey() {
      window.ethereum.request({method: 'eth_requestAccounts'}).then(data => {
        this.account = data[0];
        window.ethereum.request({method: "eth_getEncryptionPublicKey", params: [this.account]}).then(data => {
          this.publicKey = data
          return true;
        }).catch(error => {
          this.$message.error(error.message)
          return false;
        })
      }).catch(error => {
        console.log(error)
        return false;
      })
    },
    mint() {
      this.Loading.open()
      window.ethereum.request({method: 'eth_requestAccounts'}).then(data => {
        this.account = data[0];
        this.formdata.address=this.account;
        window.ethereum.request({method: "eth_getEncryptionPublicKey", params: [this.account]}).then(data => {
          this.publicKey = data
          this.formdata.publicKey=this.publicKey;
          this.axios.post("/nameCard/getEnCyptData.json",this.formdata).then(data=>{
            console.log(data)
            if(data.data&&data.data.code==1){
              console.log(data.data.data)
              this.contract.methods.mintNameCard(data.data.data).send({
                from: this.account,
                to: this.contracts.namecard.contractAddress
              }).then(console.log).catch(console.log);
              this.showData=true
              this.data=data.data.data
              this.Loading.close()
            }else {
              this.$message.error(data.data.msg)
              this.Loading.close()
            }
          }).catch(error=>{
            this.$message.error(data.msg)
            this.Loading.close()
          })
        }).catch(error => {
          this.$message.error(error.message)
          this.Loading.close()
        })
      }).catch(error => {
        console.log(error)
        this.$message.error(error.message)
        this.Loading.close()
      })

    },
    getCode() {
      // 验证码倒计时
      if(this.formdata.email.indexOf("@")==-1||this.formdata.email.indexOf(".com")==-1){
        this.$message.error("输入正确邮箱格式");
        return
      }
      this.Loading.open()
      this.axios.post("/nameCard/getCode.json",this.formdata).then(data=>{
        if(data.data.code==1){
          this.$message.success("获取成功请查收")
          this.Loading.close()
          if (!this.timer) {
            this.count = 300
            this.show = false
            this.timer = setInterval(() => {
              if (this.count > 0 && this.count <= 300) {
                this.count--
              } else {
                this.show = true
                clearInterval(this.timer)
                this.timer = null
              }
            }, 1000)
          }
        }else {
          this.$message.error(data.data.msg)
        }
        this.Loading.close()
      }).catch(err=>{
        this.$message.error(err.msg)
        this.Loading.close()
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
.code-btn {
  width: 100px;
  height: 34px;
  position: absolute;
  top: 3px;
  right: 5px;
  z-index: 222;
  color: #F5A623;
  font-size: 14px;
  border: none;
  border-left: 1px solid #bababa;
  padding-left: 16px;
  background-color: #fff;
}
</style>
