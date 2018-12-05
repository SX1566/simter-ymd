package tech.simter.ymd.dao.reactive.mongo

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction.ASC
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import reactor.test.StepVerifier
import tech.simter.ymd.dao.YmdDao
import tech.simter.ymd.dao.reactive.mongo.TestUtils.randomYmd
import tech.simter.ymd.po.Ymd

/**
 * See [SimpleReactiveMongoRepository] implementation.
 * @author RJ
 */
@Disabled
@SpringJUnitConfig(ModuleConfiguration::class)
@DataMongoTest
class SaveMethodImplTest @Autowired constructor(
  private val operations: ReactiveMongoOperations,
  private val repository: YmdReactiveRepository,
  private val dao: YmdDao
) {
  @Test
  fun `Save nothing`() {
    StepVerifier.create(dao.save()).verifyComplete()
    //.consumeErrorWith { println(it.stackTrace) }
    //.verify()
    StepVerifier.create(repository.count()).expectNext(0).verifyComplete()
  }

  @Test
  fun `Save one`() {
    // init data
    val po = randomYmd()

    // invoke
    val result = dao.save(po)

    // verify
    StepVerifier.create(result).verifyComplete()
    StepVerifier.create(
      operations.find(Query(Criteria
        .where("type").isEqualTo(po.type)
        .and("year").isEqualTo(po.year)
        .and("month").isEqualTo(po.month)
        .and("day").isEqualTo(po.day)
      ), Ymd::class.java)
    ).expectNext(po).verifyComplete()
  }

  @Test
  fun `Save two`() {
    // init data
    val po1 = randomYmd()
    val po2 = randomYmd(type = po1.type, year = po1.year + 1)

    // invoke
    val result = dao.save(po1, po2)

    // verify
    StepVerifier.create(result).verifyComplete()
    StepVerifier.create(
      operations.find(Query(Criteria
        .where("type").isEqualTo(po1.type)
        .and("year").gt(2006)
      ).with(Sort(ASC, "year")), Ymd::class.java)
    ).expectNext(po1).expectNext(po2).verifyComplete()
  }
}