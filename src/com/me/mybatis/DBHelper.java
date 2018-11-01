package com.me.mybatis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.me.bean.DataSource;





public class DBHelper {
	private DataSource dataSource;
	public  DBHelper(DataSource dataSource) {
		this.dataSource=dataSource;
		try {
			Class.forName(dataSource.getDriver());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 加载驱动
	 */
//	static{
//		try {
//			Class.forName(proties.getInstance().getProperty("driverClassName"));
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
	/**
	 * 获取连接
	 * @return
	 */
	public Connection getConnection() {
		Connection con = null;
		try {
			con = DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
		    //Context context = new InitialContext();
		    //DataSource ds = (DataSource) context.lookup("java:comp/env/mysql");
		  //  con = ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	/**
	 * 给预编译执行语句块的占位符赋值
	 * @param pstmt
	 * @param params
	 */
	public void setParams(PreparedStatement pstmt,Object... params ){
		if (params !=null  && params.length>0) {
			for (int i = 0,len = params.length; i < len; i++) {
				try {
					pstmt.setObject(i+1, params[i]);
				} catch (SQLException e) {
					System.out.println("第"+(i+1)+"个参数住值失败...");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 给预编译执行语句块的占位符赋值
	 * @param pstmt
	 * @param params
	 */
	public void setParams(PreparedStatement pstmt,List<Object> params){
		if (params !=null  && params.size()>0) {
			for (int i = 0,len = params.size(); i < len; i++) {
				try {
					pstmt.setObject(i+1, params.get(i));
				} catch (SQLException e) {
					System.out.println("第"+(i+1)+"个参数住值失败...");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 更新操作
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql,List<Object> params) {
		Connection con = null;
		PreparedStatement psmt = null;

		try {
			con = this.getConnection();
			psmt = con.prepareStatement(sql);
			this.setParams(psmt, params);
			return psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,psmt,null);
		}
		return 0;
	}

	/**
	 * 关闭方法
	 * @param con
	 * @param pstmt
	 * @param rs
	 */
	public void close(Connection con,PreparedStatement pstmt,ResultSet rs) {
		if (rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 更新数据
	 * @param sql
	 * @param params
	 * @return
	 */
	public int update(String sql,Object... params) {
		Connection con = null;
		PreparedStatement psmt = null;

		try {
			con = this.getConnection();
			psmt = con.prepareStatement(sql);
			this.setParams(psmt, params);
			//	System.out.println(psmt.equals(obj));
			return psmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,psmt,null);
		}
		return 0;
	}
	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> finds(String sql,Object... params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句

			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 

			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];
			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			Map<String, Object> map=null; //每一行的数据存到一个LISt中
			while (rs.next()) {
				map=new HashMap<String, Object>();
				for(String colName:colNames) { //取每一列的值
					map.put(colName,rs.getObject(colName));
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return list;
	}

	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,String>> findStr(String sql,String... params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();

		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句

			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 

			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];

			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			Map<String, String> map=null; //每一行的数据存到一个LISt中
			while (rs.next()) {
				map=new HashMap<String, String>();
				for(String colName:colNames) { //取每一列的值
					map.put(colName,rs.getString(colName));
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return list;
	}
	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String,Object> findSingle(String sql,Object... params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		HashMap<String, Object> map=new HashMap<String, Object>();
		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句

			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 
			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];
			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			if(rs.next()) {
				for(String colName:colNames) { //取每一列的值
					map.put(colName,rs.getObject(colName));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return map;
	}

	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String,String> findStrSingle(String sql,String... params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		Map<String, String> map=null; 
		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句

			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 

			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];

			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			if (rs.next()) {
				map=new HashMap<String, String>();
				for(String colName:colNames) { //取每一列的值
					map.put(colName,rs.getString(colName));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return map;
	}


	/**
	 * 查询
	 * @param sql
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> finds(String sql,List<Object> params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句

			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 

			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];

			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			Map<String, Object> map=null; //每一行的数据存到一个LISt中
			while (rs.next()) {
				map=new HashMap<String, Object>();
				for(String colName:colNames) { //取每一列的值
					map.put(colName,rs.getObject(colName));
					//System.out.println(rs.getObject(colName));
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return list;
	}
	/**
	 * 获取总记录数的方法 
	 * @param sql
	 * @param params
	 * @return
	 */
	public int  getTotal(String sql,Object...params) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con= this.getConnection();
			pstmt = con.prepareStatement(sql);
			this.setParams(pstmt, params);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con, pstmt, rs);
		}
		return 0;
	}

	public int getTotal(String sql,List<Object> params){
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con= this.getConnection();
			pstmt = con.prepareStatement(sql);
			this.setParams(pstmt, params);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con, pstmt, rs);
		}
		return 0;
	}
	
	/**
	 * 基于对象的查询
	 * @param sql
	 * @param c1
	 * @param params
	 * @return
	 */
	public <T> List<T> findObject(String sql,Class<T> c1,Object ...params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		List<T> list = new ArrayList<T>();
		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句
			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 
			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];
			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			//获取给定类中的所有方法->setter
			Method[] methods = c1.getDeclaredMethods();
			List<Method> setters = new ArrayList<Method>();
			for (Method md:methods) {
				if (md.getName().startsWith("set")) {
					setters.add(md);
				}
			}
			
			T t=null;
			String mname= null;
			Class<?>[] types = null;
			String typeName = null;
			
			while (rs.next()) {
				t = c1.newInstance(); //每循环一次创建一个对象
				
				//将对应列的值注入到这个对象对应的属性中
				for (String colName:colNames) { //循环所有列名
					mname = "set"+colName;   //deptno-> setDeptno
				for (Method md:setters) { //循环所有方法 SET....
					if (mname.equalsIgnoreCase(md.getName())) { //说明这个列的值应该调用这个方法注入
						//获得第一个setter方法的形参类型
						types = md.getParameterTypes();
						
						//判断这个方法有没有形参
						if (types!=null && types.length>0) { //取出第一个形参类型的类型名
							typeName = types[0].getSimpleName();
							if ("int".equals(typeName) || "Integer".equals(typeName)) {
								md.invoke(t, rs.getInt(colName)); //反向激活这个方法，第一个参数是：哪个对象的方法,第二个是这个方法的形参
							}else if ("float".equals(typeName) || "Float".equals(typeName)) {
								md.invoke(t, rs.getFloat(colName));
							}else if ("Double".equals(typeName) || "double".equals(typeName)) {
								md.invoke(t, rs.getDouble(colName));
							}else {
								md.invoke(t, rs.getString(colName));
							}
						}
						break;
					}
				}
			}
			
			list.add(t);
		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return list;
	}
	
	public <T> List<T> findObject(String sql, Class<T> cl, List<Object> params) {
		Connection con =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<T> list = new ArrayList<T>();
		
		try {
			con = this.getConnection();
			pstmt = con.prepareStatement(sql);

			this.setParams(pstmt, params);
			rs = pstmt.executeQuery();
			
			// 每循环一次就是一个对象 -> 实例化一个对象   -> 给这个对象中对应的属性赋值  -> 要获取这个对象中的所有setter方法  -> 如何让判断哪个值调用哪个setter方法       
			// 规定 : 属性名必须跟列名相同（如果建表时属性名跟列名不相同，则在查询的时候必须重命名列） -> 在列名前面 + setter 然后跟 setter方法比对  -> 获取这个结果集中所有列的列名
			
			// 获取结果集中列名
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			String[] colNames = new String[colCount];
			for (int i=0; i<colCount; i++) {
				colNames[i] = rsmd.getColumnName(i+1);
			}
			
			// 获取给定类中的所有方法  -> setter方法
			Method[] methods = cl.getDeclaredMethods();
			List<Method> setters = new ArrayList<Method>();
			for (Method md : methods) {
				if (md.getName().startsWith("set")) {
					setters.add(md);
				}
			}
			
			T t = null;
			String mname = null;
			Class<?>[] types = null;
			String typeName = null;
			
			while(rs.next()) {
				t = cl.newInstance();     // 每循环一次创建一个对象
				// 将对应列中的值注入到这个对象对应的属性中
				for (String colName : colNames) {
					mname = "set" + colName;      // deptno  -> setdeptno
					for (Method md : setters) {   // 循环所有的方法  setDeptno()
						if(mname.equalsIgnoreCase(md.getName())) { // 说明这个列的值应该调用这个方法注入
							// 获取一个 setter方法的形参类型
							types = md.getParameterTypes();
							
							// 判断这个方法有没有形参
							if (types != null && types.length > 0) { // 取出第一个形参类型的类型名
								typeName = types[0].getSimpleName();
								if("int".equals(typeName) || "Integer".equals(typeName)) {
									md.invoke(t, rs.getInt(colName)); // 反向激活这个方法    第一个参数是 : 那个对象的这个方法       第二个参数是 ：这个方法的形参
								} else if ("float".equals(typeName) || "Float".equals(typeName)) {
									md.invoke(t, rs.getFloat(colName));
								} else if ("double".equals(typeName) || "Double".equals(typeName)) {
									md.invoke(t, rs.getDouble(colName));
								} else {
									md.invoke(t, rs.getString(colName));
								}
							}
							break;
						}
					}
				}
				
			list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			this.close(con, pstmt, rs);
		}
		return list;
	}

	
	/**
	 * 基于对象的查询
	 * @param sql
	 * @param c1
	 * @param params
	 * @return
	 */
	public <T> Object findSingleObject(String sql,Class<T> c1,Object ...params) {
		Connection con = null; 
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		List<T> list = new ArrayList<T>();
		try {
			con = this.getConnection(); //获取连接
			pstmt = con.prepareStatement(sql); //预编译执行语句
			this.setParams(pstmt, params);  //赋值
			rs=pstmt.executeQuery(); //执行语句
			ResultSetMetaData rsm = rs.getMetaData(); 
			int colCount = rsm.getColumnCount();
			String[] colNames = new String[colCount];
			for (int i = 1; i <=colCount; i++) {
				colNames[i-1] = rsm.getColumnName(i).toLowerCase();
			}
			//获取给定类中的所有方法->setter
			Method[] methods = c1.getDeclaredMethods();
			List<Method> setters = new ArrayList<Method>();
			for (Method md:methods) {
				if (md.getName().startsWith("set")) {
					setters.add(md);
				}
			}
			
			T t=null;
			String mname= null;
			Class<?>[] types = null;
			String typeName = null;
			
			while (rs.next()) {
				t = c1.newInstance(); //每循环一次创建一个对象
				
				//将对应列的值注入到这个对象对应的属性中
				for (String colName:colNames) { //循环所有列名
					mname = "set"+colName;   //deptno-> setDeptno
				for (Method md:setters) { //循环所有方法 SET....
					if (mname.equalsIgnoreCase(md.getName())) { //说明这个列的值应该调用这个方法注入
						//获得第一个setter方法的形参类型
						types = md.getParameterTypes();
						
						//判断这个方法有没有形参
						if (types!=null && types.length>0) { //取出第一个形参类型的类型名
							typeName = types[0].getSimpleName();
							if ("int".equals(typeName) || "Integer".equals(typeName)) {
								md.invoke(t, rs.getInt(colName)); //反向激活这个方法，第一个参数是：哪个对象的方法,第二个是这个方法的形参
							}else if ("float".equals(typeName) || "Float".equals(typeName)) {
								md.invoke(t, rs.getFloat(colName));
							}else if ("Double".equals(typeName) || "double".equals(typeName)) {
								md.invoke(t, rs.getDouble(colName));
							}else {
								md.invoke(t, rs.getString(colName));
							}
						}
						break;
					}
				}
			}
		  return t;
			
		}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.close(con,pstmt,null);
		}
		return null;
	}


}
