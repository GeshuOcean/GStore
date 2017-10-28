package com.gstore.service;

import com.gstore.common.ServerResponse;
import com.gstore.pojo.Category;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by Ocean .
 */
public interface ICategoryService {
    public ServerResponse addCategory(String categoryName, Integer parentId);

    public ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
