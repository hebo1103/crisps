//package net.crisps.cloud.framework.web.domain;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//import net.dgg.framework.tac.elasticsearch.annotation.DggEsDocument;
//import net.dgg.framework.tac.elasticsearch.annotation.DggEsIdentify;
//import java.io.Serializable;
//import java.util.Date;
//
///**
// * <p>
// * 公共字典表
// * </p>
// *
// * @author chenyingjie
// * @since 2020-07-15
// */
//@Data
//@EqualsAndHashCode
//@Accessors(chain = true)
//@DggEsDocument(indexName = "dgg_portal_cms_tree_book", type = "cms_tree_book")
//public class TreeBook implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    /**
//     * 主键ID：统一由ID生成器生成
//     */
//    @DggEsIdentify
//    private String id;
//
//    /**
//     * 父节点ID
//     */
//    private String pid;
//
//    /**
//     * 父节点/分类编码
//     */
//    private String pcode;
//
//    /**
//     * 编码
//     */
//    private String code;
//
//    /**
//     * 名称
//     */
//    private String name;
//
//    /**
//     * 备注
//     */
//    private String description;
//
//    /**
//     * 层级
//     */
//    private String levels;
//
//    /**
//     * 排序：0-999
//     */
//    private Integer sort;
//
//    /**
//     * 状态:0禁用,1启用
//     */
//    private Integer status;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//
//    /**
//     * 创建人id
//     */
//    private Long createrId;
//
//    /**
//     * 创建人部门id
//     */
//    private Long createrOrgId;
//
//    /**
//     * 姓名+工号
//     */
//    private String createrName;
//
//    /**
//     * 最后修改时间
//     */
//    private Date updateTime;
//
//    /**
//     * 最后修改人id
//     */
//    private Long updaterId;
//
//    /**
//     * 最后修改人部门id
//     */
//    private Long updaterOrgId;
//
//    /**
//     * 姓名+工号
//     */
//    private String updaterName;
//
//    /**
//     * 外部系统读取标志
//     */
//    private String readFlag;
//
//    /**
//     * 扩展字段1
//     */
//    private String ext1;
//
//    /**
//     * 扩展字段2
//     */
//    private String ext2;
//
//    /**
//     * 扩展字段3
//     */
//    private String ext3;
//
//    /**
//     * 扩展字段4
//     */
//    private String ext4;
//
//    /**
//     * 扩展字段5
//     */
//    private String ext5;
//
//}
