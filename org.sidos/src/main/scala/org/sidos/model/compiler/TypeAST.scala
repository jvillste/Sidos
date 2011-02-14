package org.sidos.model.compiler

case class TypeAST(name:String, superTypes:List[String], properties:List[PropertyAST] )