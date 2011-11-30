package org.fireflow.example.util;

import java.util.HashMap;
import java.util.Map;

import org.hsqldb.Server;

public class HsqldbManager {
	// 数据库名称
	public final static String DATABASE_NAME = "fireflow";
	// 数据库路径
	public final static String DATABASE_PATH = "\\db\\";
	// 数据库端口缺省为9001
	public final static int PORT = 9001;
	// 数据库服务
	public final static Server server = new Server();

	public static void startupHsqldb() {

		Map<String, String> dbConfig = getDBConfig();
		server.setDatabaseName(0, dbConfig.get("dbname"));
		server.setDatabasePath(0, dbConfig.get("dbpath"));
		int port = PORT;
		try {
			port = Integer.parseInt(dbConfig.get("port"));
		} catch (Exception e) {
			port = PORT;
		}
		server.setPort(port);
		server.setSilent(true);
		// 开始数据库服务
		server.start();

		System.out.println("数据hsqldb库服务已启动!");
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
	}

	/*
	 * 获取数据库启动所需的配置信息
	 */
	private static Map<String, String> getDBConfig() {
		Map<String, String> dbConfig = new HashMap<String, String>();

		// 获得classpath路径
		String classpath = URLUtil.getClassPath(HsqldbManager.class);

		// 数据库路径
		String dbpath = "/db/";
		if (null == dbpath) {
			dbpath = DATABASE_PATH;
		}
		if (!dbpath.startsWith("\\") || !dbpath.startsWith("/")) {
			dbpath = "/" + dbpath;
		}
		if (!dbpath.endsWith("\\") || !dbpath.endsWith("/")) {
			dbpath = dbpath + "/";
		}
		// 数据库名

		String dbname = DATABASE_NAME;

		// 数据库名

		String strPort = PORT + "";

		// 构建当前数据库文件路径
		// path = path.substring(0, path.length()-7)+"lib\\db\\fireflow";
		dbpath = classpath + dbpath + dbname;

		dbConfig.put("dbpath", dbpath);
		dbConfig.put("dbname", dbname);
		dbConfig.put("port", strPort);

		return dbConfig;
	}

	/**
	 * context销毁时.关闭数据库
	 * 
	 * @param sce
	 *            ServletContextEvent
	 */
	public static void stopHsqldb() {
		// 结束数据库服务
		server.stop();

		System.out.println("数据库服务已关闭!");
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
	}

}
