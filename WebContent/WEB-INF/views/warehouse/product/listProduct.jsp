<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="${baseUrl}/static/css/common.css" rel="stylesheet" type="text/css" />
	<link href="${baseUrl}/static/bootstrap/bootstrap.min.css" rel="stylesheet"type="text/css" />
	<link href="${baseUrl}/static/bootstrap/common.css" rel="stylesheet" type="text/css"/>
	
	<link href="${baseUrl}/static/lhgdialog/prettify/common.css" type="text/css" rel="stylesheet" />
	<link href="${baseUrl}/static/lhgdialog/prettify/prettify.css" type="text/css" rel="stylesheet" />
	<link href="${baseUrl}/static/calendar/prettify.css" rel="stylesheet" type="text/css" />
	
	<link href="${baseUrl}/static/calendar/lhgcalendar.css" rel="stylesheet" type="text/css" />
	
	<link href="${baseUrl}/static/ligerui/ligerUI/skins/Aqua/css/ligerui-all.css" rel="stylesheet" type="text/css" />
    <style type="text/css"> 
 	</style>
</head>
<body>
	  <div class="toolbar1">
           <form action="${baseUrl}/product/getListProductData.do" id="searchform" name="searchform" method="post">
               <div class="pull-left">
               		<span class="pull-left" style="width:85px;">
			       		<a class="btn btn-primary btn-small" onclick="addProduct()" title="添加产品">
			           		 <i class="icon-plus"></i>添加产品
			       	 	</a>
			       	 	<input style=" visibility:hidden;">
		       	 	</span>
           			<span class="pull-left" style="width:130px;">
			       		<a class="btn btn-primary btn-small" onclick="printListSkuBarcode()" title="打印SKU条码">
			           		 <i class="icon-folder-open"></i>打印列表SKU条码
			       	 	</a>
			       	 	<input style=" visibility:hidden;">
		       	 	</span>
		       	 	<span class="pull-left" style="width:130px;">
			       		<a class="btn btn-primary btn-small" onclick="printSkuBarcode()" title="打印SKU条码">
			           		 <i class="icon-folder-open"></i>打印指定SKU条码
			       	 	</a>
			       	 	<input style=" visibility:hidden;">
		       	 	</span>
		    	</div>    
           		
               <div class="pull-right ">
               		<span class="pull-left" style="width:150px;">
               			仓库
               			<select style="width:90px;" id="warehouseId" name="warehouseId">
               				<option></option>
							<c:forEach items="${warehouseList}" var="w" >
				       	 		<option value="<c:out value='${w.id}'/>">
				       	 			<c:out value="${w.id}-${w.warehouseName}"/>
				       		 	</option>
				       		 </c:forEach>
						</select>
               		</span>
               		
               		<span class="pull-left" style="width:165px;">
               			客户帐号
               			<input type="text" name="userLoginName" data-provide="typeahead"  id="userLoginName" style="width:90px;" title="请输入客户登录名" />
               		</span>
					<span class="pull-left" style="width:190;">
						<!--  keyword关键字搜索产品sku或产品名-->
						产品名/SKU	
						<input type="text"  name="keyword"  id="keyword"   style="width:100px;"/>
					</span>
               		<span class="pull-left" style="width:175px;">
               			创建时间
	               		<input type="text"   style="width:120px;" name="createdTimeStart" id="createdTimeStart" value="${sevenDaysAgoStart}" title="起始创建时间">
               		</span>
               		
               		<span class="pull-left" style="width:200px;">
               			至	
               			<input type="text"   style="width:120px;" name="createdTimeEnd"  id="createdTimeEnd"  title="终止创建时间">
               		</span>
               		
               		<span class="pull-left" style="width:55px;">
               			<a class="btn btn-primary btn-small" id="btn_search"><i class="icon-search icon-white"></i>搜索</a>
               			<input style=" visibility:hidden;">
               		</span>
               </div>
           </form>
	</div>
	<div id="maingrid" class="pull-left" style="width:100%;"></div>
	
	
	<script type="text/javascript" src="${baseUrl}/static/jquery/jquery.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/bootstrap/bootstrap-typeahead.js"></script>
    
    <script type="text/javascript">
 		var baseUrl = "${baseUrl}";
   		$(function(){
   			//客户帐号自动完成
	        $("#userLoginName").typeahead({
	            source: function (query, process) {
	            	var userResult = "";
	                $.ajax({
	          	      type : "post",
	          	      url : baseUrl+'/user/searchUser.do?keyword='+query,
	          	      async : false,
	          	      success : function(data){
	          	       	 data = eval("(" + data + ")");
	          	      	userResult = data;
	          	      }
	          	   });
	                return userResult;
	            },
	            highlighter: function(item) {
	            	var arr = item.split("-");
	  	          	var userId = arr[0];
	  	          	var loginName = arr[1];
	  	          	var keyword = arr[2];
	  	          	var newKeyword = ("<font color='red'>"+keyword+"</font>");
	  	          	var newItem = userId.replace(keyword,newKeyword)+"-"+loginName.replace(keyword,newKeyword);
	  	          	return newItem;
	            },
	            updater: function(item) {
            	 var itemArr = item.split("-");
  	              return itemArr[1];
	     	 }
	      });
   			
	        $('#createdTimeStart,#createdTimeEnd').calendar({ format:'yyyy-MM-dd HH:mm:ss' });
   			initGrid();  
   			
   			//btn_search
   			$("#btn_search").click(function(){
   				btnSearch("#searchform",grid);
   			});
   			
   		});
   		  
   	 	 var grid = null;
	     function initGrid() {
	    	 grid = $("#maingrid").ligerGrid({
	                columns: [
						{ display: '产品名称', name: 'productName', align: 'center',type:'float',width:'6%'},
	                    { display: '客户帐号', name: 'userNameOfCustomer', align: 'center',type:'float',width:'6%'},
	                    { display: '产品类型', name: 'productTypeName', align: 'center',type:'float',width:'6%'},
	                    { display: '产品SKU', name: 'sku', align: 'center',type:'float',width:'6%'},
	  		          	{ display: '仓库SKU', name: 'warehouseSku', align: 'center',type:'float',width:'6%'},
		                { display: '价值币种', name: 'currency', align: 'center',type:'float',width:'6%'},
		                { display: '是否需要生产批次 ', name: 'isNeedBatchNo', align: 'center',type:'float',width:'6%'},
		                { display: '规格型号', name: 'model', align: 'center',type:'float',width:'6%'},
		                { display: '原厂地', name: 'origin', align: 'center',type:'float',width:'6%'},
		                { display: '报关价值（元）', name: 'customsValue', align: 'center',type:'float',width:'6%'},
		                { display: '行邮税号', name: 'taxCode', align: 'center',type:'float',width:'6%'},
		                { display: '报关重量 KG', name: 'customsWeight', align: 'center',type:'float',width:'6%'},
		                { display: '体积', name: 'volume', align: 'center',type:'float',width:'6%'},
		                { display: '上次更改时间', name: 'lastUpdateTime', align: 'center',type:'float',width:'12%'},
		                { display: '创建时间', name: 'createdTime', align: 'center',type:'float',width:'12%'},
		                { display: '备注', name: 'remark', align: 'center', type: 'float',width:'13%'},
		                {display: '操作',isSort: false,width: '10%',render: function(row) {
		            		if (!row._editing) {
		            			var h = "<a href=javascript:updateProduct(" + row.id +")>更新</a>&nbsp;&nbsp;";
		            			h+= "<a href=javascript:deleteProduct(" + row.id +")>删除</a>";
		            			return h;
		            		}
		            	}}
	                ],  
	                dataAction: 'server',
	                url: baseUrl+'/product/getListProductData.do?createdTimeStart=${sevenDaysAgoStart}',
	                pageSize: 50, 
	                pageSizeOptions:[10,50,100,500,1000],
	                usePager: 'true',
	                sortName: 'id',
	                checkbox: true,
	                rownumbers:true,
	                alternatingRow:true,
	                minColToggle:20,
	                isScroll: true,
	                enabledEdit: false,
	                clickToEdit: false,
	                enabledSort:false,
	                inWindow:true,
	                width: '100%',
	                height: '99%'
	            });
	        };		
   	</script>
   	
   	<script type="text/javascript">
   		function addProduct(){
   			
   		}
   		function deleteProduct(id){
   			
   		}
   		function updateProduct(id){
   			
   		}
   		
		function printListSkuBarcode(){
		    var contentArr = [];
		    contentArr.push('<div id="changeContent" style="padding:10px;width: 300px;">');
		    contentArr.push('   <div class="pull-left" style="width: 100%">');
		    contentArr.push('       <input class="pull-left" name="chooseOption" style="margin-left: 15px;" type="radio" checked="checked" value="selected" id="selected">');
		    contentArr.push('       <label class="pull-left" style="margin-left: 5px" for="selected">打印选中</label>');
		    contentArr.push('       <input class="pull-left" name="chooseOption" style="margin-left: 30px;" type="radio" value="all" id="all">');
		    contentArr.push('       <label class="pull-left" style="margin-left: 5px;" for="all">打印当前页</label>');
		    contentArr.push('   </div>');
		    contentArr.push('   <div class="pull-left" style="width: 100%;margin-top:10px;height:35px;">');
		    contentArr.push('       <label class="pull-left" style="margin-left: 15px;" for="all">每个SKU打印份数</label>');
		    contentArr.push('       <input class="pull-left" name="quantity" value="1"  style="margin-left: 10px;height:15px;width:80px" type="text" id="quantity">');
		    contentArr.push('   </div>');
		    contentArr.push('</div>');
		    contentArr.push('<div style="color: #ff0000;margin-left: 25px;margin-top:5px;">注：请使用大于等于80*25的标签纸</div>');
		    var contentHtml = contentArr.join('');
			$.dialog({
		  		lock: true,
		  		max: false,
		  		min: false,
		  		title: '打印SKU条码',
		  	     width: 340,
		         height: 120,
		  		content: contentHtml,
		  		button: [{
		  			name: '确认',
		  			callback: function() {
		  				var  row = grid.getSelectedRows();
		                var all = parent.$("#all").attr("checked");
		                if(all){
		                	row = grid.getRows();	 
		                }
			            if(row.length < 1){
			                parent.$.showShortMessage({msg:"请最少选择一条数据",animate:false,left:"45%"});
			                return false;
			            }
			            var ids = "";
		            	for ( var i = 0; i < row.length; i++) {
		            		ids += row[i].id+",";
						}
		            	var quantity =  parent.$("#quantity").val();
		            	if(ids!=""){
		            		//打印SKU,新建标签页
		    			    var url = baseUrl+'/warehouse/print/printSkuBarcode.do?ids='+ids+'&quantity='+quantity;
		      			  	window.open(url);
		            	}
		  			}
		  		},
		  		{
		  			name: '取消'
		  		}]
		  	})
		}
	
		function printSkuBarcode(){
		    var contentArr = [];
		    contentArr.push('<div id="changeContent" style="padding:10px;width: 340px;">');
		    contentArr.push('   <div class="pull-left" style="width: 100%;margin-top:5px;line-height:10px;">');
		    contentArr.push('       <label class="pull-left" style="margin-left: 15px;" for="all">打印指定SKU条码</label>');
		    contentArr.push('       <input class="pull-left" name="sku" style="margin-left: 10px;width:120px;height:15px;" type="text" id="sku">');
		    contentArr.push('   </div>');
		    contentArr.push('   <div class="pull-left" style="width: 100%;margin-top:10px;height:35px;">');
		    contentArr.push('       <label class="pull-left" style="margin-left: 15px;" for="all">每个SKU打印份数</label>');
		    contentArr.push('       <input class="pull-left" name="quantity" value="1" style="margin-left: 10px;height:15px;width:80px" type="text" id="quantity">');
		    contentArr.push('   </div>');
		    contentArr.push('</div>');
		    contentArr.push('<div style="color: #ff0000;margin-left: 25px;margin-top:5px;">注：请使用大于等于80*25的标签纸</div>');
		    var contentHtml = contentArr.join('');
			$.dialog({
		  		lock: true,
		  		max: false,
		  		min: false,
		  		title: '打印SKU条码',
		  	     width: 380,
		         height: 120,
		  		content: contentHtml,
		  		button: [{
		  			name: '确认',
		  			callback: function() {
		                var sku = parent.$("#sku").val();
			            if(sku == null || sku ==""){
			                parent.$.showShortMessage({msg:"请输入产品SKU",animate:false,left:"45%"});
			                return false;
			            }
			            var quantity =  parent.$("#quantity").val();
		            	//打印SKU,新建标签页
	    			    var url = baseUrl+'/warehouse/print/printSkuBarcode.do?sku='+sku+'&quantity='+quantity;
	      			  	window.open(url);
		  			}
		  		},
		  		{
		  			name: '取消'
		  		}]
		  	})
		}
   	</script>
	<script type="text/javascript" src="${baseUrl}/static/jquery/jquery.showMessage.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/ligerui/ligerUI/js/core/base.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/ligerui/ligerUI/js/ligerui.all.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/ligerui/ligerUI/js/plugins/ligeruiPatch.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/calendar/lhgcalendar.min.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/lhgdialog/prettify/prettify.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/lhgdialog/prettify/lhgdialog.js"></script>
	<script type="text/javascript" src="${baseUrl}/static/calendar/prettify.js"></script>
</body>
</html>