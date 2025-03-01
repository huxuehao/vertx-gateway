import request from "@/utils/request";

export const apiAdd = (data: any) => {
  return request({
    url: "/gateway/api/add",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};

export const apiUpdate = (data: any) => {
  return request({
    url: "/gateway/api/update",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};

export const apiDelete = (ids: string[]) => {
  return request({
    url: "/gateway/api/delete",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids,
  });
};

export const apiList = (searchConfig: any) => {
  return request({
    url: "/gateway/api/list",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};

export const apiClassifyTree = () => {
  return request({
    url: "/gateway/api/classify-tree",
    method: "get",
  });
};

export const apiPage = (searchConfig: any) => {
  return request({
    url: "/gateway/api/page",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};

export const apiSelectOne = (id: string) => {
  return request({
    url: "/gateway/api/selectOne",
    method: "get",
    params: {
      id,
    },
  });
};

export const apiOnline = (ids: string[]) => {
  return request({
    url: "/gateway/api/online",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids
  });
};

export const apiOffline = (ids: string[]) => {
  return request({
    url: "/gateway/api/offline",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids
  });
};

export const apiGetConfig = (id: string) => {
  return request({
    url: "/gateway/api/get-config",
    method: "get",
    params: {
      id,
    },
  });
};

export const apiSaveConfig = (data: any) => {
  return request({
    url: "/gateway/api/save-config",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};