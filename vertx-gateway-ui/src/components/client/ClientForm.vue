<script lang="ts" setup>
import { nextTick, onMounted, ref, watch } from "vue";
import SparkMd5 from "spark-md5";
import { ElMessageBox } from "element-plus";
import { clientTree } from "@/api/client";

const prop = {
  value: "id",
  label: "name",
  children: "children",
};

const props = defineProps({
  data: {
    type: Object,
    default: () => null,
  },
});

const rules = {
  clientName: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  clientCode: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  clientTtl: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  tokenSecret: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  tokenTtl: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
  online: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
};

let active = ref(0);
let form = ref();
let config = ref<any>({
  clientTtl: null,
  online: 0,
  tokenTtl: 60000 * 30,
  auths: []
});
let treeList = ref<any>([])
const changeStep = (currentActive: number) => {
  if (currentActive === active.value) {
    return;
  }
  active.value = currentActive;
};

// 生成编号
const genCode = () => {
  if(config.value.clientCode) {
    ElMessageBox.confirm("确认重新生成Client编号？", "提示",{
      type: 'warning',
    })
    .then(() => {
      config.value.clientCode = SparkMd5.hash(Date.now() + "").toUpperCase()
    })
  } else {
    config.value.clientCode = SparkMd5.hash(Date.now() + "").toUpperCase()
  }
};

// 生成秘钥
const genSecret = () => {
  if(config.value.tokenSecret) {
    ElMessageBox.confirm("确认重新生成Token秘钥？", "提示", {
      type: 'warning',
    })
    .then(() => {
      config.value.tokenSecret = SparkMd5.hash(Date.now() + "").toUpperCase()
    })
  } else {
    config.value.tokenSecret = SparkMd5.hash(Date.now() + "").toUpperCase()
  }
};

/**
 * 客户端授权
 */
let defaultSelected = ref<any[]>([])
let selectedIds = ref<any[]>([])

onMounted(() => {
  nextTick(() => {
    if (props.data) {
      Object.assign(config.value, props.data);
      defaultSelected.value = config.value.auths.map((item:any) => item.authId)
    }
    clientTree().then((res) => {
      treeList.value = res.data.data
      selectedIds.value = defaultSelected.value
    })
  });
});

// 复选框被选择时
const onNodeCheck = (data: any, checkedInfo: any) => {
  selectedIds.value = checkedInfo.checkedNodes.map((item: any) => item.id)
  config.value.auths = selectedIds.value.map(item => {
    return {
      clientId: config.value.id,
      authId: item,
      type: 1
    }
  })
};

// 树形数据过滤
let filterText = ref("")
let treeRef = ref()
watch(filterText, (val) => {
  treeRef.value!.filter()
})

// 过滤已选择的数据
let filterSelected = ref(false)
const handleFilterSelected = () => {
  filterSelected.value = !filterSelected.value
  filterText.value = ""
  treeRef.value!.filter()
};

const filterNode = (value: any, data: any) => {
  if(filterSelected.value && selectedIds.value) {
    return selectedIds.value.includes(data.id)
  }

  if (!filterText.value) return true
  return data.name.includes(filterText.value) || data.path?.includes(filterText.value)
};



defineExpose({
  form,
  config,
});
</script>

