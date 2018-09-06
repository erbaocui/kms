<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>机构管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
   function addDeputy() {
       // 正常打开
       top.$.jBox.open("iframe:${ctx}/tag/treeselect?url="+encodeURIComponent("/sys/office/treeData?type=3")+"&module=${module}&checked=${checked}&extId=${extId}&isAll=${isAll}", "选择人员", 300, 420, {
           ajaxData:{selectIds: $("#${id}Id").val()},buttons:{"确定":"ok", ${allowClear?"\"清除\":\"clear\", ":""}"关闭":true}, submit:function(v, h, f){
               if (v=="ok"){
                   var tree = h.find("iframe")[0].contentWindow.tree;//h.find("iframe").contents();
                   var ids = [], names = [], nodes = [];
                   if ("${checked}" == "true"){
                       nodes = tree.getCheckedNodes(true);
                   }else{
                       nodes = tree.getSelectedNodes();
                   }
                   for(var i=0; i<nodes.length; i++) {//<c:if test="${checked && notAllowSelectParent}">
                       if (nodes[i].isParent){
                           continue; // 如果为复选框选择，则过滤掉父节点
                       }//</c:if><c:if test="${notAllowSelectRoot}">
                       if (nodes[i].level == 0){
                           top.$.jBox.tip("不能选择根节点（"+nodes[i].name+"）请重新选择。");
                           return false;
                       }//</c:if><c:if test="${notAllowSelectParent}">
                       if (nodes[i].isParent){
                           top.$.jBox.tip("不能选择父节点（"+nodes[i].name+"）请重新选择。");
                           return false;
                       }//</c:if><c:if test="${not empty module && selectScopeModule}">
                       if (nodes[i].module == ""){
                           top.$.jBox.tip("不能选择公共模型（"+nodes[i].name+"）请重新选择。");
                           return false;
                       }else if (nodes[i].module != "${module}"){
                           top.$.jBox.tip("不能选择当前栏目以外的栏目模型，请重新选择。");
                           return false;
                       }//</c:if>
                       ids.push(nodes[i].id);
                       names.push(nodes[i].name);//<c:if test="${!checked}">
                       break; // 如果为非复选框选择，则返回第一个选择  </c:if>
                   }
                   $("#${id}Id").val(ids.join(",").replace(/u_/ig,""));
                   $("#${id}Name").val(names.join(","));
               }//<c:if test="${allowClear}">
               else if (v=="clear"){
                   $("#${id}Id").val("");
                   $("#${id}Name").val("");
               }//</c:if>
               if(typeof ${id}TreeselectCallBack == 'function'){
                   ${id}TreeselectCallBack(v, h, f);
               }
           }, loaded:function(h){
               $(".jbox-content", top.document).css("overflow-y","hidden");
           }
       });
   }


	</script>
</head>
<body>
<sys:message content="${message}"/>
	<table class="table table-striped table-bordered table-condensed">
		<thead><tr><th>姓名</th><th>登录名</th><shiro:hasPermission name="sys:office:edit"><th>操作</th></shiro:hasPermission></tr></thead>
		<tbody>
	    <c:forEach var="item" items="${deputyList}"  varStatus="stat">
			 <tr>
				<td>${item.name}</td>
				<td>${item.loginName}</td>
				<shiro:hasPermission name="sys:office:edit">
					<td>
					<a href="${ctx}/sys/office/deleteDeputy?userId=${item.id}&officeId=${officeId}" onclick="return confirmx('要删除？', this.href)">删除</a>
					</td>
				</shiro:hasPermission>
			 </tr>
	    </c:forEach>
		</tbody>
	</table>
</body>
</html>