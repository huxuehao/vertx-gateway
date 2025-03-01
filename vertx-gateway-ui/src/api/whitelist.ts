import request from "@/utils/request";

export const whiteListAdd = (data: any) => {
    return request({
      url: "/gateway/white/list/add",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };

  export const whiteListUpdate = (data: any) => {
    return request({
      url: "/gateway/white/list/update",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };


  export const whiteListDelete = (ids: string[]) => {
    return request({
      url: "/gateway/white/list/delete",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: ids,
    });
  };

export const whiteListList = (searchConfig: any) => {
  return request({
    url: "/gateway/white/list/list",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};