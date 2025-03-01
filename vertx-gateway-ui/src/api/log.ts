import request from "@/utils/request";

export const logDelete = (ids: string[]) => {
  return request({
    url: "/gateway/log/delete",
    method: "post",
    headers: {
      unrepeat: true,
    },
    data: ids,
  });
};

export const logPage = (searchConfig: any) => {
  return request({
    url: "/gateway/log/page",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};

export const logSelectOne = (id: string) => {
  return request({
    url: "/gateway/log/selectOne",
    method: "get",
    params: {
      id,
    },
  });
};