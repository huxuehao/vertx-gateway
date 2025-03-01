import request from "@/utils/request";

export const clientAdd = (data: any) => {
    return request({
      url: "/gateway/client/add",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };

  export const clientUpdate = (data: any) => {
    return request({
      url: "/gateway/client/update",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: data,
    });
  };


  export const clientDelete = (ids: string[]) => {
    return request({
      url: "/gateway/client/delete",
      method: "post",
      headers: {
        unrepeat: true
      },
      data: ids,
    });
  };

export const clientTree = () => {
  return request({
    url: "/gateway/client/tree",
    method: "get",
  });
};


export const clientPage = (searchConfig: any) => {
  return request({
    url: "/gateway/client/page",
    method: "get",
    params: {
      ...searchConfig,
    },
  });
};


export const clientSelectOne = (id: string) => {
    return request({
      url: "/gateway/client/selectOne",
      method: "get",
      params: {
        id
      },
    });
  };