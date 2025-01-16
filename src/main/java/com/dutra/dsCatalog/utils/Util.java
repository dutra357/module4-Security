package com.dutra.dsCatalog.utils;

import com.dutra.dsCatalog.utils.projection.IdProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
    public static <I> List<? extends IdProjection<I>> replace(List<? extends IdProjection<I>> orderedContent,
                                                              List<? extends IdProjection<I>> unorderedList) {

        Map<I, IdProjection<I>> map = new HashMap<>();
        for (IdProjection<I> entity : unorderedList) {
            map.put(entity.getId(), entity);
        }
        List<IdProjection<I>> result = new ArrayList<>();

        for (IdProjection<I> entity : orderedContent) {
            result.add(map.get(entity.getId()));
        }
        return result;
    }
}
