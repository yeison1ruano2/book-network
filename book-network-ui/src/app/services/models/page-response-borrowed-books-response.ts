/* tslint:disable */
/* eslint-disable */
import { BorrowedBooksResponse } from '../models/borrowed-books-response';
export interface PageResponseBorrowedBooksResponse {
  content?: Array<BorrowedBooksResponse>;
  first?: boolean;
  last?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
