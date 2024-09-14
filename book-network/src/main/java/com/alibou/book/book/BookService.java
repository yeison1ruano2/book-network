package com.alibou.book.book;

import com.alibou.book.common.PageResponse;
import com.alibou.book.exception.OperationNotPermittedException;
import com.alibou.book.file.FileStorageService;
import com.alibou.book.history.BookTransactionHistory;
import com.alibou.book.history.BookTransactionHistoryRepository;
import com.alibou.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {
  private final BookRepository bookRepository;
  private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
  private final BookMapper bookMapper;
  private final FileStorageService fileStorageService;

  public Integer save(BookRequest request, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Book book = bookMapper.toBook(request);
    book.setOwner(user);
    return bookRepository.save(book).getId();
  }

  public BookResponse findById(Integer bookId) {
    return bookRepository.findById(bookId)
            .map(bookMapper::toBookResponse)
            .orElseThrow(()-> new EntityNotFoundException("Libro no encontrado con el ID:" + bookId));
  }

  public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAllDisplayableBooks (pageable,user.getId());
    List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();
    return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
    );
  }

  public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
    Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()),pageable);
    List<BookResponse> bookResponse = books.stream()
            .map(bookMapper::toBookResponse)
            .toList();
    return new PageResponse<>(
            bookResponse,
            books.getNumber(),
            books.getSize(),
            books.getTotalElements(),
            books.getTotalPages(),
            books.isFirst(),
            books.isLast()
    );
  }

  public PageResponse<BorrowedBooksResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());
    List<BorrowedBooksResponse> bookResponse = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();
    return new PageResponse<>(
            bookResponse,
            allBorrowedBooks.getNumber(),
            allBorrowedBooks.getSize(),
            allBorrowedBooks.getTotalElements(),
            allBorrowedBooks.getTotalPages(),
            allBorrowedBooks.isFirst(),
            allBorrowedBooks.isLast()
    );
  }

  public PageResponse<BorrowedBooksResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
    User user = ((User)connectedUser.getPrincipal());
    Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
    Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());
    List<BorrowedBooksResponse> bookResponse = allBorrowedBooks.stream()
            .map(bookMapper::toBorrowedBookResponse)
            .toList();
    return new PageResponse<>(
            bookResponse,
            allBorrowedBooks.getNumber(),
            allBorrowedBooks.getSize(),
            allBorrowedBooks.getTotalElements(),
            allBorrowedBooks.getTotalPages(),
            allBorrowedBooks.isFirst(),
            allBorrowedBooks.isLast()
    );
  }

  public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes actualizar el estado de los libros para compartir");
    }
    book.setShareable(!book.isShareable());
    bookRepository.save(book);
    return bookId;
  }

  public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes actualizar el estado de los libros para archivar");
    }
    book.setArchived(!book.isArchived());
    bookRepository.save(book);
    return bookId;
  }

  public Integer borrowBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado ya que está archivado o no se puede compartir.");
    }
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes pedir prestado tu propio libro.");
    }
    final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowedByUser(bookId,user.getId());
    if(isAlreadyBorrowed){
      throw new OperationNotPermittedException("El libro solicitado ya está prestado");
    }
    BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
            .user(user)
            .book(book)
            .returned(false)
            .returnApproved(false)
            .build();
    return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
  }

  public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado ya que está archivado o no se puede compartir.");
    }
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes pedir prestado o devolver tu propio libro.");
    }
    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
            .orElseThrow(()-> new OperationNotPermittedException("No puedes compartir este libro"));
    bookTransactionHistory.setReturned(true);
    return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
  }

  public Integer approveReturnBorrowedBook(Integer bookId, Authentication connectedUser) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("El libro solicitado no se puede tomar prestado ya que está archivado o no se puede compartir.");
    }
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes pedir prestado o devolver tu propio libro.");
    }
    BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdOwnerId(bookId,user.getId())
            .orElseThrow(()-> new OperationNotPermittedException("El libro aún no ha sido devuelto. No puedes aprobar su devolución."));
    bookTransactionHistory.setReturnApproved(true);
    return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
  }

  public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
    Book book = bookRepository.findById(bookId)
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + bookId));
    User user = ((User)connectedUser.getPrincipal());
    String bookCover = fileStorageService.saveFile(file,bookId,user.getId());
    book.setBookCover(bookCover);
    bookRepository.save(book);
  }
}
