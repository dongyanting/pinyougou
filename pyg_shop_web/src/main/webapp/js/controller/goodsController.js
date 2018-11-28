 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService   ,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
//	商品的审核状态 0：未审核  1：已通过   2：未通过
	$scope.status=['未审核','已通过','未通过'];
	$scope.markets=['未上架','已上架','已下架'];
	$scope.itemCat={};
//	$scope.itemCat={'558':'手机' ,'559':'手机通讯','560':'手机','1205':'一级测试数据','1207':'二级测试数据','1208':'三级测试数据'};
	$scope.findAllItemCat=function(){
		itemCatService.findAll().success(function(response){
//			异步
//			response=[{"id":1,"name":"图书、音像、电子书刊","parentId":0,"typeId":35},
//			{"id":2,"name":"电子书刊","parentId":1,"typeId":35}]
			for (var i = 0; i < response.length; i++) {
				$scope.itemCat[response[i].id] = response[i].name;
			}
//			alert($scope.itemCat);
		})
	}
//	修改上下架状态
	$scope.updateMarketable=function(market){
		goodsService.updateIsMarketable($scope.selectIds,market).success(function(response){
			if(response.success){
				$scope.reloadList();
				$scope.selectIds=[];
			}else{
				alert(response.message);
			}
		})
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
