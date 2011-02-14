package org.sidos.database

trait DatabaseDefinition
{
  def databasePath:String = "db"
  def inMemory:Boolean = true
  def userName:String = "sa"
  def password:String = ""
}