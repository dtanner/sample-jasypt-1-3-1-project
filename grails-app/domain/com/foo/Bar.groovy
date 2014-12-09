package com.foo

import com.bloomhealthco.jasypt.GormEncryptedStringType

class Bar {
	String firstName
    String correlationId

    static constraints = {
        firstName nullable: true, maxSize: 384
    }

	static mapping = {
    	firstName type: GormEncryptedStringType
    }
}
