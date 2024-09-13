import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import {
  BookResponse,
  PageResponseBookResponse,
} from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css'],
})
export class BookListComponent implements OnInit {
  bookResponse: PageResponseBookResponse = {};
  page: number = 0;
  size: number = 4;
  message: string = '';
  level: string = 'success';

  constructor(private bookService: BookService, private router: Router) {}

  ngOnInit(): void {
    this.findAllBooks();
  }
  private findAllBooks() {
    this.bookService
      .findAllBooks({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (books) => {
          this.bookResponse = books;
        },
      });
  }

  gotToFirstPage() {
    this.page = 0;
    this.findAllBooks();
  }

  gotToPreviousPage() {
    this.page--;
    this.findAllBooks();
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllBooks();
  }

  gotToNextPage() {
    this.page++;
    this.findAllBooks();
  }

  gotToLastPage() {
    this.page = (this.bookResponse.totalPages as number) - 1;
    this.findAllBooks();
  }

  get isLastPage(): boolean {
    return this.page == (this.bookResponse.totalPages as number) - 1;
  }

  borrowBook(book: BookResponse) {
    this.message = '';
    this.bookService
      .borrowBook({
        'book-id': book.id as number,
      })
      .subscribe({
        next: () => {
          this.level = 'success';
          this.message = 'Libro agregado con exito en tu lista de libros';
        },
        error: (error) => {
          console.log(error);
          this.level = 'error';
          this.message = error.error.error;
        },
      });
  }
}
