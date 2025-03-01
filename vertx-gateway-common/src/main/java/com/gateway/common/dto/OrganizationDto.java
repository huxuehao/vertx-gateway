package com.gateway.common.dto;

import com.gateway.common.entity.Organization;
import com.gateway.common.util.tree.TreeNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * 描述：
 *
 * @author huxuehao
 **/
@Getter
@Setter
public class OrganizationDto extends Organization implements TreeNode<Long> {
    private List<OrganizationDto> children;

    @Override
    public void setChildren(List<? extends TreeNode<?>> children) {
        this.children = (List<OrganizationDto>)children;
    }

    @Override
    public Object clone() {
        OrganizationDto organization;
        try {
            organization = (OrganizationDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationDto that = (OrganizationDto) o;
        return Objects.equals(id, that.id);
    }
}
