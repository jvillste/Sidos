package org.sidos.model

import java.security.MessageDigest
import java.math.BigInteger

class Type(val name:String,
           var properties:List[Property] = List.empty[Property],
           var superTypes:List[Type] = List.empty[Type])
{

  def hash : String =
  {
    var digestString = name

    digestString += properties.sortBy[String](_.name).foldLeft("")(
      (accumulator, property) =>
        accumulator +
        property.name +
        property.associationType +
        (if(property.domain != this) property.domain.hash else "")
    )

    digestString += superTypes.foldLeft("")(_ + _.hash)

    hashForString(digestString)
  }

  def hashForString(string:String) =
  {
    val m = MessageDigest.getInstance("MD5")
    val data = string.getBytes()
    m.update(data,0,data.length)
    val i = new BigInteger(1,m.digest());
    String.format("%1$032X", i);
  }

}