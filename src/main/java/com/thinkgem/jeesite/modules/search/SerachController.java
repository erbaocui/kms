package com.thinkgem.jeesite.modules.search;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.service.EsService;

import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller
 * @author cuijp
 * @version 2018-8-19
 */
@Controller
@RequestMapping(value = "${adminPath}/search")
public class SerachController extends BaseController {
	@Autowired
	private EsService esService;

	@RequestMapping(value = {"searchList", ""})
	public String list(String key,  HttpServletRequest request, HttpServletResponse response, Model model) {
		long start = System.currentTimeMillis();
		Page<Article> page=new Page<Article>(request,response);
		page =  esService.search(page, key);
		page.setMessage("匹配结果，共耗时 " + (System.currentTimeMillis() - start) + "毫秒。");
		model.addAttribute("page", page);
		return "modules/search/searchList";
	}






}