app.controller("goodsEditController",function($scope,uploadService,goodsService,itemCatService,typeTemplateService){

	$scope.entity={tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}};

	//查询所有的一级分类数据
	$scope.findCategory1List=function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.category1List=response;
        })
    }

    // 监测一级分类数据的变化 相当于onChange事件
	//查询某一个一级分类数据下的二级分类数据
	$scope.$watch("entity.tbGoods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category2List=response;
        })
    })

    // 监测二级分类数据的变化 相当于onChange事件
    //查询某一个二级分类数据下的三级分类数据
    $scope.$watch("entity.tbGoods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.category3List=response;
        })
    })

    // 监测三级分类数据的变化 相当于onChange事件
    //查询某一个三级分类数据 可以获取模板id
    $scope.$watch("entity.tbGoods.category3Id",function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            // response：代表一个分类对象
           $scope.entity.tbGoods.typeTemplateId = response.typeId;
        })
    })

    // 监测模板id数据的变化 相当于onChange事件
    //查询某一个模板数据 可以获取模板中的品牌、规格、扩展属性
    $scope.$watch("entity.tbGoods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            // response：代表一个模板对象
            $scope.brandList= JSON.parse( response.brandIds); //[{"id":32,"text":"希乐"},{"id":33,"text":"富光"}]
            $scope.entity.tbGoodsDesc.customAttributeItems= JSON.parse( response.customAttributeItems); //[{"text":"内存大小"},{"text":"颜色"}]
            <!--entity.tbGoodsDesc.customAttributeItems=[{"text":"内存大小","value":"40M"},{"text":"颜色","value":"透明"}]-->
            <!--[{"text":"内存大小"},{"text":"颜色"}]-->
        })
        // 根据模板id 获取页面上需要的规格数据：
        //     [{id:27,text:'网络',options:[{optionName:'移动3G'},{optionName:'移动4G'}]},
        //     {id:32:''机身内存，options:[{optionName:'16G'},{optionName:'32G'}]}]
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;
        })

    })


	$scope.save=function () {

        $scope.entity.tbGoodsDesc.introduction= editor.html();
        goodsService.add($scope.entity).success(function (response) {
            if (response.success) {
                location.href="goods.html";
            } else {
                alert(response.message);
            }
        })


    }


    $scope.uploadFile=function () {
        uploadService.uploadFile().success(function (response) {
            if(response.success){
                // 上传成功后 获取图片路径 放到imageUrl
                //      {success:true,message:"192.168.25.133/1298863/fdg/dfa.jpg"}
                $scope.image.url=response.message;
            }else{
                alert(response.message);
            }
        })
    }

    $scope.addItemImage=function () {
        // 向 $scope.entity.tbGoodsDesc.itemImages 数组中追加对象 $scope.image
        $scope.entity.tbGoodsDesc.itemImages.push($scope.image);
    }

    $scope.deleItemImages=function (index) {
        // 从 $scope.entity.tbGoodsDesc.itemImages 数组中移除对象
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }


    // $scope.tbGoodsDesc.specificationItems=
    //     [{attributeName:'网络',attributeValue:['移动4G','联通4G']},
    //         {attributeName:'机身内存',attributeValue:['32G','64G']}]
    // 通过勾选规格小项拼凑表中需要保存的格式
    // key value: 网络 移动4G   机身内存 32G
    $scope.updateSpecificationItems=function(event,key,value){
        // 先判断即将追加的value值 在大数组中是否有一个对象和key对应
        var specItem = findObjectFromArray($scope.entity.tbGoodsDesc.specificationItems,key);
        if(event.target.checked){
            if(specItem==null){
                $scope.entity.tbGoodsDesc.specificationItems.push({attributeName:key,attributeValue:[value]});
            }else{
                specItem.attributeValue.push(value);
            }
        }else{
            var index = specItem.attributeValue.indexOf(value);
            specItem.attributeValue.splice(index,1);
            // 还需要判断specItem.attributeValue是否为空 ，如果为空应该把specItem从大数组中移除
            if(specItem.attributeValue.length==0){
                var _index=$scope.entity.tbGoodsDesc.specificationItems.indexOf(specItem);
                $scope.entity.tbGoodsDesc.specificationItems.splice(_index,1);
            }
        }
        createItemList();
    }


    function findObjectFromArray(specificationItems,key) {
        for (var i = 0; i < specificationItems.length; i++) {
            if(  specificationItems[i].attributeName==key){
                return specificationItems[i];
            }
        }
        return null;
    }
    // 动态生成sku列表   entity.itemList
    function createItemList() {
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:"1",isDefault:'0'}];
        var specItems = $scope.entity.tbGoodsDesc.specificationItems;
        //     [{attributeName:'网络',attributeValue:['移动4G','联通4G']},
        //         {attributeName:'机身内存',attributeValue:['32G','64G']}]
        for (var i = 0; i < specItems.length; i++) {
            $scope.entity.itemList = addColumn($scope.entity.itemList,specItems[i].attributeName,specItems[i].attributeValue);
        }
    }
    function addColumn(itemList,attributeName,attributeValue) {
        var newItemList=[];
        for (var i = 0; i < itemList.length; i++) {
            for (var j = 0; j < attributeValue.length; j++) {
                var newRow = JSON.parse(JSON.stringify(itemList[i]));
                newRow.spec[attributeName]=attributeValue[j];    //---{网络：移动4G}
                newItemList.push(newRow);
            }
        }
        return newItemList;
    }


})