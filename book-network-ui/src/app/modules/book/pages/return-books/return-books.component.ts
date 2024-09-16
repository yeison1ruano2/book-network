import { Component, OnInit } from '@angular/core';
import {
  BorrowedBooksResponse,
  FeedbackRequest,
  PageResponseBorrowedBooksResponse,
} from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-return-books',
  templateUrl: './return-books.component.html',
  styleUrls: ['./return-books.component.css'],
})
export class ReturnBooksComponent implements OnInit {
  returnedBooks: PageResponseBorrowedBooksResponse = {};
  page: number = 0;
  size: number = 5;
  message: string = '';
  level = 'success';
  constructor(private bookService: BookService) {}
  ngOnInit(): void {
    this.findAllReturnedBooks();
  }
  findAllReturnedBooks() {
    this.bookService
      .findAllBooksByOwner({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (res) => {
          this.returnedBooks = res;
        },
      });
  }

  gotToFirstPage() {
    this.page = 0;
    this.findAllReturnedBooks();
  }

  gotToPreviousPage() {
    this.page--;
    this.findAllReturnedBooks();
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllReturnedBooks();
  }

  gotToNextPage() {
    this.page++;
    this.findAllReturnedBooks();
  }

  gotToLastPage() {
    this.page = (this.returnedBooks.totalPages as number) - 1;
    this.findAllReturnedBooks();
  }

  get isLastPage(): boolean {
    return this.page == (this.returnedBooks.totalPages as number) - 1;
  }

  approveBookReturn(book: BorrowedBooksResponse) {
    if (!book.returned) {
      this.level = 'error';
      this.message = 'El libro no ha sido devuelto';
      return;
    }
    this.bookService
      .approveReturnBorrowBook({
        'book-id': book.id as number,
      })
      .subscribe({
        next: () => {
          this.level = 'success';
          this.message = 'Devolución de libro aprobada';
          this.findAllReturnedBooks();
        },
      });
  }
}
