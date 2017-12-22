package com.rt.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;

import com.rt.bean.QuoteInfor;


public class GetInforDAO {
	public List<Integer> getCompany(String aCompanyName,Session session){
		String sql = "SELECT id FROM company WHERE NAME LIKE ?";
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setParameter(0, "%"+aCompanyName+"%");
		List<Integer> companyList = sqlQuery.list();
		return companyList;		
	}
	
	public List<QuoteInfor> getRecord(int aCompanyId,int aMemberId, Session session){
		String sql="SELECT q.key_name as markName, q.city_name as cityName,q.price as price,q.fcode as fcode," +
				"q.FPRICE as fprice,m.`username` as username,m.`name` as name FROM quote_req_backup q " +
				"INNER JOIN member m ON m.`id`=q.`member_id`" +
				" WHERE q.`company_id`=? AND q.`member_id`=? GROUP BY q.`key_name`,q.`city_name`";
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setParameter(0, aCompanyId);
		sqlQuery.setParameter(1, aMemberId);
		List<QuoteInfor> recordList = sqlQuery.addScalar("markName", StandardBasicTypes.STRING ) 
				.addScalar("cityName", StandardBasicTypes.STRING ) 
				.addScalar("price", StandardBasicTypes.INTEGER ) 
				.addScalar("fcode", StandardBasicTypes.STRING ) 
				.addScalar("fprice", StandardBasicTypes.STRING ) 
				.addScalar("username", StandardBasicTypes.STRING ) 
				.addScalar("name", StandardBasicTypes.STRING ) 
				.setResultTransformer(Transformers.aliasToBean(QuoteInfor.class)).list();	
		return recordList;
	}
	
	public List<Integer> getMemberID(int aCompanyId, Session session){
		String sql = "SELECT member_id FROM quote_req_backup WHERE company_id=? GROUP BY member_id";
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setParameter(0, aCompanyId);
		List<Integer> memberList=sqlQuery.list();
		return memberList;
	}
	
	public List<Integer> getCompanyRole(int aCompanyId, Session session){
		String sql = "SELECT member_id FROM company_role WHERE company_id=?";
		SQLQuery sqlQuery = session.createSQLQuery(sql);
		sqlQuery.setParameter(0, aCompanyId);
		List<Integer> memberList=sqlQuery.list();
		return memberList;
	}
	
	public Map getUserName(int aMemberId , Session session){
		String sql ="SELECT username,name FROM member WHERE id=?";
		Query query  = session.createSQLQuery(sql).addScalar("username", StandardBasicTypes.STRING ) 
				.addScalar("name", StandardBasicTypes.STRING ) 
				.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);  //输出转为Map,key为字段名
		query.setParameter(0, aMemberId);
		Map<String,String> sUsername=  (Map<String,String>) query.uniqueResult();
		return sUsername;
	}
}
