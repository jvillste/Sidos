group sidos;

entity(packageName, typeName, typeFullName, typeHash, properties, extends) ::= <<

package <packageName>

trait <typeName> extends org.sidos.codegeneration.Entity <extends>
{

  <properties:property()>
}

class <typeName>Entity(val database: org.sidos.database.Database, val id:java.util.UUID) extends org.sidos.codegeneration.Entity with <packageName>.<typeName>
{

}

object <typeName>
{

  val entityType = new org.sidos.model.Type("<typeFullName>")

  def create(database:org.sidos.database.Database) = {
    new <typeName>Entity(database, database.createEntity(entityType.hash))
  }

  def instances = org.sidos.database.query.InstanceQuery(entityType.hash)

  <properties:typeProperty()>

}

>>

property() ::= <<
<if(it.isEntityProperty)>
val <it.name> = new <it.propertyType>[<it.rangeClassName>](this, <it.domainClassName>.entityType.hash, "<it.fullName>", (database:org.sidos.database.Database,id:java.util.UUID) => new <it.rangeClassName>Entity(database,id))

<else>
val <it.name> = new <it.propertyType>(this, <it.domainClassName>.entityType.hash, "<it.fullName>")

<endif>
>>

typeProperty() ::= <<
val <it.name> = new org.sidos.model.Property("<it.fullName>",entityType,<it.rangeClassName>.entityType,org.sidos.model.AssociationType.<it.associationType>) <it.queryTypeDefinition>
entityType.properties =  <it.name> :: entityType.properties

>>
