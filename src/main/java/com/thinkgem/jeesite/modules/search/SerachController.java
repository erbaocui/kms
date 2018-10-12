package com.thinkgem.jeesite.modules.search;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.cms.entity.Article;
import com.thinkgem.jeesite.modules.cms.service.EsService;

import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.thinkgem.jeesite.modules.sys.utils.DictUtils.getDictList;


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
	public String list(String key, Integer[] specialtys , Integer[] types, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		long start = System.currentTimeMillis();
		Page<Article> page=new Page<Article>(request,response);
		if(!StringUtils.isEmpty(key)){
			Page<Article> p =  esService.restSearch(page, key, types,specialtys);
			p.setMessage("匹配结果，共耗时 " + (System.currentTimeMillis() - start) + "毫秒。");
			model.addAttribute("page",p);
			session.setAttribute("articleList", p.getList());
		}
		List<Dict> specialtyList=DictUtils.getDictList("specialty");
		List<Dict> typeList=DictUtils.getDictList("type");
		//model.addAttribute("page",p);
		model.addAttribute("specialtys",specialtyList);
		model.addAttribute("types", typeList);
		model.addAttribute("checkedSpecialtys",specialtys);
		model.addAttribute("checkedTypes", types);
		model.addAttribute("key", key);
		return "modules/search/searchList";
	}






}
