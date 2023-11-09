package selenium_test.dao;

import selenium_test.vo.AirplainArriveVO;
import selenium_test.vo.AirplainDepartVO;

import java.sql.*;

public class DataDAO {
   private Connection conn; 
   private PreparedStatement ps; 

   private final String URL="jdbc:oracle:thin:@localhost:1521:xe"; 
   private static DataDAO dao;
   // 드라이버 등록 
	public DataDAO() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception ex) {
		}
	}

	// 연결
	public void getConnection() {
		try {
			conn = DriverManager.getConnection(URL, "scott", "tiger"); // 경로, id, pw
		} catch (Exception ex) {
		}
	}

	// 연결 해제
	public void disConnection() {
		try {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (Exception ex) {
		}
	}

   public static DataDAO newInstance()
   {
	   if(dao==null)
		   dao=new DataDAO();
	   return dao;
   }
   
   public void departInsert(AirplainDepartVO vo)
   {
	   try
	   {
		   getConnection();
		   String sql="INSERT INTO airplaneDepart VALUES(?,?,?,?,?,?,?,?,?)";
		   ps=conn.prepareStatement(sql);
		   
		   ps.setString(1, vo.getFlightName());
		   ps.setDate(2, vo.getDepDay());
		   ps.setTimestamp(3, vo.getDepTime());
		   ps.setString(4, vo.getFullDeparture());
		   ps.setString(5, vo.getDepCode());
		   ps.setString(6, vo.getDepName());
		   ps.setString(7, vo.getFullArrival());
		   ps.setString(8, vo.getArrCode());
		   ps.setString(9, vo.getArrName());

		   ps.executeUpdate();
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   finally
	   {
		   disConnection();
	   }
   }
  
   public void arriveInsert(AirplainArriveVO vo)
   {
	   try
	   {
		   // DB연결
		   getConnection();
		   
		   String sql="INSERT INTO airplaneArrive VALUES(?,?,?,?,?,?,?,?,?)";

		   ps=conn.prepareStatement(sql);
		   ps.setString(1, vo.getFlightName());
		   ps.setDate(2, vo.getArrDay());
		   ps.setTimestamp(3, vo.getArrTime());
		   ps.setString(4, vo.getFullDeparture());
		   ps.setString(5, vo.getDepCode());
		   ps.setString(6, vo.getDepName());
		   ps.setString(7, vo.getFullArrival());
		   ps.setString(8, vo.getArrCode());
		   ps.setString(9, vo.getArrName());
		   
		   ps.executeUpdate();
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
	   }
	   finally
	   {
		   
		   disConnection();
	   }
   }
}
