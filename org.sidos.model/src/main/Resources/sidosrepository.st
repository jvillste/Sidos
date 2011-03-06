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