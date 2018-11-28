app.controller("typeTemplateController",function($scope,$controller,specificationService,brandService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
	 $scope.search=function(pageNo,pageSize){
		 typeTemplateService.search(pageNo,pageSize,$scope.searchEntity).success(function(response){
			 $scope.paginationConf.totalItems = response.total;
			 $scope.list = response.rows;
		 })
	 }

	 
	 $scope.findBrandList=function () {
         brandService.findBrandList().success(function (response) {
             // response:[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
             $scope.brandList={data:response};
         })
     }
	 $scope.findSpecList=function () {
         specificationService.findSpecList().success(function (response) {
             $scope.specList={data:response};
         })
     }
     
     $scope.addCustomAttributeItems=function () {
		 $scope.entity.customAttributeItems.push({});
     }

     $scope.deleCustomAttributeItems=function (index) {
		 $scope.entity.customAttributeItems.splice(index,1);
     }

	 // $scope.brandList={data:[{"id":1,"text":"联想"},{"id":2,"text":"华为"},{"id":3,"text":"三星"},
      //   {"id":4,"text":"小米"},{"id":5,"text":"OPPO"},{"id":6,"text":"360"},
      //   {"id":7,"text":"中兴"},{"id":8,"text":"魅族"}]};


    // [{"firstChar":"L","id":1,"name":"联想"},{"firstChar":"H","id":2,"name":"HUAWEI"},{"firstChar":"S","id":3,"name":"三星"}]
	 
	 $scope.save=function(){
		 var obj=null;
		 if($scope.entity.id!=null){
			 obj = typeTemplateService.update($scope.entity) ;
		 }else{
			 obj = typeTemplateService.add($scope.entity) ;
		 }
		 obj.success(function(response){
			 if(response.success){
				 $scope.reloadList();
			 }else{
				 alert(response.message);
			 }
			 
		 })
	 }
	 

	 
	 $scope.findOne=function(id){
		 typeTemplateService.findOne(id).success(function(response){
			 response.brandIds = JSON.parse(response.brandIds);
			 response.specIds = JSON.parse(response.specIds);
			 response.customAttributeItems = JSON.parse(response.customAttributeItems);

			 $scope.entity = response;
		 })
	 }

	 // 把数组变成字符串 [{"id":32,"text":"希乐"},{"id":33,"text":"富光"}]---->希乐,富光

	$scope.arrayToString=function (array) {
        array = JSON.parse(array);
	 	var str="";
        for (var i = 0; i < array.length; i++) {
        	if(i==array.length-1){
                str+= array[i].text;
			}else{
                str+= array[i].text+",";
			}
        }
        return str;
    }
	
})