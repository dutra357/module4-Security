package com.dutra.dsCatalog.repositories.projections;

import com.dutra.dsCatalog.utils.projection.IdProjection;

public interface ProductProjection extends IdProjection<Long> {

    String getName();
}
