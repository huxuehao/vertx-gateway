<script lang="ts" setup>
import { onMounted, reactive, ref } from "vue";
// 拖动组件
import ResizeBox from "@/components/resize/ResizeBox.vue";
// 左侧服务树
import SearchTree from "@/components/tree/SearchTree.vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { clientTree } from "@/api/client";
import {
  logDelete,
  logPage,
  logSelectOne,
} from "@/api/log";
import ApiLogForm from "@/components/log/ApiLogForm.vue";

let currentApiId = ref(null);
let logList = ref<any>([]);
// 初始化
onMounted(async () => {
  await clientTree().then((res: any) => {
    logList.value = res.data.data;
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

//  点击服务树
const handleNodeClick = (node: any) => {
  currentApiId.value = node.id;
  load();
};
// 加载列表数据
const load = () => {
  loading.value = true;
  logPage({ ...page, ...queryParams, apiId: currentApiId.value })
    .then((res) => {
      dataList.value = res.data.data["records"];
      page.total = res.data.data["total"];
      dataList.value.forEach((bItem: any) => {
        const aItem = logList.value.find(
          (aItem: any) => aItem.id === bItem.appId
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
  currentApiId.value = null;
  queryParams.share = 1;
  load();
};

let formData = ref<any>({});
const dialogProp = reactive({
  visible: false,
  title: "日志详情",
  top: "10vh",
  width: "60%",
  modal: true,
  appendToBody: true,
  showClose: true,
  closeOnClickModal: false,
});

const handleView = (row: any) => {
  dialogProp.title = "日志详情";
  logSelectOne(row.id).then((res) => {
    formData.value = res.data.data;
    dialogProp.visible = true;
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
    logDelete(rows.map((item: any) => item.id))
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

const changePageNo = (pageNo: number) => {
  page.current = pageNo;
  load();
};
const changePageSize = (pageSize: number) => {
  page.size = pageSize;
  load();
};
</script>

<template>
  <ResizeBox :initialWidth="200" :minWidth="200" :maxWidth="350">
    <template #left>
      <div class="resize-left-box">
        <SearchTree
          :data="logList"
          title="应用服务接口树"
          @node-click="handleNodeClick"
          height="calc(100vh - 170px)"
        ></SearchTree>
      </div>
    </template>
    <template #right>
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
            <el-form-item label="接口路径" prop="reqPath" class="form-item">
              <el-input
                v-model="queryParams.reqPath"
                placeholder="接口路径"
                class="input-search"
                clearable
              ></el-input>
            </el-form-item>
            <el-form-item label="执行结果" prop="status" class="form-item">
              <el-select
                v-model="queryParams.status"
                placeholder="执行结果"
                class="input-search"
                clearable
              >
                <el-option label="成功" value="success"></el-option>
                <el-option label="失败" value="fail"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="执行时间" prop="createTime" class="form-item">
              <el-date-picker
                v-model="queryParams.createTime"
                type="datetimerange"
                style="width: 320px;"
                start-placeholder="起始时间"
                end-placeholder="结束时间"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
                date-format="yyyy-MM-dd HH:mm:ss"
              />
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
              v-permission="'gateway_classify_delete'"
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
              label="所属应用"
              prop="appName"
              show-overflow-tooltip
            ></el-table-column>
            <el-table-column
              label="接口路径"
              prop="reqPath"
              show-overflow-tooltip
            ></el-table-column>
            <el-table-column
              label="执行时间"
              prop="createTime"
              width="180px"
              align="center"
            ></el-table-column>
            <el-table-column
              label="执行结果"
              prop="status"
              width="100px"
              align="center"
            >
              <template #default="scope">
                <el-tag
                  v-if="scope.row.status === 1"
                  disable-transitions
                  type="success"
                  >成功</el-tag
                >
                <el-tag
                  v-else-if="scope.row.status === 0"
                  disable-transitions
                  type="danger"
                  >失败</el-tag
                >
                <span v-else>--</span>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              width="140"
              align="center"
              fixed="right"
            >
              <template #default="scope">
                <el-button
                  v-permission="'gateway_classify_view'"
                  link
                  type=""
                  icon="View"
                  @click="handleView(scope.row)"
                  >详情</el-button
                >
                <el-button
                  v-permission="'gateway_classify_delete'"
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
    </template>
  </ResizeBox>
  <el-dialog v-bind="dialogProp" v-model="dialogProp.visible">
    <ApiLogForm v-if="dialogProp.visible" :data="formData"></ApiLogForm>
    <template #footer>
      <el-button @click="dialogProp.visible = false" icon="CircleClose"
        >关 闭</el-button
      >
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@use "/src/style/views/index.scss" as *;
</style>
