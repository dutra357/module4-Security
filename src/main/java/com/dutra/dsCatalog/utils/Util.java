package com.dutra.dsCatalog.utils;

import com.dutra.dsCatalog.entities.Product;
import com.dutra.dsCatalog.repositories.projections.ProductProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static List<Product> replace(List<ProductProjection> orderedContent, List<Product> unorderedList) {

        Map<Long, Product> map = new HashMap<>();
        for (Product product : unorderedList) {
            map.put(product.getId(), product);
        }
        List<Product> result = new ArrayList<>();

        for (ProductProjection entity : orderedContent) {
            result.add(map.get(entity.getId()));
        }
        return result;
    }
}
