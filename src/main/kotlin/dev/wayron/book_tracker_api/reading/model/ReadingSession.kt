package dev.wayron.book_tracker_api.reading.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dev.wayron.book_tracker_api.book.model.Book
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "readings")
@JsonIgnoreProperties("hibernateLazyInitializer", "handler")
data class ReadingSession(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Int,

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  val bookId: Book,
  var progressInPercentage: Double,
  var totalProgress: Int,
  var pages: Int,
  val chapters: Int? = 0,

  @Enumerated(EnumType.STRING)
  @Column(name = "reading_state")
  var readingState: ReadingState = ReadingState.TO_READ,

  @Enumerated(EnumType.STRING)
  @Column(name = "tracking_method")
  val trackingMethod: TrackingMethod = TrackingMethod.PAGES,
  var dailyGoal: Int = 0,

  val startReadingDate: LocalDateTime = LocalDateTime.now(),
  var endReadingDate: LocalDateTime?,
  var estimatedCompletionDate: LocalDateTime?,
) {

  fun addProgress(quantityRead: Int) {
    if (readingState == ReadingState.READ) return

    totalProgress += quantityRead

    if (dailyGoal != 0) {
      val howManyReadingsLeft = if (trackingMethod == TrackingMethod.PAGES) pages else chapters!!
      val howManyDaysLeft = kotlin.math.ceil((howManyReadingsLeft - totalProgress) / dailyGoal.toDouble())
      estimatedCompletionDate = LocalDateTime.now().plusDays(howManyDaysLeft.toLong())
    }

    progressInPercentage = when (trackingMethod) {
      TrackingMethod.PAGES -> (totalProgress / pages.toDouble()) * 100
      TrackingMethod.CHAPTERS -> (totalProgress / chapters!!.toDouble()) * 100
    }

    if (progressInPercentage >= 100.0) {
      progressInPercentage = 100.0
      totalProgress = if (trackingMethod == TrackingMethod.PAGES) pages else chapters!!
      dailyGoal = 0
      readingState = ReadingState.READ
      endReadingDate = LocalDateTime.now()
      estimatedCompletionDate = LocalDateTime.now()
    }
  }

}