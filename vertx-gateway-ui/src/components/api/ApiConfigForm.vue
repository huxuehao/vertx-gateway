<script lang="ts" setup>
import { ElMessage } from 'element-plus';
import { h, nextTick, onMounted, ref } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    default: () => null
  }
})

const addReadStar = (data: { column: any; $index: number }) => {
  return [
    h(
      "span",
      {
        style: "color: #f67a7a",
      },
      "*"
    ),
    h("span", " " + data.column.label),
  ];
};

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
  routePath: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  routeMethod: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  routeContentType: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  authType: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  authHeaderName: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  accessLimitType: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  routerTimes: [
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
  ipTimes: [
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
  proxyApiType: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  proxyApiMethod: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  proxyRedirectUrl: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  loadBalancingStrategy: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  proxyTimeoutTime: [
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
  proxyRetry: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    }
  ],
  proxyRetryTimes: [
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
  proxyRetryInterval: [
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
};

let active = ref(2)
let form = ref()
let config = ref<any>({
  authType: "JWT",
  authHeaderName: "VG-AUTH",
  accessLimitType: "MINUTE",
  ipTimes: 120,
  routerTimes: 0,
  routeContentType: ["*"],
  routeParams: [],
  proxyApiType: "HTTP",
  proxyApiMethod: null,
  proxyRedirectUrl: null,
  proxyApiUrls: [],
  loadBalancingStrategy: "POLLING",
  proxyConstParams: [],
  proxyTimeoutTime: 10000,
  proxyRetry: true,
  proxyRetryTimes: 1,
  proxyRetryInterval: 3000
})
const changeStep = (currentActive: number) => {
  if (currentActive === active.value) {
    return;
  }
  active.value = currentActive;
};

const routeMethodChange = (value: any) => {
  if(!config.value.proxyApiMethod) {
    config.value.proxyApiMethod = value
  } else if (value) {
    config.value.proxyApiMethod = null
    ElMessage({
      message: "请重新选择 代理配置/代理服务方法",
      plain: true
    })
  }
};

// 添加路由参数
const clickAddRouterParams = () => {
  if (!config.value.routeParams) {
    config.value.routeParams = [];
  }
  config.value.routeParams.push({
    position: "QUERY",
    key: null,
    type: "String",
    required: true,
    defaultVal: null,
    remark: null
  });
};

// 移除路由参数
const clickRemoveRouterParams = (index: number) => {
  config.value.routeParams.splice(index, 1);
};

// 添加代理URL
const clickAddProxyUrl = () => {
  if (!config.value.proxyApiUrls) {
    config.value.proxyApiUrls = [];
  }
  config.value.proxyApiUrls.push({
    url: null,
    weight: 1
  });
};

// 移除代理URL
const clickRemoveProxyUrl = (index: number) => {
  config.value.proxyApiUrls.splice(index, 1);
};

// 添加代理URL
const clickAddProxyConstParams = () => {
  if (!config.value.proxyConstParams) {
    config.value.proxyConstParams = [];
  }
  config.value.proxyConstParams.push({
    position: "QUERY",
    key: null,
    value: null,
    required: true,
    remark: null
  });
};

// 移除代理URL
const clickRemoveProxyConstParams = (index: number) => {
  config.value.proxyConstParams.splice(index, 1);
};

defineExpose({
  form,
  config
})

</script>

<template>
  <el-steps simple :active="active" style="margin-bottom: 20px;">
    <el-step
      title="权限认证"
      icon="Tickets"
      @click.native="changeStep(0)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="访问限制"
      icon="Tickets"
      @click.native="changeStep(1)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="路由配置"
      icon="Tickets"
      @click.native="changeStep(2)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="代理配置"
      icon="Tickets"
      @click.native="changeStep(3)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="超时重试"
      icon="Tickets"
      @click.native="changeStep(4)"
      style="cursor: pointer"
    ></el-step>
  </el-steps>
  <el-form 
      ref="form"
      :rules="rules"
      :model="config"
      label-position="right"
      label-width="120px">
    <div v-show="active === 0">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="权限认证类型" prop="authType">
            <template #label>
              <span>权限认证类型</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="当选择的是“无认证”时，网关处理的用户请求是非鉴权的。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.authType"
              placeholder="请选择权限认证类型"
              clearable
            >
              <el-option label="JWT" value="JWT" />
              <el-option label="无认证" value="NONE" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="config.authType !== 'NONE'">
          <el-form-item 
            label="请求头名称" 
            prop="authHeaderName">
            <template #label>
              <span>请求头名称</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="当“权限认证类型”选择的是非“无认证”是，用户在请求网关时需要在请求头带上 token，token 的名称就是“请求头名称”"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.authHeaderName"
              placeholder="请填写请求头名称"
              maxlength="40"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 1">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="访问限制类型" prop="accessLimitType">
            <el-select
              v-model="config.accessLimitType"
              placeholder="请选择访问限制类型"
              clearable
            >
              <el-option label="每分钟" value="MINUTE" />
              <el-option label="每小时" value="HOUR" />
              <el-option label="每一天" value="DAY" />
              <el-option label="无限制" value="NONE" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.accessLimitType !== 'NONE'">
        <el-col :span="12">
          <el-form-item label="IP访问次数" prop="ipTimes">
            <template #label>
              <span>IP访问次数</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="控制单个 IP 在单位时间内访问该路由的次数上限，0 表示不限制。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.ipTimes"
              placeholder="请填写IP访问次数"
              clearable>
              <template #prepend>不超过</template>
              <template #append>次</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="路由访问次数" prop="routerTimes">
            <template #label>
              <span>路由访问次数</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="控制当前路由在单位时间内访问次数总和的上限，0 表示不限制。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.routerTimes"
              placeholder="请填写路由访问次数"
              clearable>
              <template #prepend>不超过</template>
              <template #append>次</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 2">
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form-item 
            label="网关路由地址" 
            prop="routePath">
            <template #label>
              <span>网关路由地址</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="路径不要添加 IP:PORT，正确写法如下示例：/vertx/gateway/demo"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.routePath"
              placeholder="请填写网关路由地址"
              maxlength="200"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="网关路由方法" prop="routeMethod">
            <el-select
              v-model="config.routeMethod"
              placeholder="请选择网关路由方法"
              @change="routeMethodChange"
              clearable
            >
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="HEAD" value="HEAD" />
              <el-option label="DELETE" value="DELETE" />
              <el-option label="OPTIONS" value="OPTIONS" />
              <el-option label="TRACE" value="TRACE" />
              <el-option label="CONNECT" value="CONNECT" />
              <el-option label="PATCH" value="PATCH" />
              <el-option label="ALL" value="ALL" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item 
            label="Content-Type" 
            prop="routeContentType">
            <template #label>
              <span>Content-Type</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="配置请求当前路由时的请求需要携带的 Content-Type，请求携带的 Content-Type 与当前配置不匹配时将报“找不到对应的路由”。支持正则，支持多选。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.routeContentType"
              multiple
              filterable
              collapse-tags
              placeholder="请选择网关Content-Type"
              clearable
            >
              <el-option label="*" value="*" />
              <el-option label="text/*" value="text/*" />
              <el-option label="text/plain" value="text/plain" />
              <el-option label="text/html" value="text/html" />
              <el-option label="image/*" value="image/*" />
              <el-option label="image/jpeg" value="image/jpeg" />
              <el-option label="image/png" value="image/png" />
              <el-option label="image/gif" value="image/gif" />
              <el-option label="multipart/*" value="multipart/*" />
              <el-option label="multipart/form-data" value="multipart/form-data" />
              <el-option label="application/*" value="application/*" />
              <el-option label="application/json" value="application/json" />
              <el-option label="application/pdf" value="application/pdf" />
              <el-option label="application/xml" value="application/xml" />
              <el-option label="application/octet-stream" value="application/octet-stream" />
              <el-option label="application/x-www-form-urlencoded	" value="application/x-www-form-urlencoded" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form-item label="网关路由参数">
            <template #label>
              <span>网关路由参数</span>
              <el-popover
                placement="top-start"
                :width="500"
                trigger="hover">
                <p><span style="font-weight: bolder;">QUERY</span>：表示请求路径参数。</p>
                <p><span style="font-weight: bolder;">PATH</span>：表示路径参数，使用路径参数时需要再“网关路由地址”上使用同名参数名进行占位，例如“/vertx/gateway/get/:id”，那么“网关路由参数”需要有同名的 PATH 参数。</p>
                <p><span style="font-weight: bolder;">HEADER</span>：表示请求头参数。</p>
                <p style="color: red;font-weight: bolder;">配置的参数将同步给代理服务，同时请求 Body 也将同步给代理服务。</p>
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-table
              :data="config.routeParams"
              :max-height="160"
              :header-cell-style="{
                backgroundColor: '#F5F7FA',
                color: '#666666',
              }"
              border
            >
              <el-table-column type="index" label="#" width="40px" align="center"></el-table-column>
              <el-table-column label="位置" width="100px" align="center" prop="position" :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'routeParams[' + scope.$index + '].position'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-select v-model="scope.row.position" clearable>
                      <el-option label="QUERY" value="QUERY" />
                      <el-option label="PATH" value="PATH" />
                      <el-option label="HEADER" value="HEADER" />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="参数名" align="center" prop="key" show-overflow-tooltip :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'routeParams[' + scope.$index + '].key'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-input v-model="scope.row.key" clearable></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="类型" width="100px" align="center" prop="type" :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'routeParams[' + scope.$index + '].type'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-select v-model="scope.row.type" clearable>
                      <el-option label="String" value="STRING" />
                      <el-option label="Long" value="LONG" />
                      <el-option label="Integer" value="INTEGER" />
                      <el-option label="Float" value="FLOAT" />
                      <el-option label="Double" value="DOUBLE" />
                      <el-option label="Boolean" value="BOOLEAN" />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="必填" width="60px" align="center" prop="required">
                <template #default="scope">
                  <el-checkbox v-model="scope.row.required"></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="默认值" align="center" show-overflow-tooltip prop="defaultVal">
                <template #default="scope">
                  <el-input v-model="scope.row.defaultVal" clearable></el-input>
                </template>
              </el-table-column>
              <el-table-column label="描述" align="center" show-overflow-tooltip prop="remark">
                <template #default="scope">
                  <el-input v-model="scope.row.remark" clearable></el-input>
                </template>
              </el-table-column>
              <el-table-column align="center" width="50px">
                <template #header>
                  <el-button
                    title="添加"
                    @click="clickAddRouterParams()"
                    icon="Plus"
                    circle
                    type="primary"
                  ></el-button>
                </template>
                <template #default="scope">
                  <el-button
                    title="删除"
                    @click="clickRemoveRouterParams(scope.$index)"
                    icon="Delete"
                    circle
                    type="danger"
                  ></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 3">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="代理服务类型" prop="proxyApiType">
            <el-select
              v-model="config.proxyApiType"
              placeholder="请选择代理服务类型"
              clearable
            >
              <el-option label="HTTP/HTTPS" value="HTTP" />
              <el-option label="请求重定向" value="REDIRECT" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="config.proxyApiType === 'HTTP'">
          <el-form-item label="代理服务方法" prop="proxyApiMethod">
            <el-select
              v-model="config.proxyApiMethod"
              placeholder="请选择代理服务方法"
              clearable
            >
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="HEAD" value="HEAD" />
              <el-option label="DELETE" value="DELETE" />
              <el-option label="OPTIONS" value="OPTIONS" />
              <el-option label="TRACE" value="TRACE" />
              <el-option label="CONNECT" value="CONNECT" />
              <el-option label="PATCH" value="PATCH" />
              <el-option label="ALL" value="ALL" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.proxyApiType === 'REDIRECT'">
        <el-col :span="24">
          <el-form-item 
            label="代理跳转地址" 
            prop="proxyRedirectUrl">
            <template #label>
              <span>代理跳转地址</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="请填写完整的跳转URL，如 http://ip:port/xxx"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.proxyRedirectUrl"
              placeholder="请填写代理跳转地址"
              maxlength="500"
              clearable></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.proxyApiType === 'HTTP'">
        <el-col :span="24">
          <el-form-item label="代理服务URL">
            <el-table
              :data="config.proxyApiUrls"
              :max-height="160"
              :header-cell-style="{
                backgroundColor: '#F5F7FA',
                color: '#666666',
              }"
              border
            >
              <el-table-column type="index" label="#" width="40px" align="center"></el-table-column>
              <el-table-column label="URL" align="center" prop="url" show-overflow-tooltip :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'proxyApiUrls[' + scope.$index + '].url'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-input v-model="scope.row.url" maxlength="500" clearable></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="权重" align="center" width="200px" prop="weight" :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                      label-width="0"
                      :style="{ marginBottom: 0 }"
                      :prop="'proxyApiUrls[' + scope.$index + '].weight'"
                      :rules="[
                        { required: true, message: '必填项', trigger: 'blur' },
                        { validator: validatePositiveInteger, trigger: 'blur' },
                      ]"
                    >
                    <el-input v-model="scope.row.weight" clearable></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" width="50px">
                <template #header>
                  <el-button
                    title="添加"
                    @click="clickAddProxyUrl()"
                    icon="Plus"
                    circle
                    type="primary"
                  ></el-button>
                </template>
                <template #default="scope">
                  <el-button
                    title="删除"
                    @click="clickRemoveProxyUrl(scope.$index)"
                    icon="Delete"
                    circle
                    type="danger"
                  ></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12" v-if="config.proxyApiType === 'HTTP' && config.proxyApiUrls.length > 1">
          <el-form-item label="负载均衡策略" prop="loadBalancingStrategy">
            <template #label>
              <span>负载均衡策略</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content=""
              >
                <p><span style="font-weight: bolder;">轮询</span>：请求将根据“代理服务URL”的权重进行轮询，权重越大被轮询的概率越高。</p>
                <p><span style="font-weight: bolder;">哈希</span>：同一个IP发出的请求将被转发到同一个“代理服务URL”上。</p>
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.loadBalancingStrategy"
              placeholder="请选择负载均衡策略"
              clearable
            >
              <el-option label="轮询" value="POLLING" />
              <el-option label="哈希" value="HASH" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="24">
          <el-form-item label="常量参数">
            <el-table
              :data="config.proxyConstParams"
              :max-height="160"
              :header-cell-style="{
                backgroundColor: '#F5F7FA',
                color: '#666666',
              }"
              border
            >
              <el-table-column type="index" label="#" width="40px" align="center"></el-table-column>
              <el-table-column label="位置" width="100px" align="center" prop="position" :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'proxyConstParams[' + scope.$index + '].position'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-select v-model="scope.row.position" clearable>
                      <el-option label="QUERY" value="QUERY" />
                      <el-option label="HEADER" value="HEADER" />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="参数名" align="center" prop="key" show-overflow-tooltip :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'proxyConstParams[' + scope.$index + '].key'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-input v-model="scope.row.key" clearable></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="参数值" align="center" show-overflow-tooltip prop="value" :render-header="addReadStar">
                <template #default="scope">
                  <el-form-item
                    label-width="0"
                    :style="{ marginBottom: 0 }"
                    :prop="'proxyConstParams[' + scope.$index + '].value'"
                    :rules="[
                      { required: true, message: '必填项', trigger: 'blur' },
                    ]"
                  >
                    <el-input v-model="scope.row.value" clearable></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="描述" align="center" show-overflow-tooltip prop="remark">
                <template #default="scope">
                  <el-input v-model="scope.row.remark" clearable></el-input>
                </template>
              </el-table-column>
              <el-table-column align="center" width="50px">
                <template #header>
                  <el-button
                    title="添加"
                    @click="clickAddProxyConstParams()"
                    icon="Plus"
                    circle
                    type="primary"
                  ></el-button>
                </template>
                <template #default="scope">
                  <el-button
                    title="删除"
                    @click="clickRemoveProxyConstParams(scope.$index)"
                    icon="Delete"
                    circle
                    type="danger"
                  ></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 4">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="代理超时时间" prop="proxyTimeoutTime">
            <template #label>
              <span>代理超时时间</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="网关请求“代理服务”的超时时间，单位：毫秒"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input 
              v-model="config.proxyTimeoutTime"
              placeholder="请填写代理超时时间"
              maxlength="20"
              clearable>
              <template #append>ms</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="是否开启重试" prop="proxyRetry">
            <template #label>
              <span>是否开启重试</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="网关请求“代理服务”失败后是否开启重试策略"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.proxyRetry"
              placeholder="请选择负载均衡策略"
              clearable
            >
              <el-option label="开启" :value="true" />
              <el-option label="关闭" :value="false" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0" v-if="config.proxyRetry">
        <el-col :span="12">
          <el-form-item label="重试次数" prop="proxyRetryTimes">
            <el-input 
              v-model="config.proxyRetryTimes"
              placeholder="请填写重试次数"
              maxlength="2"
              clearable>
              <template #append>次</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="重试时间间隔" prop="proxyRetryInterval">
            <el-input 
              v-model="config.proxyRetryInterval"
              placeholder="请填写重试时间间隔"
              maxlength="20"
              clearable>
              <template #append>ms</template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
  </el-form>
</template>

<style lang="scss" scoped>
</style>