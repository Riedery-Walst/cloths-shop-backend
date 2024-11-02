package ru.andreev.clothsshop.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.andreev.clothsshop.model.Product;

import java.util.List;

public class ProductSpecification {

    public static Specification<Product> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> categoryId == null ? cb.conjunction() : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasColorIds(List<Long> colorIds) {
        return (root, query, cb) -> colorIds == null || colorIds.isEmpty() ? cb.conjunction() : root.join("colors").get("id").in(colorIds);
    }

    public static Specification<Product> hasSizeIds(List<Long> sizeIds) {
        return (root, query, cb) -> sizeIds == null || sizeIds.isEmpty() ? cb.conjunction() : root.join("sizes").get("id").in(sizeIds);
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? criteriaBuilder.conjunction() : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
}