package com.softanime.storeapp.data.model.home

import com.softanime.storeapp.utils.CATEGORY_LAPTOP
import com.softanime.storeapp.utils.CATEGORY_MEN_SHOES
import com.softanime.storeapp.utils.CATEGORY_MOBILE_PHONE
import com.softanime.storeapp.utils.CATEGORY_STATIONERY

enum class ProductsCategories(val item : String) {
    MOBILE(CATEGORY_MOBILE_PHONE),
    SHOES(CATEGORY_MEN_SHOES),
    STATIONERY(CATEGORY_STATIONERY),
    LAPTOP(CATEGORY_LAPTOP)
}