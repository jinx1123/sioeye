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
public class TransitionApp {

	private static final Logger logger = LoggerFactory.getLogger(TransitionApp.class);

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

	private static String createOrderItemInsertSql(OrderItem orderItem) {
		StringBuffer result = new StringBuffer();
		result.append("(");
		result.append(OrderItem.changeOrderItem2Sql(orderItem));
		result.append(")");
		return result.toString();
	}

	private static String createOrderItemExtendInsertSql(OrderItem orderItem) {
		StringBuffer result = new StringBuffer();
		result.append("(");
		result.append(OrderItem.changeOrderItemExtend2Sql(orderItem));
		result.append(")");
		return result.toString();
	}

	private static boolean validateDuplicateTransition(String objectId) throws SQLException {
		ResultSet rs = conn.prepareStatement("select count(objectid) from orderitem where objectid='" + objectId + "'")
				.executeQuery();
		boolean result = rs.next() ? rs.getLong(1) > 0 ? true : false : false;
		return result;
	}

	private static void dealDate(int type, String querySql, StringBuffer insertOrderItemSql,
			StringBuffer insertOrderItemExtendSql) throws SQLException {
		// 查询出数据
		ps = conn.prepareStatement(querySql);
		rs = ps.executeQuery();
		OrderItem orderItem = null;
		String insertOrderItemSqlStr = null;
		String insertOrderItemExtendSqlStr = null;
		while (rs.next()) {
			// 清洗转换数据
			logger.info("开始处理:" + rs.getString(1));
			orderItem = new OrderItem(rs, type);
			if (orderItem.getStatus() == 1) {
				// 状态为1，记录数据不做处理，手工去执行数据迁移
				logger.info(orderItem.getObjectid() + "状态为1,不做处理，直接记录手工重新更新");
				continue;
			}
			if (validateDuplicateTransition(orderItem.getObjectid())) {
				logger.info(orderItem.getObjectid() + "已处理,跳过本次流程.");
				continue;
			}
			insertOrderItemSqlStr = createOrderItemInsertSql(orderItem);
			insertOrderItemExtendSqlStr = createOrderItemExtendInsertSql(orderItem);
			// 重新插入orderItem
			ps = conn.prepareStatement(insertOrderItemSql.toString() + insertOrderItemSqlStr);
			ps.execute();
			// 重新插入orderItemExtend
			ps = conn.prepareStatement(insertOrderItemExtendSql.toString() + insertOrderItemExtendSqlStr);
			ps.execute();
			logger.info("处理完成:" + orderItem.getObjectid());
		}
		ps.close();
		rs.close();
	}

	private static void inputClipId(StringBuffer clipBoughtId) throws Exception {
		Scanner scan = new Scanner(System.in);
		System.out.println("enter clipbought id：");
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
				"SELECT cb.objectid,cb.clipid,cb.orderid,cb.usersid,cb.amusementparkid,cb.parkname,cb.gameid,cb.gamename,cb.seatid,cb.seatsequenceno,cb.seatmark,cb.status,cb.cliporiginalurl,cb.clipfinalurl,cb.createtime,o.updatetime,cb.clippreviewurl,cb.clippreviewfinalurl,o.originalamount,o.actualamount,o.ordertype,o.activityid,o.shareid,o.sharename,	o.shareuploadflag,	o.parkshareid,	o.weishishareurl,cb.during,cb.\"size\",cb.width,cb.height,	cb.thumbnailurl,cb.thumbnailfinalurl FROM clipbought cb LEFT JOIN \"order\" o ON cb.orderid=o.objectid ");
		StringBuffer queryClipCountSql = new StringBuffer(
				"select count(cb.objectid) from clipbought cb LEFT JOIN \"order\" o ON cb.orderid=o.objectid ");
		StringBuffer insertBoughtOrderItemSql = new StringBuffer(
				"insert into orderitem (objectid,orderid,goodstype,goodsid,goodsname,amusementparkid,parkname,gameid,gamename,seatid,seatsequenceno,seatmark,status,thumbnailurl,createtime,updatetime,filingstatus,uploadflag,previewurl,username,\"count\",currency,originalamount,promotionamount,actualamount,promotiontype,goodsprice,activityid,downloadurl,shareid,sharename,shareuploadflag,parkshareid,shareurl,userid) values ");
		StringBuffer insertBoughtOrderItemExtendSql = new StringBuffer(
				"insert into orderitemextend (objectid,orderid,extendcontext,itemextendcontext) values ");
		// 创建连接
		preparedStatement(dataUrl.toString(), userName.toString(), passWord.toString());
		if ("sioeye".equals(clipBoughtId.toString())) {
			// 查询总数
			ResultSet rsSum = conn.prepareStatement(queryClipCountSql.toString()).executeQuery();
			sum = rsSum.next() ? rsSum.getInt(1) : 0;
		} else {
			queryClipSql.append(" where cb.objectid in (" + clipBoughtId.toString() + ")");
			queryClipCountSql.append(" where cb.objectid in (" + clipBoughtId.toString() + ")");
			ResultSet rsSum = conn.prepareStatement(queryClipCountSql.toString()).executeQuery();
			sum = rsSum.next() ? rsSum.getInt(1) : 0;
		}

		queryClipSql.append(" ORDER BY cb.objectid desc ");
		// 循环处理，每次移动数量20个
		long count = sum / 20;
		long surplus = sum % 20;
		try {
			if (sum > 0) {
				String offsetLimit = null;
				for (int i = 0; i < count; i++) {
					logger.info("clipbought第" + (i + 1) + "次执行循环转换,每次循环20条数据");
					offsetLimit = " offset " + i * 20 + " limit " + 20;
					dealDate(0, queryClipSql.toString() + offsetLimit, insertBoughtOrderItemSql,
							insertBoughtOrderItemExtendSql);
				}
				logger.info("clipbought第" + (count + 1) + "次执行循环转换,本次循环" + surplus + "条数据");
				queryClipSql.append(" offset " + count * 20 + " limit " + surplus);
				dealDate(0, queryClipSql.toString(), insertBoughtOrderItemSql, insertBoughtOrderItemExtendSql);
				logger.info("clipbought数据转换成功，一共转换数据" + sum + "条.");
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
