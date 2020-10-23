//package net.crisps.cloud.framework.orm.mybatis.domain;
//
//
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableLogic;
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Data;
//import java.util.Date;
//
///**
// * @author Administrator
// */
//@Data
//public class BaseDomain {
//    @TableId
//    private Long id;
//
//    @TableField("creator_id")
//    private Long creatorId;
//
//    @TableField("create_time")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    private Date createTime;
//
//    @TableField("modifier_id")
//    private Long modifierId;
//
//    @TableField("modify_time")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
//    private Date modifyTime;
//
//    @TableField("deleted")
//    @TableLogic
//    private Boolean deleted;
//
//}
//
