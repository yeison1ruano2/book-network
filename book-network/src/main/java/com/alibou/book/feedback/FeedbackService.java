package com.alibou.book.feedback;

import com.alibou.book.book.Book;
import com.alibou.book.book.BookRepository;
import com.alibou.book.common.PageResponse;
import com.alibou.book.exception.OperationNotPermittedException;
import com.alibou.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
  private final BookRepository bookRepository;
  private final FeedbackMapper feedbackMapper;
  private final FeedbackRepository feedbackRepository;
  public Integer save(FeedbackRequest request, Authentication connectedUser) {
    Book book = bookRepository.findById(request.bookId())
            .orElseThrow(()-> new EntityNotFoundException("El libro no fue encontrado con el ID: " + request.bookId()));
    if(book.isArchived() || !book.isShareable()){
      throw new OperationNotPermittedException("No puedo dar comentarios sobre algo archivado o que no se puede compartir. ");
    }
    User user = ((User)connectedUser.getPrincipal());
    if(!Objects.equals(book.getOwner().getId(), user.getId())){
      throw new OperationNotPermittedException("No puedes dar comentarios sobre tu propio libro");
    }
    Feedback feedback = feedbackMapper.toFeedback(request);
    return feedbackRepository.save(feedback).getId();
  }

  public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
    Pageable pageable = PageRequest.of(page,size);
    User user = ((User)connectedUser.getPrincipal());
    Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId,pageable);
    List<FeedbackResponse> feedbackResponse = feedbacks.stream()
            .map(f -> feedbackMapper.toFeedBackResponse(f,user.getId()))
            .toList();
    return new PageResponse<>(
            feedbackResponse,
            feedbacks.getNumber(),
            feedbacks.getSize(),
            feedbacks.getTotalElements(),
            feedbacks.getTotalPages(),
            feedbacks.isFirst(),
            feedbacks.isLast()
    );
  }
}
