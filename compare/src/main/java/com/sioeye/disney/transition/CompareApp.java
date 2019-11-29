package com.sioeye.disney.transition;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sioeye.disney.transition.model.OrderItem;

/**
 * Hello world!
 *
 */
public class CompareApp {

	private static final Logger logger = LoggerFactory.getLogger(CompareApp.class);

	private static Connection conn = null;
	private static PreparedStatement ps = null;
	private static ResultSet rs = null;
	private static final String driver = "org.postgresql.Driver";
	static String dataUrl = "";
	static String userName = "";
	static String passWord = "";

	private static void preparedStatement(String dataUrl, String userName, String passWord) {
		try {
			Class.forName(driver);// 加载数据驱动程序
			System.out.println("开始尝试连接数据库.....");
			conn = DriverManager.getConnection(dataUrl, userName, passWord);// 获取连接
			System.out.println("连接成功......");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean validateTransition(OrderItem orderItem) throws SQLException {
		ResultSet rs = conn.prepareStatement(
				"SELECT oi.objectid,oi.goodsid,oi.orderid,oi.goodstype,oi.userid,oi.amusementparkid,oi.parkname,oi.gameid,oi.gamename,oi.seatid,oi.seatsequenceno,oi.seatmark,oi.status,oi.previewurl,oi.createtime,oi.updatetime,oi.originalamount,oi.actualamount,oi.promotionamount,oi.promotiontype,oi.goodsprice,oi.activityid,oi.downloadurl,o.devicerecordid FROM orderitem oi left join \"order\" o on oi.orderid=o.objectid where oi.objectid='"
						+ orderItem.getObjectid() + "'")
				.executeQuery();
		if (rs.next() && rs.getString(1).equals(orderItem.getObjectid())
				&& rs.getString(2).equals(orderItem.getGoodsid()) && rs.getString(3).equals(orderItem.getOrderid())
				&& rs.getInt(4) == orderItem.getGoodstype()
				&& ((rs.getString(5) == null && orderItem.getUserId() == null)
						|| rs.getString(5).equals(orderItem.getUserId()))
				&& rs.getString(6).equals(orderItem.getAmusementparkid())
				&& ((rs.getString(7) == null && orderItem.getParkname() == null)
						|| rs.getString(7).equals(orderItem.getParkname()))
				&& ((rs.getString(8) == null && orderItem.getGameid() == null)
						|| rs.getString(8).equals(orderItem.getGameid()))
				&& ((rs.getString(9) == null && orderItem.getGamename() == null)
						|| rs.getString(9).equals(orderItem.getGamename()))
				&& ((rs.getString(10) == null && orderItem.getSeatid() == null)
						|| rs.getString(10).equals(orderItem.getSeatid()))
				&& ((rs.getString(11) == null && orderItem.getSeatsequenceno() == null)
						|| rs.getString(11).equals(orderItem.getSeatsequenceno()))
				&& ((rs.getString(12) == null && orderItem.getSeatmark() == null)
						|| rs.getString(12).equals(orderItem.getSeatmark()))
				&& rs.getInt(13) == orderItem.getStatus()
				&& ((rs.getString(14) == null && orderItem.getPreviewUrl() == null)
						|| rs.getString(14).equals(orderItem.getPreviewUrl()))
				&& rs.getTimestamp(15).compareTo(orderItem.getCreatetime()) == 0
				&& ((rs.getTimestamp(16) == null && orderItem.getUpdatetime() == null)
						|| rs.getTimestamp(16).compareTo(orderItem.getUpdatetime()) == 0)
				&& ((rs.getBigDecimal(17) == null && orderItem.getOriginalAmount() == null)
						|| rs.getBigDecimal(17).compareTo(orderItem.getOriginalAmount()) == 0)
				&& ((rs.getBigDecimal(18) == null && orderItem.getActualAmount() == null)
						|| rs.getBigDecimal(18).compareTo(orderItem.getActualAmount()) == 0)
				&& ((rs.getBigDecimal(19) == null && orderItem.getPromotionAmount() == null)
						|| rs.getBigDecimal(19).compareTo(orderItem.getPromotionAmount()) == 0)
				&& rs.getInt(20) == orderItem.getPromotionType()
				&& ((rs.getBigDecimal(21) == null && orderItem.getGoodsPrice() == null)
						|| rs.getBigDecimal(21).compareTo(orderItem.getGoodsPrice()) == 0)
				&& ((rs.getString(22) == null && orderItem.getActivityId() == null)
						|| rs.getString(22).equals(orderItem.getActivityId()))
				&& ((rs.getString(23) == null && orderItem.getDownloadUrl() == null)
						|| rs.getString(23).equals(orderItem.getDownloadUrl()))) {
			return true;
		}

		return false;
	}

	private static void dealDate(int type, String querySql) throws Exception {
		// 查询出数据
		ps = conn.prepareStatement(querySql);
		rs = ps.executeQuery();
		OrderItem orderItem = null;
		while (rs.next()) {
			// 比对数据
			logger.info("开始比对orderitem 的 id:" + rs.getString(1));
			orderItem = new OrderItem(rs, type);
			if (!validateTransition(orderItem)) {
				logger.info("data is error!!! , objectId:" + orderItem.getObjectid());
				// throw new Exception("比对数据不成功");
				continue;
			}
			logger.info("比对数据成功 orderitem 的 id:" + orderItem.getObjectid());
		}
		ps.close();
		rs.close();
	}

	private static void inputClipId(StringBuffer clipBoughtId) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("enter compare clipbought id：");
		// 判断是否还有输入
		if (scan.hasNextLine()) {
			clipBoughtId.append(scan.nextLine());
		}
		scan.close();
		if (clipBoughtId == null || "".equals(clipBoughtId)) {
			throw new Exception("param is error");
		}
	}

	private static void loadConf(String path) throws Exception {
		Properties props = new Properties();
		InputStream in = new FileInputStream(path);
		props.load(in);
		dataUrl = props.getProperty("dataUrl");
		userName = props.getProperty("userName");
		passWord = props.getProperty("passWord");
	}

	public static void main(String[] args) throws Exception {
		// 加载配置文件
		loadConf(args[0]);
		// 获取输入值
		StringBuffer clipBoughtId = new StringBuffer();
		inputClipId(clipBoughtId);
		// 总数
		long sum = 0;
		// 初始化sql
		StringBuffer queryClipSql = new StringBuffer(
				"SELECT cb.objectid,cb.clipid,cb.orderid,cb.usersid,cb.amusementparkid,cb.parkname,cb.gameid,cb.gamename,cb.seatid,cb.seatsequenceno,cb.seatmark,cb.status,cb.cliporiginalurl,cb.clipfinalurl,cb.createtime,o.updatetime,cb.clippreviewurl,cb.clippreviewfinalurl,o.originalamount,o.actualamount,o.ordertype,o.activityid FROM clipbought cb LEFT JOIN \"order\" o ON cb.orderid=o.objectid where cb.status <> 1 ");
		StringBuffer queryClipCountSql = new StringBuffer(
				"SELECT COUNT (cb.objectid) FROM clipbought cb LEFT JOIN \"order\" o ON cb.orderid=o.objectid where cb.status <> 1 ");
		// 创建连接
		preparedStatement(dataUrl.toString(), userName.toString(), passWord.toString());
		if ("sioeye".equals(clipBoughtId.toString())) {
			// 查询总数
			ResultSet rsSum = conn.prepareStatement(queryClipCountSql.toString()).executeQuery();
			sum = rsSum.next() ? rsSum.getInt(1) : 0;
		} else {
			queryClipSql.append(" and cb.objectid in (" + clipBoughtId + ")");
			queryClipCountSql.append(" and cb.objectid in (" + clipBoughtId + ")");
			ResultSet rsSum = conn.prepareStatement(queryClipCountSql.toString()).executeQuery();
			sum = rsSum.next() ? rsSum.getInt(1) : 0;
		}
		queryClipSql.append(" ORDER BY cb.objectid DESC ");
		// 循环处理，每次比较数量20个
		long count = sum / 20;
		long surplus = sum % 20;
		try {
			if (sum > 0) {
				String offsetLimit = null;
				for (int i = 0; i < count; i++) {
					logger.info("clipbought第" + (i + 1) + "次执行比对,每次循环20条数据");
					offsetLimit = " offset " + i * 20 + " limit " + 20;
					dealDate(0, queryClipSql.toString() + offsetLimit);
				}
				logger.info("clipbought第" + (count + 1) + "次执行比对,本次循环" + surplus + "条数据");
				queryClipSql.append(" offset " + count * 20 + " limit " + surplus);
				dealDate(0, queryClipSql.toString());
				logger.info("clipbought数据比对成功，一共比对数据" + sum + "条.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ps.close();
			rs.close();
			conn.close();
		}
	}
}
