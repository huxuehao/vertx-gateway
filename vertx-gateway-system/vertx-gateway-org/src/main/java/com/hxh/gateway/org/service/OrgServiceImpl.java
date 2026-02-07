package com.hxh.gateway.org.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxh.gateway.common.constant.DBConst;
import com.hxh.gateway.common.dto.OrganizationDto;
import com.hxh.gateway.common.entity.Organization;
import com.hxh.gateway.common.mp.support.MP;
import com.hxh.gateway.common.util.Func;
import com.hxh.gateway.common.util.tree.TreeUtil;
import com.hxh.gateway.org.mapper.OrgMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：组织机构
 *
 * @author huxuehao
 **/
@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Organization> implements OrgService {
    @Override
    public List<OrganizationDto> tree(Organization org) {
        List<OrganizationDto> list = baseMapper.listV2(DBConst.ORGANIZATION, MP.getQueryWrapper(org));
        return TreeUtil.convertTree(list);
    }

    @Override
    @Transactional
    public boolean deleteAllById(List<Long> ids) {
        QueryWrapper<Organization> qw = new QueryWrapper<>();
        qw.in("parent_id", ids);
        qw.eq("del_flag", 0);
        List<Organization> orgList = list(qw);

        if (Func.isEmpty(orgList)) {
            return removeBatchByIds(ids);
        }
        // 有子级的ID集合
        List<Long> hasChildIds = orgList.stream().map(Organization::getParentId).collect(Collectors.toList());
        // 没有子级的ID集合
        List<Long> noneChildIds = ids.stream().filter(id -> !hasChildIds.contains(id)).collect(Collectors.toList());
        removeBatchByIds(noneChildIds);

        return deleteAllById(hasChildIds);
    }

}
