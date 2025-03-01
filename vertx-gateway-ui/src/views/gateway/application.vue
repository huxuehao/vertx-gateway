<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { applicationList } from "@/api/application";
import {
  applicationAdd,
  applicationUpdate,
  applicationDelete,
  applicationPage,
  applicationSelectOne,
  applicationGetConfig,
  applicationSaveConfig,
  applicationOnline,
  applicationOffline
} from "@/api/application";
import AppConfigForm from "@/components/application/AppConfigForm.vue";

let appList = ref<any>([]);
// 初始化
onMounted(() => {
  applicationList({}).then((res: any) => {
    appList.value = res.data.data;
  });
  load();
  monitorSearchResize();
});

let tHeight = ref(0);
let searchBox = ref();
const monitorSearchResize = () => {
  const resizeObserver = new ResizeObserver((entries) => {
    for (const entry of entries) {
      const { height } = entry.contentRect;
      tHeight.value = height;
    }
  });
  resizeObserver.observe(searchBox.value);
};

let loading = ref(false);
// 列表数据
let dataList = ref<any>([]);
// 查询参数
let queryParams = reactive<any>({});
// 分页配置
let page = reactive({
  total: 0,
  current: 1,
  size: 20,
});
// 当前所选行
let selectedRows = ref<any>([]);

// 加载列表数据
const load = () => {
  loading.value = true;
  applicationPage({ ...page, ...queryParams })
    .then((res) => {
      dataList.value = res.data.data["records"];
      page.total = res.data.data["total"];
    })
    .finally(() => {
      loading.value = false;
    });
};
// 点击搜索
const handleSearch = () => {
  load();
};
// 点击重置
const handleReset = () => {
  for (let key in queryParams) {
    if (queryParams.hasOwnProperty(key)) {
      queryParams[key] = null;
    }
  }
  queryParams.share = 1;
  load();
};
const changePageNo = (pageNo: number) => {
  page.current = pageNo;
  load();
};
const changePageSize = (pageSize: number) => {
  page.size = pageSize;
  load();
};

/**
 * 新增编辑删除
 */
const rules = {
  name: [
    {
      required: true,
      message: "必填项",
      trigger: "blur",
    },
  ],
};
let mode = ref("add");
let addVisible = ref(false);
let formData = ref<any>({});
let stopBtn = ref(false);
const dialogProp = reactive({
  visible: false,
  title: "",
  top: "20vh",
  width: "45%",
  modal: true,
  appendToBody: true,
  showClose: true,
  closeOnClickModal: false,
});
// 点击新增
const handleAdd = () => {
  mode.value = "add";
  addVisible.value = false;
  formData.value = {
    valid: 1,
  };
  stopBtn.value = false;
  dialogProp.title = "新增";
  dialogProp.visible = true;
};

let form = ref();
const handleAddDo = () => {
  form.value.validate((valid: any) => {
    if (valid) {
      stopBtn.value = true;
      if (mode.value === "add") {
        applicationAdd(formData.value)
          .then(() => {
            dialogProp.visible = false;
            ElMessage({
              message: "保存成功",
              type: "success",
              plain: true,
            });
            load();
          })
          .finally(() => {
            stopBtn.value = false;
          });
      } else if (mode.value === "edit") {
        applicationUpdate(formData.value)
          .then((res) => {
            dialogProp.visible = false;
            load();
            if (res.data.code === 200) {
              ElMessage({
                message: "保存成功",
                type: "success",
                plain: true,
              });
            }
          })
          .finally(() => {
            stopBtn.value = false;
          });
      } else {
        loading.value = false;
        ElMessage({
          message: "模式不匹配",
          type: "error",
          plain: true,
        });
      }
    }
  });
};

const handleDelete = (rows: any) => {
  if (!rows || rows.length === 0) {
    ElMessage({
      message: "至少选择一条数据",
      type: "warning",
      plain: true,
    });
    return;
  }

  ElMessageBox.confirm("此操作将删除所选择的数据 , 是否确定?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    applicationDelete(rows.map((item: any) => item.id))
      .then((res) => {
        if (res.data.code === 200) {
          ElMessage({
            message: "删除成功",
            type: "success",
            plain: true,
          });
        }
        load();
      })
      .catch(() => {
        loading.value = false;
      })
      .finally(() => {
        selectedRows.value = [];
      });
  });
};

const handleEdit = (row: any) => {
  mode.value = "edit";
  stopBtn.value = false;
  dialogProp.title = "编辑";
  applicationSelectOne(row.id).then((res) => {
    formData.value = res.data.data;
    dialogProp.visible = true;
  });
};

