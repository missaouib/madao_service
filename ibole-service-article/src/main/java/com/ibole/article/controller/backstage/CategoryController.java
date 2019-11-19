package com.ibole.article.controller.backstage;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ibole.annotation.OptLog;
import com.ibole.article.service.backstage.CategoryService;
import com.ibole.constant.CommonConst;
import com.ibole.enums.StatusEnum;
import com.ibole.pojo.QueryVO;
import com.ibole.pojo.article.Category;
import com.ibole.utils.JsonData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 文章分类
 **/

@Api(tags = "文章分类")
@RestController
@RequestMapping(value = "/article/category", produces = "application/json")
public class CategoryController {

	private final CategoryService categoryService;

	@Autowired
	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * 查询全部数据
	 *
	 * @return JsonData
	 */
	@GetMapping
	public JsonData findCategoryByCondition(Category category, QueryVO queryVO ) {
		IPage<Category> categoryByCondition = categoryService.findCategoryByCondition(category,queryVO);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg(), categoryByCondition);
	}

	/**
	 * 根据ID查询
	 *
	 * @param id ID
	 * @return JsonData
	 */
	@GetMapping(value = "/{id}")
	public JsonData findCategoryByPrimaryKey(@PathVariable String id) {
		Category category = categoryService.findCategoryByPrimaryKey(id);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg(), category);
	}


	/**
	 * 增加
	 */
	@ApiOperation(value = "添加一条新的分类", notes = "id")
	@PostMapping
	@OptLog(operationType= CommonConst.ADD,operationName="添加文章分类")
	public JsonData insertCategory(@RequestBody @Valid Category category) {
		categoryService.insertCategory(category);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg());
	}

	/**
	 * 修改
	 */
	@PutMapping
	@OptLog(operationType= CommonConst.MODIFY,operationName="修改文章分类")
	public JsonData updateByCategorySelective(@RequestBody @Valid Category category) {
		categoryService.updateByCategorySelective(category);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg());
	}

	/**
	 * 删除
	 *
	 * @param categoryIds :分类id数组
	 */
	@DeleteMapping
	@OptLog(operationType= CommonConst.DELETE,operationName="删除文章分类")
	public JsonData deleteCategoryByIds(@RequestBody List<String> categoryIds) {
		categoryService.deleteCategoryByIds(categoryIds);
		return new JsonData(true, StatusEnum.OK.getCode(), StatusEnum.OK.getMsg());
	}

}