package org.sidos.codegeneration

import org.sidos.model.compiler.SidosCompiler
import scala.collection.JavaConversions._
import org.sidos.model.{Type,AssociationType}
import scala.reflect.BeanProperty
import org.clapper.scalasti.StringTemplateGroup
import io.Source
import java.io.{FileWriter, File, StringReader}
import java.util.ArrayList


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
    def directoryExists(path:String) : Boolean = {
      if(!new File(sourceDirectory).exists)
      {
        println(sourceDirectory + "does not exist")
        return false
      }else
      {
        return true
      }
    }

    if(!directoryExists(sourceDirectory) || !directoryExists(targetDirectory))
      return;

    var source = ""
    for (file <- new File(sourceDirectory).listFiles) {
      if (!file.isDirectory)
        if (file.getName.toLowerCase.endsWith(".sidos"))
        {
          println("adding source file 2" + file.getName)
          source += Source.fromFile(file).mkString
        }
    }

    println("compiling: " + source)
    for (_type <- SidosCompiler.compile(source))
    {
      println("generating type " + _type.name)
      try{
       generate(_type, targetDirectory)
      }catch {
        case e:Exception => println(e.toString)
      }
    }
  }

  def generate(_type:Type, targetDirectory:String)
  {
      def ripNamespace(fullName:String) = fullName.drop(fullName.lastIndexOf(".") + 1)
      def ripName(fullName:String) = fullName.take(fullName.lastIndexOf("."))
      def capitalizeFirstLetter(value:String) = value.take(1).toUpperCase + value.drop(1)
      def getGeneratedTypeName(_type:Type) = capitalizeFirstLetter(ripNamespace(_type.name))

      val typeClassTemplate = new StringTemplateGroup( Source.fromString(
"""
group sidos;

entity(packageName, typeName, typeFullName, typeHash, properties) ::= <<

package <packageName>


class <typeName>(val database: org.sidos.database.Database, val id:java.util.UUID) extends org.sidos.codegeneration.Entity
{
  def typeHash = <typeName>.typeHash

  <properties:property()>

}

object <typeName>
{
  val typeHash = "<typeHash>"

  val entityType = new org.sidos.model.Type("<typeFullName>")
  <properties:typeProperty()>

}

>>

property() ::= <<
<if(it.isEntityProperty)>
val <it.name> = new <it.propertyType>[<it.rangeClassName>](this, "<it.fullName>", (database:org.sidos.database.Database,id:java.util.UUID) => new <it.rangeClassName>(database,id))

<else>
val <it.name> = new <it.propertyType>(this, "<it.fullName>")

<endif>
>>

typeProperty() ::= <<
entityType.properties = new org.sidos.model.Property("<it.fullName>",entityType,<it.rangeClassName>.entityType,org.sidos.model.AssociationType.<it.associationType>) :: entityType.properties

>>

""")).template("entity")


      case class Property(@BeanProperty val name:String,
                     @BeanProperty val fullName:String,
                     @BeanProperty val propertyType:String,
                     @BeanProperty val rangeClassName:String,
                     @BeanProperty val associationType:String,
                     @BeanProperty val isEntityProperty:Boolean)

      typeClassTemplate.setAttribute("properties", _type.properties.map((property) => {

        val propertyType = (ripNamespace(property.range.name), property.associationType) match {
          case ("string",AssociationType.Single) => "org.sidos.codegeneration.Property[String]"
          case ("string",AssociationType.List) => "org.sidos.codegeneration.ListProperty[String]"
          case ("boolean",AssociationType.Single) => "org.sidos.codegeneration.Property[java.lang.Boolean]"
          case ("boolean",AssociationType.List) => "org.sidos.codegeneration.ListProperty[java.lang.Boolean]"
          case ("time",AssociationType.Single) => "org.sidos.codegeneration.Property[java.util.Date]"
          case ("time",AssociationType.List) => "org.sidos.codegeneration.ListProperty[java.util.Date]"
          case (_,AssociationType.Single) => "org.sidos.codegeneration.EntityProperty"
          case (_,AssociationType.List) => "org.sidos.codegeneration.EntityListProperty"
        }

        val rangeClassName = ripName(property.range.name) + "." + getGeneratedTypeName(property.range)

        Property(ripNamespace(property.name), property.name, propertyType, rangeClassName, property.associationType.toString, propertyType.equals("org.sidos.codegeneration.EntityProperty") | propertyType.equals("org.sidos.codegeneration.EntityListProperty"))

      }): _*)



      typeClassTemplate.setAttribute("packageName", ripName(_type.name))
      typeClassTemplate.setAttribute("typeName", getGeneratedTypeName(_type))
      typeClassTemplate.setAttribute("typeFullName", _type.name)
      typeClassTemplate.setAttribute("typeHash", _type.hash)

      writeFile(targetDirectory, ripName(_type.name), getGeneratedTypeName(_type)  + ".scala", typeClassTemplate.toString)


      val repositoryTemplate = new StringTemplateGroup( Source.fromString(
"""
group sidos;

repository(packageName, typeName, properties) ::= <<

package <packageName>

class <typeName>Repository(database:org.sidos.database.Database){
  def create = {
    new <typeName>(database, database.createEntity(<typeName>.typeHash))
  }

  <properties:property()>

}

>>

property() ::= <<
def getBy<it.name>(value:<it.range>) = new <it.domain>(database, database.getBy(<it.domain>.typeHash, "<it.fullName>", value).head)

>>
""")).template("repository")

    case class RepositoryProperty(@BeanProperty val name:String,
                                 @BeanProperty val fullName:String,
                                 @BeanProperty val domain:String,
                                 @BeanProperty val range:String)


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

    }): _*)

    repositoryTemplate.setAttribute("packageName", ripName(_type.name))
    repositoryTemplate.setAttribute("typeName", getGeneratedTypeName(_type))

    writeFile(targetDirectory, ripName(_type.name), getGeneratedTypeName(_type)  + "Repository.scala", repositoryTemplate.toString)


  }

  def writeFile(baseDirectory:String, packageName:String, fileName:String, contents:String)
  {
    val fileFullName = baseDirectory + "\\" + packageName.replace(".","\\") + "\\" + fileName
    println("Writing to  " + fileFullName)
    println(contents)
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


