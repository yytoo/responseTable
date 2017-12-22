package com.rt.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hibernate.Session;

import net.sf.json.JSONArray;

import com.opensymphony.xwork2.ActionSupport;
import com.rt.bean.QuoteInfor;
import com.rt.dao.GetInforDAO;
import com.rt.util.HibernateSessionFactory;

public class GetInforAction extends ActionSupport {
	private String companyName;

	public String selectRecord(){
		HttpServletResponse response = ServletActionContext.getResponse();
		//response.setContentType("text/html; charset=UTF-8");
		response.setContentType("application/json; charset=utf-8"); 
		response.setHeader("Access-Control-Allow-Origin", "http://192.168.1.104:8080");
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		Session session = HibernateSessionFactory.getSession();
		JSONArray returnJson=null;
		int iCompanyId=selectCompanyId(session);
		Map<String, String> infor = new HashMap<String, String>();	
		if(iCompanyId==0){
			infor.put("error", "公司不存在或存在多个具有该关键字的公司");
			jsonMap.put("infor", infor);
			returnJson= JSONArray.fromObject(jsonMap);	
		}else{			
			if(selectUserName(iCompanyId, session)==null){
				infor.put("error", "该公司下无用户");
				jsonMap.put("infor", infor);
				returnJson= JSONArray.fromObject(jsonMap);	
			}else{
				infor.put("success", "success");
				jsonMap.put("infor", infor);
				List<Map<String,String>> userMap=selectUserName(iCompanyId, session);			
				jsonMap.put("userMap", userMap);
				List<List<QuoteInfor>> recordMap=selectRecord(iCompanyId, session);
				jsonMap.put("recordMap", recordMap);	
				returnJson= JSONArray.fromObject(jsonMap);					
			}
			
		}
		try {
			PrintWriter out = response.getWriter();  
			System.out.println(returnJson.toString());
			out.print(returnJson.toString());  
			out.flush();
            out.close();          
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.flush();
		session.clear();   //清除hibernate的缓存
		return null;
	}
	
	public int selectCompanyId(Session session){		    //查询关键字对应的公司名
		GetInforDAO getInforDAO = new GetInforDAO();
		List<Integer> companyIdList = getInforDAO.getCompany(companyName,session);
		if(companyIdList.size()!=0 || companyIdList!=null){
			if(companyIdList.size()==1){
				return companyIdList.get(0);
			}
		}
		return 0;
	}
	
	public List<List<QuoteInfor>> selectRecord(int aCompanyId,Session session){
		List<List<QuoteInfor>> recordMap=new ArrayList<List<QuoteInfor>>();
		GetInforDAO getInforDAO = new GetInforDAO();
		List<Integer> memberList=getInforDAO.getMemberID(aCompanyId, session);
		List<QuoteInfor>  recordList=null;
		int i=0;
		for(int iMemberId:memberList){
			recordList = getInforDAO.getRecord(aCompanyId,iMemberId, session); 
			for(QuoteInfor qi:recordList){
				
				qi.setMarkName(qi.getMarkName().replace("-", " "));
				System.out.println(qi.getMarkName());
			}
			recordMap.add(recordList);
			i=i+1;
		}
		return recordMap;
	}
	
	public List<Map<String,String>> selectUserName(int aCompanyId,Session session){    //根据公司id查找quote_backup中的该公司所有记录
		GetInforDAO getInforDAO = new GetInforDAO();
		List<Integer> memberIdList = getInforDAO.getCompanyRole(aCompanyId, session);
		List<Map<String,String>> userList = new ArrayList<Map<String,String>>();
		Map<String,String> sUserName=null;
		/*
		 * 这里加memberIdList.size()==0的判断
		 */
		if(memberIdList.size()==0){
			return null;
		}
		for(int iMemberId: memberIdList){			
			sUserName=getInforDAO.getUserName(iMemberId, session);
			userList.add(sUserName);    //json的key必须为string，所以需要用String.valueof()将int转string
		}
		return userList;
	}
	

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}
