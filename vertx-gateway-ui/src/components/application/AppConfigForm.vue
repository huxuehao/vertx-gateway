<script lang="ts" setup>
import { nextTick, onMounted, ref } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    default: () => null
  }
})

onMounted(() => {
  nextTick(() => {
    if(props.data) {
      Object.assign(config.value, props.data)
    }
  })
})

const validatePositiveInteger = (rule: any, value: any, callback: any) => {
  const reg = /^(0|[1-9]\d*)$/;
  if (!reg.test(value)) {
    callback(new Error('请输入0或正整数'));
  } else {
    callback();
  }
};
const rules = {
  protocol: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  port: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  certificateType: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  certificateKey: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  certificatePath: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  corsOpen: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  allowCredentials: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  allowedOrigin: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  maxAgeSeconds: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  allowedMethods: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  contentLength: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  sessionTimeOut: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  sessionCookieName: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  decoderInitialBufferSize: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  maxPoolSize: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  maxInitialLineLength: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  maxHeaderSize: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
    {
      validator: validatePositiveInteger,
      trigger: 'blur'
    }
  ],
  keepAlive: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
};

let active = ref(0)
let form = ref()
let config = ref<any>({
  protocol: "HTTP",
  corsOpen: true,
  allowedOrigin: "^https?://.*",
  allowCredentials: false,
  maxAgeSeconds: 0,
  allowedMethods: ["GET", "POST", "PUT", "HEAD", "DELETE"],
  contentLength: 0,
  sessionTimeOut: 1800000,
  sessionCookieName: "Vertx-Gateway.session",
  decoderInitialBufferSize: 128,
  maxPoolSize: 5,
  maxInitialLineLength: 4096,
  maxHeaderSize: 8192,
  keepAlive: true
})
const changeStep = (currentActive: number) => {
  if (currentActive === active.value) {
    return;
  }
  active.value = currentActive;
};

defineExpose({
  form,
  config
})

</script>

