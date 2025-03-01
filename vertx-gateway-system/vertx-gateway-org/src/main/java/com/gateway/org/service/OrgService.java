package com.gateway.org.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gateway.common.dto.OrganizationDto;
import com.gateway.common.entity.Organization;

import java.util.List;

/**
 * 描述：组织机构
 *
 * @author huxuehao
 **/
public interface OrgService extends IService<Organization> {

    List<OrganizationDto> tree(Organization org);

    boolean deleteAllById(List<Long> ids);
}
