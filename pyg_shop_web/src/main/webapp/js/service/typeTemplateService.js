app.service("typeTemplateService",function($http){
	
//	根据模板id查询规格数据 规格数据的格式：[{"id":27,"text":"网络",options:[{},{},{}]},{"id":32,"text":"机身内存"}]
	this.findSpecList=function(id){
		return $http.get("../typeTemplate/findSpecList?id=" + id);
	}
	
	this.findAll=function(){
		return $http.get("../typeTemplate/findAll");
	}
	
	 
	
	this.findOne=function(id){
		return $http.get("../typeTemplate/findOne?id="+id);
	}
	
})