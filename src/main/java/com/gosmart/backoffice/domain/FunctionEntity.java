package com.gosmart.backoffice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`function`")
public class FunctionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long functionId;

    @Column(name = "function_code")
    private String functionCode;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "function_name")
    private String functionName;

    @Column(name = "path")
    private String path;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "remark")
    private String remark;

    @Column(name = "status")
    private String status;

    public Long getFunctionId() {
        return functionId;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