const handelOnline = (rows: any) => {
  if(rows.length === 0) {
    ElMessage({
      message: "至少选择一条记录",
      type: "warning",
      plain: true,
    });
    return
  }
  ElMessageBox.confirm("此操作将部署所选择的应用 , 是否确定?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    applicationOnline(rows.map((item: any) => item.id)).then(() => {
      ElMessage({
        message: "操作成功",
        type: "success",
        plain: true,
      });
    }).finally(() => {
      load()
    })
  })
};

const handelOffline = (rows: any) => {
  if(rows.length === 0) {
    ElMessage({
      message: "至少选择一条记录",
      type: "warning",
      plain: true,
    });
    return
  }
  ElMessageBox.confirm("此操作将卸载所选择的应用 , 是否确定?", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    loading.value = true;
    applicationOffline(rows.map((item: any) => item.id)).then(() => {
      ElMessage({
        message: "操作成功",
        type: "success",
        plain: true,
      });
    }).finally(() => {
      load()
    })
  })
};


/**
 * 应用配置
 */
const configDialogProp = reactive<any>({
  appId: "",
  visible: false,
  title: "应用配置",
  top: "10vh",
  width: "70%",
  modal: true,
  appendToBody: true,
  showClose: true,
  closeOnClickModal: false,
})

let config = ref<any>(null)
const openConfigFrom = (row: any) => {
  configDialogProp.appId = row.id
  applicationGetConfig(row.id).then((res) => {
    if(res.data.data.config) {
      config.value = JSON.parse(res.data.data.config)
    } else {
      config.value = null
    }
    configDialogProp.visible = true
  }).catch(() => {
    configDialogProp.appId = ""
  })
};

let configForm = ref()
const handleConfigSave = () => {
  configForm.value.form.validate((valid: any) => {
    if (valid) {
      stopBtn.value = true;
      applicationSaveConfig({
        id: configDialogProp.appId,
        config: JSON.stringify(configForm.value.config)
      })
        .then(() => {
          configDialogProp.visible = false;
          ElMessage({
            message: "保存成功",
            type: "success",
            plain: true,
          });
          load()
        })
        .finally(() => {
          stopBtn.value = false;
        });
    } else {
      ElMessage({
        message: "请完善表单后再保存",
        type: "warning",
        plain: true,
      });
    }
  });
};

</script>

