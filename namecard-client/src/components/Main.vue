<template>
  <div class="hello">
    <div>
      <el-tabs v-model="activeName" >
        <el-tab-pane label="锻造名片" name="mint">
          <mint></mint>
        </el-tab-pane>
        <el-tab-pane label="查看名片" name="view">
          <viewNameCard></viewNameCard>
        </el-tab-pane>
        <el-tab-pane label="名片申请授权" name="apply">
          <apply></apply>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
const mint = () => {
    return import("./mint/list")
}
const viewNameCard=()=>{return  import("./viewNameCard/list")}
const apply= function () {
  return import("./apply/list")
}
export default {
  name: 'Main',
  components:{
    mint,
    viewNameCard,
    apply
  },
  data () {
    return {
      activeName:"mint"
    }
  },
  methods:{

  },

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
