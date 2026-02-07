package com.hxh.gateway.menu.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.dto.MenuDto;
import com.hxh.gateway.common.entity.Menu;
import com.hxh.gateway.common.mp.support.MP;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.common.util.tree.TreeUtil;
import com.hxh.gateway.menu.mapper.MenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：菜单
 *
 * @author huxuehao
 **/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<MenuDto> tree(Menu menu) {
        List<MenuDto> list = baseMapper.listV2(DBConst.MENU, MP.getQueryWrapper(menu));
        return TreeUtil.convertTree(list);
    }

    @Override
    @Transactional
    public boolean deleteAllById(List<Long> ids) {
        QueryWrapper<Menu> qw = new QueryWrapper<>();
        qw.in("parent_id", ids);
        qw.eq("del_flag", 0);
        List<Menu> menuList = list(qw);

        if (Func.isEmpty(menuList)) {
            return removeBatchByIds(ids);
        }

        // 有子级的ID集合
        List<Long> hasChildIds = menuList.stream().map(Menu::getParentId).collect(Collectors.toList());
        // 没有子级的ID集合
        List<Long> noneChildIds = ids.stream().filter(id -> !hasChildIds.contains(id)).collect(Collectors.toList());
        removeBatchByIds(noneChildIds);

        return deleteAllById(hasChildIds);
    }

}