<template>
  <el-steps simple :active="active" style="margin-bottom: 20px;">
    <el-step
      title="端口配置"
      icon="Tickets"
      @click.native="changeStep(0)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="跨域配置"
      icon="Tickets"
      @click.native="changeStep(1)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="参数配置"
      icon="Tickets"
      @click.native="changeStep(2)"
      style="cursor: pointer"
    ></el-step>
  </el-steps>
  <el-form 
      ref="form"
      :rules="rules"
      :model="config"
      label-position="right"
      label-width="200px">
    <div v-show="active === 0">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="协议类型" prop="protocol" label-width="80px">
            <el-select
              v-model="config.protocol"
              placeholder="请选择协议类型"
              clearable
            >
              <el-option label="HTTP" value="HTTP" />
              <el-option label="HTTPS" value="HTTPS" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="应用端口" prop="port" label-width="80px">
            <el-input-number 
              v-model="config.port"
              placeholder="请填写应用端口"
              min="1000" 
              max="10000" 
              clearable
              style="width: 100%;"
              controls-position="right"></el-input-number>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.protocol === 'HTTPS'">
        <el-col :span="12">
          <el-form-item label="证书类型" prop="certificateType">
            <el-select
              v-model="config.certificateType"
              placeholder="请选择证书类型"
              clearable
            >
              <el-option label="PEM" value="PEM" />
              <el-option label="PFX" value="PFX" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="证书Key" prop="certificateKey">
            <el-input 
              v-model="config.certificateKey"
              placeholder="请填写证书Key"
              maxlength="100"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.protocol === 'HTTPS'">
        <el-col :span="24">
          <el-form-item label="证书Path" prop="certificatePath">
            <el-input 
              v-model="config.certificatePath"
              placeholder="请填写证书Path"
              maxlength="500"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 1">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="是否开启" prop="corsOpen" label-width="95px">
            <el-select
              v-model="config.corsOpen"
              placeholder="请选择协议类型"
              clearable
            >
              <el-option label="开启" :value="true" />
              <el-option label="关闭" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="携带凭据" prop="allowCredentials" label-width="95px">
            <template #label>
              <span>携带凭据</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="用于控制是否允许跨域请求携带 凭据(Credentials)。凭据包括 Cookies、HTTP 认证信息(如 Authorization 头)和 TLS 客户端证书 等"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.allowCredentials"
              placeholder="请选选择是否允许"
              clearable
            >
              <el-option label="允许" :value="true" />
              <el-option label="禁止" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.corsOpen">
        <el-col :span="12">
          <el-form-item label="允许来源" prop="allowedOrigin" label-width="95px">
            <template #label>
              <span>允许来源</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="用于指定允许访问服务器资源的源(Origin)，就是用来告诉浏览器，哪些源可以向服务器发起跨域请求，支持正则表达式。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.allowedOrigin"
              placeholder="请填允许来源"
              maxlength="200"
              clearable></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="缓存时间" prop="maxAgeSeconds" label-width="95px">
            <template #label>
              <span>缓存时间</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="在缓存时间内，对于相同的跨域请求，客户端将直接使用缓存的预检结果，而不再发送新的预检请求。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.maxAgeSeconds"
              placeholder="请填缓存时间"
              clearable>
              <template #append>秒</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.corsOpen">
        <el-col :span="24">
          <el-form-item label="允许方法" prop="allowedMethods" label-width="95px">
            <el-checkbox-group v-model="config.allowedMethods">
              <el-checkbox label="GET"/>
              <el-checkbox label="POST"/>
              <el-checkbox label="PUT"/>
              <el-checkbox label="HEAD"/>
              <el-checkbox label="DELETE"/>
              <el-checkbox label="OPTIONS"/>
              <el-checkbox label="TRACE"/>
              <el-checkbox label="CONNECT"/>
              <el-checkbox label="PATCH"/>
            </el-checkbox-group>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 2">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="后端服务前缀" prop="serverUrlPrefix">
            <template #label>
              <span>后端服务前缀</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="即代理服务的前缀，其目的是为了填补该应用下API的后端服务URL。 例如：http://127.0.0.1/8080"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.serverUrlPrefix"
              placeholder="请填后端服务前缀"
              maxlength="100"
              clearable></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="会话Cookie名称" prop="sessionCookieName">
            <el-input 
              v-model="config.sessionCookieName"
              placeholder="请填会话Cookie名称"
              maxlength="100"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        
        <el-col :span="12">
          <el-form-item label="会话超时时间" prop="sessionTimeOut">
            <el-input 
              v-model="config.sessionTimeOut"
              placeholder="请填写会话超时时间"
              maxlength="100"
              clearable>
              <template #append>ms</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="最大ContentLength" prop="contentLength">
            <el-input 
              v-model="config.contentLength"
              placeholder="请填最大ContentLength"
              maxlength="100"
              clearable>
              <template #append>byte</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="客户端连接池数量" prop="maxPoolSize">
            <el-input-number
              v-model="config.maxPoolSize"
              placeholder="请填写客户端连接池数量"
              min="1"
              style="width: 100%;"
              controls-position="right"
              clearable></el-input-number>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户端初始化缓冲区大小" prop="decoderInitialBufferSize">
            <el-input 
              v-model="config.decoderInitialBufferSize"
              placeholder="请填写初始化缓冲区大小"
              maxlength="100"
              clearable>
              <template #append>byte</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="客户端URL参数值最大长度" prop="maxInitialLineLength">
            <el-input 
              v-model="config.maxInitialLineLength"
              placeholder="请填写初始化缓冲区大小"
              maxlength="100"
              clearable>
              <template #append>byte</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="客户端Header最大长度" prop="maxHeaderSize">
            <el-input 
              v-model="config.maxHeaderSize"
              placeholder="请填写客户端Header最大长度"
              maxlength="100"
              clearable>
              <template #append>byte</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="客户端开启keepAlive" prop="keepAlive">
            <el-select
              v-model="config.keepAlive"
              placeholder="请选选择是否开启"
              clearable
            >
              <el-option label="开启" :value="true" />
              <el-option label="关闭" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
  </el-form>
</template>

<style lang="scss" scoped>
</style>