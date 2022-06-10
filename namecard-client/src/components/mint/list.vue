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
          <el-col :span="4" :offset="20">
            <el-form-item label="验证码:" >
              <el-input v-model="formdata.code" ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="showData">
            加密数据: {{data}}
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
      contractAddress: "0xf3f93F97868Bdfd7dDc2cC5a79Fd9E7491fFa856",
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
      let abi = [
        {
          "inputs": [
            {
              "internalType": "address",
              "name": "user",
              "type": "address"
            }
          ],
          "name": "acceptApply",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "anonymous": false,
          "inputs": [
            {
              "indexed": false,
              "internalType": "address",
              "name": "sender",
              "type": "address"
            },
            {
              "indexed": false,
              "internalType": "address",
              "name": "received",
              "type": "address"
            }
          ],
          "name": "Apply",
          "type": "event"
        },
        {
          "inputs": [
            {
              "internalType": "address",
              "name": "user",
              "type": "address"
            }
          ],
          "name": "applyForNameCard",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "bytes",
              "name": "namecard",
              "type": "bytes"
            }
          ],
          "name": "mintNameCard",
          "outputs": [],
          "stateMutability": "nonpayable",
          "type": "function"
        },
        {
          "inputs": [
            {
              "internalType": "address",
              "name": "sender",
              "type": "address"
            }
          ],
          "name": "getUserNameCard",
          "outputs": [
            {
              "internalType": "bytes",
              "name": "",
              "type": "bytes"
            }
          ],
          "stateMutability": "view",
          "type": "function"
        }
      ]
      this.contract = new this.web3.eth.Contract(abi, this.contractAddress)
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
    initData() {
      this.contract.methods.getNameCard(this.account).call(function (err, res) {
        if (err) {
          console.log("An error occured", err)
          return
        }
        console.log("The balance is: ", res)
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
            if(data.data.code==1&&data.data){
              this.contract.methods.mintNameCard(data.data).send({
                from: this.account,
                to: this.contractAddress
              }).then(this.$message.success).catch(this.$message.error);
              this.showData=true
              this.data=data.data
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
