package org.sidos.codegeneration

import org.sidos.model.compiler.SidosCompiler
import scala.collection.JavaConversions._
import org.sidos.model.{Type,AssociationType}
import scala.reflect.BeanProperty
//import org.clapper.scalasti.StringTemplateGroup
import org.antlr.stringtemplate.StringTemplateGroup
import io.Source
import java.io.{FileWriter, File, StringReader}
import java.util.{Locale, ArrayList}

object Generator
{
  def main(args: Array[String]) {

    if(args.size < 2)
    {
      println("The Generator needs two arguments")
      return;
    }
    new Generator().generate(args(0), args(1))
  }
}

class Generator
{
  def generate(sourceDirectory: String, targetDirectory: String) {

    if(!directoryExists(sourceDirectory) || !directoryExists(targetDirectory))
      return;

    var source = ""
    for (file <- new File(sourceDirectory).listFiles) {
      if (!file.isDirectory)
        if (file.getName.toLowerCase.endsWith(".sidos"))
        {
          println("adding source file " + file.getName)
          source += Source.fromFile(file,"UTF8").mkString
        }
    }

    generateFromSource(source, targetDirectory)

  }

  def generateFromSource(source:String, targetDirectory:String)
  {
    if(!directoryExists(targetDirectory))
      return;

    for (_type <- SidosCompiler.compile(source))
    {
      try{
       generate(_type, targetDirectory)
      }catch {
        case e:Exception => println(e.toString)
      }
    }
  }

  def directoryExists(path:String) : Boolean = {
    if(!new File(path).exists)
    {
      println(path + "does not exist")
      return false
    }else
    {
      return true
    }
  }

