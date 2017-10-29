package com.gstore.service;

import com.github.pagehelper.PageInfo;
import com.gstore.common.ServerResponse;
import com.gstore.pojo.Product;
import com.gstore.vo.ProductDetailVo;

/**
 * Created by Ocean .
 */
public interface IProductService {
    public ServerResponse saveOrUpdateProduct(Product product);
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    public ServerResponse<PageInfo>getProductByKeywordCatgeory(String keyword, Integer categoryId, int pageNum, int pageSize,String orderBy);
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
}
