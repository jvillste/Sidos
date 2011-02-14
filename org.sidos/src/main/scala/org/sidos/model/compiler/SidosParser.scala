package org.sidos.model.compiler

import util.parsing.combinator.JavaTokenParsers

object SidosParser extends JavaTokenParsers {
  def schemaParser:Parser[List[NamespaceAST]] = rep(namespaceParser)

  def namespaceParser: Parser[NamespaceAST] = namespaceNameParser ~ "{" ~ rep(importParser) ~ rep(typeParser) ~ "}" ^^
           { case name ~ "{" ~ imports ~ types ~ "}" => NamespaceAST(name, imports, types) }

  def typeParser: Parser[TypeAST] = ident ~ opt(":" ~> repsep(ident,",")) ~ opt("{" ~> rep(propertyParser) <~ "}") ^^
           { case name ~ superTypes ~ properties => TypeAST(name,superTypes.getOrElse(List.empty[String]), properties.getOrElse(List.empty[PropertyAST])) }

  def propertyParser: Parser[PropertyAST] = ident ~ ":" ~ typeReferenceParser ~ opt(associationTypeParser) ^^
           { case name ~ ":" ~ range ~ associationType => PropertyAST(name, range, associationType.getOrElse("single")) }

  def importParser: Parser[ImportAST] = namespaceNameParser ~ "as" ~ ident ^^
    {case namespaceName ~ "as" ~ importName => ImportAST(namespaceName, importName)}

  def namespaceNameParser: Parser[String] = """[a-zA-Z_](\w|\.)*""".r

  def typeReferenceParser: Parser[String] = """[a-zA-Z_](\w|\.)*""".r

  def associationTypeParser: Parser[String] = "single" | "list" | "set" | "manyToOneSingle" | "manyToOneList" | "manyToOneSet"

  def parse(source:String):List[NamespaceAST] =
  {
    val result = parseAll(schemaParser, source)

    if(!result.successful)
    {
      throw new Exception(result.toString)
    }

    return result.get
  }
}



