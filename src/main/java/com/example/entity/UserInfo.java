package com.example.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    /**
     * 系统标识 必需
     */
    private String syscode;
    /**
     * 状态 0禁用 1启用 可空,不传入查询全部
     */
    private String state;
    /**
     * 所属组织编号 可空,非空查询全部
     */
    private String orgcode;
    /**
     * 用户账号 可空,非空时全词匹配
     */
    private String usercode;

    /**
     * 最后修改时间格式yyyyMMddHHmmss 必需
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastchangedon1;

    /**
     * 早于该时间点的用户信息 可空,不传入截止到当前时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date lastchangedon2;

}