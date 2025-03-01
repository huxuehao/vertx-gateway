import request from "@/utils/request";

export const classifyAdd = (data: any) => {
    return request({
      url: "/gateway/classify/add",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };

  export const classifyUpdate = (data: any) => {
    return request({
      url: "/gateway/classify/update",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };


  export const classifyDelete = (ids: string[]) => {
    return request({
      url: "/gateway/classify/delete",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: ids,
    });
  };

export const classifyList = (searchConfig: any) => {
  return request({
    url: "/gateway/classify/list",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};


export const classifyPage = (searchConfig: any) => {
  return request({
    url: "/gateway/classify/page",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};


export const classifySelectOne = (id: string) => {
    return request({
      url: "/gateway/classify/selectOne",
      method: "get",
      params: {
        id
      },
    });
  };