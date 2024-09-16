import { Component, OnInit } from '@angular/core';
import {
  BorrowedBooksResponse,
  FeedbackRequest,
  PageResponseBorrowedBooksResponse,
} from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-borrowed-book-list',
  templateUrl: './borrowed-book-list.component.html',
  styleUrls: ['./borrowed-book-list.component.css'],
})
export class BorrowedBookListComponent implements OnInit {
  borrowedBooks: PageResponseBorrowedBooksResponse = {};
  feedbackRequest: FeedbackRequest = { bookId: 0, comment: '', note: 0 };
  page: number = 0;
  size: number = 5;
  selectedBook: BorrowedBooksResponse | undefined = undefined;
  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ) {}
  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }
  findAllBorrowedBooks() {
    this.bookService
      .findAllBorrowedBooks({
        page: this.page,
        size: this.size,
      })
      .subscribe({
        next: (res) => {
          this.borrowedBooks = res;
        },
      });
  }

  returnBorrowedBook(book: BorrowedBooksResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  gotToFirstPage() {
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  gotToPreviousPage() {
    this.page--;
    this.findAllBorrowedBooks();
  }

  gotToPage(page: number) {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  gotToNextPage() {
    this.page++;
    this.findAllBorrowedBooks();
  }

  gotToLastPage() {
    this.page = (this.borrowedBooks.totalPages as number) - 1;
    this.findAllBorrowedBooks();
  }

  get isLastPage(): boolean {
    return this.page == (this.borrowedBooks.totalPages as number) - 1;
  }

  returnBook(withFeedaback: boolean) {
    this.bookService
      .returnBorrowBook({
        'book-id': this.selectedBook?.id as number,
      })
      .subscribe({
        next: () => {
          if (withFeedaback) {
            this.giveFeedback();
          }
          this.selectedBook = undefined;
          this.findAllBorrowedBooks();
        },
      });
  }
  giveFeedback() {
    this.feedbackService
      .saveFeedback({
        body: this.feedbackRequest,
      })
      .subscribe({
        next: () => {},
      });
  }
}
