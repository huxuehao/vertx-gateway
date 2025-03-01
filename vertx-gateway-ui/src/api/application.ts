import request from "@/utils/request";

export const applicationAdd = (data: any) => {
  return request({
    url: "/gateway/application/add",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};

export const applicationUpdate = (data: any) => {
  return request({
    url: "/gateway/application/update",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};

export const applicationDelete = (ids: string[]) => {
  return request({
    url: "/gateway/application/delete",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids,
  });
};

export const applicationList = (searchConfig: any) => {
  return request({
    url: "/gateway/application/list",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};

export const applicationPage = (searchConfig: any) => {
  return request({
    url: "/gateway/application/page",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};

export const applicationSelectOne = (id: string) => {
  return request({
    url: "/gateway/application/selectOne",
    method: "get",
    params: {
      id,
    },
  });
};

export const applicationOnline = (ids: string[]) => {
  return request({
    url: "/gateway/application/online",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids
  });
};

export const applicationOffline = (ids: string[]) => {
  return request({
    url: "/gateway/application/offline",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids
  });
};

export const applicationGetConfig = (id: string) => {
  return request({
    url: "/gateway/application/get-config",
    method: "get",
    params: {
      id,
    },
  });
};

export const applicationSaveConfig = (data: any) => {
  return request({
    url: "/gateway/application/save-config",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: data,
  });
};