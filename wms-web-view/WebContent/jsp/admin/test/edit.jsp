<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../meta.jsp"%>
<title>管理后台</title>
<style type="text/css">
.upload-btn {
	font-size: 18px;
	text-align: center;
	background: #00b7ee;
	border-radius: 3px;
	line-height: 44px;
	padding: 0 30px;
	color: #fff;
	display: inline-block;
	margin: 0 auto 20px;
	cursor: pointer;
	box-shadow: 0 1px 1px rgba(0, 0, 0, .1);
}

.upload-btn:hover {
	background: #00a2d4;
}

.form-inline .input-group .input-group-addon {
	width: 100px;
}

.form-inline .input-group>.width100 {
	width: 100px;
}

.form-inline .input-group>.width200 {
	width: 200px;
}

.form-inline .input-group>.width400 {
	width: 400px;
}

.form-inline .input-group>.width800 {
	width: 600px;
}

.form-inline .input-group>.width800 {
	width: 800px;
}
</style>
</head>
<body style="padding-top: 12px; overflow-y: scroll;">

	<div class="container" style="margin-left: 0;">
		<div style="font-size: 25px; line-height: 45px;">用户编辑</div>

		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">用户信息</h3>
			</div>
			<div class="panel-body">

				<form class="form-inline" id="fromUpd">

					<div class="row" style="margin-left: 0; margin-right: 0;">

						<div class="input-group ">
							<span class="input-group-addon">用户名称</span> <input type="text"
								class="form-control width100 notNullVali" placeholder="用户名"
								name="userName" id="user_name">
						</div>

						<div class="input-group ">
							<span class="input-group-addon" id="sizing-addon3">登录名称</span> <input
								type="text" class="form-control width100 notNullVali"
								name="loginName" placeholder="登录名称" id="login_name">
						</div>

						<div class="input-group ">
							<span class="input-group-addon" id="sizing-addon3">登录密码</span> <input
								type="password" class="form-control width100 notNullVali"
								name="password" placeholder="登录密码" id="password">
						</div>
						<input name="id" type="hidden"/>


						<!-- <div class="input-group "> -->
						<!--   <span class="input-group-addon" id="sizing-addon3">证件类型</span> -->
						<!--   <select style="" class="form-control width100" id="card_type" name="shipway"> -->
						<!--    	     <option value="">请选择</option> -->
						<!--    	     <option value="N">身份证</option> -->
						<!--    	     <option value="Y">护照</option> -->
						<!--    	     <option value="Y">驾驶证</option> -->
						<!--    </select> -->
						<!-- </div> -->

					</div>

					<div class="row">
						<div class="col-md-2"></div>
					</div>

				</form>

			</div>
		</div>




	</div>

	<script>
	
	var isAllow=false;
	
	$(function(){
	    loadConfig();
		//getButton();
		//页面加载时从数据库中获取信息
		//获取参数id
		var id = getParam("id");
	   //获取菜单id
	    var menuid=getParam("menuid");
		//从数据库加载数据
		var symgmtPath=globalUrl.symgmtPath;
       
	
		doGet( globalUrl.symgmtPath+"/symgmt/admin/getById/"+id+"?menuid="+menuid, function(data) {
			if (data.code == 0) {
				var entity=data.data;
				var formId="fromUpd";
				//封装实体  简单封装
				packageFormBean(formId,entity)
				isAllow=true;
			} else {
				mmui.alert(data.msg, 3, 2000);
				
			}

		},true)
	})

		
		//执行新增方法
		function upd(index, locationUri, menuid) {
			if(!isAllow){
				mmui.alert("禁止操作", 3, 2000);
				return
			}
		
		    var errStr = valiNotNull("#fromUpd .notNullVali")
			if (errStr != '') {
				mmui.alert(errStr, 3, 2000);
				return false;
			}

		    
		    //把form表单序列化成json字符串
		   var jsonStr=formToJson("fromUpd");
			

			var paIfram = getIfram(locationUri)

			/*$.ajax({
				type : "POST",
				url : globalUrl.symgmtPath
						+ "/symgmt/admin/updateFromFront?menuid=" + menuid,
				async : false,
				data : jsonStr,
				contentType : "application/json",
				dataType : 'json',
				success : function(data) {
					if (data.code == 0) {
						paIfram.contentWindow.refresh(index, data.msg)
					} else {
						mmui.alert(data.msg, 3, 2000);
					}

				},
				crossDomain : true,
				xhrFields : {
					withCredentials : true
				},
			});
			*/
			
			doJsonPost(globalUrl.symgmtPath+ "/symgmt/admin/updateFromFront?menuid=" + menuid, function(data) {
				if (data.code == 0) {
					paIfram.contentWindow.refresh(index, data.msg)
				} else {
					mmui.alert(data.msg, 3, 2000);
				}

			}, false, jsonStr)
			
		}
	</script>

</body>
</html>