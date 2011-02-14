package org.sidos.model

object AssociationType extends Enumeration
{
  val Single = Value("Single")
  val Set = Value("Set")
  val List = Value("List")
  val OneToManyList = Value("OneToManyList")
  val OneToManySet = Value("OneToManySet")
  val OneToManySingle = Value("OneToManySingle")
}