  def generate(_type:Type, targetDirectory:String)
  {

      def ripNamespace(fullName:String) = fullName.drop(fullName.lastIndexOf(".") + 1)
      def ripName(fullName:String) = fullName.take(fullName.lastIndexOf("."))
      def capitalizeFirstLetter(value:String) = value.take(1).toUpperCase + value.drop(1)
      def getGeneratedTypeName(_type:Type) = capitalizeFirstLetter(ripNamespace(_type.name))
      def getFullGeneratedTypeName(_type:Type) = ripName(_type.name) + "." + getGeneratedTypeName(_type)

    //val typeClassTemplate = new StringTemplateGroup("sidos").template("sidos")
//val typeClassTemplate = new StringTemplateGroup( Source.fromString(
      val typeClassTemplate = new StringTemplateGroup( new StringReader(
"""
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

""")).getInstanceOf("entity")


      case class Property(@BeanProperty name:String,
                     @BeanProperty fullName:String,
                     @BeanProperty propertyType:String,
                     @BeanProperty rangeClassName:String,
                     @BeanProperty domainClassName:String,
                     @BeanProperty associationType:String,
                     @BeanProperty isEntityProperty:Boolean,
                     @BeanProperty queryTypeDefinition:String)

      typeClassTemplate.setAttribute("properties", _type.properties.map((property) => {

        val propertyType = (ripNamespace(property.range.name), property.associationType) match {
          case ("string",AssociationType.Single) => "org.sidos.codegeneration.SingleValueProperty[String]"
          case ("string",AssociationType.List) => "org.sidos.codegeneration.ListProperty[String]"
          case ("boolean",AssociationType.Single) => "org.sidos.codegeneration.SingleValueProperty[java.lang.Boolean]"
          case ("boolean",AssociationType.List) => "org.sidos.codegeneration.ListProperty[java.lang.Boolean]"
          case ("time",AssociationType.Single) => "org.sidos.codegeneration.SingleValueProperty[java.util.Date]"
          case ("time",AssociationType.List) => "org.sidos.codegeneration.ListProperty[java.util.Date]"
          case (_,AssociationType.Single) => "org.sidos.codegeneration.EntityProperty"
          case (_,AssociationType.List) => "org.sidos.codegeneration.EntityListProperty"
        }

        val queryTypeDefinitionOption = (ripNamespace(property.range.name), property.associationType) match {
          case ("string",AssociationType.Single) => Some("org.sidos.database.query.QueryableStringProperty")
          case ("boolean",_) => None
          case ("time",_) => None
          case (_,AssociationType.List) => Some("org.sidos.database.query.QueryableEntityListProperty[" + getFullGeneratedTypeName(property.range) + "]")
          case (_,AssociationType.Single) => Some("org.sidos.database.query.QueryableEntityProperty[" + getFullGeneratedTypeName(property.range) + "]")
        }

        //val queryTypeDefinition = (for(value <- queryTypeDefinitionOption) yield " with " + value.get) getOrElse ""
        val queryTypeDefinition = queryTypeDefinitionOption match {
          case Some(value) => " with " + value
          case _ => ""
        }


        Property(name = ripNamespace(property.name),
                fullName = property.name,
                propertyType = propertyType,
                rangeClassName = getFullGeneratedTypeName(property.range),
                domainClassName = getFullGeneratedTypeName(property.domain),
                associationType = property.associationType.toString,
                isEntityProperty = propertyType.equals("org.sidos.codegeneration.EntityProperty") | propertyType.equals("org.sidos.codegeneration.EntityListProperty"),
                queryTypeDefinition = queryTypeDefinition)

      }).toArray)

      val extendsString =  _type.superTypes.foldLeft("")( _ + " with " + getFullGeneratedTypeName(_))

      typeClassTemplate.setAttribute("extends", extendsString)

//      val superTypesList = getGeneratedTypeName(_type.superTypes.head) + _type.superTypes.tail.foldLeft("")( _ + ", " + getGeneratedTypeName(_))

      typeClassTemplate.setAttribute("packageName", ripName(_type.name))
      typeClassTemplate.setAttribute("typeName", getGeneratedTypeName(_type))
      typeClassTemplate.setAttribute("typeFullName", _type.name)
      typeClassTemplate.setAttribute("typeHash", _type.hash)


      writeFile(targetDirectory, ripName(_type.name), getGeneratedTypeName(_type)  + ".scala", typeClassTemplate.toString)

    val repositoryTemplate = new StringTemplateGroup(new StringReader(
"""
group sidosrepository;

repository(packageName, typeName, properties) ::= <<

package <packageName>

class <typeName>Repository(database:org.sidos.database.Database){

  <properties:property()>

}

>>

property() ::= <<
def getBy<it.name>(value:<it.range>) = new <it.domain>Entity(database, database.getBy(<it.domain>.typeHash, "<it.fullName>", value).head)

>>
""")).getInstanceOf("repository")

    case class RepositoryProperty(@BeanProperty name:String,
                                 @BeanProperty fullName:String,
                                 @BeanProperty domain:String,
                                 @BeanProperty range:String)


    def getScalaTypeName(typeName:String) = typeName match {
        case "org.sidos.primitive.string" => "String"
        case "org.sidos.primitive.time" => "Date"
        case _ => "UUID"
    }

    repositoryTemplate.setAttribute("properties", _type.properties.filter(_.range.name.equals("org.sidos.primitive.string")).map((property) => {

      RepositoryProperty(capitalizeFirstLetter(ripNamespace(property.name)),
        property.name,
        getGeneratedTypeName(property.domain),
        getScalaTypeName(property.range.name))

    }).toArray)

    repositoryTemplate.setAttribute("packageName", ripName(_type.name))
    repositoryTemplate.setAttribute("typeName", getGeneratedTypeName(_type))

    // Repositorys will be replaced by queries
    // writeFile(targetDirectory, ripName(_type.name), getGeneratedTypeName(_type)  + "Repository.scala", repositoryTemplate.toString)


  }

  def writeFile(baseDirectory:String, packageName:String, fileName:String, contents:String)
  {
    val fileFullName = baseDirectory + "\\" + packageName.replace(".","\\") + "\\" + fileName
    println("Writing to  " + fileFullName)
    if(baseDirectory != null)
    {
      val file = new File(fileFullName)
      new File(file.getParent()).mkdirs()
      val fileWriter = new FileWriter(file)
      fileWriter.write(contents)
      fileWriter.close()
    }
  }
}