<template>
  <el-steps simple :active="active" style="margin-bottom: 20px">
    <el-step
      title="客户端配置"
      icon="Tickets"
      @click.native="changeStep(0)"
      style="cursor: pointer"
    ></el-step>
    <el-step
      title="路由授权"
      icon="Tickets"
      @click.native="changeStep(1)"
      style="cursor: pointer"
    ></el-step>
  </el-steps>
  <el-form
    ref="form"
    :rules="rules"
    :model="config"
    label-position="right"
    label-width="120px"
  >
    <div v-show="active === 0">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="Client名称" prop="clientName">
            <el-input
              v-model="config.clientName"
              placeholder="请填写Client名称"
              maxlength="100"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="授权厂商" prop="remark">
            <el-input
              v-model="config.remark"
              placeholder="请填写授权厂商"
              maxlength="100"
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="Client编号" prop="clientCode">
            <template #label>
              <span>Client编号</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="用户获取网关服务访问 token 的唯一凭证"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-input
              v-model="config.clientCode"
              placeholder="请填写Client编号"
              maxlength="100"
              readonly
              clearable
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-button 
            type="primary" 
            style="margin-left: 20px; height: 28px"
            @click="genCode()">
            生成Client编号
          </el-button>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="Client TTL" prop="clientTtl">
            <template #label>
              <span>Client TTL</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="客户端的有效截止日期，超过有效期后用户将无法获取访问 token，已经获取到的访问 token 也将失效。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-date-picker
              v-model="config.clientTtl"
              type="datetime"
              format="YYYY-MM-DD HH:mm:ss"
              value-format="YYYY-MM-DD HH:mm:ss"
              date-format="yyyy-MM-dd HH:mm:ss"
              placeholder="请选择Client有效截止日期"
              style="width: 100%;"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="Client状态" prop="online">
            <template #label>
              <span>Client状态</span>
              <el-popover
                placement="top-start"
                :width="320"
                trigger="hover"
                content="Client状态 为 无效 时，即时 Client TTL 在有效期内，客户端也不可用；
                Client TTL 不在有效期内，即时 Client状态 为 有效 时，客户端也不可用。"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.online"
              placeholder="请选择Client状态"
            >
              <el-option label="有效" :value="1" />
              <el-option label="无效" :value="0" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-divider content-position="right">认证配置</el-divider>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="Token秘钥" prop="tokenSecret">
            <el-input
              v-model="config.tokenSecret"
              placeholder="请填写Token秘钥"
              maxlength="100"
              type="password"
              show-password
              readonly
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-button 
            type="primary" 
            style="margin-left: 20px; height: 28px"
            @click="genSecret()">
            生成Token秘钥
          </el-button>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="12">
          <el-form-item label="Token TTL" prop="tokenTtl">
            <template #label>
              <span>Token TTL</span>
              <el-popover
                placement="top-start"
                :width="300"
                trigger="hover"
                content="用户单次获取到的访问 token 的有效期"
              >
                <template #reference>
                  <span style="margin-left: 2px;margin-top: 1px;"><el-icon><QuestionFilled /></el-icon></span>
                </template>
              </el-popover>
            </template>
            <el-select
              v-model="config.tokenTtl"
              placeholder="请选择Token TTL"
              clearable
            >
              <el-option label="05分钟" :value="60000 * 5" />
              <el-option label="30分钟" :value="60000 * 30" />
              <el-option label="01小时" :value="3600000 * 1" />
              <el-option label="06小时" :value="3600000 * 6" />
              <el-option label="12小时" :value="3600000 * 12" />
              <el-option label="24小时" :value="3600000 * 24" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </div>
    <div v-show="active === 1">
      <el-row :gutter="0">
        <el-col :span="12">
          <el-input
              v-model="filterText"
              placeholder="请填写关键字"
              prefix-icon="Search"
              maxlength="100"
              clearable
            >
          </el-input>
        </el-col>
        <el-col :span="12">
          <el-button 
            :type="filterSelected ? 'info' : 'success'"
            icon="Filter"
            @click="handleFilterSelected"
            style="margin-left: 20px;">
            {{ filterSelected ? '取消数据过滤' : '过滤已选择的数据' }}
          </el-button>
          <el-button 
            icon="Refresh"
            style="margin-left: 20px;">
            重置
          </el-button>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="24">
          <div style="height: 10px;"></div>
          <el-scrollbar height="350px">
            <el-tree
              show-checkbox
              node-key="id"
              ref="treeRef"
              check-strictly
              default-expand-all
              @check="onNodeCheck"
              :data="treeList"
              :filter-node-method="filterNode"
              :default-checked-keys="defaultSelected"
              :props="prop"
            >
              <template #default="{ node, data }">
                <el-icon v-if="data[prop.children] && data[prop.children].length > 0">
                  <component
                    :is="node.expanded ? 'FolderOpened' : 'Folder'"
                  ></component>
                </el-icon>
                <template v-if="data.online !== null && data.type === 2">
                  <span style="display: inline-block;line-height: 0px;">
                    <span class="tree-node-point tree-node-point_green" v-if="data.online === 1"></span>
                    <span class="tree-node-point tree-node-point_red" v-else></span>
                  </span>
                </template>
                <span style="font-size: 12px">&nbsp;{{ node.label }}</span>
                <span v-if="data.path" class="tree-node-suffix">{{data.path}}</span>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-col>
      </el-row>
    </div>
  </el-form>
</template>

<style lang="scss" scoped>
.tree-node-point {
  display: inline-block;
  width: 5px;
  height: 5px;
  border: 2px solid #000;
  border-radius: 50%;
  &_red {
    border-color: rgb(255, 82, 82);
  }
  &_green {
    border-color: rgb(0, 195, 0);
  }
}
.tree-node-suffix {
  display: inline-block;
  font-size: 12px;
  margin-left: 10px;
  color: #c1c1c1;
}
</style>
