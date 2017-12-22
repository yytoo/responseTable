<%@ page language="java" import="java.util.*" contentType="text/html; charset=utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="<%=basePath %>js/jquery-1.9.1.min.js"></script>
  </head>
  
  <body>
 	 <div>
    	<input type="text" class="companyName" name="companyName" id="companyName" />
    	<button  class="button" onclick="getInfor()">submit</button>    
    	<div class="inofr">
    		<span>公司人员信息：</span><br>
    		<textarea rows="5" cols="200" class="companyInfor" id="companyInfor"></textarea>
    		<span>昨日报价：</span><br>
    		<textarea rows="20" cols="200" class="yesterday" id="yesterday"></textarea>
    		<span>七日内报价：</span><br>
    		<textarea rows="20" cols="200" class="sevenday" id="sevenday"></textarea>
    	</div>
    </div>
  </body>
  <script type="text/javascript">
  	function getInfor(){
	  	var companyName="companyName="+$("#companyName").val();
	  	$.ajax({
	  		type:"post",
	  		url:"http://127.0.0.1:8080/resposneTable/getInfor",
	  		data:companyName,
	  		dataType:"json",
	  		success:function(msg){
	  			var jsonString=JSON.stringify(msg);
	  			var jsondata =$.parseJSON(jsonString);
	  			var companyInfor="";
	  			var record="";
	  			var key="";
	  			var infor="";
	  			/* 获取传递过来的infor提示信息 */
	  			for(var i in jsondata[0].infor){
		  			key=i;
		  			infor=jsondata[0].infor[i];
	  			}
	  			
	  			if(key=="error" || key=="ERROR"){
		  				alert(key+":"+infor);   //如果提示信息是error，则弹框提示错误原因
		  		}else{
		  			/* 填写第一个框内的公司下所有人员信息 */
		  			$.each(jsondata[0].userMap,function(idx,obj){
	  				companyInfor=companyInfor+obj.username+":"+obj.name+"\n";
		  			});
		  			$("#companyInfor").text(companyInfor);
		  			/* 填写第二个框内的报价信息 */
		  			$.each(jsondata[0].recordMap,function(idx2,obj2){
		  				var recordInfo="";
		  				var cityname="";
		  				$.each(obj2,function(idx3,obj3){
		  					if(cityname==obj3.cityName){
		  						cityname="";
		  					}else{
		  						cityname=obj3.cityName+"\n";
		  					}
			  				if(idx3==0){
			  					recordInfo=recordInfo+obj3.username+"\n"+cityname+obj3.markName+" "+obj3.price+"\n";
			  				}else{
			  					recordInfo=recordInfo+cityname+obj3.markName+" "+obj3.price+"\n";
			  				}
		  					cityname=obj3.cityName;
		  				});
		  				record=record+recordInfo+"\n";
		  			}); 
		  			$("#yesterday").text(record);
		  		
		  		}
	  			
	  		},  	
            error:function(msg){
                alert(JSON.stringify(msg));
            }
  		});  		
  	}
  </script>
</html>
