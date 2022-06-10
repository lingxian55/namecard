import { Loading } from 'element-ui';

export default {
  temp:undefined,
  open(){
    this.temp=Loading.service({fullscreen:true})
  },
  close(){
    if(this.temp){
      this.temp.close();
    }
  }
}
