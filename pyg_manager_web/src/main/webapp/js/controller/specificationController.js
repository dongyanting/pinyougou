//只和html交互 不和 后台交互
app.controller("specificationController",function($scope,$controller, specificationService){  //specificationService注入到了controller中
	
//			当前的specificationController继承baseController
			$controller('baseController',{$scope:$scope});//继承 继承的本质：公用一个$scope

			
			
			
			
			$scope.search=function(pageNo,pageSize){
//				当前页码   每页显示的条数   查询的对象$scope.searchEntity
				specificationService.search(pageNo,pageSize,$scope.searchEntity).success(function(response){
//	             	response {total:100,  rows:[{},{},{}]}  当前页数据List   总条数
	            	$scope.paginationConf.totalItems = response.total;
	            	$scope.list = response.rows;
	            })       	 
			}
			
			
	         	
			 
			 $scope.save=function(){
				 var obj=null;
//				 entity 组合类
				 if($scope.entity.tbSpecification.id!=null){
					 obj = specificationService.update($scope.entity);
				 }else{
					 obj =  specificationService.add($scope.entity); 
				 }
				 obj.success(function(response){
//	 				 response  {success:true|false,message:"保存成功"|"保存失败"}
	               if(response.success){
//	    	    	    成功后刷新列表数据  
	 					 $scope.reloadList()
	               }else{
	            	   alert(response.message);
	               }
			    })
			 }
			 
//	 		 修改时先根据id查询
			 $scope.findOne=function(id){
				specificationService.findOne(id).success(function(response){
	                $scope.entity=response;
			    })
			 }
			 
				
			$scope.addTbSpecificationOptions=function(){
				$scope.entity.tbSpecificationOptionList.push({});
			}
			
			$scope.deleTbSpecificationOptions=function(index){
                $scope.entity.tbSpecificationOptionList.splice(index,1);
			}
		   
//	 	   删除
		   $scope.dele=function(){
		        if($scope.selectIds.length==0){
		        	 alert("请选择您需要删除的数据");
		        	 return;
		        }
		        var flag = window.confirm("确认要删除您选中的数据吗?");
		        if(flag){
		 		  specificationService.dele($scope.selectIds).success(function(response){
		             if(response.success){
//		  	    	    成功后刷新列表数据  
	                     $scope.selectIds=[];
						 $scope.reloadList();
		             }else{
		          	   alert(response.message);
		             }
				    })
		        }
		   }
															
	    })
