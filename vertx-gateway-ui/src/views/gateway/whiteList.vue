<script lang="ts" setup>
import { nextTick, onMounted, reactive, ref } from "vue";
// 拖动组件
import ResizeBox from "@/components/resize/ResizeBox.vue";
// 左侧IP树
import SearchTree from "@/components/tree/SearchTree.vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { applicationList } from "@/api/application";
import {
  whiteListAdd,
  whiteListUpdate,
  whiteListDelete,
  whiteListList
} from "@/api/whitelist";

const prop = {
  value: "id",
  label: "name",
  children: "children",
};

let currentAppId = ref(null);
let appList = ref<any>([]);
// 初始化
onMounted(async () => {
  await applicationList({}).then((res: any) => {
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
// 当前所选行
let selectedRows = ref<any>([]);

//  点击IP树
const handleNodeClick = (node: any) => {
  currentAppId.value = node.id;
  load();
};
// 加载列表数据
const load = () => {
  loading.value = true;
  whiteListList({...queryParams, parentId: currentAppId.value })
    .then((res) => {
      dataList.value = res.data.data;
      dataList.value.forEach((bItem: any) => {
        const aItem = appList.value.find(
          (aItem: any) => aItem.id === bItem.parentId
        );
        if (aItem) {
          bItem.appName = aItem.name;
        } else {
          bItem.appName = "--";
        }
      })
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
  currentAppId.value = null;
  queryParams.share = 1;
  load();
};

let stopBtn = ref(false);
// 点击新增
const handleAdd = () => {
  if (!dataList.value) {
    dataList.value = [];
  }
  dataList.value.push({
    parentId: currentAppId.value,
    ip: null,
    remark: null,
    editable: true
  });
};
// 点击编辑
const handleEdit = (row: any) => {
  row.editable = true
};
// 点击取消
const handleCancel = (row: any) => {
  row.editable = false
};
// 点击保存
const handleSave = (row: any) => {
  stopBtn.value = true
  if(row.id) {
    whiteListUpdate(row).then(() => {
      row.editable = false
      ElMessage({
        message: "保存成功",
        type: "success",
        plain: true,
      });
      load();
    }).finally(() => {
      stopBtn.value = false
    })
  } else {
    whiteListAdd(row).then(() => {
      row.editable = false
      ElMessage({
        message: "保存成功",
        type: "success",
        plain: true,
      });
      load();
    }).finally(() => {
      stopBtn.value = false
    })
  }
  
  
};
// 点击移除
const remove = (index: number) => {
  dataList.value.splice(index, 1);
};
// 点击删除
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
    whiteListDelete(rows.map((item: any) => item.id))
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
</script>

<template>
  <ResizeBox :initial-size="200" :min-size="200" :max-size="300">
    <template #first>
      <div class="resize-left-box">
        <SearchTree
          :data="appList"
          title="应用列表"
          @node-click="handleNodeClick"
          height="calc(100vh - 170px)"
        ></SearchTree>
      </div>
    </template>
    <template #second>
      <div class="resize-right-box">
        <!-- 查询区域-->
        <div class="search-box" ref="searchBox">
          <el-form
            ref="queryForm"
            class="query-form"
            :model="queryParams"
            :inline="true"
            label-width="70px"
          >
            <el-form-item label="IP Host" prop="ip" class="form-item">
              <el-input
                v-model="queryParams.ip"
                placeholder="IP Host"
                class="input-search"
                clearable
              ></el-input>
            </el-form-item>
            <el-form-item label="IP备注" prop="remark" class="form-item">
              <el-input
                v-model="queryParams.remark"
                placeholder="IP备注"
                class="input-search"
                clearable
              ></el-input>
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
              v-permission="'gateway_white_add'"
              type="primary"
              icon="CirclePlus"
              @click="handleAdd()"
              >新增</el-button
            >
            <el-button
              v-permission="'gateway_white_delete'"
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
              label="所属应用"
              prop="appName"
              show-overflow-tooltip
            >
              <template #default="scope">
                <el-tree-select
                  v-if="scope.row.editable"
                  v-model="scope.row.parentId"
                  :data="appList"
                  :render-after-expand="true"
                  :default-expand-all="false"
                  :props="prop"
                  class="input-search"
                  placeholder="请选择所属应用"
                  filterable
                  check-strictly
                  clearable
                ></el-tree-select>
                <span v-else>{{ scope.row.appName }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="IP Host"
              prop="name"
              show-overflow-tooltip
            >
              <template #default="scope">
                <el-input
                  v-if="scope.row.editable"
                  placeholder="请填写IP Host"
                  v-model="scope.row.ip"
                  maxlength="15"
                  clearable
                >
                </el-input>
                <span v-else>{{ scope.row.ip }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="IP备注"
              prop="remark"
              show-overflow-tooltip
              >
              <template #default="scope">
                <el-input
                  v-if="scope.row.editable"
                  placeholder="请填写IP备注"
                  v-model="scope.row.remark"
                  maxlength="40"
                  clearable
                >
                </el-input>
                <span v-else>{{ scope.row.remark }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="150"
              align="center"
              fixed="right"
            >
              <template #default="scope">
                <el-button
                  v-if="scope.row.editable"
                  v-permission="'gateway_white_edit'"
                  link
                  type="primary"
                  icon="CircleCheck"
                  :loading="stopBtn"
                  @click="handleSave(scope.row)"
                  >保存</el-button
                >
                <el-button
                  v-else
                  v-permission="'gateway_white_edit'"
                  link
                  type=""
                  icon="Edit"
                  @click="handleEdit(scope.row)"
                  >编辑</el-button
                >
                <el-button
                  v-if="scope.row.editable"
                  v-permission="'gateway_white_edit'"
                  link
                  type=""
                  icon="CircleClose"
                  style="color: #f63434"
                  @click="handleCancel(scope.row)"
                >
                  取消
                </el-button>
                <el-button
                  v-else-if="scope.row.id"
                  v-permission="'gateway_white_delete'"
                  link
                  type=""
                  icon="Delete"
                  style="color: #f63434"
                  @click="handleDelete([scope.row])"
                >
                  删除
                </el-button>
                <el-button
                  v-else
                  v-permission="'gateway_white_delete'"
                  link
                  type=""
                  icon="Remove"
                  style="color: #f63434"
                  @click="remove(scope.$index)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </template>
  </ResizeBox>
</template>

<style lang="scss" scoped>
@use "/src/style/views/index.scss" as *;
</style>
