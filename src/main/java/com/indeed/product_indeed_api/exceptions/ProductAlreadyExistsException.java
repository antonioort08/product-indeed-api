package com.indeed.product_indeed_api.exceptions;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String barcode) {
        super("A product with barcode " + barcode + " already exists.");
    }
}
