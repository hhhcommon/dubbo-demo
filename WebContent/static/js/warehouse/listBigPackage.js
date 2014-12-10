 //审核订单
function checkOrder(){
    var contentArr = [];
    contentArr.push('<div class="pull-left" style="width: 100%;text-align:center;line-height:20px;margin-top: 5px;">');
    contentArr.push('   <span class="pull-left">审核范围：</span>');
    contentArr.push('   <input class="pull-left" name="chooseOption" style="margin-left:30px;vertical-align: middle;" type="radio" checked="checked" value="selected" id="selected">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;" for="selected">审核选中</label>');
    contentArr.push('   <input class="pull-left" name="chooseOption" style="margin-left:56px;vertical-align: middle;" type="radio" value="all" id="all">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;" for="all">审核当前页</label>');
    contentArr.push('</div>');

    contentArr.push('<div class="pull-left" style="width: 100%;text-align:center;line-height:20px;">');
    contentArr.push('   <span class="pull-left">审核操作：</span>');
    contentArr.push('   <input class="pull-left" name="checkResult" style="margin-left:30px;vertical-align: middle;" type="radio" checked="checked" id="checkSuccess">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;color: green;" for="checkSuccess">审核通过</label>');
    contentArr.push('   <input class="pull-left" name="checkResult" style="margin-left:56px;vertical-align: middle;" type="radio" id="checkFail">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;color: red;" for="checkFail">审核不通过</label>');
    contentArr.push('</div>');

    contentArr.push('<div style="color: #ff0000;margin-left: 30px;">注: 非待审核状态的订单不受审核影响</div>');
    contentArr.push('</div>');
    var contentHtml = contentArr.join('');
	$.dialog({
  		lock: true,
  		max: false,
  		min: false,
  		title: '提示',
  		width: 360,
  		height: 85,
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
                var checkResult = parent.$("#checkSuccess").attr("checked")?1:2;//1是审核通过 2是审核不通过
                var orderIds = "";
            	for ( var i = 0; i < row.length; i++) {
            		orderIds += row[i].id+",";
				}
        		$.post(baseUrl + '/warehouse/storage/checkOutWarehouseOrder.do',{orderIds:orderIds,checkResult:checkResult},function(msg){
        			if(msg.status == "1"){
        				grid.loadData();	
        				parent.$.showDialogMessage(msg.message,null,null);
        			}else{
        				parent.$.showDialogMessage(msg.message,null,null);
        			}
                },"json");
  			}
  		},
  		{
  			name: '取消'
  		}]
  	})
}

//审核单个订单
function checkSingleOrder(id){
    var contentArr = [];
    contentArr.push('<div class="pull-left" style="width: 100%;text-align:center;line-height:20px;">');
    contentArr.push('   <span class="pull-left">审核操作：</span>');
    contentArr.push('   <input class="pull-left" name="checkResult" style="margin-left:30px;vertical-align: middle;" type="radio" checked="checked" id="checkSuccess">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;color: green;" for="checkSuccess">审核通过</label>');
    contentArr.push('   <input class="pull-left" name="checkResult" style="margin-left:56px;vertical-align: middle;" type="radio" id="checkFail">');
    contentArr.push('   <label class="pull-left" style="margin-left: 5px;line-height:20px;vertical-align: middle;color: red;" for="checkFail">审核不通过</label>');
    contentArr.push('</div>');

    contentArr.push('<div style="color: #ff0000;margin-left: 30px;">注: 非待审核状态的订单不受审核影响</div>');
    contentArr.push('</div>');
    var contentHtml = contentArr.join('');
	$.dialog({
  		lock: true,
  		max: false,
  		min: false,
  		title: '提示',
  		width: 360,
  		height: 85,
  		content: contentHtml,
  		button: [{
  			name: '确认',
  			callback: function() {
                var checkResult = parent.$("#checkSuccess").attr("checked")?1:2;//1是审核通过 2是审核不通过
        		$.post(baseUrl + '/warehouse/storage/checkOutWarehouseOrder.do',{orderIds:id,checkResult:checkResult},function(msg){
        			if(msg.status == "1"){
        				grid.loadData();	
        				parent.$.showDialogMessage(msg.message,null,null);
        			}else{
        				parent.$.showDialogMessage(msg.message,null,null);
        			}
                },"json");
  			}
  		},
  		{
  			name: '取消'
  		}]
  	})
}

