<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%--<%@ include file="/WEB-INF/views/modules/cms/front/include/taglib.jsp"%>--%>
<!DOCTYPE html>
<html>
<head>
	<title>全站搜索</title>

	<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
	<script src="${ctxStatic}/jquery/jquery-migrate-1.1.1.min.js" type="text/javascript"></script>
	<link href="${ctxStatic}/bootstrap/2.3.1/css_${not empty cookie.theme.value ? cookie.theme.value : 'cerulean'}/bootstrap.min.css" type="text/css" rel="stylesheet" />
		<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
		<!--[if lte IE 6]><link href="${ctxStatic}/bootstrap/bsie/css/bootstrap-ie6.min.css" type="text/css" rel="stylesheet" />
		<script src="${ctxStatic}/bootstrap/bsie/js/bootstrap-ie.min.js" type="text/javascript"></script><![endif]-->
		<link href="${ctxStatic}/common/jeesite.min.css" type="text/css" rel="stylesheet" />
		<link href="${ctxStaticTheme}/style.css" type="text/css" rel="stylesheet" />
		<script src="${ctxStaticTheme}/script.js" type="text/javascript"></script>

	<style type="text/css">
		form.search{margin:12px 20px 5px;} .page{margin:20px;}
		form.search input.txt{padding:3px;font-size:16px;width:300px;margin:5px;}
		form.search select.txt{padding:3px;font-size:16px;width:308px;margin:5px;}
		form.search input.txt.date{width:133px;}
		form.search .sel{margin-bottom:8px;padding:0 0 10px 5px;border-bottom:1px solid #efefef;font-size:14px;} form.search .act{font-weight:bold;}
		form.search .btn{padding:3px 18px;*padding:1px 0 0;font-size:16px;}
		dl.search{line-height:25px;border-bottom:1px solid #efefef;margin:10px 20px 20px;}
		dl.search dt{border-top:1px solid #efefef;padding:8px 5px 0px;font-size:16px;}
		dl.search dt a.title{color:#0000cc;text-decoration:underline;}
		dl.search dd{margin:0 5px 10px;font-size:14px;color:#555}
		dl.search dd span,dl.search dd a{font-size:12px;color:#008000;}
		dl.search .highlight{color:#DF0037;}
		dl.search dd span.highlight{color:#DF0037;font-size:14px;}
		dl.search dd span.info span.highlight{color:#DF0037;font-size:13px;}
	</style>
	<script type="text/javascript">
        function page(n,s){
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }
	</script>
</head>
<body>
<%--<form:checkboxes path="test" items="${specialtys}" itemLabel="lable" itemValue="value" htmlEscape="false" class="required"/>--%>
	<form:form id="searchForm" method="get" class="search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>

				<table>
					<tr>
						<td>
							<input type="text" name="key" value="${key}" class="txt"/>
						    <input type="submit" value="搜  索" class="btn"/>
						</td>
					</tr>
					<tr>
						<td>
						<label>专业：</label>


								<c:forEach items="${specialtys}" var="item" >
								         <c:set var="flag" value="false" />
										<c:forEach items="${checkedSpecialtys}" var="itemTwo" >
							                 <c:if test="${item.value ==itemTwo}">
								               <c:set var="flag" value="true" />
										       <input type="checkbox" name="specialtys" value="${item.value}" checked="checked" />${item.label}
							                 </c:if>
							            </c:forEach>
								         <c:if test="${flag ==false}">
							                <input type="checkbox" name="specialtys" value="${item.value}" />${item.label}
							             </c:if>
							    </c:forEach>
						<td>
					</tr>
					<tr>
						<td>
							<label>种类：</label>
							<c:forEach items="${types}" var="item" >
								<c:set var="flag" value="false" />
							<c:forEach items="${checkedTypes}" var="itemTwo" >
							<c:if test="${item.value ==itemTwo}">
								<c:set var="flag" value="true" />
							<input type="checkbox" name="types" value="${item.value}" checked="checked" />${item.label}
							</c:if>
							</c:forEach>
							<c:if test="${flag ==false}">
							<input type="checkbox" name="types" value="${item.value}" />${item.label}
							</c:if>
							</c:forEach>
						<td>
					</tr>
				</table>
	</form:form>
	<dl class="search">
		<c:if test="${fn:length(page.list) != 0}">
			<c:forEach items="${page.list}" var="article">
				<dt><a a href="${pageContext.request.contextPath}${fns:getFrontPath()}/view-${article.category.id}-${article.id}${fns:getUrlSuffix()}" target="_blank" class="title" >${article.title}</a></dt>
				<dd>${article.description}<span class="info"><br/>发布者：${article.createBy.name} &nbsp; 点击数：${article.hits} &nbsp; 发布时间：<fmt:formatDate value="${article.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/> &nbsp; 更新时间：<fmt:formatDate value="${article.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
					&nbsp;&nbsp;<a href="${pageContext.request.contextPath}${fns:getFrontPath()}/view-${article.category.id}-${article.id}${fns:getUrlSuffix()}" target="_blank">查看全文</a><br/></dd>
			</c:forEach>
			<%--<c:if test="${fn:length(page.list) <page.pageSize}">

				<div style="height:${(page.pageSize-fn:length(page.list))*50}px"></div>

			</c:if>--%>
		</c:if>
		<c:if test="${fn:length(page.list) eq 0}">
			<dt><c:if test="${empty q}">请键入要查找的关键字。</c:if><c:if test="${not empty q}">抱歉，没有找到与“${q}”相关内容。</c:if><br/><br/>建议：</dt>
			<dd><ul><li>检查输入是否正确；</li><li>简化输入词；</li><%--<li>尝试其他相关词，如同义、近义词等。</li>--%></ul></dd>
		</c:if>
	</dl>
	<c:if test="${fn:length(page.list) != 0}">
	<div class="pagination">${page}</div>
	</c:if>

</body>
</html>