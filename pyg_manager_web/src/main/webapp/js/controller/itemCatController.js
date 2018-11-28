 //控制层 
app.controller('itemCatController' ,function($scope,typeTemplateService ,itemCatService){	
	$scope.grade=1;  //此变量用来记录当前页面显示数据的层级 默认显示是第一级

	$scope.entity1=null; //记录面包屑上的一级对象
	$scope.entity2=null; //记录面包屑上的二级对象
	$scope.parentId=0; //记录的是即将添加对象的parentId
	//点击查询下级的方法
    $scope.setGrade=function(grade,pojo){
        $scope.grade=grade;

        if($scope.grade==1){
            $scope.entity1=null;
            $scope.entity2=null;
            $scope.parentId = 0;
        }
        if($scope.grade==2){
            $scope.entity1=pojo; //grade等于2 赋值
            $scope.entity2=null;
            $scope.parentId = $scope.entity1.id;
		}
        if($scope.grade==3){
            $scope.entity2=pojo; //grade等于3 赋值
            $scope.parentId = $scope.entity2.id;
        }
    }

	$scope.findByParentId=function (parentId) {
        itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list=response;
        })
    }
    
    $scope.findTypeTemplateAll=function () {
        typeTemplateService.findAll().success(function (response) {
			$scope.typeTemplateList=response;
        })
    }
	
 
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){		
//		保存或修改时需要对entity的parentId赋值

		var serviceObject;//服务层对象
		// 向$scope.entity中追加parentId属性并且赋值{name:"",typeId:''}----->{name:"",typeId:'',parentId:0}
        // 两种方法：
        // $scope.entity['parentId']=0;

        $scope.entity.parentId=$scope.parentId;

		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
//		        	$scope.reloadList();//重新加载
					$scope.findByParentId($scope.parentId);
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
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
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
