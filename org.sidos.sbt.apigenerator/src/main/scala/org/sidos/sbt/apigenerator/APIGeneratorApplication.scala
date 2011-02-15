package org.sidos.sbt.apigenerator


object APIGeneratorApplication
{
  def main(args: Array[String]) {
    new org.sidos.generation.Generator().generate(args(0), args(1))
  }
}