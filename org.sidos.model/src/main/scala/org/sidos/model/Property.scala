package org.sidos.model

class Property(val name:String,
               var domain:Type = null,
               var range:Type = null,
               val associationType: AssociationType.Value)