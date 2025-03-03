package dev.wayron.book_tracker_api.exceptions.logs

import dev.wayron.book_tracker_api.exceptions.ExceptionErrorMessages
import java.lang.IllegalStateException

class InvalidReadingLogException : IllegalStateException(ExceptionErrorMessages.LOG_WITH_INVALID_VALUE.message)