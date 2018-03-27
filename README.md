# RestyQL
####Query Interface for APIs

RestyQL is a query Interface for APIs benchmarking GraphQL and helps communicating between Client and back-end Server, solving the main problem of OOP.\
Regardless of whether you need it or not, a number of properties are structural problems that Object-Orient-Programming has.
It communicates with resources in form of object, Neither side client or server could select properties which they only require.\
'RestQL' is the solution to this problem by specifying and communicating what is needed.


###Feature
1. **RestyQL is about setting or asking for specific fields required or restricted on object.** \
    For example, Server can set a range of fields on objects returning as a response, and at the same time specify some fields not to be sent with 'RestyQL annotation'. Client side can also ask for specific fields on objects and specify some fields to Ignore by adding 'query','ignore' parameter when requests http.

2. Also, **RestyQL helps http communication be self-documenting**. Adding 'schema' parameter in http request, client would get empty schema of objects which describe the result of request.
    
