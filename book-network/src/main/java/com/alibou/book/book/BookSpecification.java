package com.alibou.book.book;

import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {
  private BookSpecification(){
    throw new UnsupportedOperationException("Utility class");
  }
  public static Specification<Book> withOwnerId(Integer ownerId){
    return (root,query,criteriaBuilder)-> criteriaBuilder.equal(root.get("owner").get("id"),ownerId);
  }
}
