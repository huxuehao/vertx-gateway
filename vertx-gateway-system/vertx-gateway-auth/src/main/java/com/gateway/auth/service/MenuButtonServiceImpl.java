package com.gateway.auth.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gateway.auth.mapper.MenuButtonMapper;
import com.gateway.common.constant.DBConst;
import com.gateway.common.dto.MenuButtonDto;
import com.gateway.common.entity.MenuButton;
import com.gateway.common.util.tree.TreeUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：菜单按钮
 *
 * @author huxuehao
 **/
@Service
public class MenuButtonServiceImpl extends ServiceImpl<MenuButtonMapper, MenuButton> implements MenuButtonService {
    @Override
    public List<MenuButtonDto> tree() {
        List<MenuButtonDto> list = baseMapper.listV2(DBConst.MENU, DBConst.MENU_BUTTON);
        return TreeUtil.convertTree(list);
    }
}
