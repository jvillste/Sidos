package org.sidos.database
import juvi.JDBC
import notification.{PropertyNotification}
import org.sidos.model.{Property, Type, AssociationType}
import org.sidos.model.compiler.SidosCompiler
import java.util.{Date, UUID}
import java.sql.{Timestamp, PreparedStatement, ResultSet}

trait Database extends JDBC with Relational with PropertyNotification
{

  val metamodel =
  """
  org.sidos.metamodel {

    entity{
      hasType : type
    }

    type{
      name : string
      hash : string
    }

    enumeration {
      name : string
    }

    property {
      name : string
      domain : type
      range : type
      collectionType : collectionType
    }

    collectionType : enumeration
    
  }
  """

  var typeTypeEntity : UUID = null
  var entityTypeEntity : UUID = null
  var propertyTypeEntity : UUID = null

  var typeTypeHash : String = null
  var entityTypeHash : String = null
  var propertyTypeHash : String = null

  def createMetamodelSchema
  {
    val types = SidosCompiler.compile(metamodel)
    val typeType = types.find(_.name.equals("org.sidos.metamodel.type")).get
    val entityType = types.find(_.name.equals("org.sidos.metamodel.entity")).get
    val propertyType = types.find(_.name.equals("org.sidos.metamodel.property")).get

    typeTypeHash = typeType.hash
    entityTypeHash = entityType.hash
    propertyTypeHash = propertyType.hash

    
    typeTypeEntity = createEntityID

    createTypeTables(typeType)
    createTypeTables(entityType)
    createTypeTables(propertyType)
    
    set(entityTypeHash, typeTypeEntity, "org.sidos.metamodel.entity.hasType", typeTypeEntity)


    entityTypeEntity = createEntity(typeTypeEntity)
    propertyTypeEntity = createEntity(typeTypeEntity)


    addTypeMetadata(typeTypeEntity,typeType)
    addTypeMetadata(entityTypeEntity,entityType)
    addTypeMetadata(propertyTypeEntity,propertyType)
  }

  def addType(_type:Type)
  {
    println("add type " + _type.hash + " "  + _type.name)
    
    if(!isTypeDefined(_type))
    {
      createTypeTables(_type)

      val typeEntity = createEntity(typeTypeEntity)

      addTypeMetadata(typeEntity,_type)
    }
  }

  def addTypeMetadata(typeEntity:UUID, _type:Type)
  {
    println("addTypeMetadata " + _type.hash)
    set(typeTypeHash, typeEntity, "org.sidos.metamodel.type.name",_type.name)
    set(typeTypeHash, typeEntity, "org.sidos.metamodel.type.hash",_type.hash)
    for(property <- _type.properties)
    {
      val propertyEntity = createEntity(propertyTypeEntity)

      set(propertyTypeHash, propertyEntity,"org.sidos.metamodel.property.name",property.name)
      set(propertyTypeHash, propertyEntity,"org.sidos.metamodel.property.domain",typeEntity)
    }
  }


  def createEntity(typeHash:String) : UUID =
  {
    println("create entity " + typeHash)
    val _type : UUID = getBy(typeTypeHash,"org.sidos.metamodel.type.hash",typeHash).head
    createEntity(_type)
  }

  def createEntity(_type:UUID) : UUID =
  {
    val id = createEntityID
    set(entityTypeHash, id, "org.sidos.metamodel.entity.hasType", _type)
    id
  }

  def createEntityID = UUID.randomUUID

  def isTypeDefined(_type:Type) = getBy(typeTypeHash,"org.sidos.metamodel.type.hash",_type.hash).length > 0

}