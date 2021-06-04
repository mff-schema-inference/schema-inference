package cz.cuni.mff.ksi.nosql.s13e.impl.testUtil

import cz.cuni.mff.ksi.nosql.s13e.impl.NoSQLSchema._
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

trait ModelChecking extends Matchers {

  private type JList[E] = java.util.List[E]

  trait TypeChecker extends (Type => Unit) {

    override def apply(v1: Type): Unit = check(v1)

    def check(`type`: Type): Unit

  }

  def aBoolean: TypeChecker = t => t shouldBe a[Boolean]

  def aNumber: TypeChecker = t => t shouldBe a[Number]

  def aString: TypeChecker = t => t shouldBe a[String]

  def anArrayOf(inner: TypeChecker): TypeChecker = t => {
    t shouldBe a[Array]
    inner(t.asInstanceOf[Array].getElementType)
  }

  def anAggregateOf(target: EntityVersion): TypeChecker = t => {
    t shouldBe an[Aggregate]
    t.asInstanceOf[Aggregate].getTarget shouldBe target
  }

  def aReferenceOf(target: Entity, originalTypeChecker: TypeChecker): TypeChecker = t => {
    t shouldBe an[EntityReference]
    t.asInstanceOf[EntityReference].getTarget shouldBe target
    originalTypeChecker(t.asInstanceOf[EntityReference].getOriginalType)
  }

  def aUnionOf(inner: TypeChecker*): TypeChecker = t => {
    t shouldBe a[UnionType]
    t.asInstanceOf[UnionType].getTypes should have size inner.size
    inner zip t.asInstanceOf[UnionType].getTypes.asScala foreach {
      case (checker, singleType) => checker(singleType)
    }
  }

  def checkEntity(entity: Entity, name: java.lang.String, root: scala.Boolean, expected: (Int, Int)*)
                 (checker: JList[EntityVersion] => Unit): Unit = {
    val expectedFlattened = name endsWith "*"
    entity.getName shouldBe name.stripSuffix("*")
    entity.isRoot shouldBe root
    entity.isFlattened shouldBe expectedFlattened

    val versions = entity.getVersions
    versions should have size expected.size
    Stream from 0 zip expected foreach {
      case (index, (aggregates, additional)) =>
        val version = versions.get(index)
        version.getId shouldBe index
        version.getAggregates should have size aggregates
        version.getAdditionalCount shouldBe additional
    }

    checker(entity.getVersions)
  }

  def checkProperties(properties: JList[Property], expected: (java.lang.String, TypeChecker)*): Unit = {
    properties should have size expected.size
    properties.asScala zip expected foreach {
      case (prop, (name, typeChecker)) =>
        val expectedOptional = name endsWith "?"
        prop.getName shouldBe name.stripSuffix("?")
        prop.isOptional shouldBe expectedOptional
        typeChecker(prop.getType)
    }
  }

}