package ru.andreev.clothsshop.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.andreev.clothsshop.model.Product;

public class ProductSpecification {

    public static Specification<Product> hasColor(String color) {
        return (root, query, criteriaBuilder) ->
                color == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.join("colors").get("name"), color);
    }

    public static Specification<Product> hasSize(String size) {
        return (root, query, criteriaBuilder) ->
                size == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.join("sizes").get("name"), size);
    }

    public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            } else if (minPrice != null && maxPrice == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (minPrice == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            } else {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
        };
    }

    public static Specification<Product> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category == null ? criteriaBuilder.conjunction() : criteriaBuilder.equal(root.join("category").get("name"), category);
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? criteriaBuilder.conjunction() : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
}