package com.example.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "TMCKCP")
public class JtgkTmckcp {
    @Id
    private String id;//主键id
    private BigDecimal amount;//起存金额
    private String code;//编号
    private Date createtime;//创建时间
    private String creator;//创建人
    private String creatorname;//创建人名称
    private String currency;//币种
    private Date enddate;//终止日期
    private BigDecimal fdsx;//浮动上限
    private BigDecimal fdxx;//浮动下限
    private String fkyj;//反馈意见
    private BigDecimal jtxnjxlv;//计提虚拟计息利率
    private String jzlv;//利率档次
    private Date lastmodifiedtime;//最后修改时间
    private String lastmodifier;//最后修改人
    private String lastmodifiername;//最后修改人名称
    private Integer lx;//类型
    private String note;//备注
    private Integer qxdw;//期限单位
    private String shr;//审核人
    private Date shrq;//审核日期
    private Date startdate;//开始日期
    private Integer state;//状态
    private String timestamps_createdby;
    private Date timestamps_createdon;
    private String timestamps_lastchangedby;
    private Date timestamps_lastchangedon;
    private String ver;//版本
    private BigDecimal version;//版本号
    private String whr;//维护人
    private Date whrq;//维护日期
    private BigDecimal zdlv;//最低利率
    private Integer zdqx;//最大期限
    private BigDecimal zglv;//最高利率
    private Integer zl;//种类
    private Integer zxqx;//最小期限
    private BigDecimal interestrate;//年利率
    private char interestratetype;//利率种类
    private String banktype;//银行行别
    private String fxjb;//风险级别
    private String treasureorg;//资金组织
    private String name_chs;
    private String name_cht;
    private String name_en;
    private String name_es;
    private String name_pt;
}
