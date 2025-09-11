<script lang="ts" setup>
import { nextTick, onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  clientAdd,
  clientUpdate,
  clientDelete,
  clientPage,
  clientSelectOne,
} from "@/api/client";
import ClientForm from "@/components/client/ClientForm.vue";

// 初始化
onMounted(() => {
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
  clientPage({ ...page, ...queryParams })
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
let mode = ref("add");
let addVisible = ref(false);
let formData = ref<any>({});
let stopBtn = ref(false);
const dialogProp = reactive({
  visible: false,
  title: "",
  top: "10vh",
  width: "70%",
  modal: true,
  appendToBody: true,
  showClose: true,
  closeOnClickModal: false,
});
// 点击新增
const handleAdd = () => {
  mode.value = "add";
  addVisible.value = false;
  formData.value = {};
  stopBtn.value = false;
  dialogProp.title = "新增";
  dialogProp.visible = true;
};

let clientForm = ref();
const handleAddDo = () => {
  clientForm.value.form.validate((valid: any) => {
    if (valid) {
      stopBtn.value = true;
      if (mode.value === "add") {
        clientAdd(clientForm.value.config)
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
        clientUpdate(clientForm.value.config)
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
    clientDelete(rows.map((item: any) => item.id))
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
  clientSelectOne(row.id).then((res) => {
    formData.value = res.data.data;
    dialogProp.visible = true;
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
        label-width="90px"
      >
        <el-form-item label="Client名称" prop="clientName" class="form-item">
          <el-input
            v-model="queryParams.clientName"
            placeholder="Client名称"
            class="input-search"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="授权厂商" prop="remark" class="form-item">
          <el-input
            v-model="queryParams.remark"
            placeholder="授权厂商"
            class="input-search"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="Client编号" prop="clientCode" class="form-item">
          <el-input
            v-model="queryParams.clientCode"
            placeholder="Client编号"
            class="input-search"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="Client状态" prop="online" class="form-item">
          <el-select
            v-model="queryParams.online"
            placeholder="Client状态"
            class="input-search"
            clearable
          >
            <el-option label="可用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item class="form-item">
          <el-button type="primary" icon="Search" @click="handleSearch">
            查询
          </el-button>
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
      :style="[{ height: 'calc(100% - var(--table-hg-150) - ' + tHeight + 'px)' }]"
    >
      <el-table
        :stripe="true"
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
          width="50"
          align="center"
        ></el-table-column>
        <el-table-column
          label="#"
          type="index"
          width="50"
          align="center"
        ></el-table-column>
        <el-table-column
          label="Client名称"
          prop="clientName"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          label="授权厂商"
          prop="remark"
          align="center"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          label="Client编号"
          prop="clientCode"
          align="center"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          label="Client状态"
          prop="online"
          align="center"
          width="120px"
        >
          <template #default="scope">
            <el-tag
              disable-transitions
              type="success"
              v-if="scope.row.online === 1"
              >可用</el-tag
            >
            <el-tag
              disable-transitions
              type="warning"
              v-else-if="scope.row.online === 0"
              >禁用</el-tag
            >
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="180"
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
    <ClientForm v-if="dialogProp.visible" :data="formData" ref="clientForm"></ClientForm>
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
</template>

<style lang="scss" scoped>
@use "/src/style/views/index.scss" as *;
</style>
