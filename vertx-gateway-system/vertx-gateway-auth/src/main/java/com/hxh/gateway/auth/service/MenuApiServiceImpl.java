package com.hxh.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.auth.mapper.MenuApiMapper;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.dto.MenuApiDto;
import com.hxh.gateway.common.entity.MenuApi;
import com.hxh.gateway.common.util.tree.TreeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：菜单接口
 *
 * @author huxuehao
 **/
@Service
public class MenuApiServiceImpl extends ServiceImpl<MenuApiMapper, MenuApi> implements MenuApiService {
    @Override
    public List<MenuApiDto> tree() {
        List<MenuApiDto> list = baseMapper.listV2(DBConst.MENU, DBConst.MENU_API);
        return TreeUtil.convertTree(list);
    }
}
