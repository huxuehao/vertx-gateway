<script lang="ts" setup>
import { type FormInstance } from "element-plus";
import { reactive, ref, watch } from "vue";
import { useUserStore } from "@/stores/user";
import { useRoute } from 'vue-router';

const route = useRoute()
const userStore = useUserStore();

const redirect = ref<any>(undefined);

watch(route, (newRoute) => {
    redirect.value = newRoute.query && newRoute.query.redirect;
}, { immediate: true });

// 模式
let mode = ref("pwd");
// 密码登录对象
let passwordForm = reactive({
  account: "administrator",
  password: "Admin@HanJ123.com",
});
// 表单校验规则
const rules = ref({
  account: [
    {
      required: true,
      message: "用户名不能为空",
      trigger: "blur",
    },
  ],
  password: [
    {
      required: true,
      message: "密码不能为空",
      trigger: "blur",
    },
  ],
  phoneNum: [
    {
      required: true,
      message: "手机号不能为空",
      trigger: "blur",
    },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入有效的手机号",
      trigger: "blur",
    },
  ],
  code: [
    {
      required: true,
      message: "验证码不能为空",
      trigger: "blur",
    },
  ],
});

// 登录
let pwdRef = ref<FormInstance>();
const handelSubmit = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.validate((valid) => {
    if (valid) {
      userStore
        .doLogin({
          account: passwordForm.account.trim(),
          password: passwordForm.password,
        })
        .then(() => {
          window.open(decodeURIComponent(redirect.value || "/"), '_self')
        })
    }
  });
};
</script>

<template>
  <div class="login-box">
    <el-tabs v-model="mode" class="demo-tabs">
      <el-tab-pane label="密码登录" name="pwd">
        <el-form
          :model="passwordForm"
          :rules="rules"
          ref="pwdRef"
          label-width="auto"
        >
          <el-form-item label="" prop="account" label-width="0">
            <el-input
              v-model="passwordForm.account"
              placeholder="请输入用户名"
              maxlength="100"
              prefix-icon="User"
              clearable
              size="default"
            ></el-input>
          </el-form-item>
          <el-form-item label="" prop="password" label-width="0">
            <el-input
              v-model="passwordForm.password"
              type="password"
              maxlength="100"
              autocomplete="off"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              size="default"
            ></el-input>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-button
        type="primary"
        @click="handelSubmit(mode === 'pwd' ? pwdRef : smsRef)"
        class="login-btn"
        size="default"
      >
        登 录
      </el-button>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
.login-box {
  width: 100%;
  max-width: 400px;
}

.login-btn {
  width: 100%;
  height: 40px;
}
</style>
