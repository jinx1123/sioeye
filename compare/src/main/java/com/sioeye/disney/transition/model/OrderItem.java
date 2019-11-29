package com.sioeye.disney.transition.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * orderitem
 * 
 * @author
 */
@Setter
@Getter
public class OrderItem {

	private String objectid;

	private String orderid;

	private Integer goodstype;

	private String goodsid;

	private String goodsname;

	private String amusementparkid;

	private String parkname;

	private String gameid;

	private String gamename;

	private String seatid;

	private String seatsequenceno;

	private String seatmark;

	private Integer status;

	private String thumbnailurl;

	private Date createtime;

	private Date updatetime;

	private Integer filingstatus;

	private Integer uploadflag;

	private String previewUrl;

	private String userName;

	private Integer count;

	private String currency;

	private BigDecimal originalAmount;

	private BigDecimal promotionAmount;

	private BigDecimal actualAmount;

	private Integer promotionType;

	private BigDecimal goodsPrice;

	private String activityId;

	private String downloadUrl;

	private String shareId;

	private String shareName;

	private Boolean shareUploadFlag;

	private String parkShareId;

	private String shareUrl;

	private String userId;

	private String deviceRecordId;

	public OrderItem() {

	}

	public OrderItem(ResultSet rs, int type) throws SQLException {
		this.objectid = rs.getString(1);
		this.goodsid = rs.getString(2);
		this.orderid = rs.getString(3);
		this.goodstype = type;
		this.userId = rs.getString(4) != null ? rs.getString(4) : null;
		this.amusementparkid = rs.getString(5);
		this.parkname = rs.getString(6);
		this.gameid = rs.getString(7);
		this.gamename = rs.getString(8);
		this.seatid = rs.getString(9);
		this.seatsequenceno = rs.getString(10);
		this.seatmark = rs.getString(11);
		this.status = rs.getInt(12) == 2 || rs.getInt(12) == 3 ? 2 : rs.getInt(12);
		this.previewUrl = this.status == 2 ? rs.getString(18) != null ? rs.getString(18) : null
				: rs.getString(17) != null ? rs.getString(17) : null;
		this.createtime = rs.getTimestamp(15);
		this.updatetime = rs.getTimestamp(16) != null ? rs.getTimestamp(16) : null;
		this.originalAmount = rs.getBigDecimal(19);
		this.actualAmount = rs.getBigDecimal(20);
		this.promotionAmount = this.originalAmount != null && this.actualAmount != null
				? this.originalAmount.subtract(this.actualAmount) : null;
		this.promotionType = rs.getInt(21);
		this.goodsPrice = rs.getBigDecimal(19);
		this.activityId = rs.getString(22);
		this.downloadUrl = this.status == 2 ? rs.getString(14) != null ? rs.getString(14) : null
				: rs.getString(13) != null ? rs.getString(13) : null;
	}

	public static String changeOrderItem2Sql(OrderItem orderItem) {
		StringBuffer result = new StringBuffer("'");
		result.append(orderItem.getObjectid());
		result.append("','");
		result.append(orderItem.getOrderid());
		result.append("',");
		result.append(orderItem.getGoodstype());
		result.append(",");
		result.append(orderItem.getGoodsid() != null ? "'" + orderItem.getGoodsid() + "'" : null);
		result.append(",'");
		result.append(orderItem.getGoodsname());
		result.append("',");
		result.append(orderItem.getAmusementparkid() != null ? "'" + orderItem.getAmusementparkid() + "'" : null);
		result.append(",'");
		result.append(orderItem.getParkname());
		result.append("',");
		result.append(orderItem.getGameid() != null ? "'" + orderItem.getGameid() + "'" : null);
		result.append(",'");
		result.append(orderItem.getGamename());
		result.append("',");
		result.append(orderItem.getSeatid() != null ? "'" + orderItem.getSeatid() + "'" : null);
		result.append(",'");
		result.append(orderItem.getSeatsequenceno());
		result.append("','");
		result.append(orderItem.getSeatmark());
		result.append("',");
		result.append(orderItem.getStatus());
		result.append(",");
		result.append(orderItem.getThumbnailurl() != null ? "'" + orderItem.getThumbnailurl() + "'" : null);
		result.append(",'");
		result.append(orderItem.getCreatetime());
		result.append("',");
		result.append(orderItem.getUpdatetime() != null ? "'" + orderItem.getUpdatetime() + "'" : null);
		result.append(",");
		result.append(orderItem.getFilingstatus());
		result.append(",");
		result.append(orderItem.getUploadflag());
		result.append(",");
		result.append(orderItem.getPreviewUrl() != null ? "'" + orderItem.getPreviewUrl() + "'" : null);
		result.append(",");
		result.append(orderItem.getUserName() != null ? "'" + orderItem.getUserName() + "'" : null);
		result.append(",");
		result.append(orderItem.getCount());
		result.append(",'");
		result.append(orderItem.getCurrency());
		result.append("',");
		result.append(orderItem.getOriginalAmount());
		result.append(",");
		result.append(orderItem.getPromotionAmount());
		result.append(",");
		result.append(orderItem.getActualAmount());
		result.append(",");
		result.append(orderItem.getPromotionType());
		result.append(",");
		result.append(orderItem.getGoodsPrice());
		result.append(",'");
		result.append(orderItem.getActivityId());
		result.append("',");
		result.append(orderItem.getDownloadUrl() != null ? "'" + orderItem.getDownloadUrl() + "'" : null);
		result.append(",");
		result.append(orderItem.getShareId() != null ? "'" + orderItem.getShareId() + "'" : null);
		result.append(",");
		result.append(orderItem.getShareName() != null ? "'" + orderItem.getShareName() + "'" : null);
		result.append(",");
		result.append(orderItem.getShareUploadFlag());
		result.append(",");
		result.append(orderItem.getParkShareId() != null ? "'" + orderItem.getParkShareId() + "'" : null);
		result.append(",");
		result.append(orderItem.getShareUrl() != null ? "'" + orderItem.getShareUrl() + "'" : null);
		result.append(",");
		result.append(orderItem.getUserId() != null ? "'" + orderItem.getUserId() + "'" : null);
		return result.toString();
	}

	public static String changeOrderItemExtend2Sql(OrderItem orderItem) {
		StringBuffer result = new StringBuffer("'");
		JSONObject extendContext = new JSONObject();
		result.append(orderItem.getObjectid());
		result.append("','");
		result.append(orderItem.getOrderid());
		result.append("','");
		extendContext.put("previewUrl", orderItem.getPreviewUrl());
		extendContext.put("downloadUrl", orderItem.getDownloadUrl());
		extendContext.put("thumbnailUrl", orderItem.getThumbnailurl());
		result.append(extendContext.toString());
		result.append("'");
		return result.toString();
	}
}