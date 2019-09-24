package com.cutter.point.blog.web.restapi;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.utils.WebUtils;
import com.cutter.point.blog.web.feign.PictureFeignClient;
import com.cutter.point.blog.web.global.SQLConf;
import com.cutter.point.blog.web.global.SysConf;
import com.moxi.mogublog.xo.entity.ResourceSort;
import com.moxi.mogublog.xo.entity.StudyVideo;
import com.moxi.mogublog.xo.service.ResourceSortService;
import com.moxi.mogublog.xo.service.StudyVideoService;
import com.moxi.mougblog.base.enums.EStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 学习教程 RestApi
 * @author xzx19950624@qq.com
 * @date 2018年10月21日上午11:04:11
 */
@RestController
@RequestMapping("/resource")
@Api(value="学习教程 RestApi",tags={"ResourceRestApi"})
public class ResourceRestApi {
	
	@Autowired
	private ResourceSortService resourceSortService;
	
	@Autowired
	private StudyVideoService studyVideoService;
	
	@Autowired
	private PictureFeignClient pictureFeignClient;
	
	private static Logger log = LogManager.getLogger(ResourceRestApi.class);
	
	@ApiOperation(value="通过分类来获取视频", notes="通过Uid获取博客内容")
	@GetMapping("/getStudyVideoBySort")
	public String getBlogByUid (HttpServletRequest request,
			@ApiParam(name = "resourceSortUid", value = "资源分类UID", required = false) @RequestParam(name = "resourceSortUid", required = false) String resourceSortUid,
			@ApiParam(name = "currentPage", value = "当前页数",required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
			@ApiParam(name = "pageSize", value = "每页显示数目",required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "8") Long pageSize) {
		
		QueryWrapper<StudyVideo> queryWrapper = new QueryWrapper<>();
		Page<StudyVideo> page = new Page<StudyVideo>();
		page.setCurrent(currentPage);
		page.setSize(pageSize);
		queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
		queryWrapper.orderByDesc(SQLConf.CLICK_COUNT); //按点击数降序排列
		if(!StringUtils.isEmpty(resourceSortUid)) {
			queryWrapper.eq(SQLConf.RESOURCE_SORT_UID, resourceSortUid);	
		}
		IPage<StudyVideo> pageList = studyVideoService.page(page, queryWrapper);
		List<StudyVideo> list = pageList.getRecords();
		
		//获取所有的分类
		Set<String> resourceSortUids = new HashSet<>();
		String fileIds = "";
		for(StudyVideo item : list) {
			if(StringUtils.isNotEmpty(item.getResourceSortUid())) {
				resourceSortUids.add(item.getResourceSortUid());				
			}
			if(StringUtils.isNotEmpty(item.getFileUid())) {
				fileIds = fileIds + item.getFileUid() + ",";
			}
		}
		//PictureList
		String result = this.pictureFeignClient.getPicture(fileIds, ",");
		List<Map<String, Object>> picList = WebUtils.getPictureMap(result);
		
		//ResourceSort
		Collection<ResourceSort> resourceSortList = resourceSortService.listByIds(resourceSortUids);
		for(StudyVideo item : list) {
			List<String> photoList = new ArrayList<String>();
			for(ResourceSort item2 :resourceSortList) {
				if(item.getResourceSortUid().equals(item2.getUid())) {
					item.setResourceSort(item2);
					break;
				}				
			}
			
			for(Map<String, Object> map : picList) {
				//因为资源可能有多个图片
				String fileUid = item.getFileUid();
				List<String> fileUids = StringUtils.changeStringToString(fileUid, ",");				
				for(String uid : fileUids) {
					if(map.get("uid").toString().equals(uid)) {						
						photoList.add(map.get("url").toString());
					}
				}
				
			}
			item.setPhotoList(photoList);
		}
		
		log.info("返回结果");		
		return ResultUtil.result(SysConf.SUCCESS, pageList);
	}
	
}

