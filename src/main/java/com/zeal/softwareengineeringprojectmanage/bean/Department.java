package com.zeal.softwareengineeringprojectmanage.bean;

import java.io.Serializable;

public class Department implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column department.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column department.departmentName
     *
     * @mbggenerated
     */
    private String departmentname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table department
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column department.id
     *
     * @return the value of department.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column department.id
     *
     * @param id the value for department.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column department.departmentName
     *
     * @return the value of department.departmentName
     *
     * @mbggenerated
     */
    public String getDepartmentname() {
        return departmentname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column department.departmentName
     *
     * @param departmentname the value for department.departmentName
     *
     * @mbggenerated
     */
    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname == null ? null : departmentname.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table department
     *
     * @mbggenerated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", departmentname=").append(departmentname);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}