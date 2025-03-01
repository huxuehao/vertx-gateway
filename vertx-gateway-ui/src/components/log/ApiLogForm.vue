<script lang="ts" setup>
import { nextTick, onMounted, reactive, ref } from "vue";
import CodeEditor from "../code/CodeEditor.vue";
import { formatDate, formatJSON } from "@/utils/tools"

const props = defineProps({
  data: {
    type: Object,
    default: () => {},
  },
});

let config = ref<any>({});
onMounted(() => {
  nextTick(() => {
    if (props.data) {
      config.value = props.data;
      const patter = "yyyy-MM-dd HH:mm:ss.SSS"
      config.value.startTime = formatDate(Number(config.value.startTime), patter)
      config.value.endTime = formatDate(Number(config.value.endTime), patter)
    }
  });
});

let logText = ref("")
const dialogProp = reactive({
  visible: false,
  withHeader: false,
  showClose: true,
  appendToBody: true, 
  closeOnPressEscape: false,
  title: "错误日志",
  size:"100%",
  direction: "btt"
});

// 查看header参数
const showHeaderParams = () => {
  logText.value = config.value.headerParams
  dialogProp.title = "Header参数"
  dialogProp.visible = true
};

// 查看path参数
const showPathParams = () => {
  logText.value = config.value.pathParams
  dialogProp.title = "Path参数"
  dialogProp.visible = true
};

// 查看query参数
const showQueryParams = () => {
  logText.value =  config.value.queryParams
  dialogProp.title = "Query参数"
  dialogProp.visible = true
};

// 查看错误日志
const showErrorLog = () => {
  logText.value =  config.value.errorContent
  dialogProp.title = "错误日志"
  dialogProp.visible = true
};

// 关闭日志
const closeDrawer = () => {
  dialogProp.visible = false
};

</script>

<template>
  <el-form 
    :model="config" 
    label-position="right"
    label-width="80px">
    <el-row :gutter="0">
      <el-col :span="12">
        <el-form-item label="开始时间" prop="startTime">
          <el-input v-model="config.startTime" prefix-icon="Clock"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="结束时间" prop="endTime">
          <el-input v-model="config.endTime" prefix-icon="Clock"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="0">
      <el-col :span="12">
        <el-form-item label="请求方法" prop="reqMethod">
          <el-input v-model="config.reqMethod" prefix-icon="CollectionTag"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="请求路径" prop="reqPath">
          <el-input v-model="config.reqPath" prefix-icon="Location"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="0">
      <el-col :span="24">
        <el-form-item label="" prop="">
          <el-button type="primary" @click="showHeaderParams()">查看Header参数</el-button>
          <el-button type="primary" @click="showPathParams()">查看Path参数</el-button>
          <el-button type="primary" @click="showQueryParams()">查看Query参数</el-button>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="0">
      <el-col :span="12">
        <el-form-item label="代理方法" prop="proxyMethod">
          <el-input v-model="config.proxyMethod" prefix-icon="CollectionTag"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="代理路径" prop="proxyPath">
          <el-input v-model="config.proxyPath" prefix-icon="Location"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="0">
      <el-col :span="12">
        <el-form-item label="请求IP" prop="reqIp">
          <el-input v-model="config.reqIp" prefix-icon="LocationInformation"></el-input>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="执行结果" prop="status">
          <el-select v-model="config.status">
            <template #prefix>
              <el-icon v-if="config.status"><CircleCheck /></el-icon>
              <el-icon v-else><CircleClose /></el-icon>
            </template>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="0" v-if="!config.status">
      <el-col :span="12"></el-col>
      <el-col :span="12">
        <el-form-item label="" prop="">
          <el-button type="danger" @click="showErrorLog()">查看错误日志</el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
  <el-drawer
      v-bind="dialogProp"
      v-model="dialogProp.visible"
      class="drawer-none-padding">
      <CodeEditor 
        v-if="dialogProp.visible"
        v-model="logText" 
        @close="closeDrawer()"
        :title="dialogProp.title"
        readOnly
        element-loading-background="rgba(0, 0, 0, 0)">
      </CodeEditor>
  </el-drawer>
</template>
<style lang="scss" scoped></style>