//SKU
function listLittlePackages(bigPackageId){
	var contentArr = [];
	contentArr.push('<div style="height:340px;overflow:auto; ">');
	contentArr.push('<table class="table table-condensed" style="width:749px">');
	contentArr.push('<tr class="info"><th>入库跟踪单号</th><th>承运商</th><th>销售编号</th><th>状态</th><th>入库时间</th><th>回传入库状态</th><th>创建时间</th></tr>');
	$.ajax({ 
        type : "post", 
        url :baseUrl + '/warehouse/transport/getLittlePackageItemByBigPackageId.do', 
        data : "bigPackageId="+bigPackageId, 
        async : false, 
        success : function(msg){ 
        	msg = eval("(" + msg + ")");
			$.each(msg,function(i,e){
			  	contentArr.push('<tr>');
			  	contentArr.push('<td>'+e.trackingNo+'</td>');
			  	contentArr.push('<td>'+e.carrierCode+'</td>');
        		contentArr.push('<td>'+e.poNo+'</td>');
        		contentArr.push('<td>'+e.status+'</td>');
        		contentArr.push('<td>'+e.receivedTime+'</td>');
        		contentArr.push('<td>'+e.callbackIsSuccess+'</td>');
        		contentArr.push('<td>'+e.createdTime+'</td>');
			  	contentArr.push('</tr>');
			  	contentArr.push('<tr  class="warning"><th>&nbsp;<th>编号</th><th>商品名称</th><th>商品单价(元)</th><th>规格型号</th><th>净重KG</th><th>数量</th></tr>');
			  	$.each(e.littlePackageItemList,function(j,ei){
			  		contentArr.push('<tr>');
			  		contentArr.push('<td>&nbsp;</td>');
				  	contentArr.push('<td>'+ei.sku+'</td>');
				  	contentArr.push('<td>'+ei.skuName+'</td>');
	        		contentArr.push('<td>'+(ei.skuUnitPrice/100)+'</td>');
	        		contentArr.push('<td>'+ei.specification+'</td>');
	        		contentArr.push('<td>'+(ei.skuNetWeight/1000)+'</td>');
	        		contentArr.push('<td>'+ei.quantity+'</td>');
				  	contentArr.push('</tr>');
			  	});
			});
        } 
   	});
    contentArr.push('</table>');
    contentArr.push('</div>');
    var contentHtml = contentArr.join('');
	$.dialog({
  		lock: true,
  		max: false,
  		min: false,
  		title: '转运订单小包详情',
  		width: 820,
  		height: 360,
  		content: contentHtml,
  		button: [{
  			name: '关闭',
  			callback: function() {
				
  			}
  		}]
  	})
}

function advancedSearch(){
	   $.dialog({
	          lock: true,
	          title: '批量单号搜索',
	          width: '600px',
	          height: '200px',
	          content: 'url:' + baseUrl + '/warehouse/storage/searchOutWarehouseOrder.do',
	          button: [{
	            name: '确定',
	            callback: function() {
	              var nos = this.content.$("#nos").val();
	              var noType = this.content.$("#noType").val();
	              //执行查询,返回不能查到出库订单的 客户订单号
	              $.post(baseUrl + '/warehouse/storage/executeSearchOutWarehouseOrder.do', {
	            	  nos:nos,
	            	  noType:noType
	              },
	              function(msg) {
	            	  var  allNos = msg.allNos;
	            	  if(msg.status =='0'){
	            			parent.$.showDialogMessage(msg.message, null, null);
	            	  }
	            	  if(msg.status =='1'){
	            		  if(msg.unAbleNoCount !='0'){
	            			  var  str = ('一共查询到'+msg.orderCount+'个订单,' +msg.unAbleNoCount+ '个单号不能查询到订单,它们是:<br/>'+msg.unAbleNos+'.');
	            			  if(str.length>100){
		            			 var contentArr = [];
		             			 contentArr.push('<p style="width:500px;height:100%;margin-left:2mm;margin-top:2mm;word-break:break-all;">');
		             			 contentArr.push('<b>'+str+'</b>');
		             		     contentArr.push('</p>');
		             		     var contentHtml = contentArr.join('');
		             			 $.dialog({
		             		  		lock: true,
		             		  		max: false,
		             		  		min: false,
		             		  		title: '提示',
		             		  		width: 550,
		             		  		height: 350,
		             		  		content: contentHtml,
		             		  		button: [{
		             		  			name: '确认',
		             		  			callback: function() {
		             		  				
		             		  			}
		             		  		}]
		             			  });
		            		  }else{
		            			  parent.$.showDialogMessage(str, null, null);
		            		  }
	            		  }
	            		  if(msg.unAbleNoCount == '0'){
	            			  var str = ('一共查询到'+msg.orderCount+'个订单,没有单号查询不到订单.');
	            			  parent.$.showShortMessage({msg:str,animate:false,left:"45%"});
	            		  }
	            		  //对 searchfrom 赋值, 执行查询
	            		  $("#noType").val(noType);
	            		  $("#nos").val(allNos);
	            		  btnSearch("#searchform",grid);
	            		  //查询完成后把隐藏的条件去掉
	            		  $("#noType").val("");
	            		  $("#nos").val("");
	            	 }
	              },"json");
	            }
	          }],
	          cancel: true
	        });
}
 