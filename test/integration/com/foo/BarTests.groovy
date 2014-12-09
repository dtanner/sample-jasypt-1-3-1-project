package com.foo

import grails.test.GrailsUnitTestCase
import groovy.sql.Sql

class BarTests extends GrailsUnitTestCase {
    def dataSource

    String CORRELATION_ID = "ABC123"

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testStringStringEncryption() {
        testPropertyAsStringEncryption('firstName', 'FIRST_NAME', 'foo')
    }


    void testPropertyAsStringEncryption(property, rawProperty, value) {
        def originalBar = new Bar(correlationId: CORRELATION_ID)
        originalBar."$property" = value
        originalBar.save(failOnError: "true")

        withBarForCorrelationId(CORRELATION_ID) { bar, rawBar ->
            assertEquals value, bar."$property"
            def rawPropertyValue = rawBar."$rawProperty"
            assertTrue value.toString() != rawPropertyValue
            assertTrue rawPropertyValue.endsWith("=")
        }
    }

    def withBarForCorrelationId(correlationId, closure) {
        def bar = Bar.findByCorrelationId(correlationId)
        assertNotNull bar
        retrieveRawBarFromDatabase(correlationId) { rawBar ->
            assertNotNull rawBar
            closure(bar, rawBar)
        }
    }

    def retrieveRawBarFromDatabase(correlationId, closure) {
        new Sql(dataSource).with { db ->
            try {
                def result = db.firstRow("SELECT * FROM bar where correlation_id = $correlationId")
                closure(result)
            } finally {
                db.close()
            }
        }
    }
}
