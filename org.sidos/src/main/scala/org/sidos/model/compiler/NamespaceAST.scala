package org.sidos.model.compiler

case class NamespaceAST(name:String, imports:List[ImportAST], types:List[TypeAST])