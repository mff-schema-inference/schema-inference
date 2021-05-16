package cz.cuni.mff.ksi.nosql.s13e.impl.inference

import cz.cuni.mff.ksi.nosql.s13e.impl.inference.schema.{EmptyInternalNoSqlSchema, InternalEntity, InternalNoSqlSchema}

private case object SchemaFolder extends ((InternalNoSqlSchema, InternalNoSqlSchema) => InternalNoSqlSchema) {

  override def apply(left: InternalNoSqlSchema, right: InternalNoSqlSchema): InternalNoSqlSchema =
    (left, right) match {
      case (EmptyInternalNoSqlSchema, _) => right
      case (_, EmptyInternalNoSqlSchema) => left
      case _ => InternalNoSqlSchema(None, mergeEntities(left.entities, right.entities))
    }

  private def mergeEntities(left: List[InternalEntity], right: List[InternalEntity]): List[InternalEntity] =
    (left, right) match {
      case (lh :: lt, rh :: rt)
    }

}
