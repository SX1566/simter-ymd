package tech.simter.ymd.dao.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import tech.simter.ymd.dao.YmdDao
import tech.simter.ymd.po.Ymd
import java.time.Month
import java.time.MonthDay
import java.time.Year
import java.time.YearMonth

/**
 * The JPA implementation of [YmdDao].
 *
 * @author RJ
 */
@Component
class YmdDaoImpl @Autowired constructor(
  private val repository: YmdJpaRepository
) : YmdDao {
  override fun save(vararg ymd: Ymd): Mono<Void> {
    return try {
      repository.saveAll(ymd.asIterable())
      Mono.empty()
    } catch (e: Exception) {
      Mono.error(e)
    }
  }

  override fun findYears(type: String): Flux<Year> {
    return Flux.fromStream(repository.findYears(type).map { Year.of(it) })
  }

  override fun findMonths(type: String, year: Year): Flux<Month> {
    return Flux.fromStream(repository.findMonths(type, year.value).map { Month.of(it) })
  }

  override fun findDays(type: String, yearMonth: YearMonth): Flux<MonthDay> {
    return Flux.fromStream(
      repository.findDays(type, yearMonth.year, yearMonth.monthValue)
        .map { MonthDay.of(yearMonth.month, it) }
    )
  }
}