<template>
  <div style="height: 100%;">
    <!-- 查询区域-->
    <div class="search-box" ref="searchBox">
      <el-form
        ref="queryForm"
        class="query-form"
        :model="queryParams"
        :inline="true"
        label-width="70px"
      >
        <el-form-item label="应用名称" prop="name" class="form-item">
          <el-input
            v-model="queryParams.name"
            placeholder="应用名称"
            class="input-search"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="应用端口" prop="port" class="form-item">
          <el-input
            v-model="queryParams.port"
            placeholder="应用端口"
            class="input-search"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="协议类型" prop="protocol" class="form-item">
          <el-select
            v-model="queryParams.protocol"
            placeholder="协议类型"
            class="input-search"
            clearable
          >
            <el-option label="HTTP" value="HTTP" />
            <el-option label="HTTPS" value="HTTPS" />
          </el-select>
        </el-form-item>
        <el-form-item label="应用状态" prop="online" class="form-item">
          <el-select
            v-model="queryParams.online"
            placeholder="应用状态"
            class="input-search"
            clearable
          >
            <el-option label="部署" :value="1" />
            <el-option label="卸载" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="form-item">
          <el-button type="primary" icon="Search" @click="handleSearch"
            >查询</el-button
          >
        </el-form-item>
        <el-form-item class="form-item">
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮区域-->
    <el-row class="button-box">
      <el-col :span="16">
        <el-button
          v-permission="'gateway_app_add'"
          type="primary"
          icon="CirclePlus"
          @click="handleAdd()"
          >新增</el-button
        >
        <el-button
          v-permission="'gateway_app_delete'"
          type="danger"
          icon="Delete"
          @click="handleDelete(selectedRows)"
        >
          删除
        </el-button>
        <el-button
          v-permission="'gateway_app_online'"
          type="success"
          icon="Position"
          @click="handelOnline(selectedRows)"
        >
          部署
        </el-button>
        <el-button
          v-permission="'gateway_app_offline'"
          type="danger"
          icon="Remove"
          @click="handelOffline(selectedRows)"
        >
          卸载
        </el-button>
      </el-col>
      <el-col :span="8" style="text-align: right">
        <el-button icon="RefreshRight" @click="handleSearch" title="刷新"
          >刷新
        </el-button>
      </el-col>
    </el-row>
    <!-- 列表区域 -->
    <div
      class="data-list"
      :style="[{ height: 'calc(100vh - 180px - ' + tHeight + 'px)' }]"
    >
      <el-table
        :stripe="false"
        :data="dataList"
        :header-cell-style="{
          backgroundColor: '#F5F7FA',
          color: '#666666',
        }"
        height="100%"
        v-loading="loading"
        @selection-change="(val: any) => { selectedRows = val }"
        row-key="id"
        border
      >
        <el-table-column
          type="selection"
          width="40"
          align="center"
        ></el-table-column>
        <el-table-column
          label="#"
          type="index"
          width="40"
          align="center"
        ></el-table-column>
        <el-table-column
          label="应用名称"
          prop="name"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          label="应用端口"
          prop="port"
          align="center"
          show-overflow-tooltip
        >
          <template #default="scope">
            <span>{{ scope.row.port || '--'}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="协议类型"
          prop="protocol"
          align="center"
          show-overflow-tooltip
        >
          <template #default="scope">
            <span v-if="scope.row.protocol">{{ scope.row.protocol }}</span>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column
          label="应用状态"
          prop="online"
          align="center"
        >
          <template #default="scope">
            <el-tag
              disable-transitions
              type="success"
              v-if="scope.row.online === 1"
              >部署</el-tag
            >
            <el-tag
              disable-transitions
              type="danger"
              v-else-if="scope.row.online === 0"
              >卸载</el-tag
            >
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="260"
          align="center"
          fixed="right"
        >
          <template #default="scope">
            <el-button
              v-permission="'gateway_app_edit'"
              link
              type=""
              icon="Edit"
              @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-button
              v-permission="'gateway_app_delete'"
              link
              type=""
              icon="Delete"
              style="color: #f63434"
              @click="handleDelete([scope.row])"
            >
              删除
            </el-button>
            <el-button
              v-permission="'gateway_app_config'"
              link
              type="primary"
              icon="Setting"
              @click="openConfigFrom(scope.row)"
            >
              配置
            </el-button>
            <el-button
              v-permission="'gateway_app_online'"
              v-if="scope.row.online === 0"
              link
              type="success"
              icon="Position"
              @click="handelOnline([scope.row])"
            >
              部署
            </el-button>
            <el-button
              v-permission="'gateway_app_offline'"
              v-else
              link
              type=""
              icon="Remove"
              style="color: #f63434"
              @click="handelOffline([scope.row])"
            >
              卸载
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 分页区域 -->
    <div class="page-box">
      <el-pagination
        class="page"
        background
        layout="total,sizes,prev,pager,next,jumper"
        :total="page.total"
        :current-page.sync="page.current"
        :page-sizes="[20, 30, 40, 50, 60]"
        :page-size="page.size"
        @current-change="changePageNo"
        @size-change="changePageSize"
      />
    </div>
  </div>
  <el-dialog v-bind="dialogProp" v-model="dialogProp.visible">
    <el-form
      v-if="dialogProp.visible"
      ref="form"
      :model="formData"
      :rules="rules"
      label-width="80px"
    >
      <el-row>
        <el-col :span="24">
          <el-form-item label="应用名称" prop="name">
            <el-input
              placeholder="请填写应用名称"
              v-model="formData.name"
              maxlength="40"
              clearable
            >
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="应用描述" prop="remark">
            <el-input
              placeholder="请填写应用描述"
              v-model="formData.remark"
              type="textarea"
              maxlength="100"
              show-word-limit
              clearable
            >
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button
        v-if="mode !== 'view'"
        type="primary"
        @click="handleAddDo"
        icon="CircleCheck"
        :loading="stopBtn"
        >保 存</el-button
      >
      <el-button @click="dialogProp.visible = false" icon="CircleClose"
        >关 闭</el-button
      >
    </template>
  </el-dialog>
  <el-dialog v-bind="configDialogProp" v-model="configDialogProp.visible">
    <AppConfigForm ref="configForm" :data="config" v-if="configDialogProp.visible"></AppConfigForm>
    <template #footer>
      <el-button
        type="primary"
        icon="CircleCheck"
        :loading="stopBtn"
        @click="handleConfigSave()"
        >保 存</el-button
      >
      <el-button @click="configDialogProp.visible = false" icon="CircleClose"
        >关 闭</el-button
      >
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@use "/src/style/views/index.scss" as *;
.search-box,
.button-box,
.data-list {
  margin-left: 10px;
}
</style>
