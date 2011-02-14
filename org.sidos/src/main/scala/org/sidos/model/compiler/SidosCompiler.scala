package org.sidos.model.compiler

import scala.collection.mutable._
import org.sidos.model.{AssociationType, Type, Property}

object SidosCompiler
{

  val metamodel =
  """
  org.sidos.primitive{
    string
    integer
    time
    boolean
  }
  """

  def compile(namespaceDefinitions:String) : List[Type] =
  {
    compileAST(SidosParser.parse(metamodel) ::: SidosParser.parse(namespaceDefinitions))
  }

  def compileAST(namespaceASTs:List[NamespaceAST]) : List[Type] =
  {
    // Collect types

    val namespaces = Map.empty[String, Set[Type]]

    for(namespaceAST <- namespaceASTs)
    {

      val namespace = Set.empty[Type]
      namespaces(namespaceAST.name) = namespace

      for(typeAST <- namespaceAST.types)
      {
        namespace += new Type(namespaceAST.name + "." + typeAST.name)
      }
    }

    // Connect types

    for(namespaceAST <- namespaceASTs)
    {
      val namespace = namespaces(namespaceAST.name)

      val context =  Map.empty[String, Type]


      def ripNamespace(fullName:String) = fullName.drop(fullName.lastIndexOf(".") + 1)

      for(_type <- namespaces("org.sidos.primitive"))
      {
        context(ripNamespace(_type.name)) = _type
      }

      for(_type <- namespace)
      {
        context(ripNamespace(_type.name)) = _type
      }

      for(importAST <- namespaceAST.imports)
      {
        for(_type <- namespaces(importAST.namespaceName))
        {
          

          context(importAST.importName + "." + ripNamespace(_type.name)) = _type
        }
      }

      for(typeAST <- namespaceAST.types)
      {
        val range = namespace.find(_.name == namespaceAST.name + "." + typeAST.name).get


        def findType(name:String, context:Map[String,Type]) : Type =
        {
          val foundType = context(name)

          if(foundType == null)
            throw new Exception(name + " not found for " +
                    namespaceAST.name + "." +
                    typeAST.name)

          foundType
        }


        for(propertyAST <- typeAST.properties)
        {

          val domain = findType(propertyAST.range, context)

          def associationTypeFromString(string:String) : AssociationType.Value =  string match {
              case "single" => AssociationType.Single
              case "list" => AssociationType.List
              case "set" => AssociationType.Set
          }

          range.properties = new Property(range.name + "." + propertyAST.name, range, domain, associationTypeFromString(propertyAST.associationType)) :: range.properties
        }

        for(superTypeName <- typeAST.superTypes)
        {
          range.superTypes = findType(superTypeName, context) :: range.superTypes
        }
      }
    }
    
    return namespaces.values.foldLeft(List.empty[Type])(_ ::: _.toList)
  }


}

