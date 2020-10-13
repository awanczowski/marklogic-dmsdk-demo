# DMSDK Data Load Demo

This demo application creates a connection to MarkLogic using the MarkLogic Java Client and loads documents using DMSDK 
WriteBatcher.

## Data Movement SDK (DMSDK)
DMSDK can be use for Asynchronus Multi-Document Operations. DMSDK is a package in the Java Client API intended for 
manipulating large numbers of documents and/or metadata through an asynchronous interface that efficiently distributes 
workload across a MarkLogic cluster. This framework is best suited for long running operations and/or those that 
manipulate large numbers of documents.

<https://docs.marklogic.com/guide/java/data-movement>

## Loading REST Data
A sample REST endpoint is created within this Java application. A Spring REST template is used to fetch data from the 
endpoint. This is then passed to the DMSDK WriteBatcher to store the contents. 

## Loading SQL Data
A H2 Database is pre-configured in this Java application. A Spring JPA template is used to query the H2 DB. 
This is then passed to the DMSDK WriteBatcher to store the contents. 

                                                            
                                                            You can use the Data Movement SDK out-of-the-box to insert, extract, delete, and transform documents in MarkLogic. You can also easily extend the framework to perform other operations.